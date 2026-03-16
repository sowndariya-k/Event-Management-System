package com.ems.dao;

import java.util.List;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.BookingDetail;

public interface EventDao {

	/**
	 * Lists all published future events that still have available tickets
	 */
	List<Event> listAvailableEvents() throws DataAccessException;

	/**
	 * Retrieves ticket options for a specific event
	 */
	List<Ticket> getTicketsByEventId(int eventId) throws DataAccessException;

	/**
	 * Retrieves full details of a specific event
	 */
	Event getEventById(int eventId) throws DataAccessException;

	/**
	 * View upcoming events registered by a user
	 */
	List<Event> getUpcomingEventsByUser(int userId) throws DataAccessException;

	/**
	 * View past events attended by a user
	 */
	List<Event> getPastEventsByUser(int userId) throws DataAccessException;

	/**
	 * View booking details for a specific user
	 */
	List<BookingDetail> getBookingHistory(int userId) throws DataAccessException;

}