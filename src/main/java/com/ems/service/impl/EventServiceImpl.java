package com.ems.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ems.dao.*;
import com.ems.dao.impl.FeedbackDaoImpl;
import com.ems.dao.impl.NotificationDaoImpl;
import com.ems.dao.impl.PaymentDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.enums.PaymentMethod;
import com.ems.enums.RegistrationStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Registration;
import com.ems.model.Ticket;
import com.ems.model.Category;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.PaymentService;
import com.ems.util.DateTimeUtil;

public class EventServiceImpl implements EventService {
	private final EventDao eventDao;
    private final TicketDao ticketDao;
    private final CategoryDao categoryDao; 
    private final VenueDao venueDao;
    private final RegistrationDaoImpl registrationDao;
    private final PaymentDaoImpl paymentDao;
    private final PaymentService paymentService;
    private final FeedbackDaoImpl feedbackDao;
    private final NotificationDaoImpl notificationDao;

    public EventServiceImpl(EventDao eventDao, TicketDao ticketDao, CategoryDao categoryDao,VenueDao venueDao, RegistrationDaoImpl registrationDao,
                            PaymentDaoImpl paymentDao, PaymentService paymentService,
                            FeedbackDaoImpl feedbackDao, NotificationDaoImpl notificationDao) {
        this.eventDao = eventDao;
        this.ticketDao = ticketDao;
        this.categoryDao = categoryDao;
        this.venueDao = venueDao;
        this.registrationDao = registrationDao;
        this.paymentDao = paymentDao;
        this.paymentService = paymentService;
        this.feedbackDao = feedbackDao;
        this.notificationDao = notificationDao;
    }
	@Override
	public List<Event> viewEvents() {
		return eventDao.viewEvents();
	}

	@Override
	public Event getEventById(int eventId) throws DataAccessException {
		return eventDao.getEventById(eventId);
	}

	@Override
	public List<Ticket> getTicketTypes(int eventId) {
		return eventDao.getTicketsByEventId(eventId);
	}

	 @Override
	 public List<Event> listAvailableEvents() throws DataAccessException {
	      return eventDao.listAvailableEvents();
	 }

	 @Override
	    public boolean registerForEvent(
	            int userId,
	            int eventId,
	            int ticketId,
	            int quantity,
	            double price,
	            PaymentMethod paymentMethod,
	            String offerCode) throws DataAccessException {

	        String normalizedOfferCode = (offerCode != null && !offerCode.trim().isEmpty()) ? offerCode.trim().toUpperCase()
	                : "";

	        return paymentService.processRegistration(
	                userId,
	                eventId,
	                ticketId,
	                quantity,
	                price,
	                paymentMethod,
	                normalizedOfferCode);
	    }

	 @Override
	 public List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException {
		 return eventDao.filterByPrice(minPrice, maxPrice);
	 }

	 @Override
	 public List<Event> searchByCity(int venueId) throws DataAccessException {
		 return eventDao.searchByCity(venueId);
	 }

	@Override
	public List<Event> searchByDate(LocalDate date) throws DataAccessException {
		return eventDao.searchByDate(date);
	}

	@Override
	public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {
		return eventDao.searchByDateRange(startDate, endDate);
	}

	@Override
	public List<Event> searchByCategory(int categoryId) throws DataAccessException {
	    return eventDao.searchByCategory(categoryId);
	}
	
	// my registration
	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {
	    return eventDao.viewBookingDetails(userId);
	}

	@Override
	public List<UserEventRegistration> viewUpcomingEvents(int userId) throws DataAccessException {
	    return eventDao.getUserRegistrations(userId)
	            .stream()
	            .filter(reg -> reg.getStartDateTime() != null
	                    && reg.getStartDateTime().isAfter(java.time.Instant.now()))
	            .collect(java.util.stream.Collectors.toList());
	}

	@Override
	public List<UserEventRegistration> viewPastEvents(int userId) throws DataAccessException {
	    return eventDao.getUserRegistrations(userId)
	            .stream()
	            .filter(r -> r.getEndDateTime() != null)
	            .collect(Collectors.toList());
	}


	@Override
	public boolean cancelRegistration(int userId, int registrationId) throws DataAccessException {
	    Registration registration = registrationDao.getById(registrationId);

	    if (registration == null) {
	        throw new DataAccessException("Registration not found.");
	    }

	    if (registration.getUserId() != userId) {
	        throw new DataAccessException("Registration not found.");
	    }

	    if (registration.getStatus() == RegistrationStatus.CANCELLED) {
	        throw new DataAccessException("Registration is already cancelled.");
	    }

	    registrationDao.updateStatus(registrationId, RegistrationStatus.CANCELLED);
	    return true;
	}
	
	//Ticket availability
	@Override
	public int getAvailableTickets(int eventId) throws DataAccessException {
		return ticketDao.getAvailableTickets(eventId);
	}
	@Override
	public List<Event> getAllEvents() throws DataAccessException {
		return eventDao.listAllEvents();
	}
	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		return eventDao.listEventsYetToApprove();
	}
	@Override
	public List<Event> listAvailableAndDraftEvents() throws DataAccessException {
		return eventDao.listAvailableAndDraftEvents();
	}
	@Override
	public Category getCategory(int categoryId) throws DataAccessException {
		return categoryDao.getCategory(categoryId);
	}
	 @Override
	    public List<Category> getAllCategories() throws DataAccessException {
	        return categoryDao.getActiveCategories();
	    }
	@Override
	public Map<Integer, String> getAllCities() throws DataAccessException {
		 Map<Integer, String> cities = venueDao.getAllCities();
	        if (!(cities instanceof LinkedHashMap)) {
	            Map<Integer, String> sortedCities = new LinkedHashMap<>();
	            cities.entrySet().stream()
	                    .sorted(Map.Entry.comparingByValue())
	                    .forEachOrdered(e -> sortedCities.put(e.getKey(), e.getValue()));
	            return sortedCities;
	        }
	        return cities;
	}
	@Override
	public String getVenueName(int venueId) throws DataAccessException {
		return venueDao.getVenueName(venueId);
	}
	@Override
	public String getVenueAddress(int venueId) throws DataAccessException {
		return venueDao.getVenueAddress(venueId);
	}
	@Override
	public List<Venue> getActiveVenues() throws DataAccessException {
		return venueDao.getActiveVenues();
	}
	@Override
	public List<Venue> getAllVenues() throws DataAccessException {
		return venueDao.getAllVenues();
	}
	@Override
	public Venue getVenueById(int venueId) throws DataAccessException {
		return venueDao.getVenueById(venueId);
	}
	@Override
	public boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime)
			throws DataAccessException {
		        if (startTime == null || endTime == null)
		            return false;
		        return venueDao.isVenueAvailable(
		                venueId,
		                DateTimeUtil.toTimestamp(DateTimeUtil.toUtcInstant(startTime)),
		                DateTimeUtil.toTimestamp(DateTimeUtil.toUtcInstant(endTime)));
	}
	
	@Override
	public boolean submitRating(int userId, int eventId, int rating, String comments) throws DataAccessException {
	    String normalizedComments = (comments == null || comments.trim().isEmpty()) ? null : comments.trim();

	    if (feedbackDao.isRatingAlreadySubmitted(eventId, userId)) {
	        return false;
	    }

	    return feedbackDao.submitRating(eventId, userId, rating, normalizedComments);
	}
	
	@Override
	public boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException {
		return feedbackDao.isRatingAlreadySubmitted(eventId, userId);
	}
 
}