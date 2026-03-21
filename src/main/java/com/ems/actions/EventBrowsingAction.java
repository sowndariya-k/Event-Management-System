package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.util.DateTimeUtil;

public class EventBrowsingAction {

    private final EventService eventService;
    private final Scanner scanner;

    // inject scanner from UserMenu
    public EventBrowsingAction(Scanner scanner, EventService eventService) {
        this.scanner = scanner;
        this.eventService = eventService;
    }

    public void printAllAvailableEvents() {

        List<Event> events = eventService.viewEvents();

        if (events == null || events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        System.out.println("\nEVENT LIST");
        System.out.println("=========================================================================================================");
        System.out.printf("%-6s %-40s %-25s %-22s %-10s%n",
                "ID", "TITLE", "CATEGORY", "START DATE", "TICKETS");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (Event e : events) {

            String category = "Unknown";
            int available = 0;

            try {
            	Category cat = eventService.getCategory(e.getCategoryId());
            	if (cat != null) {
            	    category = cat.getName();
            	}

                available = eventService.getAvailableTickets(e.getEventId());

            } catch (DataAccessException ex) {
                System.out.println("Error fetching event extra data");
            }

            System.out.printf("%-6d %-40s %-25s %-22s %-10d%n",
                    e.getEventId(),
                    e.getTitle(),
                    category,
                    DateTimeUtil.formatForDisplay(e.getStartDateTime()),
                    available);
        }
    }

    // 2. View event details
    public void viewEventDetails() throws DataAccessException {

        System.out.print("Enter Event ID: ");
        int eventId = scanner.nextInt();

        Event event = eventService.getEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        String category = "Unknown";
        Category category1 = eventService.getCategory(event.getCategoryId());
        if (category1 != null) {
            category = category1.getName();
        }
        String venueName = eventService.getVenueName(event.getVenueId());
        String venueAddress = eventService.getVenueAddress(event.getVenueId());
        int totalAvailable = eventService.getAvailableTickets(event.getEventId());
        List<Ticket> tickets = eventService.getTicketTypes(event.getEventId());

        System.out.println("\n==============================================");
        System.out.println("Title           : " + event.getTitle());
        System.out.println("Description     : " + (event.getDescription() != null ? event.getDescription() : "-"));
        System.out.println("Category        : " + category);
        System.out.println("Start           : " + DateTimeUtil.formatForDisplay(event.getStartDateTime()));
        System.out.println("End             : " + DateTimeUtil.formatForDisplay(event.getEndDateTime()));
        System.out.println("Total Tickets   : " + totalAvailable);

        System.out.println("\nTicket Types");
        System.out.println("----------------------------------------------");
        for (Ticket ticket : tickets) {
            System.out.println("• " + ticket.getTicketType()
                    + " | Price: ₹" + ticket.getPrice()
                    + " | Available: " + ticket.getAvailableQuantity());
        }

        System.out.println("\nVenue");
        System.out.println("----------------------------------------------");
        System.out.println("Name            : " + venueName);
        System.out.println("Address         : " + venueAddress);
        System.out.println("==============================================");
    }

    // 3. View ticket options
    public void viewTicketOptions() throws DataAccessException {

        System.out.print("Enter Event ID: ");
        int eventId = scanner.nextInt();

        List<Ticket> tickets = eventService.getTicketTypes(eventId);

        if (tickets.isEmpty()) {
            System.out.println("No tickets available.");
            return;
        }

        System.out.println("\n===== TICKETS =====");
        for (Ticket t : tickets) {
            System.out.println("Ticket ID   : " + t.getTicketId());
            System.out.println("Type        : " + t.getTicketType());
            System.out.println("Price       : ₹" + t.getPrice());
            System.out.println("Available   : " + t.getAvailableQuantity());
            System.out.println("----------------------");
        }
    }
}