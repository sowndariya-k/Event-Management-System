/*
 * Author : Sowndariya, Mythily, Jagadeep
 * EventServiceImpl implements the EventService interface
 * and provides the business logic for event browsing,
 * searching by filters, and retrieving event details by
 * delegating to the EventDao and related DAOs.
 */
 
package com.ems.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ems.dao.*;
import com.ems.dao.impl.CategoryDaoImpl;
import com.ems.dao.impl.TicketDaoImpl;
import com.ems.dao.impl.VenueDaoImpl;
import com.ems.enums.NotificationType;
import com.ems.enums.PaymentMethod;
import com.ems.enums.RegistrationStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Registration;
import com.ems.model.RegistrationTicket;
import com.ems.model.Ticket;
import com.ems.model.Category;
import com.ems.model.UserEventRegistration;
import com.ems.model.Venue;
import com.ems.service.EventService;
import com.ems.service.PaymentService;
import com.ems.util.DateTimeUtil;

public class EventServiceImpl implements EventService {

    private final EventDao eventDao;
    private final CategoryDao categoryDao;
    private final PaymentDao paymentDao;
    private final VenueDao venueDao;
    private final TicketDao ticketDao;
    private final PaymentService paymentService;
    private final FeedbackDao feedbackDao;
    private final RegistrationDao registrationDao;
    private final NotificationDao notificationDao;

    public EventServiceImpl(EventDao eventDao, TicketDaoImpl ticketDao, CategoryDaoImpl categoryDao, VenueDaoImpl venueDao,
            RegistrationDao registrationDao, PaymentDao paymentDao, PaymentService paymentService,
            FeedbackDao feedbackDao, NotificationDao notificationDao) {
        this.eventDao = eventDao;
        this.categoryDao = categoryDao;
        this.venueDao = venueDao;
        this.ticketDao = ticketDao;
        this.paymentService = paymentService;
        this.feedbackDao = feedbackDao;
        this.registrationDao = registrationDao;
        this.paymentDao = paymentDao;
        this.notificationDao = notificationDao;
    }

    @Override
    public List<Ticket> getTicketTypes(int eventId) throws DataAccessException {
        return ticketDao.getTicketTypes(eventId);
    }

    @Override
    public List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return new ArrayList<>();
        }

        List<Event> allEvents = eventDao.listAvailableEvents();
        if (allEvents.isEmpty()) {
            return new ArrayList<>();
        }

        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : allEvents) {
            List<Ticket> tickets = ticketDao.getTicketTypes(event.getEventId());
            if (tickets.stream().anyMatch(t -> t.getPrice() >= minPrice && t.getPrice() <= maxPrice)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    @Override
    public Event getEventById(int eventId) throws DataAccessException {
        return eventDao.getEventById(eventId);
    }

    @Override
    public List<Event> searchByCity(int venueId) throws DataAccessException {
        List<Event> allEvents = eventDao.listAvailableEvents();

        return allEvents.stream()
                .filter(e -> e.getVenueId() == venueId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> searchByDate(LocalDate localDate) throws DataAccessException {
        if (localDate == null) {
            return new ArrayList<>();
        }

        List<Event> allEvents = eventDao.listAvailableEvents();

        return allEvents.stream()
                .filter(e -> e.getStartDateTime() != null &&
                        DateTimeUtil.toLocalDateTime(e.getStartDateTime()).toLocalDate().isEqual(localDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return new ArrayList<>();
        }

        List<Event> allEvents = eventDao.listAvailableEvents();

        return allEvents.stream().filter(e -> {
            if (e.getStartDateTime() == null) {
                return false;
            }
            LocalDate eventDate = DateTimeUtil.toLocalDateTime(e.getStartDateTime()).toLocalDate();
            return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Event> searchBycategory(int selectedCategoryId) throws DataAccessException {
        List<Event> allEvents = eventDao.listAvailableEvents();
        return allEvents.stream()
                .filter(e -> e.getCategoryId() == selectedCategoryId)
                .collect(Collectors.toList());
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
    public List<UserEventRegistration> viewUpcomingEvents(int userId) throws DataAccessException {
        List<UserEventRegistration> registrations = eventDao.getUserRegistrations(userId);

        return registrations.stream()
                .filter(r -> r.getStartDateTime() != null &&
                        r.getStartDateTime().isAfter(DateTimeUtil.nowUtc()) &&
                        RegistrationStatus.CONFIRMED.name().equals(r.getRegistrationStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserEventRegistration> viewPastEvents(int userId) throws DataAccessException {
        List<UserEventRegistration> registrations = eventDao.getUserRegistrations(userId);
        return registrations.stream()
                .filter(r -> r.getEndDateTime() != null
                        && r.getEndDateTime().isBefore(DateTimeUtil.nowUtc()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {
        return eventDao.viewBookingDetails(userId);
    }

    @Override
    public boolean submitRating(int userId, int eventId, int rating, String comments) throws DataAccessException {
        String normalizedComments = (comments == null || comments.trim().isEmpty()) ? null : comments.trim();
        if (feedbackDao.isRatingAlreadySubmitted(eventId, userId)) {
            return false;
        }

        boolean isSuccess = feedbackDao.submitRating(eventId, userId, rating, normalizedComments);
        return isSuccess;
    }

    @Override
    public List<Event> getAllEvents() throws DataAccessException {
        return eventDao.listAllEvents();
    }

    @Override
    public Category getCategory(int categoryId) throws DataAccessException {
        return categoryDao.getCategory(categoryId);
    }

    @Override
    public int getAvailableTickets(int eventId) throws DataAccessException {
        return ticketDao.getAvailableTickets(eventId);
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
    public List<Category> getAllCategory() throws DataAccessException {
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
    public List<Event> listAvailableEvents() throws DataAccessException {
        return eventDao.listAvailableEvents();
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
    public Venue getVenueById(int venueId) throws DataAccessException {
        return venueDao.getVenueById(venueId);
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
    public boolean cancelRegistration(int userId, int registrationId) throws DataAccessException {

        Registration reg = registrationDao.getById(registrationId);

        if (reg == null) {
            System.out.println("DEBUG: Registration not found");
            return false;
        }

        if (reg.getUserId() != userId) {
            System.out.println("DEBUG: User mismatch");
            return false;
        }

        if (reg.getStatus() != RegistrationStatus.CONFIRMED) {
            System.out.println("DEBUG: Status is " + reg.getStatus());
            return false;
        }

        registrationDao.updateStatus(registrationId, RegistrationStatus.CANCELLED);

        List<RegistrationTicket> tickets = registrationDao.getRegistrationTickets(registrationId);
        for (RegistrationTicket rt : tickets) {
            ticketDao.updateAvailableQuantity(rt.getTicketId(), rt.getQuantity());
        }

        paymentDao.updatePaymentStatus(registrationId);

        notificationDao.sendNotification(
                userId,
                "Your registration has been cancelled. Refund will be processed within 5-7 business days.",
                NotificationType.EVENT
        );

        return true;
    }


    @Override
    public boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException {
        return feedbackDao.isRatingAlreadySubmitted(eventId, userId);
    }
}