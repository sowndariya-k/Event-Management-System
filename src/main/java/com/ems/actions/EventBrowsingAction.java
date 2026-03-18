package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.service.impl.EventServiceImpl;

public class EventBrowsingAction {

    private EventService eventService;

    public EventBrowsingAction() {
        eventService = new EventServiceImpl();
    }

    public void execute() {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\nBrowse Events");
            System.out.println("1. View All Events");
            System.out.println("2. View Event Details");
            System.out.println("3. View Ticket Options");
            System.out.println("4. Register for Event");
            System.out.println("5. Exit to User Menu");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {

            // 1 View All Events
            case 1:
                List<Event> events = eventService.viewEvents();
                if (events.isEmpty()) {
                    System.out.println("No events found.");
                } else {
                    for (Event e : events) {
                        System.out.println("\n==============================================");
                        System.out.println("Event ID   : " + e.getEventId());
                        System.out.println("Title      : " + e.getTitle());
                        System.out.println("Start Date : " + e.getStartDateTime());
                        System.out.println("End Date   : " + e.getEndDateTime());
                        System.out.println("Venue ID   : " + e.getVenueId());
                        System.out.println("Capacity   : " + e.getCapacity());
                        System.out.println("Status     : " + e.getStatus());
                        System.out.println("==============================================");
                    }
                }
                break;

            // 2 View Event Details
            case 2:
                System.out.print("Enter Event ID: ");
                int eventId = sc.nextInt();
                Event event = eventService.viewEventDetails(eventId);
                if (event != null) {
                    System.out.println("\n==============================================");
                    System.out.println("Title        : " + event.getTitle());
                    System.out.println("Description  : " + event.getDescription());
                    System.out.println("Start Date   : " + event.getStartDateTime());
                    System.out.println("End Date     : " + event.getEndDateTime());
                    System.out.println("Venue ID     : " + event.getVenueId());
                    System.out.println("Category ID  : " + event.getCategoryId());
                    System.out.println("Capacity     : " + event.getCapacity());
                    System.out.println("Status       : " + event.getStatus());
                    System.out.println("==============================================");
                } else {
                    System.out.println("Event not found.");
                }
                break;

            // 3 View Ticket Options
            case 3:
                System.out.print("Enter Event ID: ");
                int eId = sc.nextInt();
                List<Ticket> tickets = eventService.viewTicketOptions(eId);
                if (tickets.isEmpty()) {
                    System.out.println("No tickets found for this event.");
                } else {
                    System.out.println("\n==============================================");
                    for (Ticket t : tickets) {
                        System.out.println("Ticket ID        : " + t.getTicketId());
                        System.out.println("Ticket Type      : " + t.getTicketType());
                        System.out.println("Price            : ₹" + t.getPrice());
                        System.out.println("Total Quantity   : " + t.getTotalQuantity());
                        System.out.println("Available        : " + t.getAvailableQuantity());
                        System.out.println("----------------------------------------------");
                    }
                    System.out.println("==============================================");
                }
                break;

            // 4 Register for Event
            case 4:
                System.out.println("Register for Event - Coming Soon.");
                // TODO: call RegistrationAction here once implemented
                break;

            // 5 Exit to User Menu
            case 5:
                System.out.println("Returning to User Menu...");
                return;

            default:
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}