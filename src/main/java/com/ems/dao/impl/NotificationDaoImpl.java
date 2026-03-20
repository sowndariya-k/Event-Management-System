package com.ems.dao.impl;

import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;

/*
 * Handles database operations related to notifications.
 *
 * Responsibilities:
 * - Persist system, role based, and user specific notifications
 * - Retrieve user notifications
 * - Update notification read state
 */
public class NotificationDaoImpl implements NotificationDao {

	@Override
	public List<Notification> getUnreadNotifications(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markAsRead(int notificationId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Notification> getAllNotifications(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markAllAsRead(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean sendNotification(int userId, String message, NotificationType notificationType)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendSystemWideNotification(String message, NotificationType notificationType)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendNotificationByRole(String message, NotificationType notificationType, UserRole role)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}


	

}
