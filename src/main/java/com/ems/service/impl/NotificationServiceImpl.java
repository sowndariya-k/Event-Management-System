package com.ems.service.impl;

import java.util.Comparator;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.impl.NotificationDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.enums.NotificationType;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;
import com.ems.service.NotificationService;
import com.ems.util.InputValidationUtil;
/*
 * Handles notification related business operations.
 *
 * Responsibilities:
 * - Send notifications to users and groups
 * - Display user notifications
 * - Maintain read and unread notification states
 */
public class NotificationServiceImpl implements NotificationService {

	@Override
	public void sendSystemWideNotification(String message, NotificationType notificationType)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEventNotification(int eventId, String message, NotificationType type) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayUnreadNotifications(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPersonalNotification(int userId, String message, NotificationType type) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayAllNotifications(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}
	 

}
