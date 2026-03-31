package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.AdminMenuHelper;
import com.ems.util.InputValidationUtil;

public class OrganizerRegistrationAction {
	
	private static final int PAGE_SIZE = 10;
	
	private final Scanner scanner;
	private OrganizerService organizerService;
	private final EventService eventService;
	
	public OrganizerRegistrationAction(Scanner scanner) {
        this.organizerService = ApplicationUtil.organizerService();
        this.eventService = ApplicationUtil.eventService();
        this.scanner = scanner;
    }

	
	/**
	 * Views the total number of registrations for a specific event.
	 * @param organizerId the ID of the organizer
	 * @return the count of registrations
	 * @throws DataAccessException 
	 */
	public void viewEventRegistrations(int organizerId) throws DataAccessException {
		 List<Event> events;
		 
	        try {
	            events = organizerService.getOrganizerEvents(organizerId);
	        } catch (DataAccessException e) {
	            System.out.println("Error fetching your events: " + e.getMessage());
	            return;
	        }
	 
	        if (events == null || events.isEmpty()) {
	            System.out.println("You have no events.");
	            return;
	        }
	 
	        // ── Pagination loop ──────────────────────────────────────────────
	        int page = 0;
	        int totalPages = (int) Math.ceil((double) events.size() / PAGE_SIZE);
	 
	        while (true) {
	 
	            printEventPage(events, page, totalPages);
	 
	            System.out.print("Enter choice (1-" + Math.min(PAGE_SIZE, events.size() - page * PAGE_SIZE)
	                    + " to select"
	                    + (page < totalPages - 1 ? " | N - Next" : "")
	                    + (page > 0 ? " | P - Prev" : "")
	                    + " | Q - Back): ");
	 
	            String input = scanner.nextLine().trim();
	 
	            // ── Navigation commands ──────────────────────────────────────
	            if (input.equalsIgnoreCase("Q")) {
	                return;
	            }
	 
	            if (input.equalsIgnoreCase("N")) {
	                if (page < totalPages - 1) {
	                    page++;
	                } else {
	                    System.out.println("Already on the last page.");
	                }
	                continue;
	            }
	 
	            if (input.equalsIgnoreCase("P")) {
	                if (page > 0) {
	                    page--;
	                } else {
	                    System.out.println("Already on the first page.");
	                }
	                continue;
	            }
	 
	            // ── Event selection by row number ────────────────────────────
	            int rowNumber;
	            try {
	                rowNumber = Integer.parseInt(input);
	            } catch (NumberFormatException e) {
	                System.out.println("Invalid input. Enter a row number or N / P / Q.");
	                continue;
	            }
	 
	            int pageStart = page * PAGE_SIZE;
	            int pageEnd   = Math.min(pageStart + PAGE_SIZE, events.size());
	            int maxRow    = pageEnd - pageStart;
	 
	            if (rowNumber < 1 || rowNumber > maxRow) {
	                System.out.println("Please enter a number between 1 and " + maxRow + ".");
	                continue;
	            }
	 
	            Event selected = events.get(pageStart + rowNumber - 1);
	            showRegistrationReport(selected);
	 
	            // After returning from the report, loop back to the event list
	        }
		
	}
	/* ===================== PRIVATE HELPERS ===================== */
	 
    /**
     * Prints one page of the organizer's event table.
     *
     * Columns: NO | TITLE | CATEGORY | START DATE | TICKETS
     */
    private void printEventPage(List<Event> events, int page, int totalPages) {
 
        int pageStart = page * PAGE_SIZE;
        int pageEnd   = Math.min(pageStart + PAGE_SIZE, events.size());
 
        System.out.println("\nYour Events  (Page " + (page + 1) + " of " + totalPages + ")");
        System.out.println("=".repeat(95));
        System.out.printf("%-4s  %-35s  %-20s  %-18s  %-8s%n",
                "NO", "TITLE", "CATEGORY", "START DATE", "TICKETS");
        System.out.println("-".repeat(95));
 
        for (int i = pageStart; i < pageEnd; i++) {
 
            Event e        = events.get(i);
            int    rowNo   = i - pageStart + 1;
            String category = resolveCategoryName(e.getCategoryId());
            int    tickets  = resolveAvailableTickets(e.getEventId());
            String startDate = e.getStartDateTime() != null
                    ? DateTimeUtil.formatForDisplay(e.getStartDateTime())
                    : "-";
 
            // Truncate long titles so the table stays aligned
            String title = e.getTitle().length() > 35
                    ? e.getTitle().substring(0, 32) + "..."
                    : e.getTitle();
 
            System.out.printf("%-4d  %-35s  %-20s  %-18s  %-8d%n",
                    rowNo, title, category, startDate, tickets);
        }
 
        System.out.println("=".repeat(95));
    }
 
    /**
     * Fetches and prints the registration report for the selected event.
     *
     * Columns: NO | EVENT TITLE | USER | TICKET TYPE | QTY | REGISTERED ON
     */
    
    private void showRegistrationReport(Event selected) {

        List<EventRegistrationReport> report;

        try {
            report = organizerService.getEventWiseRegistrations(selected.getEventId());
        } catch (DataAccessException e) {
            System.out.println("Error fetching registrations: " + e.getMessage());
            return;
        }

        System.out.println("\nRegistration Report: " + selected.getTitle());


        AdminMenuHelper.printEventRegistrationReport(report);

        InputValidationUtil.readNonEmptyString(scanner, "\nPress Enter to go back...");
    }
 
	/** Safely resolves a category name; returns "Unknown" on any error. */
    private String resolveCategoryName(int categoryId) {
        try {
            Category cat = eventService.getCategory(categoryId);
            return (cat != null) ? cat.getName() : "Unknown";
        } catch (DataAccessException e) {
            return "Unknown";
        }
    }
 
    /** Safely resolves available ticket count; returns 0 on any error. */
    private int resolveAvailableTickets(int eventId) {
        try {
            return eventService.getAvailableTickets(eventId);
        } catch (DataAccessException e) {
            return 0;
        }
    }
	

}