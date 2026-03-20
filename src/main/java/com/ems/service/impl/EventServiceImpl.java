package com.ems.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.dao.CategoryDao;
import com.ems.dao.EventDao;
import com.ems.dao.TicketDao;
import com.ems.dao.impl.FeedbackDaoImpl;
import com.ems.dao.impl.NotificationDaoImpl;
import com.ems.dao.impl.PaymentDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.PaymentService;

public class EventServiceImpl implements EventService {
	private final EventDao eventDao;
    private final TicketDao ticketDao;
    private final CategoryDao categoryDao; 
    private final RegistrationDaoImpl registrationDao;
    private final PaymentDaoImpl paymentDao;
    private final PaymentService paymentService;
    private final FeedbackDaoImpl feedbackDao;
    private final NotificationDaoImpl notificationDao;

    public EventServiceImpl(EventDao eventDao, TicketDao ticketDao, CategoryDao categoryDao, RegistrationDaoImpl registrationDao,
                            PaymentDaoImpl paymentDao, PaymentService paymentService,
                            FeedbackDaoImpl feedbackDao, NotificationDaoImpl notificationDao) {
        this.eventDao = eventDao;
        this.ticketDao = ticketDao;
        this.categoryDao = categoryDao;
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
	 public List<Event> searchByCity(String city) throws DataAccessException {
	     return eventDao.searchByCity(city);
	 }

	@Override
	public List<Event> searchByDate(LocalDate date) throws DataAccessException {
	    return eventDao.searchByDate(date.toString());
	}

	@Override
	public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {

	    return eventDao.searchByDateRange(startDate.toString(), endDate.toString());
	}

	@Override
	public List<Event> searchByCategory(int categoryId) throws DataAccessException {
	    return eventDao.searchByCategory(categoryId);
	}
	
	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {
		return null;
	}

	@Override
	public List<UserEventRegistration> viewUpcomingEvents(int userId) throws DataAccessException {
		return null;
	}

	@Override
	public List<UserEventRegistration> viewPastEvents(int userId) throws DataAccessException {
		return null;
	}

	@Override
	public boolean submitRating(int userId, int eventId, int rating, String comments) throws DataAccessException {
		return false;
	}

	@Override
	public boolean cancelRegistration(int userId, int registrationId) throws DataAccessException {
		return false;
	}
	@Override
	public int getAvailableTickets(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<Event> getAllEvents() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Event> listAvailableAndDraftEvents() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Category getCategory(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Category> getAllCategories() throws DataAccessException {
		return categoryDao.getAllCategories();
	}
	@Override
	public Map<Integer, String> getAllCities() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getVenueName(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getVenueAddress(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Venue> getActiveVenues() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Venue> getAllVenues() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Venue getVenueById(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isVenueAvailable(int venueId, LocalDateTime startTime, LocalDateTime endTime)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

}