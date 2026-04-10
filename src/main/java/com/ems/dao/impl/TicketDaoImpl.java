/*
 * Author : Mythily
 * TicketDaoImpl implements the TicketDao interface and
 * manages all ticket-related database operations including
 * creation, availability updates, and retrieval from
 * MySQL using JDBC PreparedStatement queries.
 */
package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.TicketDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Ticket;
import com.ems.util.DBConnectionUtil;

/*
 * Handles database operations related to tickets.
 *
 * Responsibilities:
 * - Retrieve ticket availability and ticket details
 * - Persist ticket creation and updates
 * - Manage ticket quantity and pricing
 */
public class TicketDaoImpl implements TicketDao {

	@Override
	public int getAvailableTickets(int eventId) throws DataAccessException {

		String sql = "select sum(available_quantity) as total_available " +
				"from tickets " +
				"where event_id = ?";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, eventId);

			try (ResultSet rs = ps.executeQuery()) {

				if (!rs.next()) {
					throw new DataAccessException(
							"Unable to determine ticket availability for eventId: " + eventId);
				}

				int total = rs.getInt("total_available");

				if (rs.wasNull()) {
					return 0; // No tickets configured for this event is a valid state
				}

				return total;
			}

		} catch (SQLException e) {
			throw new DataAccessException(
					"Error fetching available tickets for eventId: " + eventId, e);
		}
	}

	@Override
	public List<Ticket> getTicketTypes(int eventId) throws DataAccessException {

		// Returns only ticket types that can still be purchased
		String sql = "select * from tickets where event_id = ? and available_quantity > 0";

		List<Ticket> tickets = new ArrayList<>();

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, eventId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Ticket ticket = new Ticket();
					ticket.setTicketId(rs.getInt("ticket_id"));
					ticket.setEventId(eventId);
					ticket.setTicketType(rs.getString("ticket_type"));
					ticket.setPrice(rs.getDouble("price"));
					ticket.setTotalQuantity(rs.getInt("total_quantity"));
					ticket.setAvailableQuantity(rs.getInt("available_quantity"));
					tickets.add(ticket);
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error fetching ticket types");
		}

		return tickets;
	}

	@Override
	public boolean updateAvailableQuantity(int ticketId, int quantity)
			throws DataAccessException {

		// Adjusts available quantity using a delta value
		String sql = "update tickets set available_quantity = available_quantity + ? "
				+ "where ticket_id = ?";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, quantity);
			ps.setInt(2, ticketId);

			int rowsAffected = ps.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			throw new DataAccessException("Error updating ticket quantity");
		}
	}

	// Organizer functions:
	@Override
	public boolean createTicket(Ticket ticket) throws DataAccessException {
		String sql = "insert into tickets (event_id,ticket_type,price,total_quantity,available_quantity) values (?,?,?,?,?)";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ticket.getEventId());
			ps.setString(2, ticket.getTicketType());
			ps.setDouble(3, ticket.getPrice());
			ps.setInt(4, ticket.getTotalQuantity());
			ps.setInt(5, ticket.getAvailableQuantity());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Error creating ticket!");
		}
	}

	@Override
	public boolean updateTicketPrice(int ticketId, double price) throws DataAccessException {
		String sql = "update tickets set price=? where ticket_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setDouble(1, price);
			ps.setInt(2, ticketId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Error updating ticket price!");
		}
	}

	@Override
	public boolean updateTicketQuantity(int ticketId, int quantity)
			throws DataAccessException {

		// Resets both total and available quantity
		String sql = "update tickets set total_quantity=?, available_quantity=? where ticket_id=?";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, quantity);
			ps.setInt(2, quantity);
			ps.setInt(3, ticketId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Error updating ticket quantity!");
		}
	}

	@Override
	public List<Ticket> getTicketsByEvent(int eventId) throws DataAccessException {
		List<Ticket> list = new ArrayList<>();
		String sql = "select * from tickets where event_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Ticket t = new Ticket();
				t.setTicketId(rs.getInt("ticket_id"));
				t.setEventId(rs.getInt("event_id"));
				t.setTicketType(rs.getString("ticket_type"));
				t.setPrice(rs.getDouble("price"));
				t.setTotalQuantity(rs.getInt("total_quantity"));
				t.setAvailableQuantity(rs.getInt("available_quantity"));
				list.add(t);
			}
		} catch (Exception e) {
			throw new DataAccessException("Error getting tickets by event!");
		}
		return list;
	}
}