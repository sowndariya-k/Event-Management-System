package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

public class AdminEventManagementAction {
	private final AdminService adminService;
	private final EventService eventService;
	private final Scanner scanner;

	public AdminEventManagementAction(Scanner scanner) {
		this.adminService = ApplicationUtil.adminService();
		this.eventService = ApplicationUtil.eventService();
		this.scanner = scanner;
	}
	

	// LIST ALL EVENTS
	public void listAllEvents() {
		 try {
	            List<Event> events = getAllEvents();

	            if (events.isEmpty()) {
	                System.out.println("No events available at the moment.");
	                return;
	            }

	            AdminMenuHelper.printAllEventsWithStatus(events, 1);

	        } catch (DataAccessException e) {
	            System.out.println("Error listing events: " + e.getMessage());
	        }
		
	}
	
	// EVENT DETAILS
	public void printEventDetails() {
		 try {
	            Event event = selectAnyEvent();
	            if (event == null) return;

	            AdminMenuHelper.printEventDetails(event);

	        } catch (DataAccessException e) {
	            System.out.println("Error printing event details: " + e.getMessage());
	        }
	}
	
	// LIST TICKETS
	public void listTicketsForEvent() {
		 try {
	            List<Event> events = getAllEvents();

	            List<Event> filtered = events.stream()
	                    .filter(e -> EventStatus.PUBLISHED.equals(e.getStatus()))
	                    .toList();

	            if (filtered.isEmpty()) {
	                System.out.println("No published events available.");
	                return;
	            }

	            AdminMenuHelper.printAllEventsWithStatus(filtered, 1);

	            int choice = InputValidationUtil.readInt(scanner,
	                    "Select an event (1-" + filtered.size() + "): ");

	            while (choice < 1 || choice > filtered.size()) {
	                choice = InputValidationUtil.readInt(scanner, "Enter valid choice: ");
	            }

	            Event event = filtered.get(choice - 1);

	            List<Ticket> tickets = eventService.getTicketTypes(event.getEventId());

	            if (tickets.isEmpty()) {
	                System.out.println("No ticket options available.");
	                return;
	            }

	            AdminMenuHelper.printTicketDetails(tickets);

	        } catch (DataAccessException e) {
	            System.out.println("Error listing tickets: " + e.getMessage());
	        }
	}

	 // AVAILABLE EVENTS
    public void listAvailableEvents() {
        try {
            List<Event> events = eventService.listAvailableEvents();

            if (events.isEmpty()) {
                System.out.println("No events available.");
                return;
            }

            AdminMenuHelper.printAllEventsWithStatus(events, 1);

        } catch (DataAccessException e) {
            System.out.println("Error listing available events: " + e.getMessage());
        }
    }
	

 // APPROVE EVENT
	public void approveEvent(int userId) {
		try {
            List<Event> events = getEventsAwaitingApproval();

            if (events == null || events.isEmpty()) {
                System.out.println("No events waiting for approval.");
                return;
            }

            AdminMenuHelper.printAllEventsWithStatus(events, 1);

            int choice = InputValidationUtil.readInt(scanner,
                    "Select event (1-" + events.size() + "): ");

            while (choice < 1 || choice > events.size()) {
                choice = InputValidationUtil.readInt(scanner, "Enter valid choice: ");
            }

            Event selected = events.get(choice - 1);

            char confirm = InputValidationUtil.readChar(scanner,
                    "Approve this event? (Y/N): ");

            if (confirm == 'Y' || confirm == 'y') {
                boolean success = adminService.approveEvents(userId, selected.getEventId());

                if (success) {
                    System.out.println("Event approved successfully.");
                } else {
                    System.out.println("Event approval failed.");
                }
            } else {
                System.out.println("Action cancelled.");
            }

        } catch (DataAccessException e) {
            System.out.println("Error approving event: " + e.getMessage());
        }


		
	}
	

	// CANCEL EVENT
	public void cancelEvent() {
		 try {
	            List<Event> events = getAvailableAndDraftEvents();

	            if (events.isEmpty()) {
	                System.out.println("No events available to cancel.");
	                return;
	            }

	            AdminMenuHelper.printAllEventsWithStatus(events, 1);

	            int choice = InputValidationUtil.readInt(scanner,
	                    "Select event (1-" + events.size() + "): ");

	            while (choice < 1 || choice > events.size()) {
	                choice = InputValidationUtil.readInt(scanner, "Enter valid choice: ");
	            }

	            Event selected = events.get(choice - 1);

	            char confirm = InputValidationUtil.readChar(scanner,
	                    "Cancel this event? (Y/N): ");

	            if (confirm == 'Y' || confirm == 'y') {
	                adminService.cancelEvent(selected.getEventId());
	                System.out.println("Event cancelled successfully.");
	            } else {
	                System.out.println("Action cancelled.");
	            }

	        } catch (DataAccessException e) {
	            System.out.println("Error cancelling event: " + e.getMessage());
	        }

		
	}

	

	public void markCompletedEvents() {
		
		
		
	}
	
	
	// SELECT EVENT
    public Event selectAnyEvent() throws DataAccessException {

        List<Event> events = getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No events available.");
            return null;
        }

        AdminMenuHelper.printAllEventsWithStatus(events, 1);

        int choice = InputValidationUtil.readInt(scanner,
                "Select event (1-" + events.size() + "): ");

        while (choice < 1 || choice > events.size()) {
            choice = InputValidationUtil.readInt(scanner, "Enter valid choice: ");
        }

        return events.get(choice - 1);
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
