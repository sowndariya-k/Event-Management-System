package com.ems.menu;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.ems.dao.*;
import com.ems.dao.impl.*;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.util.InputValidationUtil;
import com.ems.actions.EventBrowsingAction;


public class UserMenu {
	Scanner scanner;
	User loggedInUser;
	
	public UserMenu(Scanner scanner, User user) throws DataAccessException {
		this.scanner = scanner;
		this.loggedInUser = user;
		this.start();
	}

	private void start() throws DataAccessException {
	    UserDao userDao = new UserDaoImpl();
	    EventDao eventDao = new EventDaoImpl();
	    VenueDao venueDao = new VenueDaoImpl();
	    CategoryDao categoryDao = new CategoryDaoImpl();
	    TicketDao ticketDao = new TicketDaoImpl();

	    while (true) {	
	        System.out.println("\nUser Menu"
	                + "\n\nEnter your choice:\n"
	                + "1. Browse available events\r\n"
	                + "2. Search and filter events\r\n"
	                + "3. View my registrations\r\n"
	                + "4. View my notifications\r\n"
	                + "5. Register for an event\r\n"
	                + "6. Logout"
	                + "\n>");
	        int input = InputValidationUtil.readInt(scanner, "");

	        switch (input) {
	        case 1:
	        	new EventBrowsingAction().execute();
	            break; // ✅ THIS WAS MISSING — was falling through into case 2

	        case 2:
	            boolean exitFilter = false;
	            while (!exitFilter) { // ✅ use a flag instead of return
	                System.out.println("\nEnter your choice:\n"
	                        + "1. Search by category\r\n"
	                        + "2. Search by date\r\n"
	                        + "3. Search by city\r\n"
	                        + "4. Filter by price\r\n"
	                        + "5. Filter by availability\r\n"
	                        + "6. Exit to user menu"
	                        + "\n>");
	                int filterChoice = InputValidationUtil.readInt(scanner, "");
	                switch (filterChoice) {
	                case 1:
	                    searchByCategory(categoryDao);
	                    break;
	                case 2:
	                    System.out.print("Enter date (yyyy-mm-dd): ");
	                    String date = scanner.nextLine();

	                    List<Event> events = eventDao.searchByDate(date);

	                    if (events.isEmpty()) {
	                        System.out.println("❌ No events found on this date");
	                        break;
	                    }

	                    System.out.println("\n===== Events Found =====");

	                    for (Event event : events) {
	                        System.out.println("ID: " + event.getEventId());
	                        System.out.println("Title: " + event.getTitle());
	                        System.out.println("Start: " + formatDateTime(event.getStartDateTime()));
	                        System.out.println("-----------------------------------");
	                    }
	                    break;
	                case 3:
	                    System.out.print("Enter city: ");
	                    String city = scanner.nextLine();

	                    List<Event> eventsByCity = eventDao.searchByCity(city);

	                    if (eventsByCity.isEmpty()) {
	                        System.out.println("❌ No events found in this city");
	                        break;
	                    }

	                    System.out.println("\n===== Events Found =====");

	                    for (Event event : eventsByCity) {
	                        System.out.println("ID: " + event.getEventId());
	                        System.out.println("Title: " + event.getTitle());
	                        System.out.println("Start: " + formatDateTime(event.getStartDateTime()));
	                        System.out.println("-----------------------------------");
	                    }
	                    break;
	                case 4:
	                    System.out.print("Enter max price: ");
	                    double price = Double.parseDouble(scanner.nextLine());

	                    List<Event> eventsByPrice = eventDao.filterByPrice(price);

	                    if (eventsByPrice.isEmpty()) {
	                        System.out.println(" No events found under this price");
	                        break;
	                    }

	                    System.out.println("\n===== Events Found =====");

	                    for (Event event : eventsByPrice) {
	                        System.out.println("ID: " + event.getEventId());
	                        System.out.println("Title: " + event.getTitle());
	                        System.out.println("Start: " + formatDateTime(event.getStartDateTime()));
	                        System.out.println("-----------------------------------");
	                    }
	                    break;
	                case 5:
	                    List<Event> availableEvents = eventDao.filterByAvailability();

	                    if (availableEvents.isEmpty()) {
	                        System.out.println("❌ No available events");
	                        break;
	                    }

	                    System.out.println("\n===== Available Events =====");

	                    for (Event event : availableEvents) {
	                        System.out.println("ID: " + event.getEventId());
	                        System.out.println("Title: " + event.getTitle());
	                        System.out.println("Start: " + formatDateTime(event.getStartDateTime()));
	                        System.out.println("-----------------------------------");
	                    }
	                    break;
	                case 6:
	                    exitFilter = true; // ✅ exits inner loop, back to User Menu
	                    break;
	                default:
	                    exitFilter = true;
	                    break;
	                }
	            }
	            break; // ✅ back to outer while loop

	        case 3:
	            break;
	        case 4:
	            break;
	        case 5:
	            break;
	        case 6:
	            System.out.println("Logout - Going back to main menu");
	            return;
	        default:
	            return;
	        }
	    }
	}
	private void searchByCategory(CategoryDao categoryDao) throws DataAccessException {

	    EventDao eventDao = new EventDaoImpl();

	    // Step 1: Get categories from DB
	    List<Category> categories = categoryDao.getAllCategories();

	    if (categories.isEmpty()) {
	        System.out.println("No categories available");
	        return;
	    }

	    // Step 2: Display categories
	    System.out.println("\nAvailable Categories:");
	    for (Category c : categories) {
	        System.out.println(c.getCategoryId() + " - " + c.getName());
	    }

	    // Step 3: Take input
	    int categoryId = InputValidationUtil.readInt(scanner, "Enter Category ID: ");

	    // Step 4: Fetch events
	    List<Event> events = eventDao.searchByCategory(categoryId);

	    // Step 5: Display results
	    if (events.isEmpty()) {
	        System.out.println("❌ No events found for this category");
	        return;
	    }

	    System.out.println("\n===== Events Found =====");

	    for (Event event : events) {
	        System.out.println("ID: " + event.getEventId());
	        System.out.println("Title: " + event.getTitle());
	        System.out.println("Start: " + formatDateTime(event.getStartDateTime()));
	        System.out.println("-----------------------------------");
	    }
	}
	

	private void printAllAvailableEvents(EventDao eventDao, VenueDao venueDao, CategoryDao categoryDao, TicketDao ticketDao) throws DataAccessException {
		List<Event> events = eventDao.listAvailableEvents();
		for (Event event : events) {

			Category category = categoryDao.getCategory(event.getCategoryId());
	        String venueName = venueDao.getVenueName(event.getVenueId());
	        String venueAddress = venueDao.getVenueAddress(event.getVenueId());
	        int totalAvailable = ticketDao.getAvailableTickets(event.getEventId());
	        List<Ticket> tickets = ticketDao.getTicketTypes(event.getEventId());

	        System.out.println("\n==============================================");
	        System.out.println("Event ID        : " + event.getEventId());
	        System.out.println("Title           : " + event.getTitle());

	        if (event.getDescription() != null) {
	            System.out.println("Description     : " + event.getDescription());
	        }

	        System.out.println("Category        : " + category);
	        System.out.println("Duration        : "
	                + formatDateTime(event.getStartDateTime())
	                + " to "
	                + formatDateTime(event.getEndDateTime()));

	        System.out.println("Total Tickets   : " + totalAvailable);

	        System.out.println("\nTicket Types");
	        System.out.println("----------------------------------------------");

	        for (Ticket ticket : tickets) {
	            System.out.println("• "
	                    + ticket.getTicketType()
	                    + " | Price: ₹"
	                    + ticket.getPrice()
	                    + " | Available: "
	                    + ticket.getAvailableQuantity());
	        }

	        System.out.println("\nVenue");
	        System.out.println("----------------------------------------------");
	        System.out.println("Name            : " + venueName);
	        System.out.println("Address         : " + venueAddress);

	        System.out.println("==============================================");
	    }
	}

	private String formatDateTime(Instant dateTime) {
	    if (dateTime == null) {
	        return "N/A";
	    }
	    LocalDateTime localDateTime = LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault());
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	    return localDateTime.format(formatter);
	}

	
}
