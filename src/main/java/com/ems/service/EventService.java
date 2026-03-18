package com.ems.service;

import java.util.List;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;

public interface EventService {
    List<Event> viewEvents();
    List<Event> listAvailableEvents();
    Event viewEventDetails(int eventId);
    List<Ticket> viewTicketOptions(int eventId);
    List<Event> viewUpcomingEvents();
    List<Event> viewPastEvents();
    List<BookingDetail> viewBookingDetails(int userId);
}