/*
 * Author : Sowndariya
 * EventBrowsingAction allows users and guests to browse
 * the list of published events, view event details, and
 * navigate through available events using console menus.
 */

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
 * Used for listing events, viewing details, and ticket options.
 */
public class EventBrowsingAction {

    private final EventService eventService;
	private Scanner scanner;

    public EventBrowsingAction(Scanner scanner) {
    	this.scanner = scanner;
        this.eventService = ApplicationUtil.eventService();
    }

    /**
     * Retrieves all events that are currently available.
     *
     * @return list of available events
     */
    public List<Event> getAllAvailableEvents() throws DataAccessException {
        return eventService.listAvailableEvents();
    }

    /**
     * Retrieves ticket types for a given event.
     *
     * @param eventId the ID of the event
     * @return list of ticket types for the event
     */
    public List<Ticket> getTicketsForEvent(int eventId) throws DataAccessException {
        return eventService.getTicketTypes(eventId);
    }

    /**
     * Displays a summarized list of all available events.
     */
    public void printAllAvailableEvents() {
        try {
            List<Event> filteredEvents = getAllAvailableEvents();

            if (filteredEvents == null || filteredEvents.isEmpty()) {
                System.out.println("There are no available events!");
                return;
            }

            MenuHelper.printEventSummaries(filteredEvents);
        } catch (DataAccessException e) {
            System.out.println("Error listing events: " + e.getMessage());
        }
    }

    /**
     * Displays detailed information for a selected event.
     * Allows the user to choose an event from the available list.
     */
    public void viewEventDetails() {
        try {
            List<Event> events = getAllAvailableEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("No events available at the moment.");
                return;
            }

             MenuHelper.printEventSummaries(events);

            int choice = MenuHelper.selectFromList(
                    scanner, events.size(),
                    "Select an event number");

            Event selectedEvent = events.get(choice - 1);
            MenuHelper.printEventDetails(selectedEvent);
        } catch (DataAccessException e) {
            System.out.println("Error viewing event details: " + e.getMessage());
        }
    }

    /**
     * Displays ticket options for a selected event.
     * Allows the user to view available ticket types.
     */
    public void viewTicketOptions() {
        try {
            List<Event> events = getAllAvailableEvents();

            if (events == null || events.isEmpty()) {
                System.out.println("No events available at the moment.");
                return;
            }

           MenuHelper.printEventSummaries(events);

            int choice = MenuHelper.selectFromList(
                    scanner, events.size(),
                    "Select an event number");

            Event selectedEvent = events.get(choice - 1);
            int eventId = selectedEvent.getEventId();

            List<Ticket> tickets = getTicketsForEvent(eventId);

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