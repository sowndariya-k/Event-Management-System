package com.ems.actions;

import java.util.Scanner;

import com.ems.dao.FeedbackDao;
import com.ems.dao.impl.FeedbackDaoImpl;
import com.ems.exception.DataAccessException;

public class FeedbackAction {

    private FeedbackDao feedbackDao = new FeedbackDaoImpl();

    // ✅ OLD METHOD (used by UserMenu) - DO NOT REMOVE
    public void submitRating(int userId) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Event ID: ");
        int eventId = sc.nextInt();

        System.out.print("Enter Rating (1-5): ");
        int rating = sc.nextInt();
        sc.nextLine(); // consume newline

        System.out.print("Enter Comments: ");
        String comments = sc.nextLine();

        // call main method
        submitRating(eventId, userId, rating, comments);
    }

    // ✅ MAIN METHOD (your correct logic)
    public void submitRating(int eventId, int userId, int rating, String comments) {

        if (eventId <= 0 || userId <= 0) {
            System.out.println("Invalid event or user ID");
            return;
        }

        if (comments == null || comments.trim().isEmpty()) {
            System.out.println("Comments cannot be empty");
            return;
        }

        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5");
            return;
        }

        try {
            if (feedbackDao.isRatingAlreadySubmitted(eventId, userId)) {
                System.out.println("Feedback already submitted");
                return;
            }

            boolean result = feedbackDao.submitRating(eventId, userId, rating, comments);

            if (result) {
                System.out.println("Feedback submitted successfully");
            } else {
                System.out.println("Failed to submit feedback");
            }

        } catch (DataAccessException e) {
            System.out.println("Error while submitting feedback");
            e.printStackTrace();
        }
    }
}