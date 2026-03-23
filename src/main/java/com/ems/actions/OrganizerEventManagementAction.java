package com.ems.actions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/**
 * Action class responsible for organizer event management operations.
 * Handles user interaction and validation logic only.
 * Delegates business logic to OrganizerService and EventService.
 */

public class OrganizerEventManagementAction {
	private final OrganizerService organizerService;
    private final EventService eventService;
    private final Scanner scanner;

    public OrganizerEventManagementAction(Scanner scanner) {
        this.organizerService = ApplicationUtil.organizerService();
        this.eventService = ApplicationUtil.eventService();
        this.scanner = scanner;
    }
    
    /* ===================== Event creation ===================== */
    /**
     * Handles full event creation workflow for an organizer.
     * Includes category selection, venue selection, date validation,
     * availability checks, and capacity validation.
     *
     * @param userId the organizer ID
     * @throws DataAccessException 
     */
	public void createEvent(int userId) {
		
		try {
            System.out.println("\n===== Create Event =====");

            // Title
            String title;
            while (true) {
                System.out.print("Enter Title (15-150 chars): ");
                title = scanner.nextLine();
                if (title.length() >= 15 && title.length() <= 150) break;
                System.out.println("Invalid title length");
            }

            // Description
            String description;
            while (true) {
                System.out.print("Enter Description (≥150 chars): ");
                description = scanner.nextLine();
                if (description.length() >= 150) break;
                System.out.println("Description must be at least 150 characters");
            }

            // Category
            List<Category> categories = getAllCategories();

            if (categories == null || categories.isEmpty()) {
                System.out.println("No categories available. Cannot create event.");
                return;
            }

            int categoryId = -1;

            while (true) {
                System.out.println("Available Categories:");
                for (Category c : categories) {
                    System.out.println(c.getCategoryId() + " - " + c.getName());
                }

                System.out.print("Enter Category ID (1-10): ");
                String input = scanner.nextLine().trim();

                try {
                    categoryId = Integer.parseInt(input);

                    // Check if the entered ID exists in the categories list
                    boolean valid = false;
                    for (Category c : categories) {
                        if (c.getCategoryId() == categoryId) {
                            valid = true;
                            break;
                        }
                    }

                    if (valid) break; // valid input
                    else System.out.println("Enter valid category");

                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a numeric category ID");
                }
            }
            
            
            // Venue
            List<Venue> venues = getAllVenues();

            if (venues == null || venues.isEmpty()) {
                System.out.println("No venues available. Cannot create event.");
                return;
            }

            int venueId = -1;

            while (true) {
                System.out.println("Available Venues:");
                for (Venue v : venues) {
                    System.out.println(v.getVenueId() + " - " + v.getName());
                }

                System.out.print("Enter Venue ID: ");
                String input = scanner.nextLine().trim();

                try {
                    venueId = Integer.parseInt(input);

                    // Check if entered venue ID exists
                    boolean valid = false;
                    for (Venue v : venues) {
                        if (v.getVenueId() == venueId) {
                            valid = true;
                            break;
                        }
                    }

                    if (valid) break; // valid input
                    else System.out.println("Please enter valid number");

                } catch (NumberFormatException ex) {
                    System.out.println("Please enter numeric value only");
                }
            }	

            // Dates
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            while (true) {
                try {
                    System.out.print("Enter Start Date (dd-MM-yyyy HH:mm): ");
                    String startInput = scanner.nextLine().trim();
                    startDateTime = LocalDateTime.parse(startInput, dateTimeFormatter);

                    // Reject past dates
                    if (startDateTime.isBefore(LocalDateTime.now())) {
                        System.out.println("Start date/time cannot be in the past.");
                        continue;
                    }

                    System.out.print("Enter End Date (dd-MM-yyyy HH:mm): ");
                    String endInput = scanner.nextLine().trim();
                    endDateTime = LocalDateTime.parse(endInput, dateTimeFormatter);

                    // End date must be after start
                    if (!endDateTime.isAfter(startDateTime)) {
                        System.out.println("End date must be after start date");
                        continue;
                    }

                    // Check venue availability
                    if (!isVenueAvailable(venueId, startDateTime, endDateTime)) {
                        System.out.println("Venue not available for selected time.");
                        continue;
                    }

                    break; // valid dates
                } catch (java.time.format.DateTimeParseException ex) {
                    System.out.println("Invalid date/time format. Use dd-MM-yyyy HH:mm (e.g., 20-05-2026 12:50)");
                }
            }

            // Capacity
            int capacity;
            int maxCapacity = getVenueById(venueId).getMaxCapacity();
            while (true) {
                System.out.print("Enter Capacity (≤ " + maxCapacity + "): ");
                try {
                    capacity = Integer.parseInt(scanner.nextLine());
                    if (capacity > 0 && capacity <= maxCapacity) break;
                } catch (Exception ignored) {}
                System.out.println("Exceeds venue capacity");
            }

            // Create Event Object
            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);
            event.setCategoryId(categoryId);
            event.setVenueId(venueId);
            event.setStartDateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
            event.setEndDateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
            event.setCapacity(capacity);
            event.setOrganizerId(userId);

            int eventId = organizerService.createEvent(event);
            System.out.println(title + " event has been submitted for approval. ID: " + eventId);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error creating event");
        }
		
	}
	
	 /* ===================== Event details ===================== */
    /**
     * Displays detailed view of organizer owned events.
     *
     * @param userId organizer ID
     */
	public void viewMyEventDetails(int userId) {
		try {
            List<Event> events = getOrganizerEvents(userId);
            if (events == null || events.isEmpty()) {
                System.out.println("No events found.");
                return;
            }

            System.out.println("\nAVAILABLE EVENTS");
            System.out.println("===================================================================================================================================");
            System.out.printf("%-5s %-5s %-30s %-20s %-17s %-17s %-10s %-10s %-10s%n",
                    "NO", "ID", "TITLE", "CATEGORY", "START DATE", "END DATE", "CAPACITY", "TICKETS", "STATUS");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

            int i = 1;
            for (Event e : events) {
                String title = e.getTitle().length() > 30 ? e.getTitle().substring(0, 27) + "..." : e.getTitle();
                int category = e.getCategoryId();
                ZonedDateTime startZdt = e.getStartDateTime().atZone(ZoneId.systemDefault());
                String startDate = startZdt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

                ZonedDateTime endZdt = e.getEndDateTime().atZone(ZoneId.systemDefault());
                String endDate = endZdt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                int tickets = organizerService.viewEventRegistrations(e.getEventId());	
                System.out.printf("%-5d %-5s %-30s %-20s %-17s %-17s %-10s %-10s %-10s%n",
                        i,
                        String.valueOf(e.getEventId()),
                        title,
                        category,
                        startDate,
                        endDate,
                        String.valueOf(e.getCapacity()),
                        String.valueOf(tickets),
                        e.getStatus());
                i++;
            }

            System.out.println("===================================================================================================================================");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error fetching events");
        }
		
		
	}
	
	/* ===================== EVENT DETAILS UPDATION ===================== */
    /**
     * Updates editable event details.
     * Venue modification is intentionally restricted.
     *
     * @param userId organizer ID
     */
	public void updateEventDetails(int userId) {
		
		try {
	        viewMyEventDetails(userId);

	        System.out.print("Enter Event ID to update: ");
	        int eventId = Integer.parseInt(scanner.nextLine());

	        Event event = getOrganizerEventById(userId, eventId);
	        if (event == null) {
	            System.out.println("Invalid Event");
	            return;
	        }

	        System.out.print("Enter new Title: ");
	        String title = scanner.nextLine();

	        System.out.print("Enter new Description: ");
	        String description = scanner.nextLine();

	        System.out.print("Enter new Category ID: ");
	        int categoryId = Integer.parseInt(scanner.nextLine());

	        boolean updated = updateEventDetails(
	                eventId,
	                title,
	                description,
	                categoryId,
	                event.getVenueId()
	        );

	        if (!updated) {
	            System.out.println("Update failed");
	            return;
	        }

	        System.out.print("Do you want to update schedule? (Y/N): ");
	        char ch = scanner.nextLine().toUpperCase().charAt(0);

	        if (ch == 'Y') {

	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	            System.out.print("Enter new Start (dd-MM-yyyy HH:mm): ");
	            LocalDateTime start = LocalDateTime.parse(scanner.nextLine(), formatter);

	            System.out.print("Enter new End (dd-MM-yyyy HH:mm): ");
	            LocalDateTime end = LocalDateTime.parse(scanner.nextLine(), formatter);

	            // validation
	            if (end.isBefore(start)) {
	                System.out.println("End time cannot be before start time");
	                return;
	            }

	            System.out.println("Start: " + start);
	            System.out.println("End: " + end);

	            boolean scheduleUpdated =
	                    organizerService.updateEventSchedule(eventId, start, end);

	            if (!scheduleUpdated) {
	                System.out.println("Schedule update failed");
	                return;
	            }
	        }

	        System.out.println("Updated successfully");

	    } catch (Exception e) {
	        e.printStackTrace();  // VERY IMPORTANT for debugging
	    }
		
		
	}
	
	 /* ===================== UPDATE EVENT CAPACITY ===================== */
    /**
     * Allows organizer to update capacity for upcoming events.
     * Capacity cannot be less than already booked tickets.
     *
     * @param userId the organizer ID
     */
	public void updateEventCapacity(int userId) {
		try {
	        viewMyEventDetails(userId);

	        System.out.print("Enter Event ID: ");
	        int eventId = Integer.parseInt(scanner.nextLine());

	        System.out.print("Enter new Capacity: ");
	        int capacity = Integer.parseInt(scanner.nextLine());
	        
	        int booked = organizerService.viewEventRegistrations(eventId);

	        if (capacity < booked) {
	            System.out.println("Capacity cannot be less than booked tickets: " + booked);
	            return;
	        }

	        boolean updated = updateEventCapacity(eventId, capacity);

	        System.out.println(updated ? "Capacity updated" : "Failed");

	    } catch (Exception e) {
	    	e.printStackTrace();
	        System.out.println("Error updating capacity");
	    }
		
		
		
	}
	
	 /* ===================== PUBLISH EVENT ===================== */

    /**
     * Publishes an approved event.
     * Allows optional final edits before publishing.
     * Requires ticket types to be created before publishing.
     *
     * @param userId the organizer ID
     */

	public void publishEvent(int userId) {
		try {
	        viewMyEventDetails(userId);

	        System.out.print("Enter Event ID to publish: ");
	        int eventId = Integer.parseInt(scanner.nextLine());

	        boolean result = organizerService.publishEvent(eventId);

	        System.out.println(result ? "Event published" : "Publish failed");

	    } catch (Exception e) {
	        System.out.println("Error publishing event");
	    }

		
	}
	
	 /* ===================== EVENT CANCELLATION ===================== */
    /**
     * Cancels an event created by the organizer.
     * Published events require admin approval.
     */
	public void cancelEvent(int userId) {
		 try {
		        viewMyEventDetails(userId);

		        System.out.print("Enter Event ID to cancel: ");
		        int eventId = Integer.parseInt(scanner.nextLine());

		        boolean result = organizerService.cancelEvent(eventId);

		        System.out.println(result ? "Event cancelled" : "Cancellation failed");

		    } catch (Exception e) {
		        System.out.println("Error cancelling event");
		    }

		
	}
	
	 /* ===================== DATA RETRIVAL METHODS ===================== */

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId the ID of the organizer
     * @return list of events created by the organizer
     * @throws DataAccessException 
     */

	public List<Event> getOrganizerEvents(int organizerId) throws DataAccessException {
		 return organizerService.getOrganizerEvents(organizerId);
	}
	
	 /**
     * Retrieves a specific event owned by the organizer.
     *
     * @param organizerId the ID of the organizer
     * @param eventId     the ID of the event
     * @return event if found and authorized, otherwise null
     */
    public Event getOrganizerEventById(int organizerId, int eventId) throws DataAccessException {
        return organizerService.getOrganizerEventById(organizerId, eventId);
    }

    /**
     * Retrieves all available event categories.
     *
     * @return list of categories
     */
    
    
    public List<Category> getAllCategories() throws DataAccessException {
        return eventService.getAllCategories();
    }

    /**
     * Retrieves all available venues.
     *
     * @return list of venues
     */
    public List<Venue> getAllVenues() throws DataAccessException {
        return eventService.getActiveVenues();
    }

    /**
     * Retrieves formatted venue address.
     *
     * @param venueId the ID of the venue
     * @return formatted address string
     */
    public String getVenueAddress(int venueId) throws DataAccessException {
        return eventService.getVenueAddress(venueId);
    }

    /**
     * Checks venue availability for a given time range.
     *
     * @param venueId   the venue ID
     * @param startTime event start date and time
     * @param endTime   event end date and time
     * @return true if venue is available
     */
    public boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime)
            throws DataAccessException {
        return eventService.isVenueAvailable(venueId, startTime, endTime);
    }

    /**
     * Retrieves venue details by ID.
     *
     * @param venueId the ID of the venue
     * @return venue object
     */
    public Venue getVenueById(int venueId) throws DataAccessException {
        return eventService.getVenueById(venueId);
    }

    /**
     * Updates editable event details.
     * Venue change is intentionally restricted.
     *
     * @param eventId     the event ID
     * @param title       updated title
     * @param description updated description
     * @param categoryId  updated category ID
     * @param venueId     existing venue ID
     * @return true if update succeeds
     */
    public boolean updateEventDetails(int eventId, String title, String description,
            int categoryId, int venueId) throws DataAccessException {
        return organizerService.updateEventDetails(
                eventId,
                title,
                description,
                categoryId,
                venueId);
    }

    /**
     * Updates event capacity.
     *
     * @param eventId  the event ID
     * @param capacity new capacity
     * @return true if update succeeds
     */
    public boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException {
        return organizerService.updateEventCapacity(eventId, capacity);
    }


}
