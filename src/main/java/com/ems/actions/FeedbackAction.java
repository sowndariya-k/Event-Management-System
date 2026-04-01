package com.ems.actions;

import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.exception.DataAccessException;


/**
 * Action class for feedback operations.
 * Delegates business logic to EventService.
 */
public class FeedbackAction {

	private final EventService eventService;
    private final Scanner scanner;

    public FeedbackAction(Scanner scanner) {
        this.eventService = ApplicationUtil.eventService();
        this.scanner = scanner;
    }
    
    /**
	 * Allows a user to submit a rating and optional feedback
	 * for an event they have previously attended.
	 *
	 * @param userId the ID of the user submitting feedback
	 */

    public void submitRating(int userId) {
		try {
			List<UserEventRegistration> past = eventService.viewPastEvents(userId);

			if (past == null || past.isEmpty()) {
				System.out.println("No past events to rate.");
				return;
			}

			List<UserEventRegistration> filtered = new ArrayList<>();
			for (UserEventRegistration r : past) {
				// 1. Only finished events
				if (r.getEndDateTime() != null && r.getEndDateTime().isBefore(DateTimeUtil.nowUtc())) {
					// 3. Remove already reviewed events
					if (!eventService.isRatingAlreadySubmitted(r.getEventId(), userId)) {
						filtered.add(r);
					}
				}
			}

			// 2. Remove duplicates by eventId
			past = filtered.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toMap(
									UserEventRegistration::getEventId,
									r -> r,
									(existing, duplicate) -> existing),
							map -> new ArrayList<>(map.values())));

			if (past.isEmpty()) {
				System.out.println("You have no events pending review.");
				return;
			}

			MenuHelper.printEventsList(filtered);

			int choice = MenuHelper.selectFromList(
					scanner, past.size(),
					"Select an event to rate");

			int eventId = past.get(choice - 1).getEventId();

			int rating;
			do {
				rating = InputValidationUtil.readInt(scanner,
						"Rate the event (1-5): ");

				if (rating < 1 || rating > 5) {
					System.out.println("Please enter a rating between 1 and 5.");
				}
			} while (rating < 1 || rating > 5);

			String comments = InputValidationUtil.readString(scanner,
					"Enter feedback (optional, press Enter to skip):\n");

			comments = (comments == null || comments.trim().isEmpty())
					? null
					: comments.trim();

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