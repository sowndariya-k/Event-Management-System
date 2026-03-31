package com.ems.actions;

import java.time.LocalDateTime;
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
		
		
		
	}
	
	 /* ===================== Event details ===================== */
    /**
     * Displays detailed view of organizer owned events.
     *
     * @param userId organizer ID
     */
	public void viewMyEventDetails(int userId) {
		
		
		
	}
	
	/* ===================== EVENT DETAILS UPDATION ===================== */
    /**
     * Updates editable event details.
     * Venue modification is intentionally restricted.
     *
     * @param userId organizer ID
     */
	public void updateEventDetails(int userId) {
		
		
		
	}
	
	 /* ===================== UPDATE EVENT CAPACITY ===================== */
    /**
     * Allows organizer to update capacity for upcoming events.
     * Capacity cannot be less than already booked tickets.
     *
     * @param userId the organizer ID
     */
	public void updateEventCapacity(int userId) {
		
		
		
		
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


		
	}
	
	 /* ===================== EVENT CANCELLATION ===================== */
    /**
     * Cancels an event created by the organizer.
     * Published events require admin approval.
     */
	public void cancelEvent(int userId) {


		
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
