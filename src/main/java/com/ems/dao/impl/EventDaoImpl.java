package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.util.DBConnectionUtil;

public class EventDaoImpl implements EventDao {

	@Override
	public List<Event> listAvailableEvents() throws DataAccessException {

		List<Event> events = new ArrayList<>();

		String query = "SELECT * FROM events WHERE status='approved' AND start_datetime > NOW()";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {

				Event event = new Event();

				event.setEventId(rs.getInt("event_id"));
				event.setTitle(rs.getString("title"));
				event.setDescription(rs.getString("description"));
				event.setCategoryId(rs.getInt("category_id"));
				event.setVenueId(rs.getInt("venue_id"));

				event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
				event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());

				events.add(event);
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

				event = new Event();

				event.setEventId(rs.getInt("event_id"));
				event.setTitle(rs.getString("title"));
				event.setDescription(rs.getString("description"));
				event.setCategoryId(rs.getInt("category_id"));
				event.setVenueId(rs.getInt("venue_id"));

				event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
				event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
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
				ticket.setPrice(rs.getDouble("price"));
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

		String query = "SELECT e.* FROM events e "
				+ "JOIN bookings b ON e.event_id = b.event_id "
				+ "WHERE b.user_id = ? AND e.start_datetime > NOW()";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				Event event = new Event();

				event.setEventId(rs.getInt("event_id"));
				event.setTitle(rs.getString("title"));
				event.setDescription(rs.getString("description"));
				event.setCategoryId(rs.getInt("category_id"));
				event.setVenueId(rs.getInt("venue_id"));

				event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
				event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());

				events.add(event);
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving upcoming events", e);
		}

		return events;
	}

	@Override
	public List<Event> getPastEventsByUser(int userId) throws DataAccessException {

		List<Event> events = new ArrayList<>();

		String query = "SELECT e.* FROM events e "
				+ "JOIN bookings b ON e.event_id = b.event_id "
				+ "WHERE b.user_id = ? AND e.start_datetime < NOW()";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				Event event = new Event();

				event.setEventId(rs.getInt("event_id"));
				event.setTitle(rs.getString("title"));
				event.setDescription(rs.getString("description"));
				event.setCategoryId(rs.getInt("category_id"));
				event.setVenueId(rs.getInt("venue_id"));

				event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
				event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());

				events.add(event);
			}

		} catch (Exception e) {
			throw new DataAccessException("Error retrieving past events", e);
		}

		return events;
	}

	@Override
	public List<BookingDetail> getBookingHistory(int userId) throws DataAccessException {

		List<BookingDetail> bookings = new ArrayList<>();

		String query = "SELECT * FROM bookings WHERE user_id = ?";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				BookingDetail booking = new BookingDetail();

				booking.setBookingId(rs.getInt("booking_id"));
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