package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

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
			List<Event> myEvents = organizerService.getOrganizerEvents(userId);
			if (myEvents == null || myEvents.isEmpty()) {
				System.out.println("You have no events.");
				return;
			}
 
			System.out.println("\nYour Events:");
			System.out.println("==============================================");
			for (Event e : myEvents) {
				System.out.println("Event ID: " + e.getEventId() + " | " + e.getTitle());
			}
			System.out.println("----------------------------------------------");
 
			int eventId = InputValidationUtil.readInt(scanner, "Enter Event ID: ");
 
			List<Ticket> tickets = organizerService.viewTicketAvailability(eventId);
			if (tickets == null || tickets.isEmpty()) {
				System.out.println("No tickets found for this event.");
				return;
			}
 
			System.out.println("\nTickets for Event ID " + eventId + ":");
			System.out.println("==============================================");
			for (Ticket t : tickets) {
				System.out.println("Ticket ID : " + t.getTicketId()
						+ " | Type: " + t.getTicketType()
						+ " | Current Price: ₹" + t.getPrice());
			}
			System.out.println("----------------------------------------------");
 
			int ticketId = InputValidationUtil.readInt(scanner, "Enter Ticket ID to update price: ");
 
			double newPrice = InputValidationUtil.readDouble(scanner, "Enter New Price (₹): ");
			while (newPrice <= 0) {
				System.out.println("Price must be greater than 0.");
				newPrice = InputValidationUtil.readDouble(scanner, "Enter New Price (₹): ");
			}
 
			boolean updated = organizerService.updateTicketPrice(ticketId, newPrice);
			if (updated) {
				System.out.println("Ticket price updated successfully.");
			} else {
				System.out.println("Failed to update ticket price. Please check the Ticket ID.");
			}
 
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
			List<Event> myEvents = organizerService.getOrganizerEvents(userId);
			if (myEvents == null || myEvents.isEmpty()) {
				System.out.println("You have no events.");
				return;
			}
 
			System.out.println("\nYour Events:");
			System.out.println("==============================================");
			for (Event e : myEvents) {
				System.out.println("Event ID: " + e.getEventId()
						+ " | " + e.getTitle()
						+ " | Capacity: " + e.getCapacity());
			}
			System.out.println("----------------------------------------------");
 
			int eventId = InputValidationUtil.readInt(scanner, "Enter Event ID: ");
 
			Event selectedEvent = myEvents.stream()
					.filter(e -> e.getEventId() == eventId)
					.findFirst()
					.orElse(null);
 
			if (selectedEvent == null) {
				System.out.println("Event not found or does not belong to you.");
				return;
			}
 
			List<Ticket> tickets = organizerService.viewTicketAvailability(eventId);
			if (tickets == null || tickets.isEmpty()) {
				System.out.println("No tickets found for this event.");
				return;
			}
 
			// Calculate total tickets already defined across all ticket types
			int totalAllocated = tickets.stream().mapToInt(Ticket::getTotalQuantity).sum();
			int eventCapacity = selectedEvent.getCapacity();
			int remainingCapacity = eventCapacity - totalAllocated;
 
			System.out.println("\nEvent Capacity      : " + eventCapacity);
			System.out.println("Total Tickets Defined: " + totalAllocated);
			System.out.println("Remaining Capacity  : " + remainingCapacity);
			System.out.println("==============================================");
			for (Ticket t : tickets) {
				System.out.println("Ticket ID : " + t.getTicketId()
						+ " | Type: " + t.getTicketType()
						+ " | Total Qty: " + t.getTotalQuantity()
						+ " | Available: " + t.getAvailableQuantity());
			}
			System.out.println("----------------------------------------------");
 
			int ticketId = InputValidationUtil.readInt(scanner, "Enter Ticket ID to update quantity: ");
 
			Ticket selectedTicket = tickets.stream()
					.filter(t -> t.getTicketId() == ticketId)
					.findFirst()
					.orElse(null);
 
			if (selectedTicket == null) {
				System.out.println("Ticket not found for this event.");
				return;
			}
 
			// Max allowed new quantity = current total + remaining capacity
			int maxAllowed = selectedTicket.getTotalQuantity() + remainingCapacity;
			int newQty = InputValidationUtil.readInt(scanner,
					"Enter New Total Quantity (max " + maxAllowed + "): ");
 
			while (newQty <= 0 || newQty > maxAllowed) {
				System.out.println("Quantity must be between 1 and " + maxAllowed + ".");
				newQty = InputValidationUtil.readInt(scanner,
						"Enter New Total Quantity (max " + maxAllowed + "): ");
			}
 
			boolean updated = organizerService.updateTicketQuantity(ticketId, newQty);
			if (updated) {
				System.out.println("Ticket quantity updated successfully.");
			} else {
				System.out.println("Failed to update ticket quantity. Please check the Ticket ID.");
			}
 
		} catch (DataAccessException e) {
			System.out.println("Error updating ticket quantity: " + e.getMessage());
		}
		
	}
	
	/**
	 * Helps to see how much ticket has been sold on the particular event
	 * 
	 * @param userId the organizer ID
	 */
	public void viewTicketAvailability(int userId) {
		
		try {
			List<Event> myEvents = organizerService.getOrganizerEvents(userId);
			if (myEvents == null || myEvents.isEmpty()) {
				System.out.println("You have no events.");
				return;
			}
 
			System.out.println("\nYour Events:");
			System.out.println("==============================================");
			for (Event e : myEvents) {
				System.out.println("Event ID: " + e.getEventId() + " | " + e.getTitle());
			}
			System.out.println("----------------------------------------------");
 
			int eventId = InputValidationUtil.readInt(scanner, "Enter Event ID: ");
 
			List<Ticket> tickets = organizerService.viewTicketAvailability(eventId);
			if (tickets == null || tickets.isEmpty()) {
				System.out.println("No tickets found for this event.");
				return;
			}
 
			System.out.println("\nTicket Availability for Event ID " + eventId + ":");
			System.out.println("==============================================");
			System.out.printf("%-10s %-20s %-12s %-12s %-10s%n",
					"Ticket ID", "Type", "Total Qty", "Available", "Sold");
			System.out.println("----------------------------------------------");
			for (Ticket t : tickets) {
				int sold = t.getTotalQuantity() - t.getAvailableQuantity();
				System.out.printf("%-10d %-20s %-12d %-12d %-10d%n",
						t.getTicketId(), t.getTicketType(),
						t.getTotalQuantity(), t.getAvailableQuantity(), sold);
			}
			System.out.println("==============================================");
 
		} catch (DataAccessException e) {
			System.out.println("Error fetching ticket availability: " + e.getMessage());
		}
		
	}
	
	/* ===================== HELPER FUNCTIONS ===================== */

	private void createTicketForEvent(int eventId, int remainingCapacity) throws DataAccessException {

		Ticket ticket = new Ticket();
		ticket.setEventId(eventId);

		ticket.setTicketType(InputValidationUtil.readNonEmptyString(scanner, "Ticket Type: "));

		ticket.setPrice(InputValidationUtil.readDouble(scanner, "Ticket Price (₹): "));

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