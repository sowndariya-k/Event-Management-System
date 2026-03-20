package com.ems.actions;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

import com.ems.model.Event;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.service.EventService;
import com.ems.util.InputValidationUtil;

public class EventSearchAction {

    private final EventService eventService;
    private final Scanner scanner;

    public EventSearchAction(EventService eventService, Scanner scanner) {
        this.eventService = eventService;
        this.scanner = scanner;
    }

    public void handleSearchByCategory() {

        try {
            // Step 1: Get all categories
            List<Category> categories = eventService.getAllCategory();

            if (categories == null || categories.isEmpty()) {
                System.out.println("❌ No categories available");
                return;
            }

            // Step 2: Display categories
            System.out.println("\n===== Available Categories =====");
            for (Category c : categories) {
                System.out.println(c.getCategoryId() + " - " + c.getName());
            }

            // Step 3: Take input
            int categoryId = InputValidationUtil.readInt(scanner, "Enter Category ID: ");

            // Step 4: Fetch events
            List<Event> events = eventService.searchBycategory(categoryId);

            // Step 5: Display results
            if (events == null || events.isEmpty()) {
                System.out.println("❌ No events found for this category");
                return;
            }

            System.out.println("\n===== Events Found =====");

            for (Event event : events) {
                System.out.println("ID: " + event.getEventId());
                System.out.println("Title: " + event.getTitle());
                System.out.println("Start: " + event.getStartDateTime());
                System.out.println("-----------------------------------");
            }

        } catch (Exception e) {
            System.out.println("❌ Error while searching by category");
        }
    }

    public void handleSearchByDate() {
        System.out.print("Enter date (yyyy-mm-dd): ");
        String date = scanner.nextLine();

        try {
            List<Event> events = eventService.searchByDate(java.time.LocalDate.parse(date));
            printEvents(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void handleSearchByDateRange() {

        try {
            System.out.print("Enter start date (yyyy-mm-dd): ");
            String start = scanner.nextLine();

            System.out.print("Enter end date (yyyy-mm-dd): ");
            String end = scanner.nextLine();

            List<Event> events = eventService.searchByDateRange(
                    LocalDate.parse(start),
                    LocalDate.parse(end)
            );

            if (events.isEmpty()) {
                System.out.println("❌ No events found in this date range");
                return;
            }

            System.out.println("\n===== Events Found =====");

            for (Event event : events) {
                System.out.println("ID: " + event.getEventId());
                System.out.println("Title: " + event.getTitle());
                System.out.println("Start: " + event.getStartDateTime());
                System.out.println("-----------------------------------");
            }

        } catch (Exception e) {
            System.out.println("❌ Invalid input or error occurred");
        }
    }

    public void handleSearchByCity() {

        try {
            System.out.print("Enter city name: ");
            String city = scanner.nextLine().trim();

            List<Event> events = eventService.searchByCity(city);

            if (events == null || events.isEmpty()) {
                System.out.println("❌ No events found");
                return;
            }

            for (Event event : events) {
                System.out.println(event.getTitle());
            }

        } catch (DataAccessException e) {
            System.out.println("Error fetching events: " + e.getMessage());
        }
    }	

    public void handleFilterByPrice() {
        double min = InputValidationUtil.readInt(scanner, "Enter Min Price: ");
        double max = InputValidationUtil.readInt(scanner, "Enter Max Price: ");

        try {
            List<Event> events = eventService.filterByPrice(min, max);
            printEvents(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printEvents(List<Event> events) {
        for (Event e : events) {
            System.out.println("ID: " + e.getEventId());
            System.out.println("Title: " + e.getTitle());
            System.out.println("------------------------");
        }
    }
}