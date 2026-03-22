package com.ems.actions;

import java.util.Scanner;

import com.ems.exception.DataAccessException;
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
		
		
		
		
	}
	
	/**
	 * update the ticket quantity of the event
	 * Only works if the event capacity is more than the cummulated ticket count of
	 * event
	 * 
	 * @param userId the organizer ID
	 */
	public void updateTicketQuantity(int userId) {
		
		
		
		
		
	}
	
	/**
	 * Helps to see how much ticket has been sold on the particular event
	 * 
	 * @param userId the organizer ID
	 */
	public void viewTicketAvailability(int userId) {
		
		
		
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
