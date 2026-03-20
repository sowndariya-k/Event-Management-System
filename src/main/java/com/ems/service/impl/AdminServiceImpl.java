package com.ems.service.impl;

import java.util.List;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.enums.UserStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.EventRegistrationReport;
import com.ems.model.EventRevenueReport;
import com.ems.model.User;
import com.ems.model.Venue;
import com.ems.service.AdminService;

/*
 * Handles administrative business operations.
 *
 * Responsibilities:
 * - Manage users, categories, venues, and events
 * - Approve or cancel events
 * - Generate administrative reports
 * - Send system and targeted notifications
 */
public class AdminServiceImpl implements AdminService {

	@Override
	public List<User> getUsersList(String userType) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changeStatus(UserStatus status, int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendSystemWideNotification(String message, NotificationType notificationType)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendNotificationByRole(String message, NotificationType selectedType, UserRole role)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendNotificationToUser(String message, NotificationType selectedType, int userId)
			throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean approveEvents(int userId, int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelEvent(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markCompletedEvents() throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventRevenueReport> getRevenueReport() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getOrganizerWisePerformance() throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Category> getAllCategories() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCategory(String name) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCategory(int categoryId, String name) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCategory(int categoryId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteCategory(int categoryId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addVenue(Venue venue) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateVenue(Venue selectedVenue) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeVenue(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateVenue(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	

}
