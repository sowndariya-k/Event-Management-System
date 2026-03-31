package com.ems.actions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;

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
            String title = InputValidationUtil.readNonEmptyString(scanner,
                    "Enter event title (15 to 150 characters):\n");

            while (title.length() > 150 || title.length() < 15) {
                title = InputValidationUtil.readNonEmptyString(scanner,
                        "Title must be between 15 and 150 characters. Try again:\n");
            }
            String description = InputValidationUtil.readNonEmptyString(scanner,
                    "\nEnter event description (minimum 150 characters):\n");
            while (description.length() < 150 || description.length() > 16383) {
                description = InputValidationUtil.readNonEmptyString(scanner,
                        "Description must be between 150 and 16383 characters. Try again:\n");
            }
            System.out.println();
            List<Category> categories = getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("There are no available category!");
                return;
            }
            int defaultIndex = 1;
            for (Category category : categories) {
                System.out.println(defaultIndex + ". " + category.getName());
                defaultIndex++;
            }

            int choice = InputValidationUtil.readInt(scanner,
                    "Select category number: ");

            while (choice < 1 || choice > categories.size()) {
                choice = InputValidationUtil.readInt(scanner,
                        "Invalid choice. Please select a number from the list:\n");
            }

            Category selectedCategory = categories.get(choice - 1);
            int categoryId = selectedCategory.getCategoryId();
            System.out.println();
            List<Venue> venues = getAllVenues();
            if (venues.isEmpty()) {
                System.out.println("No venues available at the moment.");
                return;
            }
            defaultIndex = 1;
            for (Venue venue : venues) {
                System.out.println(
                        defaultIndex + ". " + venue.getName() + ", \n"
                                + getVenueAddress(venue.getVenueId()) + "\n");
                defaultIndex++;
            }

            int venueChoice = InputValidationUtil.readInt(scanner,
                    "Select venue number: ");

            while (venueChoice < 1 || venueChoice > venues.size()) {
                venueChoice = InputValidationUtil.readInt(scanner,
                        "Please enter a valid number from the list.: ");
            }

            Venue selectedVenue = venues.get(venueChoice - 1);
            int venueId = selectedVenue.getVenueId();

            LocalDateTime startTime = null;

            while (startTime == null) {
                String input = InputValidationUtil.readString(scanner,
                        "Enter event start date and time (dd-MM-yyyy HH:mm):\n");

                startTime = DateTimeUtil.parseLocalDateTime(input);

                if (startTime == null) {
                    System.out.println("Invalid start date time. Please try again.");
                    continue;
                }

                LocalDateTime now = LocalDateTime.now();

                if (!startTime.isAfter(now)) {
                    System.out.println("Start time must be in the future.");
                    startTime = null;
                }
            }

            LocalDateTime endTime = null;
            while (endTime == null) {
                String input = InputValidationUtil.readString(scanner,
                        "Enter the event end date and time (dd-MM-yyyy HH:mm): \n");
                endTime = DateTimeUtil.parseLocalDateTime(input);
                if (endTime == null || endTime.isBefore(startTime)) {
                    System.out.println("End date and time must be after the start time. Try again:\n");
                    endTime = null;
                }
            }
            while (true) {

                if (isVenueAvailable(venueId, startTime, endTime)) {
                    break;
                }

                System.out.println(
                        "Selected venue is not available for this time.\n\n"
                                + "1. Choose a different venue\n"
                                + "2. Change event date or time\n"
                                + "3. Cancel event creation\n\n>");

                int c = InputValidationUtil.readInt(scanner, "");

                switch (c) {
                    case 1:
                        venues = getAllVenues();
                        defaultIndex = 1;
                        for (Venue venue : venues) {
                            System.out.println(
                                    defaultIndex + ". " + venue.getName()
                                            + getVenueAddress(venue.getVenueId()));
                            defaultIndex++;
                        }
                        venueChoice = InputValidationUtil.readInt(scanner, "Select venue number: ");

                        while (venueChoice < 1 || venueChoice > venues.size()) {
                            venueChoice = InputValidationUtil.readInt(scanner,
                                    "Please enter a valid number from the list: ");
                        }
                        selectedVenue = venues.get(venueChoice - 1);
                        venueId = selectedVenue.getVenueId();
                        break;

                    case 2:
                        startTime = null;
                        while (startTime == null) {
                            String input = InputValidationUtil.readString(scanner,
                                    "Enter event start date and time (dd-MM-yyyy HH:mm):\n");
                            startTime = DateTimeUtil.parseLocalDateTime(input);
                            if (startTime == null) {
                                System.out.println("Invalid start date time. Please try again.");
                            }
                        }

                        endTime = null;
                        while (endTime == null) {
                            String input = InputValidationUtil.readString(scanner,
                                    "Enter event end date and time (dd-MM-yyyy HH:mm):\n");
                            endTime = DateTimeUtil.parseLocalDateTime(input);
                            if (endTime == null || endTime.isBefore(startTime)) {
                                System.out.println("End date must be after start time. Try again.");
                                endTime = null;
                            }
                        }
                        break;

                    case 3:
                        return;

                    default:
                        System.out.println("Enter the valid option!");
                }
            }

            int eventCapacity = InputValidationUtil.readInt(scanner,
                    "Maximum capacity of the selected venue: "
                            + selectedVenue.getMaxCapacity()
                            + "\nEnter the event capacity:");

            while (eventCapacity <= 0 || eventCapacity > selectedVenue.getMaxCapacity()) {
                eventCapacity = InputValidationUtil.readInt(scanner,
                        "Enter the valid event capacity:");
            }

            Event event = new Event();
            event.setOrganizerId(userId);
            event.setTitle(title);
            event.setDescription(description);
            event.setVenueId(venueId);
            event.setStartDateTime(DateTimeUtil.toUtcInstant(startTime));
            event.setEndDateTime(DateTimeUtil.toUtcInstant(endTime));
            event.setCapacity(eventCapacity);
            event.setCategoryId(categoryId);

            organizerService.createEvent(event);
            System.out.println(event.getTitle() + " event has been submitted for approval.");
        } catch (DataAccessException e) {
            System.out.println("Error creating event: " + e.getMessage());
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

            if (events.isEmpty()) {
                System.out.println("You have not created any events yet.");
                return;
            }
            
            AdminMenuHelper.printAllEventsWithStatus(events);

            int choice = MenuHelper.selectFromList(scanner, events.size(), "Select an event");

            Event selectedEvent = events.get(choice - 1);

            Event event = getOrganizerEventById(
                    userId,
                    selectedEvent.getEventId());

            if (event == null) {
                System.out.println("You are not authorized to view this event");
                return;
            }

            MenuHelper.printEventDetails(event);
        } catch (DataAccessException e) {
            System.out.println("Error viewing event details: " + e.getMessage());
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
            List<Event> events = getOrganizerEvents(userId);
            if (events.isEmpty()) {
                System.out.println("The organizer hasn't hosted any events");
                return;
            }
            Instant now = DateTimeUtil.nowUtc();
            List<Event> sortedEvents = events.stream()
                    .filter(e -> EventStatus.DRAFT.equals(e.getStatus())
                            && e.getStartDateTime().isAfter(now))
                    .sorted(Comparator.comparing(Event::getStartDateTime))
                    .toList();

            if (sortedEvents.isEmpty()) {
                System.out.println("No upcoming events available for editing.");
                return;
            }
            
            AdminMenuHelper.printAllEventsWithStatus(sortedEvents);

            int choice = MenuHelper.selectFromList(scanner, sortedEvents.size(), "Select an event");

            Event selectedEvent = sortedEvents.get(choice - 1);

            System.out.println("Press Enter to keep the current value.");

            String title = InputValidationUtil.readString(scanner,
                    "Title (" + selectedEvent.getTitle() + "): ");
            if (title.isBlank()) {
                title = selectedEvent.getTitle();
            }

            String description = InputValidationUtil.readString(scanner,
                    "Description (" + selectedEvent.getDescription() + "): ");
            if (description.isBlank()) {
                description = selectedEvent.getDescription();
            }

            List<Category> categories = getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("No categories available");
                return;
            }

            // Find index of current category in the list
            int currentCategoryIndex = 0;
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getCategoryId() == selectedEvent.getCategoryId()) {
                    currentCategoryIndex = i + 1;
                    break;
                }
            }

            System.out.println("Categories (enter 0 to keep current category)");
            for (int i = 0; i < categories.size(); i++) {
                String mark = (categories.get(i).getCategoryId() == selectedEvent.getCategoryId()) ? " (current)" : "";
                System.out.println((i + 1) + ". " + categories.get(i).getName() + mark);
            }

            int categoryChoice;
            while (true) {
                String line = InputValidationUtil.readString(scanner,
                        "Category (" + currentCategoryIndex + "): ").trim();

                if (line.isBlank()) {
                    categoryChoice = 0; // keep current
                    break;
                }

                try {
                    categoryChoice = Integer.parseInt(line);
                    if (categoryChoice >= 0 && categoryChoice <= categories.size()) break;
                    System.out.println("Please enter a valid number from the list.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                }
            }

            int categoryId = (categoryChoice == 0) ? selectedEvent.getCategoryId()
                    : categories.get(categoryChoice - 1).getCategoryId();

            boolean result = updateEventDetails(
                    selectedEvent.getEventId(),
                    title,
                    description,
                    categoryId,
                    selectedEvent.getVenueId());

            System.out.println(
                    result
                            ? "Event details updated successfully.\n"
                            : "Failed to update event details.\n");
        }catch (DataAccessException e) { 
        	System.out.println("Error updating event details: " + e.getMessage()); 
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
            List<Event> events = getOrganizerEvents(userId);
            if (events.isEmpty()) {
                System.out.println("You have not created any events yet.");
                return;
            }
            Instant now = DateTimeUtil.nowUtc();
            List<Event> sortedEvents = events.stream()
                    .filter(e -> EventStatus.DRAFT.equals(e.getStatus())
                            && e.getStartDateTime().isAfter(now))
                    .sorted(Comparator.comparing(Event::getStartDateTime))
                    .collect(Collectors.toList());

            if (sortedEvents.isEmpty()) {
                System.out.println("No upcoming events available for updating capacity.");
                return;
            }

            AdminMenuHelper.printAllEventsWithStatus(sortedEvents);
            
            int choice = MenuHelper.selectFromList(scanner, sortedEvents.size(), "Select an event");

            Event selectedEvent = sortedEvents.get(choice - 1);
            int eventId = selectedEvent.getEventId();

            int booked = organizerService.viewEventRegistrations(eventId);

            System.out.println(
                    "Current capacity: " + selectedEvent.getCapacity()
                            + " | Tickets already booked: " + booked);

            Venue venue = getVenueById(selectedEvent.getVenueId());
            System.out.println("The maximum capacity of venue is: " + venue.getMaxCapacity());

            int capacity = InputValidationUtil.readInt(scanner,
                    "Enter the new capacity: ");

            while (capacity < booked || capacity > venue.getMaxCapacity()) {
                capacity = InputValidationUtil.readInt(scanner,
                        "Enter new capacity (between "
                                + booked + " and " + venue.getMaxCapacity() + "): ");
            }

            boolean result = updateEventCapacity(eventId, capacity);
            System.out.println(result ? "Capacity updated" : "Update failed");
        } catch (DataAccessException e) {
            System.out.println("Error updating event capacity: " + e.getMessage());
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
	            List<Event> events = getOrganizerEvents(userId);

	            if (events.isEmpty()) {
	                System.out.println("No events found.");
	                return;
	            }

	            Instant now = DateTimeUtil.nowUtc();
	            List<Event> eligibleEvents = events.stream()
	                    .filter(e -> EventStatus.APPROVED.equals(e.getStatus())
	                            && e.getStartDateTime().isAfter(now)
	                            && e.getApprovedAt() != null)
	                    .sorted(Comparator.comparing(Event::getStartDateTime))
	                    .collect(Collectors.toList());

	            if (eligibleEvents.isEmpty()) {
	                System.out.println("No approved upcoming events available for publishing.");
	                return;
	            }
	            
	            AdminMenuHelper.printAllEventsWithStatus(eligibleEvents);

	            int choice = MenuHelper.selectFromList(scanner, eligibleEvents.size(), "Select an event");

	            Event selectedEvent = eligibleEvents.get(choice - 1);

	            int eventId = selectedEvent.getEventId();
	            int capacity = selectedEvent.getCapacity();
	            int remainingCapacity = capacity;

	            List<Ticket> draftTickets = new ArrayList<>();

	            System.out.println("\nYou must allocate tickets equal to total capacity.");

	            while (remainingCapacity > 0) {

	                System.out.println("\n1. Add ticket type");
	                System.out.println("2. Cancel and discard all entered tickets");

	                int option = InputValidationUtil.readInt(scanner, "> ");

	                if (option == 2) {

	                    if (!draftTickets.isEmpty()) {
	                        System.out.println("\nWarning: All entered ticket data will be lost.");
	                        System.out.println("1. Confirm Cancel");
	                        System.out.println("2. Continue Editing");

	                        int confirmCancel = InputValidationUtil.readInt(scanner, "> ");

	                        if (confirmCancel == 1) {
	                            System.out.println("Publishing cancelled. No tickets were saved.");
	                            return;
	                        } else {
	                            continue;
	                        }
	                    }

	                    return;
	                }

	                if (option != 1) {
	                    System.out.println("Invalid option.");
	                    continue;
	                }

	                Ticket ticket = new Ticket();
	                ticket.setEventId(eventId);

	                String ticketType;

	                while (true) {
	                    ticketType = InputValidationUtil.readNonEmptyString(scanner, "Enter the ticket Type: ").trim();

	                    if (ticketType.length() < 3 || ticketType.length() > 30) {
	                        System.out.println("Ticket Type must be 3-30 characters.");
	                        continue;
	                    }

	                    final String normalizedType = ticketType;

	                    boolean exists = draftTickets.stream()
	                            .anyMatch(t -> t.getTicketType() != null &&
	                                    normalizedType.equalsIgnoreCase(t.getTicketType().trim()));

	                    if (exists) {
	                        System.out.println("Ticket type already exists. Please enter a different type.");
	                        continue;
	                    }

	                    break;
	                }

	                ticketType = ticketType.trim();
	                ticket.setTicketType(ticketType);

	                double price;
	                do {
	                    price = InputValidationUtil.readDouble(scanner, "Enter the ticket Price (₹): ");
	                } while (price <= 0);

	                ticket.setPrice(price);

	                int qty = InputValidationUtil.readInt(scanner,
	                        "Enter the ticket Quantity (max " + remainingCapacity + "): ");

	                while (qty <= 0 || qty > remainingCapacity) {
	                    qty = InputValidationUtil.readInt(scanner,
	                            "Enter valid quantity (1-" + remainingCapacity + "): ");
	                }

	                ticket.setTotalQuantity(qty);
	                ticket.setAvailableQuantity(qty);

	                draftTickets.add(ticket);
	                remainingCapacity -= qty;
	            }
	            MenuHelper.printTicketSummaries(draftTickets);
	            System.out.println("\nAll tickets prepared successfully.");
	            System.out.println("1. Confirm and Publish");
	            System.out.println("2. Cancel and discard");

	            int confirm = InputValidationUtil.readInt(scanner, "> ");

	            if (confirm != 1) {
	                System.out.println("Publishing cancelled. No tickets were saved.");
	                return;
	            }

	            for (Ticket ticket : draftTickets) {
	                organizerService.createTicket(ticket);
	            }

	            boolean published = organizerService.publishEvent(eventId);

	            System.out.println(published ? "Event published successfully" : "Publish failed");
	        } catch (DataAccessException e) {
	            System.out.println("Error publishing event: " + e.getMessage());
	        }
	    }
	
	 /* ===================== EVENT CANCELLATION ===================== */
    /**
     * Cancels an event created by the organizer.
     * Published events require admin approval.
     */
	public void cancelEvent(int userId) {
        try {
            List<Event> events = getOrganizerEvents(userId);

            if (events.isEmpty()) {
                System.out.println("No events found.");
                return;
            }

            Instant now = DateTimeUtil.nowUtc();

            List<Event> actionableEvents = events.stream()
                    .filter(e -> (EventStatus.DRAFT.equals(e.getStatus())
                            || EventStatus.PUBLISHED.equals(e.getStatus())
                            || EventStatus.APPROVED.equals(e.getStatus()))
                            && e.getStartDateTime().isAfter(now))
                    .sorted(Comparator.comparing(Event::getStartDateTime))
                    .collect(Collectors.toList());

            if (actionableEvents.isEmpty()) {
                System.out.println("No events available for cancellation.");
                return;
            }
            
            AdminMenuHelper.printAllEventsWithStatus(actionableEvents);

            int choice = MenuHelper.selectFromList(scanner, actionableEvents.size(), "Select an event");
            Event selectedEvent = actionableEvents.get(choice - 1);

            char confirm = InputValidationUtil.readChar(scanner,
                    "Are you sure you want to cancel this event (Y/N): ");

            if (Character.toUpperCase(confirm) != 'Y') {
                System.out.println("Event cancellation aborted.\n");
                return;
            }
            if (EventStatus.PUBLISHED.equals(selectedEvent.getStatus())
                    || EventStatus.APPROVED.equals(selectedEvent.getStatus())) {

                System.out.println("\nThis event is already approved/published.");
                System.out.println("Cancellation requires admin approval.");
                System.out.println("Note: Sending a request does NOT confirm the event is cancelled.");
                System.out.println("Venues and dates are already finalized, and other events may have been affected.");

                System.out.println("\n1. Send cancellation request");
                System.out.println("2. Back");

                String cancellationChoice = InputValidationUtil.readNonEmptyString(scanner,
                        "Select an option: ");

                if (cancellationChoice.equals("2")) {
                    System.out.println("Returning to previous menu.");
                    return;
                }

                if (!cancellationChoice.equals("1")) {
                    System.out.println("Invalid option.");
                    return;
                }

                String message;
                do {
                    message = InputValidationUtil.readNonEmptyString(scanner,
                            "Enter a detailed reason for cancellation (minimum 10 characters): ");

                    if (message.length() < 10) {
                        System.out.println("Reason must be at least 10 characters long.");
                    }

                } while (message.length() < 10);

                char finalConfirm = InputValidationUtil.readChar(scanner,
                        "Confirm sending cancellation request to admin (Y/N): ");

                if (Character.toUpperCase(finalConfirm) != 'Y') {
                    System.out.println("Cancellation request not sent.");
                    return;
                }

                organizerService.sendCancellationRequest(selectedEvent, message);
                System.out.println("Cancellation request sent successfully.");
                System.out.println("Please wait for admin approval.");

            } else {

                boolean result = organizerService.cancelEvent(selectedEvent.getEventId());

                if (!result) {
                    System.out.println("Event cancellation failed.");
                    return;
                }

                System.out.println("Event cancelled successfully.");
            }
        } catch (DataAccessException e) {
            System.out.println("Error cancelling event: " + e.getMessage());
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
