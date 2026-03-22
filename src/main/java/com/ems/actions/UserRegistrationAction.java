package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;

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
            System.out.println("\nUpcoming Registered Events:");
            System.out.println("==============================================");
            for (UserEventRegistration reg : upcoming) {
                System.out.println("Registration ID   : " + reg.getRegistrationId());
                System.out.println("Event             : " + reg.getTitle());
                System.out.println("Category          : " + reg.getCategory());
                System.out.println("Start Date        : " + reg.getStartDateTime());
                System.out.println("End Date          : " + reg.getEndDateTime());
                System.out.println("Ticket Type       : " + reg.getTicketType());
                System.out.println("Tickets Purchased : " + reg.getTicketsPurchased());
                System.out.println("Amount Paid       : ₹" + reg.getAmountPaid());
                System.out.println("Status            : " + reg.getRegistrationStatus());
                System.out.println("----------------------------------------------");
            }
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
            System.out.println("\nPast Registered Events:");
            System.out.println("==============================================");
            for (UserEventRegistration reg : past) {
                System.out.println("Registration ID   : " + reg.getRegistrationId());
                System.out.println("Event             : " + reg.getTitle());
                System.out.println("Category          : " + reg.getCategory());
                System.out.println("Start Date        : " + reg.getStartDateTime());
                System.out.println("End Date          : " + reg.getEndDateTime());
                System.out.println("Ticket Type       : " + reg.getTicketType());
                System.out.println("Tickets Purchased : " + reg.getTicketsPurchased());
                System.out.println("Amount Paid       : ₹" + reg.getAmountPaid());
                System.out.println("Status            : " + reg.getRegistrationStatus());
                System.out.println("----------------------------------------------");
            }
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
	        System.out.println("\nBooking Details:");
	        System.out.println("==============================================");
	        for (BookingDetail b : bookings) {
	            System.out.println("Event Name   : " + b.getEventName());
	            System.out.println("Start Date   : " + b.getStartDateTime());
	            System.out.println("Venue        : " + b.getVenueName());
	            System.out.println("City         : " + b.getCity());
	            System.out.println("Ticket Type  : " + b.getTicketType());
	            System.out.println("Quantity     : " + b.getQuantity());
	            System.out.println("Total Cost   : ₹" + b.getTotalCost());
	            System.out.println("----------------------------------------------");
	        }
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


