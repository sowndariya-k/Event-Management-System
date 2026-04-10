/*
 * Author : Sowndariya
 * NotificationService is the service interface that
 * declares contracts for sending and retrieving notifications
 * to users and organizers based on notification type and
 * recipient.
 */
package com.ems.service;

import com.ems.exception.DataAccessException;
import com.ems.enums.NotificationType;

public interface NotificationService {

	// send notifications
	void sendSystemWideNotification(String message, NotificationType notificationType) throws DataAccessException;

	void sendEventNotification(int eventId, String message, NotificationType type) throws DataAccessException;

	// read notifications
	void displayUnreadNotifications(int userId) throws DataAccessException;

	//
	void sendPersonalNotification(int userId, String message, NotificationType type) throws DataAccessException;

	void displayAllNotifications(int userId) throws DataAccessException;
}