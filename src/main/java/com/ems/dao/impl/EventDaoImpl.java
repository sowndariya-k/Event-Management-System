package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.util.DBConnectionUtil;

public class EventDaoImpl implements EventDao {

	// Helper method to map a ResultSet row to an Event object
	private Event mapRowToEvent(ResultSet rs) throws SQLException {
		Event event = new Event();
		event.setEventId(rs.getInt("event_id"));
		event.setOrganizerId(rs.getInt("organizer_id"));
		event.setTitle(rs.getString("title"));
		event.setDescription(rs.getString("description"));
		event.setCategoryId(rs.getInt("category_id"));
		event.setVenueId(rs.getInt("venue_id"));
		
		if(rs.getTimestamp("start_datetime") != null) {
			event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
		}
		if(rs.getTimestamp("end_datetime") != null) {
			event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
		}
		
		event.setCapacity(rs.getInt("capacity"));
		
		String status = rs.getString("status");
		if(status != null) {
			event.setStatus(EventStatus.valueOf(status));
		}
		
		int approvedBy = rs.getInt("approved_by");
		if(!rs.wasNull()) {
			event.setApprovedBy(approvedBy);
		}
		
		if(rs.getTimestamp("approved_at") != null) {
			event.setApprovedAt(rs.getTimestamp("approved_at").toInstant());
		}
		if(rs.getTimestamp("created_at") != null) {
			event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
		}
		if(rs.getTimestamp("updated_at") != null) {
			event.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
		}
		
		return event;
	}

	@Override
	public List<Event> listAvailableEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		// Events that are published/approved, in the future, and have available tickets
		String query = "SELECT DISTINCT e.* FROM events e "
				+ "JOIN tickets t ON e.event_id = t.event_id "
				+ "WHERE e.status IN ('PUBLISHED', 'APPROVED') AND e.start_datetime > NOW() "
				+ "AND t.available_quantity > 0";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				events.add(mapRowToEvent(rs));
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving events", e);
		}
		return events;
	}

	@Override
	public Event getEventById(int eventId) throws DataAccessException {
		Event event = null;
		String query = "SELECT * FROM events WHERE event_id = ?";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				event = mapRowToEvent(rs);
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving event details", e);
		}
		return event;
	}

	@Override
	public List<Ticket> getTicketsByEventId(int eventId) throws DataAccessException {
		List<Ticket> tickets = new ArrayList<>();
		String query = "SELECT * FROM tickets WHERE event_id = ?";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Ticket ticket = new Ticket();
				ticket.setTicketId(rs.getInt("ticket_id"));
				ticket.setEventId(rs.getInt("event_id"));
				ticket.setTicketType(rs.getString("ticket_type"));
				ticket.setPrice(rs.getDouble("price"));
				ticket.setTotalQuantity(rs.getInt("total_quantity"));
				ticket.setAvailableQuantity(rs.getInt("available_quantity"));
				tickets.add(ticket);
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving ticket options", e);
		}
		return tickets;
	}

	@Override
	public List<Event> getUpcomingEventsByUser(int userId) throws DataAccessException {
		List<Event> events = new ArrayList<>();
		// Query using the registrations table instead of bookings
		String query = "SELECT e.* FROM events e "
				+ "JOIN registrations r ON e.event_id = r.event_id "
				+ "WHERE r.user_id = ? AND e.start_datetime > NOW() AND r.status = 'CONFIRMED'";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				events.add(mapRowToEvent(rs));
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving upcoming events", e);
		}
		return events;
	}

	@Override
	public List<Event> getPastEventsByUser(int userId) throws DataAccessException {
		List<Event> events = new ArrayList<>();
		// Query using the registrations table instead of bookings
		String query = "SELECT e.* FROM events e "
				+ "JOIN registrations r ON e.event_id = r.event_id "
				+ "WHERE r.user_id = ? AND e.start_datetime < NOW() AND r.status = 'CONFIRMED'";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				events.add(mapRowToEvent(rs));
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving past events", e);
		}
		return events;
	}

	@Override
	public List<BookingDetail> getBookingHistory(int userId) throws DataAccessException {
		List<BookingDetail> bookings = new ArrayList<>();
		// Query using registrations and payments table
		String query = "SELECT r.registration_id, r.user_id, r.event_id, p.amount as total_amount "
				+ "FROM registrations r "
				+ "JOIN payments p ON r.registration_id = p.registration_id "
				+ "WHERE r.user_id = ?";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				BookingDetail booking = new BookingDetail();
				booking.setBookingId(rs.getInt("registration_id"));
				booking.setUserId(rs.getInt("user_id"));
				booking.setEventId(rs.getInt("event_id"));
				booking.setTotalAmount(rs.getDouble("total_amount"));
				bookings.add(booking);
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving booking history", e);
		}
		return bookings;
	}
}