package com.ems.actions;

import java.util.List;

import com.ems.dao.EventDao;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.model.Event;

public class EventSearchAction {

    EventDao eventDAO = new EventDaoImpl();

    public void searchEvent(String title) {

        List<Event> events = eventDAO.searchEventsByTitle(title);

        for(Event e : events) {
            System.out.println(e);
        }
    }

    public void filterByCategory(int categoryId) {

        List<Event> events = eventDAO.filterEventsByCategory(categoryId);

        for(Event e : events) {
            System.out.println(e);
        }
    }
}