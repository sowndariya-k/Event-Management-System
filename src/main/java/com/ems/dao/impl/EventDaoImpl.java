package com.ems.dao.impl;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.dao.CategoryDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Category;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.model.UserEventRegistration;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class EventDaoImpl implements EventDao {

    Connection con;

    public EventDaoImpl() {
        try {
            con = DBConnectionUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> viewEvents() {

        List<Event> events = new ArrayList<>();

        try {

            String query = "SELECT * FROM events";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Event event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    // 2 View Event Details
    @Override
    public Event getEventById(int eventId) {

        Event event = null;

        try {

            String query = "SELECT * FROM events WHERE event_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setCategoryId(rs.getInt("category_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));

                Timestamp approvedAt = rs.getTimestamp("approved_at");
                if (approvedAt != null) {
                    event.setApprovedAt(approvedAt.toInstant());
                }

                event.setApprovedBy(rs.getInt("approved_by"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    event.setCreatedAt(createdAt.toInstant());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    event.setUpdatedAt(updatedAt.toInstant());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return event;
    }

 // 3 View Ticket Options
    @Override
    public List<Ticket> getTicketsByEventId(int eventId) {

        List<Ticket> tickets = new ArrayList<>();

        try {

            String query = "SELECT * FROM tickets WHERE event_id=?";  // FIXED: ticket → tickets
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ticket ticket = new Ticket();

                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setTicketType(rs.getString("ticket_type"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setTotalQuantity(rs.getInt("total_quantity"));
                ticket.setAvailableQuantity(rs.getInt("available_quantity"));

                tickets.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // 4 List Available Events
    @Override
	public List<Event> listAvailableEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "select * from events e " +
				"where e.status = ? " +
				"and e.start_datetime > UTC_TIMESTAMP() " +
				"and exists (select 1 from tickets t " +
				"where t.event_id = e.event_id " +
				"and t.available_quantity > 0)";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, EventStatus.PUBLISHED.toString());
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching available events", e);
		}
		return events;
	}

 // Shared mapper to convert ResultSet into Event objects
 	public List<Event> getEventList(ResultSet rs) throws DataAccessException {
 		List<Event> events = new ArrayList<>();
 		try {
 			while (rs.next()) {
 				Event event = new Event();
 				event.setEventId(rs.getInt("event_id"));
 				event.setOrganizerId(rs.getInt("organizer_id"));
 				event.setTitle(rs.getString("title"));
 				String desc = rs.getString("description");
 				if (desc != null && !desc.isEmpty()) {
 					event.setDescription(desc);
 				}
 				event.setCategoryId(rs.getInt("category_id"));
 				event.setVenueId(rs.getInt("venue_id"));
 				event.setStartDateTime(DateTimeUtil.fromTimestamp(rs.getTimestamp("start_datetime")));
 				event.setEndDateTime(DateTimeUtil.fromTimestamp(rs.getTimestamp("end_datetime")));

 				Timestamp updatedAt = rs.getTimestamp("updated_at");
 				if (updatedAt != null)
 					event.setUpdatedAt(DateTimeUtil.fromTimestamp(updatedAt));

 				Timestamp approvedAt = rs.getTimestamp("approved_at");
 				if (approvedAt != null)
 					event.setApprovedAt(DateTimeUtil.fromTimestamp(approvedAt));

 				Timestamp createdAt = rs.getTimestamp("created_at");
 				if (createdAt != null)
 					event.setCreatedAt(DateTimeUtil.fromTimestamp(createdAt));

 				event.setCapacity(rs.getInt("capacity"));
 				try {
 					event.setStatus(EventStatus.valueOf(rs.getString("status")));
 				} catch (IllegalArgumentException e) {
 					event.setStatus(EventStatus.DRAFT);
 				}
 				Integer approvedBy = rs.getInt("approved_by");
 				if (approvedBy != 0) {
 					event.setApprovedBy(approvedBy);
 				}

 				events.add(event);
 			}
 			rs.close();
 		} catch (SQLException e) {
 			throw new DataAccessException("Error while fetching event list");
 		}

 		return events;
 	}


	@Override
	public List<Event> listAllEvents() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean approveEvent(int eventId, int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancelEvent(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOrganizerId(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Event> listAvailableAndDraftEvents() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void completeEvents() throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UserEventRegistration> getUserRegistrations(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getOrganizerWiseEventCount() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createEvent(Event event) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateEventStatus(int eventId, EventStatus status) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Event> getEventsByOrganizer(int organizerId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrganizerEventSummary> getEventSummaryByOrganizer(int organizerId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventRevenueReport> getEventWiseRevenueReport() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateEventSchedule(int eventId, Instant start, Instant end) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	

	// ---------------------- SEARCH & FILTER METHODS ----------------------
	
	// ---------------------- SEARCH BY CATEGORY ----------------------

	@Override
	public List<Event> searchByCategory(int categoryId) {

	    List<Event> events = new ArrayList<>();

	    try {
	        String query = "SELECT * FROM events WHERE category_id = ?";
	        PreparedStatement ps = con.prepareStatement(query);
	        ps.setInt(1, categoryId);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {

	            Event event = new Event();

	            event.setEventId(rs.getInt("event_id"));
	            event.setTitle(rs.getString("title"));
	            event.setCategoryId(rs.getInt("category_id"));
	            event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
	            event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
	            event.setVenueId(rs.getInt("venue_id"));
	            event.setCapacity(rs.getInt("capacity"));

	            events.add(event);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return events;
	}
	
	@Override
	public List<Category> getAllCategories() throws DataAccessException {

	    List<Category> categories = new ArrayList<>();

	    String query = "SELECT * FROM categories";

	    try (PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Category c = new Category(
	                    rs.getInt("category_id"),
	                    rs.getString("name")
	            );
	            categories.add(c);
	        }

	    } catch (SQLException e) {
	        throw new DataAccessException("Error fetching categories", e);
	    }

	    return categories;
	}

	// ---------------------- SEARCH BY DATE ----------------------
	
	@Override
	public List<Event> searchByDate(String date) {

	    List<Event> events = new ArrayList<>();

	    try {
	        String query = "SELECT * FROM events WHERE DATE(start_datetime) = ?";
	        PreparedStatement ps = con.prepareStatement(query);
	        ps.setString(1, date);

	        ResultSet rs = ps.executeQuery();

	        events = getEventList(rs); // ✅ reuse

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return events;
	}	
	
	// ---------------------- SEARCH BY DATE RANGE ----------------------
	
	@Override
	public List<Event> searchByDateRange(String startDate, String endDate) throws DataAccessException {

	    List<Event> events = new ArrayList<>();

	    String query = "SELECT * FROM events WHERE DATE(start_datetime) BETWEEN ? AND ?";

	    try (PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setString(1, startDate);
	        ps.setString(2, endDate);

	        ResultSet rs = ps.executeQuery();

	        events = getEventList(rs);

	    } catch (SQLException e) {
	        throw new DataAccessException("Error while searching events by date range", e);
	    }

	    return events;
	}
	
	// ---------------------- SEARCH BY CITY ----------------------

	@Override
	public List<Event> searchByCity(String city) {

	    List<Event> events = new ArrayList<>();

	    try {
	        String query = "SELECT e.* FROM events e " +
	                       "JOIN venues v ON e.venue_id = v.venue_id " +
	                       "WHERE LOWER(TRIM(v.city)) = LOWER(TRIM(?))";

	        PreparedStatement ps = con.prepareStatement(query);
	        ps.setString(1, city);

	        ResultSet rs = ps.executeQuery();

	        events = getEventList(rs); // reuse mapper

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return events;
	}
	
	// ---------------------- FILTER BY PRICE ----------------------

	@Override
	public List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException {

	    List<Event> events = new ArrayList<>();

	    String query = "SELECT DISTINCT e.* FROM events e " +
	                   "JOIN tickets t ON e.event_id = t.event_id " +
	                   "WHERE t.price >= ? AND t.price <= ?";

	    try (PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setDouble(1, minPrice);
	        ps.setDouble(2, maxPrice);

	        ResultSet rs = ps.executeQuery();

	        // ✅ USE COMMON MAPPER (IMPORTANT FIX)
	        events = getEventList(rs);

	    } catch (SQLException e) {
	        throw new DataAccessException("Error filtering events by price", e);
	    }

	    return events;
	}

	// ---------------------- FILTER BY AVAILABILITY ----------------------
	
	@Override
	public List<Event> filterByAvailability() {

	    List<Event> events = new ArrayList<>();

	    try {
	        String query = "SELECT DISTINCT e.* FROM events e " +
	                       "JOIN tickets t ON e.event_id = t.event_id " +
	                       "WHERE t.available_quantity > 0";

	        PreparedStatement ps = con.prepareStatement(query);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {

	            Event event = new Event();

	            event.setEventId(rs.getInt("event_id"));
	            event.setTitle(rs.getString("title"));
	            event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
	            event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
	            event.setVenueId(rs.getInt("venue_id"));
	            event.setCategoryId(rs.getInt("category_id"));
	            event.setCapacity(rs.getInt("capacity"));

	            events.add(event);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return events;
	}

}