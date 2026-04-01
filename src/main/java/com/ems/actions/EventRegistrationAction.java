package com.ems.actions;

import java.time.Instant;
import java.util.List;
import java.util.Scanner;

import com.ems.enums.PaymentMethod;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.model.Ticket;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;
import com.ems.service.OfferService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.exception.DataAccessException;

/*
 * Handles event registration and cancellation workflows.
 * Responsible for user interaction and validation only.
 */
public class EventRegistrationAction {

    private final EventService eventService;
    private final OfferService offerService;
    private final Scanner scanner;

    public EventRegistrationAction(Scanner scanner) {
        this.eventService = ApplicationUtil.eventService();
        this.offerService = ApplicationUtil.offerService();
        this.scanner = scanner;
    }
    
    /**
     * Initiates registration flow for available events.
     *
     * @param userId the ID of the user
     */

    public void registerForAvailableEvent(int userId) {
        try {
            List<Event> events = eventService.listAvailableEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("No events available for registration at the moment.");
                return;
            }

            performRegistration(userId, events);

        } catch (DataAccessException e) {
            System.out.println("Error during registration process: " + e.getMessage());
        }
    }

    /**
     * Performs the core event registration workflow.
     *
     * @param userId the ID of the user
     * @param events list of available events
     */
    private void performRegistration(int userId, List<Event> events) throws DataAccessException {

        MenuHelper.printEventSummaries(events);

        int eventChoice = MenuHelper.selectFromList(scanner, events.size(), "Select an event");

        Event selectedEvent = events.get(eventChoice - 1);
        int eventId = selectedEvent.getEventId();

        /**
         * Retrieves ticket types and validates availability.
         */
        List<Ticket> tickets = eventService.getTicketTypes(eventId);

        if (tickets == null || tickets.isEmpty()) {
            System.out.println("No ticket types available for this event.");
            return;
        }

        MenuHelper.printTicketSummaries(tickets);

        int ticketChoice = MenuHelper.selectFromList(scanner, tickets.size(), "Select a ticket type");

        Ticket selectedTicket = tickets.get(ticketChoice - 1);
        int ticketId = selectedTicket.getTicketId();

        /**
         * Reads ticket quantity with validation.
         */
        int quantity = readQuantity(selectedTicket.getAvailableQuantity());

        /**
         * Displays payment options and retrieves user selection.
         */
        PaymentMethod selectedMethod = selectPaymentMethod();

        /**
         * Calculates and displays total amount.
         */
        double totalAmount = quantity * selectedTicket.getPrice();
        System.out.println("\nTotal amount: ₹" + String.format("%.2f", totalAmount));

        /**
         * Reads optional offer code.
         */
        Offer offer = null;
        String offerCode = "";

        while (true) {

            offerCode = InputValidationUtil.readString(
                    scanner,
                    "\nEnter an offer code or press Enter to continue without one: ");

         // User skipped entering an offer code
            if (offerCode == null || offerCode.trim().isEmpty()) {
                break;
            }

            offer = offerService.getOffer(eventId, offerCode.trim().toUpperCase());
            Instant now = DateTimeUtil.nowUtc();

            // Offer does not exist
            if (offer == null) {
                System.out.println("\nSorry, that offer code does not exist.");
            }
            
            // Offer exists but is expired or not yet active
            
            else if (now.isBefore(offer.getValidFrom()) || now.isAfter(offer.getValidTo())) {
                System.out.println("\nSorry, this offer code is no longer valid.");
            }
            
            // Offer already used by the user
            
            else if (offerService.hasUserUsedOfferCode(userId, offer.getOfferId())) {
                System.out.println("\nYou have already used this offer code.");
            } 
            
            // Offer is valid and applicable
            
            else {
                double initialPrice = selectedTicket.getPrice() * quantity;
                double discount = initialPrice * (offer.getDiscountPercentage() / 100.0);
                double finalPrice = initialPrice - discount;

                System.out.println("\nDiscount applied: " + offer.getDiscountPercentage() + "%");
                System.out.println("Final amount: ₹" + finalPrice);
                break;
            }

         // Ask user whether to retry or continue
            String choice = InputValidationUtil.readString(
                    scanner,
                    "\n[R] Retry offer\n[C] Continue\nChoice: ");

            if (choice.equalsIgnoreCase("C")) {
                offer = null;
                offerCode = "";
                break;
            }
        }

        char confirm = InputValidationUtil.readChar(
                scanner,
                "\nAre you sure you want to make payment [y/n]: ");

        if (confirm == 'y' || confirm == 'Y') {

        	/**
             * Attempts event registration.
             */
            boolean success = eventService.registerForEvent(
                    userId,
                    eventId,
                    ticketId,
                    quantity,
                    selectedTicket.getPrice(),
                    selectedMethod,
                    offerCode);

            if (success) {
                System.out.println("\nRegistration successful! Your tickets are confirmed.");
                System.out.println("Check 'My Registrations' for booking details.");
            } else {
                System.out.println("\nRegistration failed.");
            }

        }
        
        else {
            System.out.println("\nAction cancelled.");
        }
    }

    /**
     * Cancels an existing event registration for the user.
     *
     * @param userId the ID of the user
     */
    public void cancelRegistration(int userId) {
        try {
            List<UserEventRegistration> upcoming = eventService.viewUpcomingEvents(userId);

            if (upcoming == null || upcoming.isEmpty()) {
                System.out.println("You have no upcoming events.");
                return;
            }

            // ✅ FIX: No pagination
            MenuHelper.printEventsList(upcoming);

            int choice = MenuHelper.selectFromList(scanner, upcoming.size(), "Select a registration");

            UserEventRegistration registration = upcoming.get(choice - 1);

            char confirm = InputValidationUtil.readChar(
                    scanner,
                    "Are you sure you want to cancel? (Y/N): ");

            if (confirm == 'Y' || confirm == 'y') {

                boolean success = eventService.cancelRegistration(
                        userId,
                        registration.getRegistrationId());

                if (success) {
                    System.out.println("Registration cancelled successfully.");
                } else {
                    System.out.println("Cancellation failed.");
                }

            } else {
                System.out.println("Cancellation aborted.");
            }

        } catch (DataAccessException e) {
            System.out.println("Error cancelling registration: " + e.getMessage());
        }
    }
    
    /* ===================== HELPER METHODS ===================== */

    /**
     * Reads ticket quantity with validation against available tickets.
     *
     * @param maxAvailable maximum available ticket quantity
     * @return validated ticket quantity
     */

    private int readQuantity(int maxAvailable) {

        int quantity;

        while (true) {
            quantity = InputValidationUtil.readInt(
                    scanner,
                    "\nEnter number of tickets (1-" + maxAvailable + "): ");

            if (quantity >= 1 && quantity <= maxAvailable) {
                return quantity;
            }

            System.out.println("Invalid quantity.");
        }
    }
    
    /**
     * Displays available payment methods and retrieves user selection.
     *
     * @return selected payment method
     */

    private PaymentMethod selectPaymentMethod() {

        System.out.println("\nAvailable payment methods:");
        PaymentMethod[] methods = PaymentMethod.values();

        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i].name().replace("_", " "));
        }

        int choice = MenuHelper.selectFromList(scanner, methods.length, "Select payment method");

        return methods[choice - 1];
    }
}