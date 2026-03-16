package com.ems.dao;

import java.util.List;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;

public interface EventDao {

	/**
	 * Lists all published future events that still have available tickets
	 *
	 * @return list of available events
	 * @throws DataAccessException
	 */
	List<Event> listAvailableEvents() throws DataAccessException;

	/**
	 * Retrieves ticket options for a specific event
	 *
	 * @param eventId
	 * @return list of ticket options
	 * @throws DataAccessException
	 */
	List<Ticket> getTicketsByEventId(int eventId) throws DataAccessException;

	/**
	 * Retrieves full details of a specific event
	 *
	 * @param eventId
	 * @return Event object
	 * @throws DataAccessException
	 */
	Event getEventById(int eventId) throws DataAccessException;

}