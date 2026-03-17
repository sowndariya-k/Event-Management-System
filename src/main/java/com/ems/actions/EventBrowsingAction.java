package com.ems.actions;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.service.impl.EventServiceImpl;

public class EventBrowsingAction {

    private EventService eventService;
    private Scanner scanner;
    
    // DateTime formatter for nice display
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
            .withZone(java.time.ZoneId.systemDefault());

    public EventBrowsingAction() {
        this.eventService = new EventServiceImpl();
        this.scanner = new Scanner(System.in);
    }

    /**
     * View all available events
     */
    public void viewAvailableEvents() {
        System.out.println("\n--- Available Events ---");
        try {
            List<Event> events = eventService.getAvailableEvents();
            
            if (events.isEmpty()) {
                System.out.println("No events are currently scheduled.");
                return;
            }

            System.out.printf("%-5s | %-30s | %-20s | %-20s\n", "ID", "Title", "Start Time", "End Time");
            System.out.println("------------------------------------------------------------------------------------");
            
            for (Event event : events) {
                String startTimeStr = event.getStartDateTime() != null ? FORMATTER.format(event.getStartDateTime()) : "N/A";
                String endTimeStr = event.getEndDateTime() != null ? FORMATTER.format(event.getEndDateTime()) : "N/A";
                
                // Truncate title if too long
                String shortTitle = event.getTitle().length() > 28 ? event.getTitle().substring(0, 25) + "..." : event.getTitle();
                
                System.out.printf("%-5d | %-30s | %-20s | %-20s\n", 
                        event.getEventId(), shortTitle, startTimeStr, endTimeStr);
            }
            System.out.println("------------------------------------------------------------------------------------");

        } catch (DataAccessException e) {
            System.out.println("Error retrieving events: " + e.getMessage());
        }
    }

    /**
     * View details for a specific event based on user input
     */
    public void viewEventDetails() {
        System.out.print("\nEnter Event ID to view details: ");
        int eventId = getIntInput();

        if (eventId <= 0) {
            System.out.println("Invalid Event ID.");
            return;
        }

        try {
            Event event = eventService.getEventDetails(eventId);
            if (event == null) {
                System.out.println("Event not found with ID: " + eventId);
                return;
            }

            System.out.println("\n--- Event Details ---");
            System.out.println("ID:          " + event.getEventId());
            System.out.println("Title:       " + event.getTitle());
            System.out.println("Description: " + event.getDescription());
            System.out.println("Status:      " + event.getStatus());
            System.out.println("Capacity:    " + event.getCapacity());
            
            if (event.getStartDateTime() != null) {
                System.out.println("Start Time:  " + FORMATTER.format(event.getStartDateTime()));
            }
            if (event.getEndDateTime() != null) {
                System.out.println("End Time:    " + FORMATTER.format(event.getEndDateTime()));
            }
            System.out.println("---------------------");

        } catch (DataAccessException e) {
            System.out.println("Error retrieving event details: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    /**
     * View ticket options for an event based on user input
     */
    public void viewTicketOptions() {
        System.out.print("\nEnter Event ID to view tickets: ");
        int eventId = getIntInput();

        if (eventId <= 0) {
            System.out.println("Invalid Event ID.");
            return;
        }

        try {
            List<Ticket> tickets = eventService.getTicketOptions(eventId);
            
            if (tickets.isEmpty()) {
                System.out.println("No ticket options available for Event ID: " + eventId);
                return;
            }

            System.out.println("\n--- Ticket Options ---");
            System.out.printf("%-5s | %-20s | %-10s | %-15s\n", "ID", "Type", "Price", "Available");
            System.out.println("----------------------------------------------------------");
            
            for (Ticket t : tickets) {
                System.out.printf("%-5d | %-20s | RS %-7.2f | %d / %d\n", 
                        t.getTicketId(), t.getTicketType(), t.getPrice(), 
                        t.getAvailableQuantity(), t.getTotalQuantity());
            }
            System.out.println("----------------------------------------------------------");

        } catch (DataAccessException e) {
            System.out.println("Error retrieving tickets: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    /**
     * Helper method to safely get integer input
     */
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
