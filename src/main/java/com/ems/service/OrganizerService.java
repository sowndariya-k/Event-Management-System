package com.ems.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;

public interface OrganizerService {

    // event creation & management
    int createEvent(Event event) throws DataAccessException;

    boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId)
            throws DataAccessException;

    boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end) throws DataAccessException;

    boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException;

    boolean publishEvent(int eventId) throws DataAccessException;

    boolean cancelEvent(int eventId) throws DataAccessException;

    // ticket management
    boolean createTicket(Ticket ticket) throws DataAccessException;

    boolean updateTicketPrice(int ticketId, double price) throws DataAccessException;

    boolean updateTicketQuantity(int ticketId, int quantity) throws DataAccessException;

    List<Ticket> viewTicketAvailability(int eventId) throws DataAccessException;

    // registrations & reports
    int viewEventRegistrations(int eventId) throws DataAccessException;

    List<EventRevenueReport> getRevenueReport(int organizerId) throws DataAccessException;

    List<OrganizerEventSummary> getOrganizerEventSummary(int organizerId) throws DataAccessException;

    // notifications
    void sendEventUpdate(int eventId, String message) throws DataAccessException;

    void sendScheduleChange(int eventId, String message) throws DataAccessException;

    // organizer data
    List<Event> getOrganizerEvents(int organizerId) throws DataAccessException;

    Event getOrganizerEventById(int organizerId, int eventId) throws DataAccessException;

    void sendCancellationRequest(Event selectedEvent, String message) throws DataAccessException;

    List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException;

}