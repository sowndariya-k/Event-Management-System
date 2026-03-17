package com.ems.service;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;

public interface EventService {

    /**
     * View all available events
     */
    List<Event> getAvailableEvents() throws DataAccessException;

    /**
     * View details of a specific event
     */
    Event getEventDetails(int eventId) throws DataAccessException;

    /**
     * View ticket options for a specific event
     */
    List<Ticket> getTicketOptions(int eventId) throws DataAccessException;

    /**
     * View upcoming events registered by user
     */
    List<Event> getUpcomingEvents(int userId) throws DataAccessException;

    /**
     * View past events attended by user
     */
    List<Event> getPastEvents(int userId) throws DataAccessException;

    /**
     * View booking history
     */
    List<BookingDetail> getBookingHistory(int userId) throws DataAccessException;
}