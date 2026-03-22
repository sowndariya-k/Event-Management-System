package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.enums.PaymentMethod;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.service.OfferService;
import com.ems.exception.DataAccessException;

public class EventRegistrationAction {

	private final EventService eventService;
    private final OfferService offerService;
    private final Scanner scanner;

    public EventRegistrationAction(EventService eventService, OfferService offerService, Scanner scanner) {
        this.eventService = eventService;
        this.offerService = offerService;
        this.scanner = scanner; // Reuse scanner from MainMenu
    }

	public void registerForAvailableEvent(int userId) {
		try {
			List<Event> events = eventService.listAvailableEvents();

			if (events == null || events.isEmpty()) {
				System.out.println("No events available for registration at the moment.");
				return;
			}

			// List events
			System.out.println("\nAvailable Events:");
			for (int i = 0; i < events.size(); i++) {
				Event e = events.get(i);
				System.out.println((i + 1) + ". " + e.getTitle() + " (ID: " + e.getEventId() + ")");
			}

			int eventChoice = readInt("Select an event (1-" + events.size() + "): ", 1, events.size());
			Event selectedEvent = events.get(eventChoice - 1);

			// Get tickets for selected event
			List<Ticket> tickets = eventService.getTicketTypes(selectedEvent.getEventId());
			if (tickets == null || tickets.isEmpty()) {
				System.out.println("No ticket types available for this event.");
				return;
			}

			System.out.println("\nAvailable Ticket Types:");
			for (int i = 0; i < tickets.size(); i++) {
				Ticket t = tickets.get(i);
				System.out.println((i + 1) + ". " + t.getTicketType() + " - ₹" + t.getPrice() + " (Available: "
						+ t.getAvailableQuantity() + ")");
			}

			int ticketChoice = readInt("Select a ticket type (1-" + tickets.size() + "): ", 1, tickets.size());
			Ticket selectedTicket = tickets.get(ticketChoice - 1);

			int quantity = readInt("Enter quantity (1-" + selectedTicket.getAvailableQuantity() + "): ", 1,
					selectedTicket.getAvailableQuantity());

			// Payment method
			PaymentMethod[] methods = PaymentMethod.values();
			System.out.println("\nPayment Methods:");
			for (int i = 0; i < methods.length; i++) {
				System.out.println((i + 1) + ". " + methods[i].name().replace("_", " "));
			}

			int paymentChoice = readInt("Select payment method (1-" + methods.length + "): ", 1, methods.length);
			PaymentMethod selectedMethod = methods[paymentChoice - 1];

			// Offer code (optional)
			Offer offer = null;
			String offerCode = "";
			while (true) {
				System.out.print("\nEnter offer code or press Enter to skip: ");
				offerCode = scanner.nextLine().trim();

				if (offerCode.isEmpty())
					break;

				offer = offerService.getOffer(selectedEvent.getEventId(), offerCode.toUpperCase());
				if (offer == null) {
					System.out.println("Invalid offer code.");
					continue;
				}

				// You can add more validation here if needed, like checking dates or usage
				break;
			}

			double totalAmount = quantity * selectedTicket.getPrice();
			if (offer != null) {
				double discount = totalAmount * (offer.getDiscountPercentage() / 100.0);
				totalAmount -= discount;
				System.out.println("Discount applied: " + offer.getDiscountPercentage() + "%");
			}

			System.out.println("Total amount to pay: ₹" + String.format("%.2f", totalAmount));

			System.out.print("Confirm payment? (y/n): ");
			char confirm = scanner.nextLine().trim().toLowerCase().charAt(0);

			if (confirm == 'y') {
				boolean success = eventService.registerForEvent(userId, selectedEvent.getEventId(),
						selectedTicket.getTicketId(), quantity, selectedTicket.getPrice(), selectedMethod, offerCode);

				if (success) {
					System.out.println("Registration successful!");
				} else {
					System.out.println("Registration failed. Try again later.");
				}
			} else {
				System.out.println("Registration cancelled.");
			}

		} catch (DataAccessException e) {
			System.out.println("Error during registration: " + e.getMessage());
		}
	}

	/**
	 * Cancels an existing event registration for the user.
	 *
	 * @param userId the ID of the user
	 */

	public void cancelRegistration(int userId) {
		try {
            System.out.print("Enter Registration ID to cancel: ");
            int registrationId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Are you sure you want to cancel registration #"
                    + registrationId + "? (y/n): ");
            char confirm = scanner.nextLine().trim().toLowerCase().charAt(0);

            if (confirm != 'y') {
                System.out.println("Cancellation aborted.");
                return;
            }

            boolean success = eventService.cancelRegistration(userId, registrationId);
            if (success) {
                System.out.println("Registration #" + registrationId + " cancelled successfully.");
            } else {
                System.out.println("Cancellation failed. Registration not found or already cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid registration ID.");
        } catch (DataAccessException e) {
            System.out.println("Error during cancellation: " + e.getMessage());
        }
	}
	
	private int readInt(String prompt, int min, int max) {
		int value;
		while (true) {
			System.out.print(prompt);
			String line = scanner.nextLine();
			try {
				value = Integer.parseInt(line);
				if (value >= min && value <= max)
					return value;
			} catch (NumberFormatException ignored) {
			}
			System.out.println("Enter a number between " + min + " and " + max + ".");
		}
	}
}
