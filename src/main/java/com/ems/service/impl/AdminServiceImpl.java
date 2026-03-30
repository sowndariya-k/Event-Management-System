package com.ems.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ems.dao.CategoryDao;
import com.ems.dao.EventDao;
import com.ems.dao.NotificationDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.UserDao;
import com.ems.dao.VenueDao;
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
import com.ems.service.NotificationService;

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
	private final UserDao userDao;
	private final EventDao eventDao;
	private final NotificationDao notificationDao;
	private final RegistrationDao registrationDao;
	private final NotificationService notificationService;
	private final CategoryDao categoryDao;
	private final VenueDao venueDao;

	public AdminServiceImpl(UserDao userDao, EventDao eventDao, NotificationDao notificationDao,
			RegistrationDao registrationDao, CategoryDao categoryDao, VenueDao venueDao,
			NotificationService notificationService) {
		this.userDao = userDao;
		this.eventDao = eventDao;
		this.notificationDao = notificationDao;
		this.registrationDao = registrationDao;
		this.categoryDao = categoryDao;
		this.venueDao = venueDao;
		this.notificationService = notificationService;
	}

	/*
	 * Retrieves users filtered by role. Used by admin to review attendees or
	 * organizers.
	 */
	@Override
	public List<User> getUsersList(String userType) throws DataAccessException {
		List<User> users = userDao.findAllUsers(userType);
		if (users.isEmpty()) {
			System.out.println("No users available for the selected role");
		}
		return users;
	}
	
	/*
	 * Retrieves all users sorted alphabetically.
	 */
	@Override
	public List<User> getAllUsers() {

		List<User> users = new ArrayList<>();
		try {
			users = userDao.findAllUsers();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

		if (users.isEmpty()) {
			System.out.println("No users found.");
		}

		// sorting
		users.sort(Comparator.comparing(User::getFullName));
		return users;

	}

	/*
	 * Updates user account status.
	 *
	 * Rule: - Admin accounts cannot be modified
	 */
	@Override
	public boolean changeStatus(UserStatus status, int userId) throws DataAccessException {
	    boolean updated = userDao.updateUserStatus(userId, status);

	    if (updated && UserStatus.ACTIVE.equals(status)) {
	        userDao.resetFailedAttempts(userId);
	    }

	    return updated;
	}

	/*
	 * Sends a notification message to all users in the system.
	 */
	@Override
	public void sendSystemWideNotification(String message, NotificationType notificationType)
			throws DataAccessException {
		notificationService.sendSystemWideNotification(message, notificationType);
		 System.out.println("The message has been sent to all users.");
		
	}

	/*
	 * Sends a notification to all users of a specific role.
	 */
	@Override
	public void sendNotificationByRole(String message, NotificationType selectedType, UserRole role)
			throws DataAccessException {
		notificationDao.sendNotificationByRole(message, selectedType, role);
		
	}

	/*
	 * Sends a notification to a specific user.
	 */
	@Override
	public void sendNotificationToUser(String message, NotificationType selectedType, int userId)
			throws DataAccessException {
		notificationDao.sendNotification(userId, message, selectedType);
		
	}

	/*
	 * Approves an event submitted by an organizer.
	 *
	 * Rule: - Organizer is notified after approval
	 */
	@Override
	public boolean approveEvents(int userId, int eventId) throws DataAccessException {
		boolean isApproved = eventDao.approveEvent(eventId, userId);
		if (isApproved) {
			notificationDao.sendNotification(eventDao.getOrganizerId(eventId),
					"Your event: " + eventId + " has been approved!", NotificationType.EVENT);
		}
		return isApproved;
	}

	/*
	 * Cancels an event.
	 *
	 * Rule: - Organizer is notified after cancellation
	 */
	@Override
	public void cancelEvent(int eventId) throws DataAccessException {
		boolean isCancelled = eventDao.cancelEvent(eventId);
		if (isCancelled) {
			notificationDao.sendNotification(eventDao.getOrganizerId(eventId),
					"Your event: " + eventId + " has been cancelled!", NotificationType.EVENT);
		}
	}

	/*
	 * Marks completed events based on end time.
	 */
	@Override
	public void markCompletedEvents() throws DataAccessException {
		try {
			eventDao.completeEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}

	/*
	 * Displays registration details for a specific event. Registrations are shown
	 * in reverse chronological order.
	 */
	@Override
	public List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException {
		List<EventRegistrationReport> reports = registrationDao.getEventWiseRegistrations(eventId);

		if (reports.isEmpty()) {
			return new ArrayList<>();
		}
		return reports;
	}

	/*
	 * Displays revenue generated per event.
	 */
	@Override
	public List<EventRevenueReport> getRevenueReport() throws DataAccessException {
		return eventDao.getEventWiseRevenueReport();
	}

	/*
	 * Displays organizer performance based on total events created.
	 */
	@Override
	public void getOrganizerWisePerformance() throws DataAccessException {
		Map<String, Integer> organizerStats = eventDao.getOrganizerWiseEventCount();

		if (organizerStats.isEmpty()) {
			System.out.println("No organizer data available.");
			return;
		}

		System.out.println("\nOrganizer Wise Performance");
		System.out.println("-----------------------------------");

		organizerStats.forEach((organizer, count) -> {
			System.out.println("Organizer : " + organizer + " | Total Events : " + count);
		});

		System.out.println("-----------------------------------");
	}

	/*
	 * Category management operations.
	 */
	@Override
	public List<Category> getAllCategories() throws DataAccessException {
		return categoryDao.getAllCategories();
	}

	@Override
	public void addCategory(String name) throws DataAccessException {
		categoryDao.addCategory(name);
	}

	@Override
	public void updateCategory(int categoryId, String name) throws DataAccessException {
		categoryDao.updateCategoryName(categoryId, name);
	}

	@Override
	public void updateCategory(int categoryId) throws DataAccessException {
		try {
			categoryDao.activateCategory(categoryId);
			} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	public void deleteCategory(int categoryId) throws DataAccessException {
		try {
			categoryDao.deactivateCategory(categoryId);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Venue management operations.
	 */
	@Override
	public void addVenue(Venue venue) throws DataAccessException {
		try {
			venueDao.addVenue(venue);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	public void updateVenue(Venue venue) throws DataAccessException {
		try {
			venueDao.updateVenue(venue);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	public void removeVenue(int venueId) throws DataAccessException {
		try {
			venueDao.deactivateVenue(venueId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		
	}

	@Override
	public void activateVenue(int venueId) throws DataAccessException {
		venueDao.activateVenue(venueId);
	}

	

}