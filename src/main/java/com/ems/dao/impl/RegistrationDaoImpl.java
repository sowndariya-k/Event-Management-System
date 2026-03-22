package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.RegistrationDao;
import com.ems.enums.RegistrationStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;
import com.ems.model.Registration;
import com.ems.model.RegistrationTicket;
import com.ems.util.DBConnectionUtil;

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
		List<EventRegistrationReport> list = new ArrayList<>();

	    String sql = "SELECT * FROM registrations WHERE event_id=?";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, eventId);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            EventRegistrationReport r = new EventRegistrationReport();
	            list.add(r);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error fetching registrations", e);
	    }

	    return list;
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
	    String sql = "SELECT registration_id, user_id, event_id, registration_date, status " +
	                 "FROM registrations WHERE registration_id = ?";
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, registrationId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            Registration reg = new Registration();
	            reg.setRegistrationId(rs.getInt("registration_id"));
	            reg.setUserId(rs.getInt("user_id"));
	            reg.setEventId(rs.getInt("event_id"));
	            reg.setRegistrationDate(rs.getTimestamp("registration_date").toInstant());

	            // FIXED — safely parse status enum
	            String statusStr = rs.getString("status");
	            if (statusStr != null) {
	                reg.setStatus(RegistrationStatus.valueOf(statusStr.trim().toUpperCase()));
	            }

	            rs.close();
	            return reg;
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error fetching registration by ID: " + registrationId, e);
	    }
	    return null;
	}

	@Override
	public void updateStatus(int registrationId, RegistrationStatus status) throws DataAccessException {
	    String sql = "UPDATE registrations SET status = ? WHERE registration_id = ?";
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, status.name());
	        ps.setInt(2, registrationId);
	        int rows = ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error updating registration status for ID: " + registrationId, e);
	    }
	}

	@Override
	public List<RegistrationTicket> getRegistrationTickets(int registrationId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
		
	}

}