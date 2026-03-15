package com.ems.dao;

<<<<<<< HEAD
public class EventDao {

	
=======
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
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433

}
