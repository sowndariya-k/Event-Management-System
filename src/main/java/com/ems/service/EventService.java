package com.ems.service;

import java.util.List;

import com.ems.dao.EventDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;

public class EventService {

    private EventDao eventDao;

    // Constructor
    public EventService() {
        eventDao = new EventDaoImpl();
    }

    /**
     * View all available events
     */
    public List<Event> getAvailableEvents() throws DataAccessException {
        return eventDao.listAvailableEvents();
    }

    /**
     * View details of a specific event
     */
    public Event getEventDetails(int eventId) throws DataAccessException {

        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }

        return eventDao.getEventById(eventId);
    }

    /**
     * View ticket options for a specific event
     */
    public List<Ticket> getTicketOptions(int eventId) throws DataAccessException {

        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }

        return eventDao.getTicketsByEventId(eventId);
    }
}