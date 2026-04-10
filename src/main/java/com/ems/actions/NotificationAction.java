/*
 * Author : Sowndariya
 * NotificationAction retrieves and displays notifications
 * for the logged-in user or organizer, fetching unread
 * and historical notifications from the notification service.
 */
package com.ems.actions;

import com.ems.exception.DataAccessException;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/**
 * Action class for notification operations.
 * Delegates business logic to appropriate services.
 * This action is shared by both Admin and Organizer menus.
 */

public class NotificationAction {

    private final NotificationService notificationService;
    private final OrganizerService organizerService;

    public NotificationAction() {
        this.notificationService = ApplicationUtil.notificationService();
        this.organizerService = ApplicationUtil.organizerService();
    }
    /**
     * Displays unread notifications for a user.
     * 
     * @param userId the ID of the user
     */
    public void displayUnreadNotifications(int userId) {
        try {
            notificationService.displayUnreadNotifications(userId);
        } catch (DataAccessException e) {
            System.out.println("Error displaying unread notifications: " + e.getMessage());
        }
    }
	
	/**
     * Displays all notifications for a user.
     * 
     * @param userId the ID of the user
     */
	public void displayAllNotifications(int userId) {
		 try {
	            notificationService.displayAllNotifications(userId);
	        } catch (DataAccessException e) {
	            System.out.println("Error displaying all notifications: " + e.getMessage());
	        }
	}
	
	/**
     * Sends an event update notification to all registered attendees.
     * 
     * @param eventId the ID of the event
     * @param message the message to send
     */
	public void sendEventUpdate(int eventId, String message) {
		 try {
	            organizerService.sendEventUpdate(eventId, message);
	        } catch (DataAccessException e) {
	            System.out.println("Error sending event update: " + e.getMessage());
	        }
	}
	
	/**
     * Sends a schedule change notification to all registered attendees.
     * 
     * @param eventId the ID of the event
     * @param message the message to send
     */
	 public void sendScheduleChange(int eventId, String message) {
		 try {
	            organizerService.sendScheduleChange(eventId, message);
	        } catch (DataAccessException e) {
	            System.out.println("Error sending schedule change: " + e.getMessage());
	        }
	    }

	
}
