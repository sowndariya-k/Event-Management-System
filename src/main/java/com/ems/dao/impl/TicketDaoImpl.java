package com.ems.dao.impl;

import java.util.List;

import com.ems.dao.TicketDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Ticket;
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
	public int getAvailableTickets(int eventId) {
		// TODO Auto-generated method stub
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
