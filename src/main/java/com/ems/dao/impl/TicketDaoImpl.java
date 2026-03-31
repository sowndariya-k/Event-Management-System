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


	public int getAvailableTickets(int eventId) throws DataAccessException {
	    String sql = "SELECT SUM(available_quantity) FROM tickets WHERE event_id = ?";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, eventId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return rs.getInt(1);
	        }

	    } catch (SQLException e) {
	        throw new DataAccessException("Error fetching ticket count", e);
	    }

	    return 0;
	}

	@Override
	public List<Ticket> getTicketTypes(int eventId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateAvailableQuantity(int ticketId, int quantity) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createTicket(Ticket ticket) throws DataAccessException {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO tickets (event_id, ticket_type, price, total_quantity, available_quantity) "
				+ "VALUES (?, ?, ?, ?, ?)";
 
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
 
			ps.setInt(1, ticket.getEventId());
			ps.setString(2, ticket.getTicketType());
			ps.setDouble(3, ticket.getPrice());
			ps.setInt(4, ticket.getTotalQuantity());
			ps.setInt(5, ticket.getAvailableQuantity()); // initialized to totalQuantity by service
 
			return ps.executeUpdate() > 0;
 
		} catch (SQLException e) {
			throw new DataAccessException("Error creating ticket", e);
		}
	}

	@Override
	public boolean updateTicketPrice(int ticketId, double price) throws DataAccessException {
		// TODO Auto-generated method stub
		String sql = "UPDATE tickets SET price = ? WHERE ticket_id = ?";
		 
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
 
			ps.setDouble(1, price);
			ps.setInt(2, ticketId);
 
			return ps.executeUpdate() > 0;
 
		} catch (SQLException e) {
			throw new DataAccessException("Error updating ticket price", e);
		}
	}

	@Override
	public boolean updateTicketQuantity(int ticketId, int quantity) throws DataAccessException {
		// TODO Auto-generated method stub
		String sql = "UPDATE tickets "
				+ "SET available_quantity = available_quantity + (? - total_quantity), "
				+ "    total_quantity = ? "
				+ "WHERE ticket_id = ?";
 
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
 
			ps.setInt(1, quantity);
			ps.setInt(2, quantity);
			ps.setInt(3, ticketId);
 
			return ps.executeUpdate() > 0;
 
		} catch (SQLException e) {
			throw new DataAccessException("Error updating ticket quantity", e);
		}
	}

	@Override
	public List<Ticket> getTicketsByEvent(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		String sql = "SELECT ticket_id, event_id, ticket_type, price, total_quantity, available_quantity "
				+ "FROM tickets WHERE event_id = ?";
 
		List<Ticket> tickets = new ArrayList<>();
 
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
 
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
 
			while (rs.next()) {
				Ticket ticket = new Ticket(
						rs.getInt("ticket_id"),
						rs.getInt("event_id"),
						rs.getString("ticket_type"),
						rs.getDouble("price"),
						rs.getInt("total_quantity"),
						rs.getInt("available_quantity"));
				tickets.add(ticket);
			}
 
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching tickets for event", e);
		}
 
		return tickets;
	}
	

	
}
