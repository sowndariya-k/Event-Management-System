package com.ems.dao.impl;

import java.util.List;

import com.ems.dao.RegistrationDao;
import com.ems.enums.RegistrationStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;
import com.ems.model.Registration;
import com.ems.model.RegistrationTicket;

/*
 * Handles database operations related to event registrations.
 *
 * Responsibilities:
 * - Persist and update event registrations and ticket allocations
 * - Retrieve registration data for events and organizers
 * - Generate registration, sales, and revenue reports
 */
public class RegistrationDaoImpl implements RegistrationDao {

	@Override
	public List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getRegisteredUserIdsByEvent(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEventRegistrationCount(int eventId) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Registration getById(int registrationId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStatus(int registrationId, RegistrationStatus status) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RegistrationTicket> getRegistrationTickets(int registrationId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
