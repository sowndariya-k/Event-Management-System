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

    public FeedbackAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    /**
     * Allows a user to submit a rating and optional feedback
     * for an event they have previously attended.
     *
     * @param userId the ID of the user submitting feedback
     */
    public void submitRating(int userId, Scanner scanner) { 
        try {
            List<UserEventRegistration> past = eventService.viewPastEvents(userId);

            if (past == null || past.isEmpty()) {
                System.out.println("No past events to rate.");
                return;
            }

            List<UserEventRegistration> filtered = new ArrayList<>();
            Set<Integer> seenEventIds = new HashSet<>();

            for (UserEventRegistration r : past) {
                if (r.getEndDateTime() != null && r.getEndDateTime().isBefore(DateTimeUtil.nowUtc())
                        && !eventService.isRatingAlreadySubmitted(r.getEventId(), userId)
                        && !seenEventIds.contains(r.getEventId())) {
                    filtered.add(r);
                    seenEventIds.add(r.getEventId());
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("You have no events pending review.");
                return;
            }

            System.out.println("===== Events Pending Review =====");
            System.out.printf("%-5s %-30s %-20s %-20s\n", "NO", "EVENT TITLE", "START DATE", "END DATE");
            int index = 1;
            for (UserEventRegistration r : filtered) {
                Event event = eventService.getEventById(r.getEventId());
                System.out.printf("%-5d %-30s %-20s %-20s\n",
                        index++,
                        event.getTitle(),
                        r.getStartDateTime(),
                        r.getEndDateTime());
            }

            int choice;
            do {
                System.out.print("Select an event to rate (1-" + filtered.size() + "): ");
                choice = InputValidationUtil.readInt(scanner, ""); // safe now
            } while (choice < 1 || choice > filtered.size());

            int eventId = filtered.get(choice - 1).getEventId();

            int rating;
            do {
                System.out.print("Rate the event (1-5): ");
                rating = InputValidationUtil.readInt(scanner, "");
                if (rating < 1 || rating > 5) {
                    System.out.println("Please enter a rating between 1 and 5.");
                }
            } while (rating < 1 || rating > 5);

            System.out.println("Enter feedback (optional, press Enter to skip): ");
            String comments = scanner.nextLine().trim();
            comments = comments.isEmpty() ? null : comments;

            boolean isSuccess = eventService.submitRating(userId, eventId, rating, comments);

            if (isSuccess) {
                System.out.println("Thank you for your feedback!");
            } else {
                System.out.println("Failed to submit your rating!");
            }

        } catch (DataAccessException e) {
            System.out.println("Error submitting feedback: " + e.getMessage());
        }
    }
}