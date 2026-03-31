package com.ems.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.ems.model.Event;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.exception.DataAccessException;

public class FeedbackAction {

    private final EventService eventService;

    private static final int TABLE_WIDTH = 110;
    private static final String SEPARATOR = "=".repeat(TABLE_WIDTH);
    private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

    public FeedbackAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public void submitRating(int userId, Scanner scanner) {

        try {
            List<UserEventRegistration> registrations =
                    eventService.viewPastEvents(userId);

            if (registrations == null || registrations.isEmpty()) {
                System.out.println("No registrations found.");
                return;
            }

            List<UserEventRegistration> filtered = new ArrayList<>();
            Set<Integer> seenEventIds = new HashSet<>();

            for (UserEventRegistration r : registrations) {

                // 🔥 FIX 1: REMOVE strict past check (for your current data)
                // boolean isPast = r.getEndDateTime() != null &&
                //        r.getEndDateTime().isBefore(DateTimeUtil.nowUtc());

                boolean alreadyRated =
                        eventService.isRatingAlreadySubmitted(r.getEventId(), userId);

                if (!alreadyRated && !seenEventIds.contains(r.getEventId())) {
                    filtered.add(r);
                    seenEventIds.add(r.getEventId());
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("You have no events pending review.");
                return;
            }

            // ================= DISPLAY =================
            printEvents(filtered);

            int choice = selectFromList(scanner, filtered.size(),
                    "Select an event to rate");

            int eventId = filtered.get(choice - 1).getEventId();

            // ================= RATING =================
            int rating;
            do {
                System.out.print("Rate the event (1-5): ");
                rating = InputValidationUtil.readInt(scanner, "");

                if (rating < 1 || rating > 5) {
                    System.out.println("Enter value between 1 and 5.");
                }

            } while (rating < 1 || rating > 5);

            // ================= COMMENTS =================
            System.out.print("Enter feedback (optional): ");
            scanner.nextLine(); // clear buffer
            String comments = scanner.nextLine().trim();

            comments = comments.isEmpty() ? null : comments;

            boolean success =
                    eventService.submitRating(userId, eventId, rating, comments);

            if (success) {
                System.out.println("Thank you for your feedback!");
            } else {
                System.out.println("Failed to submit feedback.");
            }

        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= DISPLAY METHOD =================

    private void printEvents(List<UserEventRegistration> list) {

        System.out.println("\nEVENTS AVAILABLE FOR RATING");
        System.out.println(SEPARATOR);

        System.out.printf("%-5s %-30s %-20s %-20s %-10s%n",
                "NO", "TITLE", "CATEGORY", "DATE & TIME", "TICKETS");

        System.out.println(SUB_SEPARATOR);

        int i = 1;

        for (UserEventRegistration r : list) {
            try {
                Event event = eventService.getEventById(r.getEventId());
                String formattedDate = DateTimeUtil
						.formatForDisplay(r.getStartDateTime());

                System.out.printf("%-5d %-30s %-20s %-20s %-10d%n",
                        i++,
                        truncate(event.getTitle(), 29),
                        truncate(r.getCategory(), 19),
                        formattedDate,
                        r.getTicketsPurchased());

            } catch (Exception e) {
                System.out.printf("%-5d %-30s %-50s%n",
                        i++,
                        "Error",
                        "[Unable to fetch event]");
            }
        }

        System.out.println(SEPARATOR);
    }

    // ================= INPUT =================

    private int selectFromList(Scanner scanner, int max, String prompt) {
        int choice;

        while (true) {
            System.out.print(prompt + " (1-" + max + "): ");
            choice = InputValidationUtil.readInt(scanner, "");

            if (choice >= 1 && choice <= max) {
                return choice;
            }

            System.out.println("Invalid choice. Try again.");
        }
    }

    // ================= UTIL =================

    private String truncate(String value, int length) {
        if (value == null) return "";
        return value.length() <= length
                ? value
                : value.substring(0, length - 3) + "...";
    }
}