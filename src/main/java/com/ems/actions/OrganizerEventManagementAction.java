package com.ems.actions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

	        System.out.print("Enter Title: ");
	        String title = scanner.nextLine();

	        System.out.print("Enter Description: ");
	        String description = scanner.nextLine();

	        // Category selection
	        List<Category> categories = getAllCategories();
	        for (Category c : categories) {
	            System.out.println(c.getCategoryId() + " - " + c.getName());
	        }
	        System.out.print("Enter Category ID: ");
	        int categoryId = Integer.parseInt(scanner.nextLine());

	        // Venue selection
	        List<Venue> venues = getAllVenues();
	        for (Venue v : venues) {
	            System.out.println(v.getVenueId() + " - " + v.getName());
	        }
	        System.out.print("Enter Venue ID: ");
	        int venueId = Integer.parseInt(scanner.nextLine());

	        System.out.print("Enter Start Date (yyyy-MM-dd): ");
	        LocalDate startDate = LocalDate.parse(scanner.nextLine());

	        System.out.print("Enter End Date (yyyy-MM-dd): ");
	        LocalDate endDate = LocalDate.parse(scanner.nextLine());
	        
	        LocalDateTime start = startDate.atStartOfDay();
	        LocalDateTime end = endDate.atTime(23, 59); 

	        if (!isVenueAvailable(venueId, start, end)) {
	            System.out.println("Venue not available for selected time.");
	            return;
	        }

	        System.out.print("Enter Capacity: ");
	        int capacity = Integer.parseInt(scanner.nextLine());

	        Event event = new Event();
	        event.setTitle(title);
	        event.setDescription(description);
	        event.setCategoryId(categoryId);
	        event.setVenueId(venueId);
	        event.setStartDateTime(start.atZone(ZoneId.systemDefault()).toInstant());
	        event.setEndDateTime(end.atZone(ZoneId.systemDefault()).toInstant());
	        event.setCapacity(capacity);
	        event.setOrganizerId(userId);

	        int eventId = organizerService.createEvent(event);

	        System.out.println("Event created successfully. ID: " + eventId);

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
	        
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        
	        
	        for (Event e : events) {
	        	
	        	String startDate = e.getStartDateTime()
		                .atZone(ZoneId.systemDefault())
		                .toLocalDate()  
		                .format(formatter);
	        	
	        	String endDate = e.getEndDateTime()
	                    .atZone(ZoneId.systemDefault())
	                    .toLocalDate()
	                    .format(formatter);
	        	
	            System.out.println("ID: " + e.getEventId());
	            System.out.println("Title: " + e.getTitle());
	            System.out.println("Start: " + startDate);
	            System.out.println("End Date  : " + endDate);
	            System.out.println("Status: " + e.getStatus());
	            System.out.println("------------------------");
	        }

	    } catch (Exception e) {
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

		        
		        System.out.print("Do you want to update schedule? (Y/N): ");
		        char ch = scanner.nextLine().toUpperCase().charAt(0);

		        if (ch == 'Y') {
		            System.out.print("Enter new Start (yyyy-MM-ddTHH:mm): ");
		            LocalDateTime start = LocalDateTime.parse(scanner.nextLine());

		            System.out.print("Enter new End (yyyy-MM-ddTHH:mm): ");
		            LocalDateTime end = LocalDateTime.parse(scanner.nextLine());

		            boolean scheduleUpdated =
		                    organizerService.updateEventSchedule(eventId, start, end);

		            if (!scheduleUpdated) {
		                System.out.println("Schedule update failed");
		            }
		        }

		        System.out.println(updated ? "Updated successfully" : "Update failed");

		    } catch (Exception e) {
		        System.out.println("Error updating event");
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
