package com.ems.service.impl;

import java.util.List;
import com.ems.dao.EventDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;

public class EventServiceImpl implements EventService {

    private EventDao eventDao;

    public EventServiceImpl() {
        eventDao = new EventDaoImpl();
    }

    @Override
    public List<Event> viewEvents() {
        return eventDao.viewEvents();
    }

    @Override
    public List<Event> listAvailableEvents() {
        return eventDao.listAvailableEvents();
    }

    @Override
    public Event viewEventDetails(int eventId) {
        return eventDao.viewEventDetails(eventId);
    }

    @Override
    public List<Ticket> viewTicketOptions(int eventId) {
        return eventDao.viewTicketOptions(eventId);
    }

    @Override
    public List<Event> viewUpcomingEvents() {
        return eventDao.viewUpcomingEvents();
    }

    @Override
    public List<Event> viewPastEvents() {
        return eventDao.viewPastEvents();
    }

    @Override
    public List<BookingDetail> viewBookingDetails(int userId) {
        return eventDao.viewBookingDetails(userId);
    }
}