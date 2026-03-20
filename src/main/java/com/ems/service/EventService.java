package com.ems.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;

public interface EventService {

    List<Event> viewEvents() ;
 // ticket information
 	List<Ticket> getTicketTypes(int eventId) throws DataAccessException;

 	int getAvailableTickets(int eventId) throws DataAccessException;

 	// event filtering
 	List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException;

    // event searching
 	List<Event> searchByCity(int cityId) throws DataAccessException;

 	List<Event> searchByDate(LocalDate localDate) throws DataAccessException;

 	List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException;

 	List<Event> searchByCategory(int categoryId) throws DataAccessException;

 	// event registration and booking
 	boolean registerForEvent(
 			int userId,
 			int eventId,
 			int ticketId,
 			int quantity,
 			double price,
 			PaymentMethod paymentMethod,
 			String offerCode) throws DataAccessException;

 	List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException;

 	// user event history
 	List<UserEventRegistration> viewUpcomingEvents(int userId) throws DataAccessException;

 	List<UserEventRegistration> viewPastEvents(int userId) throws DataAccessException;

 	// feedback
 	boolean submitRating(int userId, int eventId, int rating, String comments) throws DataAccessException;

 	// Event listing & retrival
 	List<Event> getAllEvents() throws DataAccessException;

 	List<Event> listAvailableEvents() throws DataAccessException;

 	List<Event> listEventsYetToApprove() throws DataAccessException;

 	List<Event> listAvailableAndDraftEvents() throws DataAccessException;

 	Event getEventById(int eventId) throws DataAccessException;

 	// Category & city lookups
 	Category getCategory(int eventId) throws DataAccessException;

 	List<Category> getAllCategories() throws DataAccessException;

 	Map<Integer, String> getAllCities() throws DataAccessException;

 	// Venue information & availability
 	String getVenueName(int venueId) throws DataAccessException;

 	String getVenueAddress(int venueId) throws DataAccessException;

 	List<Venue> getActiveVenues() throws DataAccessException;

 	List<Venue> getAllVenues() throws DataAccessException;

 	Venue getVenueById(int venueId) throws DataAccessException;

 	boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime) throws DataAccessException;

 	boolean cancelRegistration(int userId, int registrationId) throws DataAccessException;

 	boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException;
}