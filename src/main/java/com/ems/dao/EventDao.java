package com.ems.dao;

import java.util.List;
import com.ems.model.Event;

public interface EventDao {

    List<Event> getAllEvents();

    Event getEventById(int eventId);

    List<Event> searchEventsByTitle(String title);

    List<Event> filterEventsByCategory(int categoryId);

    List<Event> filterEventsByVenue(int venueId);
}