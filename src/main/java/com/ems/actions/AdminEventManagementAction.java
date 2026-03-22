package com.ems.actions;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;

public class AdminEventManagementAction {
	private final AdminService adminService;
	private final EventService eventService;

	public AdminEventManagementAction() {
		this.adminService = ApplicationUtil.adminService();
		this.eventService = ApplicationUtil.eventService();
	}
	

	public void listAllEvents() {
		
		
	}
	
	public void printEventDetails() {
		
		
	}
	
	public void listTicketsForEvent() {


		
	}

	public void listAvailableEvents() {


		
	}

	

	public void approveEvent(int userId) {


		
	}
	

	public void cancelEvent() {


		
	}

	

	public void markCompletedEvents() {
		
		
		
	}
	
	
	public Event selectAnyEvent() throws DataAccessException {
		return null;
		
	}
	
	//data retrieval
	
	public List<Event> getAllEvents() throws DataAccessException {
		return eventService.getAllEvents();
	}

	public Event getEventById(int eventId) throws DataAccessException {
		return eventService.getEventById(eventId);
	}

	public List<Event> getEventsAwaitingApproval() throws DataAccessException {
		return eventService.listEventsYetToApprove();
	}
	
	public List<Event> getAvailableAndDraftEvents() throws DataAccessException {
		return eventService.listAvailableAndDraftEvents();
	}
	public int getAvailableTickets(int eventId) throws DataAccessException {
		return eventService.getAvailableTickets(eventId);
	}



}
