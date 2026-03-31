package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.impl.EventDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.dao.impl.TicketDaoImpl;
import com.ems.enums.EventStatus;
import com.ems.enums.NotificationType;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.util.DateTimeUtil;
/*
 * Handles organizer related business operations.
 *
 * Responsibilities:
 * - Create, update, publish, and cancel events
 * - Manage event schedules, capacity, and tickets
 * - Access registration, sales, and revenue data
 * - Send event related notifications to attendees
 */
public class OrganizerServiceImpl implements OrganizerService {

    private final EventDaoImpl eventDao;
    private final TicketDaoImpl ticketDao;
    private final RegistrationDaoImpl registrationDao;
    private final NotificationService notificationService;

    public OrganizerServiceImpl(EventDaoImpl eventDao, TicketDaoImpl ticketDao,
                                RegistrationDaoImpl registrationDao, NotificationService notificationService) {
        this.eventDao = eventDao;
        this.ticketDao = ticketDao;
        this.registrationDao = registrationDao;
        this.notificationService = notificationService;
    }
    
    /*
	 * Creates a new event in DRAFT state.
	 *
	 * Rule: - Newly created events are always saved as DRAFT
	 */
	@Override
	public int createEvent(Event event) throws DataAccessException {
		event.setStatus(EventStatus.DRAFT);
		int eventId = eventDao.createEvent(event);
		return eventId;
	}
	
	/*
	 * Updates basic event information.
	 *
	 * Used when organizer edits title, description, category, or venue.
	 */
	@Override
	public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId)
			throws DataAccessException {
		boolean updated = eventDao.updateEventDetails(eventId, title, description, categoryId, venueId);
		return updated;
	}
	
	/*
	 * Updates the event start and end schedule.
	 *
	 * Used for rescheduling upcoming events.
	 */
	@Override
	public boolean updateEventSchedule(int eventId, LocalDateTime start, LocalDateTime end) throws DataAccessException {
		boolean updated = eventDao.updateEventSchedule(
				eventId,
				DateTimeUtil.toUtcInstant(start),
				DateTimeUtil.toUtcInstant(end));
		return updated;
	}
	
	/*
	 * Updates the maximum allowed capacity for an event.
	 */
	@Override
	public boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException {
		boolean updated = eventDao.updateEventCapacity(eventId, capacity);
		return updated;
	}
	
	
	/*
	 * Publishes an event and makes it visible to users.
	 */
	@Override
	public boolean publishEvent(int eventId) throws DataAccessException {
		boolean published = eventDao.updateEventStatus(eventId, EventStatus.PUBLISHED);
		return published;
	}
	
	/*
	 * Cancels an existing event.
	 *
	 * Used when an event cannot proceed as planned.
	 */
	@Override
	public boolean cancelEvent(int eventId) throws DataAccessException {
		boolean cancelled = eventDao.updateEventStatus(eventId, EventStatus.CANCELLED);
		return cancelled;
	}
	// ticket management
	
	/*
	 * Creates a ticket type for an event.
	 *
	 * Rule: - Available quantity is initialized to total quantity
	 */
	@Override
	public boolean createTicket(Ticket ticket) throws DataAccessException {
		ticket.setAvailableQuantity(ticket.getTotalQuantity());
		return ticketDao.createTicket(ticket);
	}

	/*
	 * Updates the price of an existing ticket type.
	 */
	@Override
	public boolean updateTicketPrice(int ticketId, double price) throws DataAccessException {
		return ticketDao.updateTicketPrice(ticketId, price);
	}

	/*
	 * Updates the total quantity of tickets available.
	 */
	@Override
	public boolean updateTicketQuantity(int ticketId, int quantity) throws DataAccessException {
		return ticketDao.updateTicketQuantity(ticketId, quantity);
	}

	/*
	 * Retrieves current ticket availability for an event.
	 */
	@Override
	public List<Ticket> viewTicketAvailability(int eventId) throws DataAccessException {

		List<Ticket> tickets = ticketDao.getTicketsByEvent(eventId);
		if (tickets == null) {
			return new ArrayList<>();
		}
		return tickets;
	}

	// registrations & reports
	/*
	 * Returns the total number of registrations for an event.
	 */
	@Override
	public int viewEventRegistrations(int eventId) throws DataAccessException {
		return registrationDao.getEventRegistrationCount(eventId);
	}
	

	/*
	 * Returns the revenue report for an event.
	 */
	
	@Override
	public List<EventRevenueReport> getRevenueReport(int organizerId) throws DataAccessException {
		return eventDao.getEventWiseRevenueReportByOrganizer(organizerId);
	}

	/*
	 * Retrieves all events created by a specific organizer.
	 */
	@Override
	public List<OrganizerEventSummary> getOrganizerEventSummary(int organizerId) throws DataAccessException {
		return eventDao.getEventSummaryByOrganizer(organizerId);
	}
	
	// notifications
	/*
	 * Sends a general update notification to all event attendees.
	 */
	@Override
	public void sendEventUpdate(int eventId, String message) throws DataAccessException {
		notificationService.sendEventNotification(eventId, message, NotificationType.EVENT);
		
	}

	/*
	 * Sends a schedule change notification to all event attendees.
	 */
	@Override
	public void sendScheduleChange(int eventId, String message) throws DataAccessException {
		notificationService.sendEventNotification(eventId, message, NotificationType.EVENT);
		
	}

	// organizer data
	/*
	 * Retrieves all events created by a specific organizer.
	 */
	@Override
	public List<Event> getOrganizerEvents(int organizerId) throws DataAccessException {
		return eventDao.getEventsByOrganizer(organizerId);
	}

	@Override
	public Event getOrganizerEventById(int organizerId, int eventId) throws DataAccessException {
		Event event = eventDao.getEventById(eventId);
		if (event != null && event.getOrganizerId() == organizerId) {
			return event;
		}
		return null;
	}
	

	@Override
	public void sendCancellationRequest(Event selectedEvent, String message) throws DataAccessException {
		String notificationMessage = "CANCELLATION REQUEST\n\n" +
				"Event: " + selectedEvent.getTitle() + "\n" +
				"Event ID: " + selectedEvent.getEventId() + "\n" +
				"Start Time: " + DateTimeUtil.formatForDisplay(selectedEvent.getStartDateTime()) + "\n\n" +
				"Requested by Organizer:\n" +
				selectedEvent.getOrganizerId() + " (User ID: " + selectedEvent.getOrganizerId() + ")\n\n" +
				"Organizer Message:\n" +
				message;

		notificationService.sendPersonalNotification(
				selectedEvent.getApprovedBy(),
				notificationMessage,
				NotificationType.EVENT);
	}
	
	/*
	 * Displays registration details for a specific event. Registrations are shown
	 * in reverse chronological order.
	 */
	@Override
	public List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException {
		List<EventRegistrationReport> reports = registrationDao.getEventWiseRegistrations(eventId);

		if (reports.isEmpty()) {
			return new ArrayList<>();
		}
		return reports;
	}
	
	

}
