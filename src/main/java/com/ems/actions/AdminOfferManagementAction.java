/*
 * Author : Sowndariya
 * AdminOfferManagementAction enables the admin to manage
 * discount offers, including creating, updating, deleting,
 * and listing offers with their validity periods and
 * discount values.
 */
package com.ems.actions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.service.EventService;
import com.ems.service.OfferService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;

public class AdminOfferManagementAction {
	private final OfferService offerService;
	private final EventService eventService;
	private final Scanner scanner;

	public AdminOfferManagementAction(Scanner scanner) {
		this.scanner=scanner;
		this.offerService = ApplicationUtil.offerService();
		this.eventService = ApplicationUtil.eventService();
	}
	public List<Offer> getAllOffers() throws DataAccessException {
		return offerService.getAllOffers();
	}

	public boolean createOffer(int eventId, String code, int discount, LocalDateTime from, LocalDateTime to)
			throws DataAccessException {
		return offerService.createOffer(eventId, code, discount, from, to);
	}

	public void toggleOfferStatus(int offerId, LocalDateTime newValidTo) throws DataAccessException {
		offerService.toggleOfferStatus(offerId, newValidTo);
	}

	public Map<String, Integer> getOfferUsageReport() throws DataAccessException {
		return offerService.getOfferUsageReport();
	}

	// ===================== VIEW OFFERS=====================
	public void viewAllOffers() {
		try {
			List<Offer> offers = getAllOffers();

			if (offers.isEmpty()) {
				System.out.println("No offers found.");
			} else {
				AdminMenuHelper.printOffers(offers);
			}
		} catch (DataAccessException e) {
			System.out.println("Error viewing offers: " + e.getMessage());
		}
	}

	// ===================== CREATE OFFER=====================

	public void createOffer() {
		try {
			List<Event> events = eventService.listAvailableEvents();

			if (events.isEmpty()) {
				System.out.println("No events available");
				return;
			}

			MenuHelper.printEventSummaries(events);

			int eChoice = InputValidationUtil.readInt(scanner,
					"Select event (1-" + events.size() + "): ");

			while (eChoice < 1 || eChoice > events.size()) {
				eChoice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
			}

			Event event = events.get(eChoice - 1);

			String code = InputValidationUtil.readNonEmptyString(scanner, "Enter the offer code: ");

			int discount = InputValidationUtil.readInt(scanner, "Enter the discount percentage: ");
			while (discount < 0 || discount > 100) {
				discount = InputValidationUtil.readInt(scanner,
						"Enter the discount percentage (1 - 100): ");
			}

			LocalDateTime from = null;

			while (from == null) {
				String input = InputValidationUtil.readString(scanner,
						"Enter the valid from (dd-MM-yyyy HH:mm): ");

				from = DateTimeUtil.parseLocalDateTime(input);

				if (from == null
						|| DateTimeUtil.toUtcInstant(from).isBefore(DateTimeUtil.nowUtc())
						|| DateTimeUtil.toUtcInstant(from).isAfter(event.getStartDateTime())) {

					System.out.println("Invalid 'from' date time.");
					from = null;
				}
			}

			LocalDateTime to = null;

			while (to == null) {
				String input = InputValidationUtil.readString(scanner,
						"Enter the valid to (dd-MM-yyyy HH:mm): ");

				to = DateTimeUtil.parseLocalDateTime(input);

				if (to == null
						|| DateTimeUtil.toUtcInstant(to).isBefore(DateTimeUtil.nowUtc())
						|| to.isBefore(from)
						|| DateTimeUtil.toUtcInstant(to).isAfter(event.getStartDateTime())) {

					System.out.println("Invalid 'to' date time.");
					to = null;
				}
			}

			boolean isCreated = createOffer(
					event.getEventId(),
					code,
					discount,
					from,
					to);

			System.out.println(isCreated
					? "Offer created successfully. Offer: " + code
					: "Offer creation failed");

		} catch (DataAccessException e) {
			System.out.println("Error creating offer: " + e.getMessage());
		}
	}

	// =====================CHANGE STATUS =====================
	public void changeOfferStatus() {
		try {
			System.out.println("\n1. Activate offer\n2. Deactivate offer\n3. Back\n>");

			int option = InputValidationUtil.readInt(scanner, "");

			if (option == 3) return;

			List<Offer> offers = getAllOffers();

			if (offers.isEmpty()) {
				System.out.println("No offers found.");
				return;
			}

			List<Offer> filtered;

			if (option == 1) {
				filtered = AdminMenuHelper.filterExpiredOffers(offers);
			} else if (option == 2) {
				filtered = AdminMenuHelper.filterActiveOffers(offers);
			} else {
				System.out.println("Invalid option.");
				return;
			}

			if (filtered.isEmpty()) {
				System.out.println("No applicable offers found");
				return;
			}

			AdminMenuHelper.printOffers(filtered);

			int choice = InputValidationUtil.readInt(scanner,
					"Select offer (1-" + filtered.size() + "): ");

			while (choice < 1 || choice > filtered.size()) {
				choice = InputValidationUtil.readInt(scanner, "Enter valid choice: ");
			}

			Offer selectedOffer = filtered.get(choice - 1);

			LocalDateTime newValidTo;

			if (option == 1) {
				String input = InputValidationUtil.readString(scanner,
						"Activate until (dd-MM-yyyy HH:mm): ");
				newValidTo = DateTimeUtil.parseLocalDateTime(input);
			} else {
				newValidTo = DateTimeUtil.toLocalDateTime(DateTimeUtil.nowUtc());
			}

			Event event = eventService.getEventById(selectedOffer.getEventId());

			if (event == null) {
				System.out.println("No event found for the offer!");
				return;
			}

			if (DateTimeUtil.toUtcInstant(newValidTo).isAfter(event.getStartDateTime())) {
				System.out.println("Offer validity must end before the event starts.");
				return;
			}

			char confirm = InputValidationUtil.readChar(scanner,
					"Are you sure you want to update offer status (Y/N): ");

			if (confirm == 'Y' || confirm == 'y') {
				toggleOfferStatus(selectedOffer.getOfferId(), newValidTo);
				System.out.println(option == 1
						? "Offer activated successfully."
						: "Offer deactivated successfully.");
			} else {
				System.out.println("Process aborted!");
			}

		} catch (DataAccessException e) {
			System.out.println("Error changing offer status: " + e.getMessage());
		}
	}

	// ===================== USAGE REPORT =====================
	public void viewOfferUsageReport() {
		try {
			Map<String, Integer> report = getOfferUsageReport();
			AdminMenuHelper.printOfferUsageReport(report);
		} catch (DataAccessException e) {
			System.out.println("Error viewing offer usage report: " + e.getMessage());
		}
	}

}
