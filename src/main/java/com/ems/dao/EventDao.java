package com.ems.dao;

/*
 * Author : Sowndariya, Mythily, Jagadeep
 * EventDao is the DAO interface that defines contract
 * methods for event-related database operations including
 * CRUD, status updates, filtering by date, category,
 * and organizer, and fetching event reports.
 */
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.UserEventRegistration;

public interface EventDao {

        /**
         * Lists all published future events that still have available tickets
         *
         * @return list of available events
         * @throws DataAccessException
         */
        List<Event> listAvailableEvents() throws DataAccessException;

        /**
         * Lists all events regardless of status or time
         *
         * @return list of all events
         * @throws DataAccessException
         */
        List<Event> listAllEvents() throws DataAccessException;

        /**
         * Lists future events that are not yet approved
         *
         * @return list of draft events awaiting approval
         * @throws DataAccessException
         */
        List<Event> listEventsYetToApprove() throws DataAccessException;

        /**
         * Approves the given event by an admin user
         *
         * @param eventId
         * @param userId  approving admin
         * @return true if approval succeeded
         * @throws DataAccessException
         */
        boolean approveEvent(int eventId, int userId) throws DataAccessException;

        /**
         * Cancels a future event
         *
         * @param eventId
         * @return true if cancellation succeeded
         * @throws DataAccessException
         */
        boolean cancelEvent(int eventId) throws DataAccessException;

        /**
         * Returns the organizer id for the given event
         *
         * @param eventId
         * @return organizer id
         * @throws DataAccessException
         */
        int getOrganizerId(int eventId) throws DataAccessException;

        /**
         * Lists future events that are either published or in draft state
         *
         * @return list of events
         * @throws DataAccessException
         */
        List<Event> listAvailableAndDraftEvents() throws DataAccessException;

        /**
         * Fetches full event details by id
         *
         * @param eventId
         * @return event details
         * @throws DataAccessException
         */
        Event getEventById(int eventId) throws DataAccessException;

        /**
         * Marks completed events based on end time
         *
         * @throws DataAccessException
         */
        void completeEvents() throws DataAccessException;

        /**
         * Retrieves all event registrations of a user
         *
         * @param userId
         * @return list of user registrations
         * @throws DataAccessException
         */
        List<UserEventRegistration> getUserRegistrations(int userId) throws DataAccessException;

        /**
         * Retrieves booking and payment details for a user
         *
         * @param userId
         * @return list of booking details
         * @throws DataAccessException
         */
        List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException;

        /**
         * Returns organizer names mapped to their event counts
         *
         * @return organizer wise event count
         * @throws DataAccessException
         */
        Map<String, Integer> getOrganizerWiseEventCount() throws DataAccessException;

        // organizer functions

        /**
         * Creates a new event and returns the generated event id
         *
         * @param event
         * @return generated event id
         * @throws DataAccessException
         */
        int createEvent(Event event) throws DataAccessException;

        /**
         * Updates basic event metadata
         * 
         * @param eventId
         * @param title
         * @param description
         * @param categoryId
         * @param venueId
         * @return true if update succeeded
         * @throws DataAccessException
         */
        boolean updateEventDetails(int eventId, String title, String description,
                        int categoryId, int venueId) throws DataAccessException;

        /**
         * Updates total capacity of the event
         * 
         * @param eventId
         * @param capacity
         * @return true if update succeeded
         * @throws DataAccessException
         */
        boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException;

        /**
         * Updates event lifecycle status
         * 
         * @param eventId
         * @param status
         * @return true if update succeeded
         * @throws DataAccessException
         */
        boolean updateEventStatus(int eventId, EventStatus status) throws DataAccessException;

        /**
         * Fetches all events created by the organizer
         * 
         * @param organizerId
         * @return List of events
         * @throws DataAccessException
         */
        List<Event> getEventsByOrganizer(int organizerId) throws DataAccessException;

        /**
         * Returns summary data for organizer events
         * 
         * @param organizerId
         * @return list of organizer summary
         * @throws DataAccessException
         */
        List<OrganizerEventSummary> getEventSummaryByOrganizer(int organizerId)
                        throws DataAccessException;

        /**
         * Returns revenue report grouped by event
         * 
         * @return list of event revenue report
         * @throws DataAccessException
         */
        List<EventRevenueReport> getEventWiseRevenueReport() throws DataAccessException;

        /**
         * Updates event start and end time
         * 
         * @param eventId
         * @param start
         * @param end
         * @return true if update succeeded
         * @throws DataAccessException
         */
        boolean updateEventSchedule(int eventId, Instant start, Instant end) throws DataAccessException;

        /**
         * Returns revenue report of organizer events grouped by event
         * 
         * @param organizerId
         * @return list of event revenue report
         * @throws DataAccessException
         */
        List<EventRevenueReport> getEventWiseRevenueReportByOrganizer(int organizerId)
                        throws DataAccessException;
}