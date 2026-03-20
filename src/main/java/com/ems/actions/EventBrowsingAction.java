package com.ems.actions;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;

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

        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        // Header for table
        System.out.printf("%-10s %-40s %-25s%n", "Event ID", "Title", "Date & Time");
        System.out.println("--------------------------------------------------------------------------");

        // Formatter for Instant
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                       .withZone(ZoneId.systemDefault());

        for (Event e : events) {
            String formattedDate = formatter.format(e.getStartDateTime());

            System.out.printf("%-10d %-40s %-25s%n",
                    e.getEventId(),
                    e.getTitle(),
                    formattedDate
            );
        }

        System.out.println("--------------------------------------------------------------------------");
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

        System.out.println("\n===== EVENT DETAILS =====");
        System.out.println("Title       : " + event.getTitle());
        System.out.println("Description : " + event.getDescription());
        System.out.println("Start       : " + event.getStartDateTime());
        System.out.println("End         : " + event.getEndDateTime());
        System.out.println("Venue ID    : " + event.getVenueId());
        System.out.println("Category ID : " + event.getCategoryId());
        System.out.println("==========================");
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