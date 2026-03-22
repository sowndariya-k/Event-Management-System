package com.ems.dao.impl;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
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
                event.setCategoryId(rs.getInt("category_id"));

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
 	    List<Event> events = new ArrayList<>();
 	    String query = "SELECT * FROM events"; // get everything, ignore filters
 	    try (PreparedStatement ps = con.prepareStatement(query);
 	         ResultSet rs = ps.executeQuery()) {
 	        events = getEventList(rs); // reuse your mapper
 	    } catch (SQLException e) {
 	        throw new DataAccessException("Error fetching all events", e);
 	    }
 	    return events;
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
		 String sql = "UPDATE registrations SET status = 'CANCELLED' WHERE event_id = ? AND status = 'CONFIRMED'";
		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
		        ps.setInt(1, eventId);
		        int rows = ps.executeUpdate();
		        return rows > 0;
		    } catch (SQLException e) {
		        throw new DataAccessException("Error cancelling event registrations", e);
		    }
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
	    List<UserEventRegistration> registrations = new ArrayList<>();
	    String sql = "SELECT r.registration_id, r.user_id, r.event_id, r.registration_date, r.status AS reg_status, " +
	                 "e.title, e.start_datetime, e.end_datetime, " +
	                 "c.name AS category_name, " +
	                 "t.ticket_type, rt.quantity, p.amount " +
	                 "FROM registrations r " +
	                 "JOIN events e ON r.event_id = e.event_id " +
	                 "JOIN categories c ON e.category_id = c.category_id " +
	                 "LEFT JOIN registration_tickets rt ON r.registration_id = rt.registration_id " +
	                 "LEFT JOIN tickets t ON rt.ticket_id = t.ticket_id " +
	                 "LEFT JOIN payments p ON r.registration_id = p.registration_id " +
	                 "WHERE r.user_id = ? AND r.status = 'CONFIRMED'";
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            UserEventRegistration reg = new UserEventRegistration();
	            reg.setRegistrationId(rs.getInt("registration_id"));
	            reg.setEventId(rs.getInt("event_id"));
	            reg.setTitle(rs.getString("title"));                                    // FIXED
	            reg.setCategory(rs.getString("category_name"));                        // FIXED
	            reg.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
	            reg.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
	            reg.setRegistrationDate(rs.getTimestamp("registration_date").toInstant());
	            reg.setRegistrationStatus(rs.getString("reg_status"));                 // FIXED
	            reg.setTicketType(rs.getString("ticket_type"));                        // FIXED
	            reg.setTicketsPurchased(rs.getInt("quantity"));                        // FIXED
	            reg.setAmountPaid(rs.getDouble("amount"));                             // FIXED
	            registrations.add(reg);
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error fetching user registrations", e);
	    }
	    return registrations;
	}

	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {
	    List<BookingDetail> bookings = new ArrayList<>();
	    String sql = "SELECT r.registration_id, e.title, e.start_datetime, v.name AS venue_name, v.city, " +
	                 "t.ticket_type, rt.quantity, p.amount AS total_cost " +
	                 "FROM registrations r " +
	                 "JOIN events e ON r.event_id = e.event_id " +
	                 "JOIN venues v ON e.venue_id = v.venue_id " +
	                 "LEFT JOIN registration_tickets rt ON r.registration_id = rt.registration_id " +
	                 "LEFT JOIN tickets t ON rt.ticket_id = t.ticket_id " +
	                 "LEFT JOIN payments p ON r.registration_id = p.registration_id " +
	                 "WHERE r.user_id = ? AND r.status = 'CONFIRMED'";
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            BookingDetail booking = new BookingDetail(
	            	rs.getInt("registration_id"),	
	                rs.getString("title"),
	                rs.getTimestamp("start_datetime").toInstant(),
	                rs.getString("venue_name"),
	                rs.getString("city"),
	                rs.getString("ticket_type"),
	                rs.getInt("quantity"),
	                rs.getDouble("total_cost")
	            );
	            bookings.add(booking);
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error fetching booking details", e);
	    }
	    return bookings;
	}

	@Override
	public Map<String, Integer> getOrganizerWiseEventCount() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	// ----------------------organizer functions----------------------

	@Override
	public int createEvent(Event event) throws DataAccessException {
		
		return 0;
	}
	
	@Override
	public boolean updateEventSchedule(int eventId, Instant start, Instant end) throws DataAccessException {
		String sql = "update events set start_datetime=?, end_datetime=?, updated_at=? where event_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setTimestamp(1, DateTimeUtil.toTimestamp(start));
			ps.setTimestamp(2, DateTimeUtil.toTimestamp(end));
			Instant now = DateTimeUtil.nowUtc();
			ps.setTimestamp(3, Timestamp.from(now));

			ps.setInt(4, eventId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Failed to update the event schedule");
		}
	}

	@Override
	public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId)
			throws DataAccessException {
		
		return false;
	}

	@Override
	public boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException {
		
		return false;
	}

	@Override
	public boolean updateEventStatus(int eventId, EventStatus status) throws DataAccessException {
		
		return false;
	}
	
	//----------------------end

	@Override
	public List<Event> getEventsByOrganizer(int organizerId) throws DataAccessException {
		
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
	public List<EventRevenueReport> getEventWiseRevenueReportByOrganizer(int organizerId)
	        throws DataAccessException {

	    return null;
	}
	

	// ---------------------- SEARCH & FILTER METHODS ----------------------
	
	// ---------------------- SEARCH BY CATEGORY ----------------------

	@Override
	public List<Event> searchByCategory(int categoryId) throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE category_id = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, categoryId);
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by category", e);
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
	public List<Event> searchByDate(LocalDate date) throws DataAccessException {
	    if (date == null) return new ArrayList<>();
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE DATE(start_datetime) = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, date.toString());
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by date", e);
	    }
	    return events;
	}
	// ---------------------- SEARCH BY DATE RANGE ----------------------
	
	@Override
	public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {
	    if (startDate == null || endDate == null || startDate.isAfter(endDate)) return new ArrayList<>();
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE DATE(start_datetime) BETWEEN ? AND ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, startDate.toString());
	        ps.setString(2, endDate.toString());
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by date range", e);
	    }
	    return events;
	}
	
	// ---------------------- SEARCH BY CITY ----------------------

	@Override
	public List<Event> searchByCity(int venueId) throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE venue_id = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, venueId);
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by city", e);
	    }
	    return events;
	}

	
	// ---------------------- FILTER BY PRICE ----------------------

	@Override
	public List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT DISTINCT e.* FROM events e " +
	                   "JOIN tickets t ON e.event_id = t.event_id " +
	                   "WHERE t.price BETWEEN ? AND ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setDouble(1, minPrice);
	        ps.setDouble(2, maxPrice);
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error filtering by price", e);
	    }
	    return events;
	}

	// ---------------------- FILTER BY AVAILABILITY ----------------------
	
	@Override
	public List<Event> filterByAvailability() throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT DISTINCT e.* FROM events e " +
	                   "JOIN tickets t ON e.event_id = t.event_id " +
	                   "WHERE t.available_quantity > 0";
	    try (PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {
	        events = getEventList(rs);
	    } catch (SQLException e) {
	        throw new DataAccessException("Error filtering by availability", e);
	    }
	    return events;
	}

}