/*
 * Author : Sowndariya
 * AdminMenuHelper provides reusable display and formatting
 * utility methods for the admin menu, including tabular
 * output of users, events, venues, tickets, offers, and
 * report data to the console.
 */
package com.ems.util;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.model.EventRevenueReport;
import com.ems.model.Offer;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.exception.DataAccessException;

public class AdminMenuHelper {

    private static final EventService eventService = ApplicationUtil.eventService();

    private static final int TABLE_WIDTH = 110;
    private static final String SEPARATOR = "=".repeat(TABLE_WIDTH);
    private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

    // -----------------------------------------------------------------------
    // Event display
    // -----------------------------------------------------------------------

    /**
     * Prints all events with their status, starting the display index at 1.
     *
     * @param events list of events to display
     */
    public static void printAllEventsWithStatus(List<Event> events) {
        printAllEventsWithStatus(events, 1);
    }

    /**
     * Prints events in a formatted table with numbering starting from the given
     * index.
     *
     * @param events     list of events to display
     * @param startIndex number to use for the first row
     */
    public static void printAllEventsWithStatus(List<Event> events, int startIndex) {
        if (events == null || events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        System.out.println("\nAVAILABLE EVENTS");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-30s %-20s %-20s %-10s %-10s%n",
                "NO", "TITLE", "CATEGORY", "START DATE", "TICKETS", "STATUS");
        System.out.println(SUB_SEPARATOR);

        int index = startIndex;
        for (Event event : events) {
            try {
                String category = eventService.getCategory(event.getCategoryId()).getName();
                int available = eventService.getAvailableTickets(event.getEventId());

                System.out.printf("%-5d %-30s %-20s %-20s %-10d %-10s%n",
                        index++,
                        truncate(event.getTitle(), 29),
                        category,
                        DateTimeUtil.formatForDisplay(event.getStartDateTime()),
                        available,
                        event.getStatus());
            } catch (DataAccessException e) {
                System.out.printf("%-5d %-30s %-50s%n", index++, truncate(event.getTitle(), 29),
                        "[Error fetching details]");
            }
        }

        System.out.println(SEPARATOR);
    }

    // -----------------------------------------------------------------------
    // Ticket display
    // -----------------------------------------------------------------------

    /**
     * Prints ticket details in formatted table.
     * Ticket lists are always shown in full; pagination offset is not applicable.
     *
     * @param tickets list of tickets
     */
    public static void printTicketDetails(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            System.out.println("No ticket types found.");
            return;
        }

        System.out.println("\nTICKET DETAILS");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-20s %-10s %-15s%n",
                "NO", "TYPE", "PRICE", "CAPACITY");
        System.out.println(SUB_SEPARATOR);

        int index = 1;
        for (Ticket t : tickets) {
            System.out.printf("%-5d %-20s ₹%-9.2f %-7d/%-7d%n",
                    index++,
                    t.getTicketType(),
                    t.getPrice(),
                    t.getAvailableQuantity(),
                    t.getTotalQuantity());
        }

        System.out.println(SEPARATOR);
    }

    /**
     * Prints a capacity summary (total vs. available) across all ticket types.
     *
     * @param tickets list of tickets
     */
    public static void printTicketCapacitySummary(List<Ticket> tickets) {
        int total = 0;
        int available = 0;

        for (Ticket t : tickets) {
            total += t.getTotalQuantity();
            available += t.getAvailableQuantity();
        }

        System.out.println("\nEVENT CAPACITY SUMMARY");
        System.out.println(SEPARATOR);
        System.out.println("Total Tickets     : " + total);
        System.out.println("Available Tickets : " + available);
        System.out.println(SEPARATOR);
    }

    // -----------------------------------------------------------------------
    // Organizer event summary
    // -----------------------------------------------------------------------

    /**
     * Displays the event summary for an organizer, sorted by status priority
     * then title, starting the display index at 1.
     *
     * @param summaries list of organizer event summaries
     */
    public static void printOrganizerEventSummary(List<OrganizerEventSummary> summaries) {
        printOrganizerEventSummary(summaries, 1);
    }

    /**
     * Prints organizer event summaries in table format.
     *
     * @param summaries  list of summaries to display
     * @param startIndex number to use for the first row
     */
    public static void printOrganizerEventSummary(List<OrganizerEventSummary> summaries, int startIndex) {
        if (summaries == null || summaries.isEmpty()) {
            System.out.println("No organizer data found.");
            return;
        }

        List<OrganizerEventSummary> orderedList = summaries.stream()
                .sorted(Comparator
                        .comparingInt(OrganizerEventSummary::getStatusPriority)
                        .thenComparing(OrganizerEventSummary::getTitle))
                .toList();

        System.out.println("\nORGANIZER EVENT SUMMARY");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-45s %-12s %-15s %-15s%n",
                "NO", "EVENT TITLE", "STATUS", "BOOKED", "TOTAL");
        System.out.println(SUB_SEPARATOR);

        int index = startIndex;
        for (OrganizerEventSummary s : orderedList) {
            System.out.printf("%-5d %-45s %-12s %-15d %-15d%n",
                    index++,
                    truncate(s.getTitle(), 44),
                    s.getStatus(),
                    s.getBookedTickets(),
                    s.getTotalTickets());
        }

        System.out.println(SEPARATOR);
    }

    // -----------------------------------------------------------------------
    // Venue display
    // -----------------------------------------------------------------------

    /**
     * Prints venues in formatted table, starting the display index at 1.
     *
     * @param venues list of venues
     */
    public static void printVenues(List<Venue> venues) {
        printVenues(venues, 1);
    }

    /**
     * Prints venues in table format.
     *
     * @param venues     list of venues to display
     * @param startIndex number to use for the first row
     */
    public static void printVenues(List<Venue> venues, int startIndex) {
        if (venues == null || venues.isEmpty()) {
            System.out.println("No venues found.");
            return;
        }

        System.out.println("\nAVAILABLE VENUES");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-22s %-18s %-15s %-12s %-10s %-12s%n",
                "NO", "VENUE NAME", "STREET", "CITY", "STATE", "CAPACITY", "STATUS");
        System.out.println(SUB_SEPARATOR);

        int index = startIndex;
        for (Venue v : venues) {
            String status = v.isStatus() ? "ACTIVE" : "INACTIVE";

            System.out.printf("%-5d %-22s %-18s %-15s %-12s %-10d %-12s%n",
                    index++,
                    truncate(v.getName(), 21),
                    truncate(v.getStreet(), 17),
                    v.getCity(),
                    v.getState(),
                    v.getMaxCapacity(),
                    status);
        }

        System.out.println(SEPARATOR);
    }

    /**
     * Prints full details for a single venue.
     *
     * @param v the venue to display
     */
    public static void printVenueDetails(Venue v) {
        if (v == null) {
            System.out.println("Venue not found.");
            return;
        }

        System.out.println("\nVENUE DETAILS");
        System.out.println(SEPARATOR);
        System.out.println("Name        : " + v.getName());
        System.out.println("Street      : " + v.getStreet());
        System.out.println("City        : " + v.getCity());
        System.out.println("State       : " + v.getState());
        System.out.println("Pincode     : " + v.getPincode());
        System.out.println("Capacity    : " + v.getMaxCapacity());
        System.out.println(SEPARATOR);
    }

    // -----------------------------------------------------------------------
    // Report display
    // -----------------------------------------------------------------------

    /**
     * Prints the event registration report, starting the display index at 1.
     *
     * @param reports list of event registration reports
     */
    public static void printEventRegistrationReport(List<EventRegistrationReport> reports) {
        printEventRegistrationReport(reports, 1);
    }

    /**
     * Prints the event registration report.
     *
     * @param reports    list of registration records
     * @param startIndex number to use for the first row
     */
    public static void printEventRegistrationReport(List<EventRegistrationReport> reports, int startIndex) {
        if (reports == null || reports.isEmpty()) {
            System.out.println("No registration records found.");
            return;
        }

        System.out.println("\nEVENT REGISTRATION REPORT");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-30s %-20s %-15s %-10s %-20s%n",
                "NO", "EVENT TITLE", "USER", "TICKET TYPE", "QTY", "REGISTERED ON");
        System.out.println(SUB_SEPARATOR);

        int index = startIndex;
        for (EventRegistrationReport r : reports) {
            System.out.printf("%-5d %-30s %-20s %-15s %-10d %-20s%n",
                    index++,
                    truncate(r.getEventTitle(), 29),
                    truncate(r.getUserName(), 19),
                    truncate(r.getTicketType(), 14),
                    r.getQuantity(),
                    DateTimeUtil.formatForDisplay(r.getRegistrationDate()));
        }

        System.out.println(SEPARATOR);
    }

    /**
     * Prints the offer usage report (offer code → times used).
     * Map entries have no natural selection index; pagination offset is not
     * applicable.
     *
     * @param report map of offer code to usage count
     */
    public static void printOfferUsageReport(Map<String, Integer> report) {
        if (report == null || report.isEmpty()) {
            System.out.println("No offer usage data found.");
            return;
        }

        System.out.println("\nOFFER USAGE REPORT");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-20s %-15s%n", "NO", "OFFER CODE", "USAGE COUNT");
        System.out.println(SUB_SEPARATOR);

        int index = 1;
        for (Map.Entry<String, Integer> entry : report.entrySet()) {
            System.out.printf("%-5d %-20s %-15d%n",
                    index++,
                    truncate(entry.getKey(), 19),
                    entry.getValue());
        }

        System.out.println(SEPARATOR);
    }

    /**
     * Prints all offers, starting the display index at 1.
     *
     * @param offers list of offers
     */
    public static void printOffers(List<Offer> offers) {
        printOffers(offers, 1);
    }

    /**
     * Prints all offers with a caller-supplied start index.
     *
     * @param offers     list of offers
     * @param startIndex 1-based index of the first item in this page
     */
    public static void printOffers(List<Offer> offers, int startIndex) {
        if (offers == null || offers.isEmpty()) {
            System.out.println("No offers found.");
            return;
        }

        System.out.println("\nOFFERS REPORT");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-10s %-15s %-12s %-20s %-20s %-12s%n",
                "NO", "OFFER ID", "CODE", "DISCOUNT", "VALID FROM", "VALID TO", "STATUS");
        System.out.println(SUB_SEPARATOR);

        int index = startIndex;
        for (Offer o : offers) {
            String discount = o.getDiscountPercentage() != null
                    ? o.getDiscountPercentage() + "%"
                    : "NA";

            String status;
            if (o.getValidFrom() != null && o.getValidTo() != null) {
                Instant now = DateTimeUtil.nowUtc();
                if (now.isBefore(o.getValidFrom()))
                    status = "UPCOMING";
                else if (!now.isAfter(o.getValidTo()))
                    status = "ACTIVE";
                else
                    status = "EXPIRED";
            } else {
                status = "UNKNOWN";
            }

            System.out.printf("%-5d %-10d %-15s %-12s %-20s %-20s %-12s%n",
                    index++,
                    o.getOfferId(),
                    truncate(o.getCode(), 14),
                    discount,
                    DateTimeUtil.formatForDisplay(o.getValidFrom()),
                    DateTimeUtil.formatForDisplay(o.getValidTo()),
                    status);
        }

        System.out.println(SEPARATOR);
    }

    /**
     * Prints the event-wise revenue report.
     * Revenue totals are aggregated across the full list; pagination offset is not
     * applicable.
     *
     * @param reports list of event revenue reports
     */
    public static void printEventRevenueReport(List<EventRevenueReport> reports) {
        if (reports == null || reports.isEmpty()) {
            System.out.println("No revenue data available.");
            return;
        }

        System.out.println("\nEVENT WISE REVENUE REPORT");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-35s %-12s %-12s %-15s %-15s%n",
                "NO", "EVENT TITLE", "REGS", "TICKETS", "REVENUE", "AVG PRICE");
        System.out.println(SUB_SEPARATOR);

        int index = 1;
        double grandTotal = 0;
        for (EventRevenueReport report : reports) {
            grandTotal += report.getTotalRevenue();
            System.out.printf("%-5d %-35s %-12d %-12d ₹%-14.2f ₹%-14.2f%n",
                    index++,
                    truncate(report.getEventTitle(), 34),
                    report.getTotalRegistrations(),
                    report.getTicketsSold(),
                    report.getTotalRevenue(),
                    report.getAvgTicketPrice());
        }

        System.out.println(SEPARATOR);
        System.out.printf("TOTAL REVENUE: ₹%.2f%n", grandTotal);
    }

    // -----------------------------------------------------------------------
    // Category display
    // -----------------------------------------------------------------------

    /**
     * Prints categories with their active/inactive status, starting at index 1.
     *
     * @param categories list of categories
     */
    public static void printCategories(List<Category> categories) {
        printCategories(categories, 1);
    }

    /**
     * Prints categories with their active/inactive status and a caller-supplied
     * start index.
     *
     * @param categories list of categories
     * @param startIndex 1-based index of the first item in this page
     */
    public static void printCategories(List<Category> categories, int startIndex) {
        if (categories == null || categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println("\nAVAILABLE CATEGORIES");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-30s %-12s%n", "NO", "CATEGORY NAME", "STATUS");
        System.out.println(SUB_SEPARATOR);

        int index = startIndex;
        for (Category c : categories) {
            String status = c.getIsActive() == 1 ? "ACTIVE" : "INACTIVE";
            System.out.printf("%-5d %-30s %-12s%n",
                    index++,
                    truncate(c.getName(), 29),
                    status);
        }

        System.out.println(SEPARATOR);
    }

    // -----------------------------------------------------------------------
    // Offer filter helpers
    // -----------------------------------------------------------------------

    public static List<Offer> filterActiveOffers(List<Offer> offers) {
        Instant now = DateTimeUtil.nowUtc();
        return offers.stream()
                .filter(o -> o.getValidTo() != null && o.getValidTo().isAfter(now))
                .toList();
    }

    public static List<Offer> filterExpiredOffers(List<Offer> offers) {
        Instant now = DateTimeUtil.nowUtc();
        return offers.stream()
                .filter(o -> o.getEventId() != 0
                        && o.getValidTo() != null
                        && o.getValidTo().isBefore(now))
                .toList();
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Truncates a string to max characters, appending "..." when cut.
     *
     * @param value the string to truncate
     * @param max   maximum display length
     * @return the original string, or a truncated version ending with "..."
     */
    private static String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max - 3) + "...";
    }
}