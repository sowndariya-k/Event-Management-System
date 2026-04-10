/*
 * Author : Sowndariya
 * NotificationDao is the DAO interface that defines
 * contract methods for creating, fetching, and managing
 * notifications for users and organizers by type and
 * recipient.
 */
package com.ems.dao;

import java.util.List;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;

public interface NotificationDao {

        /**
         * Retrieves all unread notifications for the user
         *
         * @param userId
         * @return list of unread notifications
         * @throws DataAccessException
         */
        List<Notification> getUnreadNotifications(int userId) throws DataAccessException;

        /**
         * Marks a single notification as read
         *
         * @param notificationId
         * @throws DataAccessException
         */
        void markAsRead(int notificationId) throws DataAccessException;

        /**
         * Retrieves all notifications of a user
         *
         * @param userId
         * @return list of notifications
         * @throws DataAccessException
         */
        List<Notification> getAllNotifications(int userId) throws DataAccessException;

        /**
         * Marks all unread notifications of the user as read
         *
         * @param userId
         * @throws DataAccessException
         */
        void markAllAsRead(int userId) throws DataAccessException;

        /**
         * Sends a notification to a specific user
         *
         * @param userId
         * @param message
         * @param notificationType
         * @return true if notification was sent
         * @throws DataAccessException
         */
        boolean sendNotification(int userId, String message, NotificationType notificationType)
                        throws DataAccessException;

        /**
         * Sends a system wide notification to all active users
         *
         * @param message
         * @param notificationType
         * @throws DataAccessException
         */
        void sendSystemWideNotification(String message, NotificationType notificationType)
                        throws DataAccessException;

        /**
         * Sends a notification to all active users of a given role
         *
         * @param message
         * @param notificationType
         * @param role
         * @throws DataAccessException
         */
        void sendNotificationByRole(String message, NotificationType notificationType, UserRole role)
                        throws DataAccessException;
}