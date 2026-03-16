package com.ems.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.model.Event;

public class EventDaoImpl implements EventDao {

    private List<Event> eventList = new ArrayList<>();

    @Override
    public List<Event> getAllEvents() {
        return eventList;
    }

    @Override
    public Event getEventById(int eventId) {

        for(Event event : eventList) {
            if(event.getEventId() == eventId) {
                return event;
            }
        }

        return null;
    }

    @Override
    public List<Event> searchEventsByTitle(String title) {

        List<Event> result = new ArrayList<>();

        for(Event event : eventList) {
            if(event.getTitle().equalsIgnoreCase(title)) {
                result.add(event);
            }
        }

        return result;
    }

    @Override
    public List<Event> filterEventsByCategory(int categoryId) {

        List<Event> result = new ArrayList<>();

        for(Event event : eventList) {
            if(event.getCategoryId() == categoryId) {
                result.add(event);
            }
        }

        return result;
    }

    @Override
    public List<Event> filterEventsByVenue(int venueId) {

        List<Event> result = new ArrayList<>();

        for(Event event : eventList) {
            if(event.getVenueId() == venueId) {
                result.add(event);
            }
        }

        return result;
    }
}