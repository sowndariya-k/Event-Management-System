/*
 * Author : Sowndariya
 * AdminTicketManagementAction handles admin operations for
 * ticket management, including viewing all tickets, sorting
 * them, and overseeing ticket availability across events.
 */

package com.ems.actions;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.model.Ticket;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;

public class AdminTicketManagementAction {
	private final EventService eventService;
	private final AdminService adminService;
	private final Scanner scanner;

	public AdminTicketManagementAction(Scanner scanner) {
		this.scanner=scanner;
		this.eventService = ApplicationUtil.eventService();
		this.adminService = ApplicationUtil.adminService();
	}


	public void viewTicketsByEvent() {
		try {
			List<Event> events = getAvailableEvents();
			if (events.isEmpty()) {
				System.out.println("No events available at the moment.");
				return;
			}

			printEventSummaries(events);

			int eChoice = InputValidationUtil.readInt(scanner,
					"Select an event (1-" + events.size() + "): ");
			while (eChoice < 1 || eChoice > events.size()) {
				eChoice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
			}

			Event selectedEvent = events.get(eChoice - 1);
			List<Ticket> tickets = getTicketsForEvent(selectedEvent.getEventId());

			if (tickets.isEmpty()) {
				System.out.println("No tickets found for this event");
				return;
			}

			AdminMenuHelper.printTicketDetails(tickets);

		} catch (DataAccessException e) {
			System.out.println("Error viewing tickets: " + e.getMessage());
		}
	}

	public void viewTicketSummary() {
		try {
			List<Event> events = getAvailableEvents();
			if (events.isEmpty()) {
				System.out.println("No events available at the moment.");
				return;
			}

			printEventSummaries(events);

			int eChoice = InputValidationUtil.readInt(scanner,
					"Select an event (1-" + events.size() + "): ");
			while (eChoice < 1 || eChoice > events.size()) {
				eChoice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
			}

			Event selectedEvent = events.get(eChoice - 1);
			List<Ticket> tickets = getTicketsForEvent(selectedEvent.getEventId());

			AdminMenuHelper.printTicketCapacitySummary(tickets);

		} catch (DataAccessException e) {
			System.out.println("Error viewing ticket summary: " + e.getMessage());
		}
	}

	public void viewEventRegistrations() {
		try {
			List<Event> events = getAvailableEvents();
			if (events.isEmpty()) {
				System.out.println("No events available at the moment.");
				return;
			}

			printEventSummaries(events);

			int eChoice = InputValidationUtil.readInt(scanner,
					"Select an event (1-" + events.size() + "): ");
			while (eChoice < 1 || eChoice > events.size()) {
				eChoice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
			}

			Event selectedEvent = events.get(eChoice - 1);
			List<EventRegistrationReport> reports = getEventWiseRegistrations(selectedEvent.getEventId());

			if (reports.isEmpty()) {
				System.out.println("No registrations found for this event");
				return;
			}

			reports.sort(Comparator.comparing(EventRegistrationReport::getRegistrationDate).reversed());

			AdminMenuHelper.printEventRegistrationReport(reports);

		} catch (DataAccessException e) {
			System.out.println("Error viewing registrations: " + e.getMessage());
		}
	}

	public List<Event> getAvailableEvents() throws DataAccessException {
		return eventService.listAvailableEvents();
	}

	public List<Ticket> getTicketsForEvent(int eventId) throws DataAccessException {
		return eventService.getTicketTypes(eventId);
	}

	public List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException {
		return adminService.getEventWiseRegistrations(eventId);
	}

	// ===== REQUIRED (MenuHelper) =====

		private static final int TABLE_WIDTH = 110;
		private static final String SEPARATOR = "=".repeat(TABLE_WIDTH);
		private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

		private void printEventSummaries(List<Event> events) {
			System.out.println("\nAVAILABLE EVENTS");
			System.out.println(SEPARATOR);

			System.out.printf("%-5s %-30s %-20s %-20s %-10s%n",
					"NO", "TITLE", "CATEGORY", "START DATE", "TICKETS");

			System.out.println(SUB_SEPARATOR);

			int i = 1;
			for (Event e : events) {
				try {
					String categoryName = eventService
							.getCategory(e.getCategoryId())
							.getName();

					int tickets = eventService
							.getAvailableTickets(e.getEventId());

					String formattedDate = DateTimeUtil
							.formatForDisplay(e.getStartDateTime());

					System.out.printf("%-5d %-30s %-20s %-20s %-10d%n",
							i++,
							truncate(e.getTitle(), 29),
							truncate(categoryName, 19),
							formattedDate,
							tickets);

				} catch (DataAccessException ex) {
					System.out.printf("%-5d %-30s %-50s%n",
							i++,
							truncate(e.getTitle(), 29),
							"[Error fetching details]");
				}
			}

			System.out.println(SEPARATOR);
		}

		private String truncate(String value, int max) {
			if (value == null) return "";
			if (value.length() <= max) return value;
			return value.substring(0, max - 3) + "...";
		}
}
