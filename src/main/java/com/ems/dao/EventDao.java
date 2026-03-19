package com.ems.dao;

import java.util.List;

import com.ems.model.Event;
import com.ems.model.Ticket;

public interface EventDao {
	List<Event> viewEvents();  
	
    List<Event> listAvailableEvents();

    Event getEventById(int eventId);  

    List<Ticket> getTicketsByEventId(int eventId); 
}