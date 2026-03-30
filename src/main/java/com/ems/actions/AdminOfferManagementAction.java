package com.ems.actions;

import java.time.Instant;
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

	public void viewAllOffers() {
		try {
			List<Offer> offers = getAllOffers();

			if (offers.isEmpty()) {
				System.out.println("No offers found.");
				return;
			}

			printOffers(offers);

		} catch (DataAccessException e) {
			System.out.println("Error viewing offers: " + e.getMessage());
		}
	}


	public void createOffer() {
		try {
			List<Event> events = eventService.listAvailableEvents();

			if (events.isEmpty()) {
				System.out.println("No events available");
				return;
			}

			printEvents(events);

			int eChoice = InputValidationUtil.readInt(scanner,
					"Select event (1-" + events.size() + "): ");

			while (eChoice < 1 || eChoice > events.size()) {
				eChoice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
			}

			Event event = events.get(eChoice - 1);

			String code = InputValidationUtil.readNonEmptyString(scanner, "Enter the offer code: ");

			int discount = InputValidationUtil.readInt(scanner, "Enter the discount percentage: ");
			while (discount < 1 || discount > 100) {
				discount = InputValidationUtil.readInt(scanner, "Enter valid discount (1-100): ");
			}

			LocalDateTime from = null;
			while (from == null) {
				String input = InputValidationUtil.readString(scanner,
						"Enter the valid from (dd-MM-yyyy HH:mm): ");

				from = DateTimeUtil.parseLocalDateTime(input);

				if (from == null
						|| DateTimeUtil.toUtcInstant(from).isBefore(DateTimeUtil.nowUtc())
						|| DateTimeUtil.toUtcInstant(from).isAfter(event.getStartDateTime())) {

					System.out.println("Invalid 'from' date time. Please try again.");
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

					System.out.println("Invalid 'to' date time. Please try again.");
					to = null;
				}
			}

			boolean isCreated = createOffer(event.getEventId(), code, discount, from, to);

			if (isCreated) {
				System.out.println("Offer created successfully. Offer: " + code);
			} else {
				System.out.println("Offer creation failed");
			}

		} catch (DataAccessException e) {
			System.out.println("Error creating offer: " + e.getMessage());
		}
	}

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

			printOffers(offers);

			int choice = InputValidationUtil.readInt(scanner,
					"Select offer (1-" + offers.size() + "): ");

			while (choice < 1 || choice > offers.size()) {
				choice = InputValidationUtil.readInt(scanner, "Enter valid choice: ");
			}

			Offer selectedOffer = offers.get(choice - 1);

			LocalDateTime newValidTo;

			if (option == 1) {
				String input = InputValidationUtil.readString(scanner,
						"Activate until (dd-MM-yyyy HH:mm): ");
				newValidTo = DateTimeUtil.parseLocalDateTime(input);
			} else {
				newValidTo = DateTimeUtil.toLocalDateTime(DateTimeUtil.nowUtc());
			}

			char confirm = InputValidationUtil.readChar(scanner,
					"Are you sure you want to update offer status (Y/N): ");

			if (confirm == 'Y' || confirm == 'y') {
				toggleOfferStatus(selectedOffer.getOfferId(), newValidTo);
				System.out.println(option == 1 ? "Offer activated successfully." : "Offer deactivated successfully.");
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
			printOfferUsage(report);
		} catch (DataAccessException e) {
			System.out.println("Error viewing report: " + e.getMessage());
		}
	}


	// ===================== DISPLAY METHODS =====================


	private static final int TABLE_WIDTH = 110;
	private static final String SEPARATOR = "=".repeat(TABLE_WIDTH);
	private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

		private void printEvents(List<Event> events) {
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
							categoryName,
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

		private void printOffers(List<Offer> offers) {
		    System.out.println("\nOFFERS REPORT");
		    System.out.println("==============================================================================================================");
		    System.out.printf("%-5s %-10s %-15s %-12s %-20s %-20s %-10s%n",
		            "NO", "OFFER ID", "CODE", "DISCOUNT", "VALID FROM", "VALID TO", "STATUS");
		    System.out.println("--------------------------------------------------------------------------------------------------------------");

		    int i = 1;

		    for (Offer o : offers) {
		        String status;
		        Instant now = Instant.now();

		        if (o.getValidTo() != null && o.getValidTo().isBefore(now)) {
		            status = "EXPIRED";
		        } else if (o.getValidFrom() != null && o.getValidFrom().isAfter(now)) {
		            status = "UPCOMING";
		        } else {
		            status = "ACTIVE";
		        }

		        System.out.printf("%-5d %-10d %-15s %-12s %-20s %-20s %-10s%n",
		                i++,
		                o.getOfferId(),
		                o.getCode(),
		                (o.getDiscountPercentage() != null ? o.getDiscountPercentage() : 0) + "%",
		                DateTimeUtil.formatForDisplay(o.getValidFrom()),
		                DateTimeUtil.formatForDisplay(o.getValidTo()),
		                status
		        );
		    }

		    System.out.println("==============================================================================================================");
		}
		private void printOfferUsage(Map<String, Integer> report) {
			System.out.println("\nOFFER USAGE REPORT");
			System.out.println("==============================================================================================================");
			System.out.printf("%-5s %-20s %-15s%n", "NO", "OFFER CODE", "USAGE COUNT");
			System.out.println("--------------------------------------------------------------------------------------------------------------");

			int i = 1;
			for (Map.Entry<String, Integer> entry : report.entrySet()) {
				System.out.printf("%-5d %-20s %-15d%n", i++, entry.getKey(), entry.getValue());
			}

			System.out.println("==============================================================================================================");
		}

		private String truncate(String value, int length) {
			if (value == null) return "";
			return value.length() <= length ? value : value.substring(0, length - 3) + "...";
		}
}
