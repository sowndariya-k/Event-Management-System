package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;
import com.ems.util.MenuHelper;

public class UserRegistrationAction {

    private final EventService eventService;
    private final Scanner scanner;

    // Constructor injection: require EventService
    public UserRegistrationAction(EventService eventService, Scanner scanner) {
        this.eventService = eventService;
        this.scanner = scanner;
    }


	 /**
     * Displays a list of upcoming events registered by the user.
     *
     * @param userId the ID of the user
     */
    public void listUpcomingEvents(int userId) {
        try {
            List<UserEventRegistration> upcoming = eventService.viewUpcomingEvents(userId);
            if (upcoming == null || upcoming.isEmpty()) {
                System.out.println("No upcoming registered events found.");
                return;
            }
            MenuHelper.printEventsList(upcoming);
            
        } catch (DataAccessException e) {
            System.out.println("Error fetching upcoming events: " + e.getMessage());
        }
    }
	
	 /**
     * Displays a list of past events registered by the user.
     *
     * @param userId the ID of the user
     */
    public void listPastEvents(int userId) {
        try {
            List<UserEventRegistration> past = eventService.viewPastEvents(userId);
            if (past == null || past.isEmpty()) {
                System.out.println("No past registered events found.");
                return;
            }
            MenuHelper.printEventsList(past);
        } catch (DataAccessException e) {
            System.out.println("Error fetching past events: " + e.getMessage());
        }
    }
	
	/**
     * Displays booking details for all events registered by the user.
     *
     * @param userId the ID of the user
     */
	
	public void viewBookingDetails(int userId) {
	    try {
	        List<BookingDetail> bookings = eventService.viewBookingDetails(userId);
	        if (bookings == null || bookings.isEmpty()) {
	            System.out.println("No booking details found.");
	            return;
	        } 
	        MenuHelper.printBookingDetails(bookings);
	    } catch (DataAccessException e) {
	        System.out.println("Error fetching booking details: " + e.getMessage());
	    }
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