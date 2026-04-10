/*
 * Author : Sowndariya
 * EventSearchAction enables users to search for events
 * by various filters such as category, date range, location,
 * and keyword, and displays matching event results.
 */

package com.ems.actions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.exception.DataAccessException;

/**
 * Action class for event search and filter operations.
 * Delegates all business logic to EventService.
 */
public class EventSearchAction {
	
    private final EventService eventService;
    private Scanner scanner;

    public EventSearchAction(Scanner scanner) {
    	this.scanner = scanner;
        this.eventService = ApplicationUtil.eventService();
    }

    /**
     * Handles searching events by category.
     * Displays available categories, validates user input,
     * and prints matching events.
     */
    public void handleSearchByCategory() {
        try {
            List<Category> categories = getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("No categories available.");
                return;
            }

            MenuHelper.displayCategories(categories);

            int choice = InputValidationUtil.readInt(scanner, "Select category: ");

            if (choice < 1 || choice > categories.size()) {
                System.out.println("Invalid category selection.");
                return;
            }

            Category selected = categories.get(choice - 1);
            List<Event> events = searchByCategory(selected.getCategoryId());

            if (events.isEmpty()) {
                System.out.println(
                        "No events found in category: " + selected.getName());
                return;
            }

            MenuHelper.printEventSummaries(events);
        } catch (DataAccessException e) {
            System.out.println("Error searching by category: " + e.getMessage());
        }
    }

    /**
     * Handles searching events by a specific date.
     * Prompts user for date input and displays matching events.
     */
    public void handleSearchByDate() {
        try {
            String input = InputValidationUtil.readNonEmptyString(scanner,
                    "Enter date (dd-mm-yyyy): ");

            LocalDate date = DateTimeUtil.parseLocalDate(input);
            if (date == null) {
                System.out.println("Invalid date format.");
                return;
            }

            List<Event> events = searchByDate(date);

            if (events.isEmpty()) {
                System.out.println("No events found on " + date);
                return;
            }

            MenuHelper.printEventSummaries(events);
        } catch (DataAccessException e) {
            System.out.println("Error searching by date: " + e.getMessage());
        }
    }

    /**
     * Handles searching events within a date range.
     * Validates date order before performing search.
     */
    public void handleSearchByDateRange() {
        try {
            String startInput = InputValidationUtil.readNonEmptyString(scanner,
                    "Enter start date (dd-mm-yyyy): ");

            LocalDate start = DateTimeUtil.parseLocalDate(startInput);
            if (start == null)
                return;

            String endInput = InputValidationUtil.readNonEmptyString(scanner,
                    "Enter end date (dd-mm-yyyy): ");

            LocalDate end = DateTimeUtil.parseLocalDate(endInput);
            if (end == null)
                return;

            if (start.isAfter(end)) {
                System.out.println("Start date cannot be after end date.");
                return;
            }

            List<Event> events = searchByDateRange(start, end);

            if (events.isEmpty()) {
                System.out.println("No events found in selected range.");
                return;
            }

           MenuHelper.printEventSummaries(events);
        } catch (DataAccessException e) {
            System.out.println("Error searching by date range: " + e.getMessage());
        }
    }

    /**
     * Handles searching events by city.
     * Displays available cities and validates user selection.
     */
    public void handleSearchByCity() {
        try {
            List<Venue> venues = eventService.getAllVenues();

            if (venues == null || venues.isEmpty()) {
                System.out.println("No venues available.");
                return;
            }

            MenuHelper.displayVenues(venues);

            int choice = MenuHelper.selectFromList(
                    scanner, venues.size(),
                    "Select venue");

            Venue selected = venues.get(choice - 1);

            List<Event> events = searchByCity(selected.getVenueId());

            if (events.isEmpty()) {
                System.out.println("No events found at " + selected.getName());
                return;
            }

            MenuHelper.printEventSummaries(events);
        } catch (DataAccessException e) {
            System.out.println("Error searching by city: " + e.getMessage());
        }
    }

    /**
     * Handles filtering events by price range.
     * Validates price limits before searching.
     */
    public void handleFilterByPrice() {
        try {
            double min = InputValidationUtil.readDouble(scanner, "Enter minimum price: ");
            double max = InputValidationUtil.readDouble(scanner, "Enter maximum price: ");

            if (min < 0 || min > max) {
                System.out.println("Invalid price range.");
                return;
            }

            List<Event> events = filterByPrice(min, max);

            if (events.isEmpty()) {
                System.out.println("No events found in price range.");
                return;
            }

            MenuHelper.printEventSummaries(events);
        } catch (DataAccessException e) {
            System.out.println("Error filtering by price: " + e.getMessage());
        }
    }

    /* ===================== DATA RETRIEVAL METHODS ===================== */

    /**
     * Retrieves all available categories.
     *
     * @return list of categories, or empty list if none exist
     */
    public List<Category> getAllCategories() throws DataAccessException {
        return eventService.getAllCategory();
    }

    /**
     * Searches events by category ID.
     *
     * @param categoryId the category ID to search by
     * @return list of events in the category
     */
    public List<Event> searchByCategory(int categoryId) throws DataAccessException {
        return eventService.searchBycategory(categoryId);
    }

    /**
     * Searches events by specific date.
     *
     * @param date the date to search by
     * @return list of events on that date
     */
    public List<Event> searchByDate(LocalDate date) throws DataAccessException {
        return eventService.searchByDate(date);
    }

    /**
     * Searches events within a date range.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of events in the date range
     */
    public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {
        return eventService.searchByDateRange(startDate, endDate);
    }

    /**
     * Retrieves all cities where events are available.
     *
     * @return map of city ID to city name
     */
    public Map<Integer, String> getAllCities() throws DataAccessException {
        return eventService.getAllCities();
    }

    /**
     * Searches events by city ID.
     *
     * @param cityId the city ID to search by
     * @return list of events in that city
     */
    public List<Event> searchByCity(int cityId) throws DataAccessException {
        return eventService.searchByCity(cityId);
    }

    /**
     * Filters events by price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of events in the price range
     */
    public List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException {
        return eventService.filterByPrice(minPrice, maxPrice);
    }
}