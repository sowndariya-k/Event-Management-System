package com.ems.dao;

import java.util.List;

import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;

public interface EventDao {

    List<Event> viewEvents();

    List<Event> listAvailableEvents(); // ADDED: was missing, called in UserMenu

    Event viewEventDetails(int eventId);

    List<Ticket> viewTicketOptions(int eventId);

    List<Event> viewUpcomingEvents();

    List<Event> viewPastEvents();

    List<BookingDetail> viewBookingDetails(int userId);
    
    List<Event> searchByCategory(int categoryId);
    
    List<Event> searchByDate(String date);
    
    List<Event> searchByCity(String city);
    
    List<Event> filterByPrice(double maxPrice);
    
    List<Event> filterByAvailability();

}