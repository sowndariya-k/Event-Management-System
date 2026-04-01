package com.ems.actions;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;

/**
 * Action class for organizer ticket management operations.
 * Delegates business logic to appropriate services.
 */
public class OrganizerTicketManagementAction {
	private final Scanner scanner;
	private final OrganizerService organizerService;

	public OrganizerTicketManagementAction(Scanner scanner) {
		this.scanner = scanner;
		this.organizerService = ApplicationUtil.organizerService();
	}
	
	/**
	 * Creates a new ticket for an event.
	 * 
	 * @param ticket the ticket object to create
	 */
	public void createTicket(Ticket ticket) throws DataAccessException {
		organizerService.createTicket(ticket);
	}
	
	/**
	 * Updates the ticket price of the event
	 * 
	 * @param userId the organizer ID
	 */
	public void updateTicketPrice(int userId) {
		try {
			List<Event> events = organizerService.getOrganizerEvents(userId);

			if (events.isEmpty()) {
				System.out.println("No events found.");
				return;
			}

			Instant now = DateTimeUtil.nowUtc();
			
			System.out.println("DEBUG EVENTS:");
			events.forEach(e -> 
			    System.out.println(e.getEventId() + " | " + e.getStatus() + " | " + e.getStartDateTime())
			);
			List<Event> validEvents = events.stream()
			        .filter(e -> e.getStatus() == EventStatus.APPROVED
			                || e.getStatus() == EventStatus.PUBLISHED)
			        .sorted(Comparator.comparing(Event::getStartDateTime))
			        .collect(Collectors.toList());

			if (validEvents.isEmpty()) {
				System.out.println("No events available");
				return;
			}
			
			MenuHelper.printEventSummaries(validEvents);

			int eventChoice = InputValidationUtil.readInt(scanner,
					"Select an event number (1-" + validEvents.size() + "): ");

			while (eventChoice < 1 || eventChoice > validEvents.size()) {
				eventChoice = InputValidationUtil.readInt(scanner,
						"Please enter a valid number from the list.: ");
			}

			Event selectedEvent = validEvents.get(eventChoice - 1);

			if (EventStatus.PUBLISHED.toString().equals(selectedEvent.getStatus())) {

				System.out.println("\nWarning: This event is already published.");
				System.out.println("Tickets may have already been sold.");
				System.out.println("Changing ticket price may affect customers and financial records.\n");

				char confirm = InputValidationUtil.readChar(scanner,
						"Do you still want to continue? (Y/N): ");

				if (Character.toUpperCase(confirm) != 'Y') {
					System.out.println("Price update cancelled.");
					return;
				}
			}

			List<Ticket> tickets = organizerService.viewTicketAvailability(selectedEvent.getEventId());

			if (tickets.isEmpty()) {
				System.out.println("No tickets available for this event");
				return;
			}

			MenuHelper.printTicketSummaries(tickets);

			int choice = InputValidationUtil.readInt(scanner,
					"Choose the ticket (1 - " + tickets.size() + "): ");

			while (choice < 1 || choice > tickets.size()) {
				choice = InputValidationUtil.readInt(scanner,
						"Choose the ticket (1 - " + tickets.size() + "): ");
			}

			Ticket selectedTicket = tickets.get(choice - 1);

			double newPrice = InputValidationUtil.readDouble(scanner, "Enter new price: ");

			while (newPrice <= 0) {
				newPrice = InputValidationUtil.readDouble(scanner, "Enter a valid price: ");
			}

			System.out.println("\nSelected Ticket Details:");
			MenuHelper.printTicketSummaries(List.of(selectedTicket));

			System.out.println("\nPRICE CHANGE SUMMARY");
			System.out.println("--------------------------------------");
			System.out.println("Ticket Type : " + selectedTicket.getTicketType());
			System.out.println("Old Price   : ₹" + selectedTicket.getPrice());
			System.out.println("New Price   : ₹" + newPrice);
			System.out.println("--------------------------------------");

			char finalConfirm = InputValidationUtil.readChar(scanner,
					"Confirm price update? (Y/N): ");

			if (Character.toUpperCase(finalConfirm) != 'Y') {
				System.out.println("Price update cancelled.");
				return;
			}

			boolean result = organizerService.updateTicketPrice(
					selectedTicket.getTicketId(),
					newPrice);

			System.out.println(result
					? "Ticket price updated successfully."
					: "Unable to update ticket. Please try again.");

		} catch (DataAccessException e) {
			System.out.println("Error updating ticket price: " + e.getMessage());
		}
	}
	
	/**
	 * update the ticket quantity of the event
	 * Only works if the event capacity is more than the cummulated ticket count of
	 * event
	 * 
	 * @param userId the organizer ID
	 */
	public void updateTicketQuantity(int userId) {
		try {
			List<Event> events = organizerService.getOrganizerEvents(userId);

			if (events.isEmpty()) {
				System.out.println("No events found.");
				return;
			}

			Instant now = DateTimeUtil.nowUtc();

			List<Event> validEvents = events.stream()
			        .filter(e -> e.getStatus() == EventStatus.APPROVED
			                || e.getStatus() == EventStatus.PUBLISHED)
			        .sorted(Comparator.comparing(Event::getStartDateTime))
			        .collect(Collectors.toList());

			if (validEvents.isEmpty()) {
				System.out.println("No events available");
				return;
			}

			MenuHelper.printEventSummaries(validEvents);

			int eventChoice = MenuHelper.selectFromList(scanner, validEvents.size(), "Select an event number");
			Event selectedEvent = validEvents.get(eventChoice - 1);

			int capacity = selectedEvent.getCapacity();

			List<Ticket> tickets = organizerService.viewTicketAvailability(selectedEvent.getEventId());

			int totalTickets = tickets.stream().mapToInt(Ticket::getTotalQuantity).sum();

			if (totalTickets == capacity) {
				System.out.println("Ticket quantity already matches event capacity");
				return;
			}

			int remaining = capacity - totalTickets;

			System.out.println("\nEvent Capacity: " + capacity +
					"\nCurrent Ticket Quantity: " + totalTickets +
					"\nRemaining Capacity: " + remaining);

			int option = InputValidationUtil.readInt(scanner,
					"\n1. Update existing ticket\n2. Add new ticket\nSelect an option: ");

			if (option == 2) {
				createTicketForEvent(selectedEvent.getEventId(), remaining);
				return;
			}

			if (option != 1) {
				System.out.println("Invalid option.");
				return;
			}

			MenuHelper.printTicketSummaries(tickets);

			int choice = InputValidationUtil.readInt(scanner,
					"Choose the ticket (1 - " + tickets.size() + "): ");

			while (choice < 1 || choice > tickets.size()) {
				choice = InputValidationUtil.readInt(scanner,
						"Choose the ticket (1 - " + tickets.size() + "): ");
			}

			Ticket selectedTicket = tickets.get(choice - 1);

			int maxAllowed = selectedTicket.getTotalQuantity() + remaining;

			int newQty = InputValidationUtil.readInt(scanner,
					"Enter new quantity (max " + maxAllowed + "): ");

			while (newQty <= 0 || newQty > maxAllowed) {
				newQty = InputValidationUtil.readInt(scanner, "Enter valid quantity: ");
			}

			boolean result = organizerService.updateTicketQuantity(
					selectedTicket.getTicketId(), newQty);

			System.out.println(result ? "Updated successfully" : "Update failed");

		} catch (DataAccessException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	
	/**
	 * Helps to see how much ticket has been sold on the particular event
	 * 
	 * @param userId the organizer ID
	 */
	public void viewTicketAvailability(int userId) {
		try {
			List<Event> events = organizerService.getOrganizerEvents(userId);

			if (events.isEmpty()) {
				System.out.println("No events found.");
				return;
			}

			List<Event> validEvents = events.stream()
			        .filter(e -> e.getStatus() == EventStatus.APPROVED
			                || e.getStatus() == EventStatus.PUBLISHED)
			        .sorted(Comparator.comparing(Event::getStartDateTime))
			        .collect(Collectors.toList());

			if (validEvents.isEmpty()) {
				System.out.println("No events available");
				return;
			}

			MenuHelper.printEventSummaries(validEvents);

			int eventChoice = MenuHelper.selectFromList(scanner, validEvents.size(), "Select a event");

			Event selectedEvent = validEvents.get(eventChoice - 1);

			List<Ticket> tickets = organizerService.viewTicketAvailability(selectedEvent.getEventId());

			MenuHelper.printTicketSummaries(tickets);

		} catch (DataAccessException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	
	/* ===================== HELPER FUNCTIONS ===================== */

	private void createTicketForEvent(int eventId, int remainingCapacity) throws DataAccessException {

		Ticket ticket = new Ticket();
		ticket.setEventId(eventId);

		ticket.setTicketType(
				InputValidationUtil.readNonEmptyString(scanner, "Ticket Type: "));

		ticket.setPrice(
				InputValidationUtil.readDouble(scanner, "Ticket Price (₹): "));

		int qty = InputValidationUtil.readInt(scanner,
				"Ticket Quantity (max " + remainingCapacity + "): ");

		while (qty <= 0 || qty > remainingCapacity) {
			qty = InputValidationUtil.readInt(scanner, "Enter valid quantity: ");
		}

		ticket.setTotalQuantity(qty);

		createTicket(ticket);

		System.out.println("Ticket added successfully");
	}

	

}