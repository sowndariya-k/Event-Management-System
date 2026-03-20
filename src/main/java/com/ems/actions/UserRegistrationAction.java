package com.ems.actions;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;

public class UserRegistrationAction {

    private final EventService eventService;
    

    // Constructor injection: require EventService
    public UserRegistrationAction(EventService eventService) {
        this.eventService = eventService;
    }

	
	 /**
     * Displays a list of upcoming events registered by the user.
     *
     * @param userId the ID of the user
     */
	public void listUpcomingEvents(int userId) {
		
		
	}
	
	 /**
     * Displays a list of past events registered by the user.
     *
     * @param userId the ID of the user
     */
	public void listPastEvents(int userId) {
		
		
	}
	
	/**
     * Displays booking details for all events registered by the user.
     *
     * @param userId the ID of the user
     */
	
	public void viewBookingDetails(int userId) {
		
		
	}
	
	/* ===================== DATA RETRIEVAL METHODS ===================== */

    /**
     * Retrieves booking details for a user.
     *
     * @param userId the ID of the user
     * @return list of booking details
     */
    public List<BookingDetail> getBookingDetails(int userId) throws DataAccessException {
        return eventService.viewBookingDetails(userId);
    }

    /**
     * Retrieves upcoming events registered by the user.
     *
     * @param userId the ID of the user
     * @return list of upcoming user event registrations
     */
    public List<UserEventRegistration> getUpcomingEvents(int userId) throws DataAccessException {
        return eventService.viewUpcomingEvents(userId);
    }

    /**
     * Retrieves past events registered by the user.
     *
     * @param userId the ID of the user
     * @return list of past user event registrations
     */
    public List<UserEventRegistration> getPastEvents(int userId) throws DataAccessException {
        return eventService.viewPastEvents(userId);
    }
}


