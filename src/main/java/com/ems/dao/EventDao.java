package com.ems.dao;
import java.util.List;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;

public interface EventDao {

	/**
	 * Lists all published future events that still have available tickets
	 *
	 * @return list of available events
	 * @throws DataAccessException
	 */
	List<Event> listAvailableEvents() throws DataAccessException;
}
