package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.MenuHelper;
import com.ems.exception.DataAccessException;

/*
 * Handles read-only event browsing operations.
 * Clean version without pagination.
 */
public class EventBrowsingAction {

    private final EventService eventService;
    private final Scanner scanner;

    public EventBrowsingAction(Scanner scanner) {
        this.eventService = ApplicationUtil.eventService();
        this.scanner = scanner;
    }

    public List<Event> getAllAvailableEvents() throws DataAccessException {
        return eventService.listAvailableEvents();
    }

    public List<Ticket> getTicketsForEvent(int eventId) throws DataAccessException {
        return eventService.getTicketTypes(eventId);
    }

    // 1. View all events
    public void printAllAvailableEvents() {
        try {
            List<Event> events = getAllAvailableEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("There are no available events!");
                return;
            }

            MenuHelper.printEventSummaries(events);

        } catch (DataAccessException e) {
            System.out.println("Error listing events: " + e.getMessage());
        }
    }

    // 2. View event details
    public void viewEventDetails() {
        try {
            List<Event> events = getAllAvailableEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("No events available at the moment.");
                return;
            }

            MenuHelper.printEventSummaries(events);

            int choice = MenuHelper.selectFromList(
                    scanner,
                    events.size(),
                    "Select an event number");

            Event selectedEvent = events.get(choice - 1);

            MenuHelper.printEventDetails(selectedEvent);

        } catch (DataAccessException e) {
            System.out.println("Error viewing event details: " + e.getMessage());
        }
    }

    // 3. View ticket options
    public void viewTicketOptions() {
        try {
            List<Event> events = getAllAvailableEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("No events available at the moment.");
                return;
            }

            MenuHelper.printEventSummaries(events);

            int choice = MenuHelper.selectFromList(
                    scanner,
                    events.size(),
                    "Select an event number");

            Event selectedEvent = events.get(choice - 1);

            List<Ticket> tickets = getTicketsForEvent(selectedEvent.getEventId());

            if (tickets == null || tickets.isEmpty()) {
                System.out.println("No ticket types available for this event.");
                return;
            }

            MenuHelper.printTicketSummaries(tickets);

        } catch (DataAccessException e) {
            System.out.println("Error viewing ticket options: " + e.getMessage());
        }
    }
}