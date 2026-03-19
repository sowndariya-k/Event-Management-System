package com.ems.service;

import java.util.List;

import com.ems.model.Event;
import com.ems.model.Ticket;

public interface EventService {

    List<Event> viewEvents();
    //List<Event> listAvailableEvents();

    Event getEventById(int eventId);

    List<Ticket> getTicketTypes(int eventId);
}