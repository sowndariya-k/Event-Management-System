package com.ems.service;

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

public interface AdminService {

	// user management
	List<User> getUsersList(String userType) throws DataAccessException;

	List<User> getAllUsers() throws DataAccessException;

	boolean changeStatus(UserStatus status, int userId) throws DataAccessException;

	// notification management
	void sendSystemWideNotification(String message, NotificationType notificationType) throws DataAccessException;

	void sendNotificationByRole(String message, NotificationType selectedType, UserRole role)
			throws DataAccessException;

	void sendNotificationToUser(String message, NotificationType selectedType, int userId) throws DataAccessException;

	// event management
	boolean approveEvents(int userId, int eventId) throws DataAccessException;

	void cancelEvent(int eventId) throws DataAccessException;

	void markCompletedEvents() throws DataAccessException;

	// reports & analytics
	List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException;

	List<EventRevenueReport> getRevenueReport() throws DataAccessException;

	void getOrganizerWisePerformance() throws DataAccessException;

	// category management
	List<Category> getAllCategories() throws DataAccessException;

	void addCategory(String name) throws DataAccessException;

	void updateCategory(int categoryId, String name) throws DataAccessException;

	void updateCategory(int categoryId) throws DataAccessException;

	void deleteCategory(int categoryId) throws DataAccessException;

	// Venue management
	void addVenue(Venue venue) throws DataAccessException;

	void updateVenue(Venue selectedVenue) throws DataAccessException;

	void removeVenue(int venueId) throws DataAccessException;

	void activateVenue(int venueId) throws DataAccessException;
}