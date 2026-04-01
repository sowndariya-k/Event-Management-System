package com.ems.actions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.service.EventService;
import com.ems.service.OfferService;
import com.ems.service.OrganizerService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;

public class OrganizerOfferManagementAction {
	private final Scanner scanner;
	private final OfferService offerService;
    private final EventService eventService;
    private final OrganizerService organizerService;

    public OrganizerOfferManagementAction(Scanner scanner) {
    	this.scanner=scanner;
        this.offerService = ApplicationUtil.offerService();
        this.eventService = ApplicationUtil.eventService();
        this.organizerService = ApplicationUtil.organizerService();
    }
    
    public List<Offer> getAllOffers(int userId) throws DataAccessException {

        List<Event> myEvents = organizerService.getOrganizerEvents(userId);

        Set<Integer> eventIds = myEvents.stream()
                .map(Event::getEventId)
                .collect(Collectors.toSet());

        return offerService.getAllOffers()
                .stream()
                .filter(o -> eventIds.contains(o.getEventId()))
                .collect(Collectors.toList());
    }
    
    public boolean createOffer(int eventId, String code, int discount,LocalDateTime from, LocalDateTime to) throws DataAccessException {
    	return offerService.createOffer(eventId, code, discount, from, to);
    }
    
    public void toggleOfferStatus(int offerId, LocalDateTime newValidTo) throws DataAccessException {
        offerService.toggleOfferStatus(offerId, newValidTo);
    }
    
    public Map<String, Integer> getOfferUsageReport(int userId) throws DataAccessException {

        List<Offer> myOffers = getAllOffers(userId);

        Set<String> myCodes = myOffers.stream()
                .map(Offer::getCode)
                .collect(Collectors.toSet());

        return offerService.getOfferUsageReport()
                .entrySet()
                .stream()
                .filter(e -> myCodes.contains(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }


 // -------------------- CREATE OFFER --------------------
    
    public void createOffer(int userId) {
        try {
            List<Event> events = organizerService.getOrganizerEvents(userId);

            if (events.isEmpty()) {
                System.out.println("No events available.");
                return;
            }

            MenuHelper.printEventSummaries(events);

            int eChoice = InputValidationUtil.readInt(
                    scanner,
                    "Select event (1-" + events.size() + "): ");

            while (eChoice < 1 || eChoice > events.size()) {
                eChoice = InputValidationUtil.readInt(
                        scanner,
                        "Enter a valid choice: ");
            }

            Event event = events.get(eChoice - 1);

            String code = InputValidationUtil.readNonEmptyString(
                    scanner,
                    "Enter the offer code: ");

            int discount = InputValidationUtil.readInt(
                    scanner,
                    "Enter the discount percentage: ");

            while (discount < 0 || discount > 100) {
                discount = InputValidationUtil.readInt(
                        scanner,
                        "Enter the discount percentage (1 - 100): ");
            }

            LocalDateTime from = null;

            while (from == null) {

                String input = InputValidationUtil.readString(
                        scanner,
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

                String input = InputValidationUtil.readString(
                        scanner,
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

    // -------------------- VIEW OFFERS --------------------
	public void viewAllOffers(int userId) {
        try {
            List<Offer> offers = getAllOffers(userId);

            if (offers.isEmpty()) {
                System.out.println("No offers found.");
            } else {
                AdminMenuHelper.printOffers(offers);
            }
        } catch (DataAccessException e) {
            System.out.println("Error viewing offers: " + e.getMessage());
        }
    }


	// -------------------- ACTIVATE OFFER --------------------
	public void activateOffer(int userId) {
        try {
            List<Offer> offers = getAllOffers(userId);

            if (offers.isEmpty()) {
                System.out.println("No offers found.");
                return;
            }

            List<Offer> filtered = AdminMenuHelper.filterExpiredOffers(offers);

            if (filtered.isEmpty()) {
                System.out.println("No expired offers available.");
                return;
            }

            AdminMenuHelper.printOffers(filtered);

            int choice = InputValidationUtil.readInt(
                    scanner,
                    "Select offer (1-" + filtered.size() + "): ");

            while (choice < 1 || choice > filtered.size()) {
                choice = InputValidationUtil.readInt(
                        scanner,
                        "Enter valid choice: ");
            }

            Offer selectedOffer = filtered.get(choice - 1);

            Event event = eventService.getEventById(selectedOffer.getEventId());

            if (event == null || event.getOrganizerId() != userId) {
                System.out.println("Unauthorized action.");
                return;
            }

            String dateInput = InputValidationUtil.readString(
                    scanner,
                    "Activate until (dd-MM-yyyy HH:mm): ");

            LocalDateTime newValidTo = DateTimeUtil.parseLocalDateTime(dateInput);

            if (newValidTo == null ||
                    DateTimeUtil.toUtcInstant(newValidTo)
                            .isAfter(event.getStartDateTime())) {

                System.out.println("Invalid validity date.");
                return;
            }

            toggleOfferStatus(selectedOffer.getOfferId(), newValidTo);

            System.out.println("Offer activated successfully.");
        } catch (DataAccessException e) {
            System.out.println("Error activating offer: " + e.getMessage());
        }
    }


	 // -------------------- DEACTIVATE OFFER --------------------

	 public void deactivateOffer(int userId) {
	        try {
	            List<Offer> offers = getAllOffers(userId);

	            if (offers.isEmpty()) {
	                System.out.println("No offers found.");
	                return;
	            }

	            List<Offer> filtered = AdminMenuHelper.filterActiveOffers(offers);

	            if (filtered.isEmpty()) {
	                System.out.println("No active offers available.");
	                return;
	            }

	            AdminMenuHelper.printOffers(filtered);

	            int choice = InputValidationUtil.readInt(
	                    scanner,
	                    "Select offer (1-" + filtered.size() + "): ");

	            while (choice < 1 || choice > filtered.size()) {
	                choice = InputValidationUtil.readInt(
	                        scanner,
	                        "Enter valid choice: ");
	            }

	            Offer selectedOffer = filtered.get(choice - 1);

	            Event event = eventService.getEventById(selectedOffer.getEventId());

	            if (event == null || event.getOrganizerId() != userId) {
	                System.out.println("Unauthorized action.");
	                return;
	            }

	            LocalDateTime now = DateTimeUtil.toLocalDateTime(DateTimeUtil.nowUtc());

	            toggleOfferStatus(selectedOffer.getOfferId(), now);

	            System.out.println("Offer deactivated successfully.");
	        } catch (DataAccessException e) {
	            System.out.println("Error deactivating offer: " + e.getMessage());
	        }
	    }


	
	public void viewOfferUsageReport(int userId) {
		 try {
	            Map<String, Integer> report = getOfferUsageReport(userId);

	            if (report.isEmpty()) {
	                System.out.println("No usage data found.");
	                return;
	            }

	            AdminMenuHelper.printOfferUsageReport(report);
	        } catch (DataAccessException e) {
	            System.out.println("Error viewing usage report: " + e.getMessage());
	        }
	}
	
	// -------------------- Display events --------------------

    private void printEventSummaries(List<Event> events) {

        if (events == null || events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        final int TABLE_WIDTH = 110;
        final String SEPARATOR = "=".repeat(TABLE_WIDTH);
        final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

        System.out.println("\nAVAILABLE EVENTS");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-30s %-20s %-20s %-10s%n",
                "NO", "TITLE", "CATEGORY", "START DATE", "TICKETS");
        System.out.println(SUB_SEPARATOR);

        int index = 1;
        for (Event event : events) {
            try {
                String category = eventService.getCategory(event.getCategoryId()).getName();
                int available = eventService.getAvailableTickets(event.getEventId());

                System.out.printf("%-5d %-30s %-20s %-20s %-10d%n",
                        index++,
                        event.getTitle(),
                        category,
                        DateTimeUtil.formatForDisplay(event.getStartDateTime()),
                        available);

            } catch (DataAccessException e) {
                System.out.printf("%-5d %-30s %-50s%n",
                        index++, event.getTitle(), "[Error fetching details]");
            }
        }

        System.out.println(SEPARATOR);
    }



}
