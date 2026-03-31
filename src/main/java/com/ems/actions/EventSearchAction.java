package com.ems.actions;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

import com.ems.model.Event;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.service.EventService;
import com.ems.util.InputValidationUtil;


/**
 * Action class for event search and filter operations.
 * Delegates all business logic to EventService.
 */
public class EventSearchAction {

    private final EventService eventService;
    private final Scanner scanner;

    public EventSearchAction(EventService eventService, Scanner scanner) {
        this.eventService = eventService;
        this.scanner = scanner;
    }

    /**
     * Handles searching events by category.
     * Displays available categories, validates user input,
     * and prints matching events.
     */
    public void handleSearchByCategory() {
        try {
            List<Category> categories = eventService.getAllCategories();

            if (categories == null || categories.isEmpty()) {
                System.out.println("No categories available");
                return;
            }

            System.out.println("\n===== Available Categories =====");
            for (Category c : categories) {
                System.out.println(c.getCategoryId() + " - " + c.getName());
            }

            int categoryId = InputValidationUtil.readInt(scanner, "Enter Category ID: ");

            List<Event> events = eventService.searchByCategory(categoryId);

            printEvents(events);

        } catch (Exception e) {
            System.out.println("Error while searching by category");
        }
    }

    // ================= DATE =================
    public void handleSearchByDate() {
        try {
            System.out.print("Enter date (yyyy-mm-dd): ");
            String date = scanner.nextLine().trim();

            List<Event> events = eventService.searchByDate(LocalDate.parse(date));

            printEvents(events);

        } catch (Exception e) {
            System.out.println("Invalid date format");
        }
    }

    // ================= DATE RANGE =================
    public void handleSearchByDateRange() {
        try {
            System.out.print("Enter start date (yyyy-mm-dd): ");
            String start = scanner.nextLine().trim();

            System.out.print("Enter end date (yyyy-mm-dd): ");
            String end = scanner.nextLine().trim();

            List<Event> events = eventService.searchByDateRange(
                    LocalDate.parse(start),
                    LocalDate.parse(end)
            );

            printEvents(events);

        } catch (Exception e) {
            System.out.println("Invalid date range input");
        }
    }

    // ================= CITY =================
    public void handleSearchByCity() {

        try {
            System.out.print("Enter Venue ID: ");
            int venueId = Integer.parseInt(scanner.nextLine());

            List<Event> events = eventService.searchByCity(venueId);

            printEvents(events);

        } catch (Exception e) {
            System.out.println("Error while searching by venue");
        }
    }

    // ================= PRICE =================
    public void handleFilterByPrice() {
        try {
            System.out.print("Enter Min Price: ");
            double min = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter Max Price: ");
            double max = Double.parseDouble(scanner.nextLine());

            if (min > max) {
                System.out.println("Min price cannot be greater than max price");
                return;
            }

            List<Event> events = eventService.filterByPrice(min, max);

            if (events == null || events.isEmpty()) {
                System.out.println("No events found in this price range");
                return;
            }

            printEvents(events);

        } catch (NumberFormatException e) {
            System.out.println("Invalid price input. Please enter numbers only.");
        } catch (Exception e) {
            System.out.println("Error while filtering events");
        }
    }

    // ================= COMMON PRINT =================
    private void printEvents(List<Event> events) {
        if (events == null || events.isEmpty()) {
            System.out.println("No events found");
            return;
        }

        System.out.println("\n===== Events Found =====");

        for (Event e : events) {
            System.out.println("ID: " + e.getEventId());
            System.out.println("Title: " + e.getTitle());
            System.out.println("Start: " + e.getStartDateTime());
            try {
                System.out.println("Venue: " + eventService.getVenueName(e.getVenueId()) 
                                   + " (ID: " + e.getVenueId() + ")");
            } catch (DataAccessException ex) {
                System.out.println("Venue info not available");
            }
            System.out.println("-----------------------------------");
        }
    }
}