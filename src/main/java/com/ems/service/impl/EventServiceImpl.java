package com.ems.service.impl;

import java.util.List;

import com.ems.dao.EventDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;

public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    public EventServiceImpl() {
        this.eventDao = new EventDaoImpl();
    }

    @Override
    public List<Event> viewEvents() {
        return eventDao.viewEvents();
    }

    @Override
    public Event getEventById(int eventId) {
        return eventDao.getEventById(eventId);
    }

    @Override
    public List<Ticket> getTicketTypes(int eventId) {
        return eventDao.getTicketsByEventId(eventId);
    }
}