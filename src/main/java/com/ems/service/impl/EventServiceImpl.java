package com.ems.service.impl;

import java.util.List;

import com.ems.dao.EventDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;

public class EventServiceImpl implements EventService {

    private EventDao eventDao;

    // Constructor
    public EventServiceImpl() {
        eventDao = new EventDaoImpl();
    }

    @Override
    public List<Event> getAvailableEvents() throws DataAccessException {
        return eventDao.listAvailableEvents();
    }

    @Override
    public Event getEventDetails(int eventId) throws DataAccessException {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        return eventDao.getEventById(eventId);
    }

    @Override
    public List<Ticket> getTicketOptions(int eventId) throws DataAccessException {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        return eventDao.getTicketsByEventId(eventId);
    }

    @Override
    public List<Event> getUpcomingEvents(int userId) throws DataAccessException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return eventDao.getUpcomingEventsByUser(userId);
    }

    @Override
    public List<Event> getPastEvents(int userId) throws DataAccessException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return eventDao.getPastEventsByUser(userId);
    }

    @Override
    public List<BookingDetail> getBookingHistory(int userId) throws DataAccessException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return eventDao.getBookingHistory(userId);
    }
}
