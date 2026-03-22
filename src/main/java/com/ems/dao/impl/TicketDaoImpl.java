package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		return false;
	}

	@Override
	public boolean updateTicketPrice(int ticketId, double price) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateTicketQuantity(int ticketId, int quantity) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Ticket> getTicketsByEvent(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
