/*
 * Author : Sowndariya
 * NotificationServiceImpl implements the NotificationService
 * interface and provides the business logic for creating and
 * delivering notifications to users and organizers by
 * delegating to the NotificationDao.
 */
package com.ems.service.impl;

import java.util.Comparator;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.dao.RegistrationDao;
import com.ems.enums.NotificationType;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;
import com.ems.service.NotificationService;

/*
 * Handles notification related business operations.
 *
 * Responsibilities:
 * - Send notifications to users and groups
 * - Fetch user notifications
 * - Maintain read and unread notification states
 */
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final RegistrationDao registrationDao;

    public NotificationServiceImpl(NotificationDao notificationDao,
                                   RegistrationDao registrationDao) {
        this.notificationDao = notificationDao;
        this.registrationDao = registrationDao;
    }

    /*
     * Sends a notification to all users
     */
    @Override
    public void sendSystemWideNotification(String message, NotificationType notificationType)
            throws DataAccessException {

        notificationDao.sendSystemWideNotification(message, notificationType);
    }

   

    /*
     * Sends a notification to all users registered for a specific event.
     * Used for event updates or schedule changes.
     */
    @Override
    public void sendEventNotification(int eventId, String message, NotificationType type)
            throws DataAccessException {

        List<Integer> userIds = registrationDao.getRegisteredUserIdsByEvent(eventId);

        for (Integer userId : userIds) {
            notificationDao.sendNotification(userId, message, type);
        }
    }

    /*
     * Sends notification to a specific user
     */
    @Override
    public void sendPersonalNotification(int userId, String message, NotificationType type)
            throws DataAccessException {

        notificationDao.sendNotification(userId, message, type);
    }

    /*
     * Displays unread notifications for a user.
     * Notifications are automatically marked as read after display.
     */
    @Override
    public void displayUnreadNotifications(int userId) throws DataAccessException {

        List<Notification> notifications = notificationDao.getUnreadNotifications(userId);

        if (notifications == null || notifications.isEmpty()) {
            System.out.println("\nNo unread notifications.");
            return;
        }

        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());

        System.out.println("\n===== UNREAD NOTIFICATIONS =====");

        int index = 1;
        for (Notification n : notifications) {

            System.out.println(index++ + ")");
            System.out.println("Type    : " + n.getType());
            System.out.println("Message : " + n.getMessage());
            System.out.println("Time    : " + n.getCreatedAt());
            System.out.println("--------------------------------");

            notificationDao.markAsRead(n.getNotificationId());
        }
    }

    /*
     * Displays all notifications for a user.
     * All notifications are marked as read once displayed.
     */
    @Override
    public void displayAllNotifications(int userId) throws DataAccessException {

        List<Notification> notifications = notificationDao.getAllNotifications(userId);

        if (notifications == null || notifications.isEmpty()) {
            System.out.println("\nNo notifications found.");
            return;
        }

        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());

        System.out.println("\n===== ALL NOTIFICATIONS =====");

        int index = 1;
        for (Notification n : notifications) {
            System.out.println(index++ + ")");
            System.out.println("Type    : " + n.getType());
            System.out.println("Message : " + n.getMessage());
            System.out.println("Time    : " + n.getCreatedAt());
            System.out.println("--------------------------------");
        }

        notificationDao.markAllAsRead(userId);
    }
}