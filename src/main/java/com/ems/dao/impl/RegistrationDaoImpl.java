/*
 * Author : Mythily
 * RegistrationDaoImpl implements the RegistrationDao
 * interface and handles all event registration persistence
 * operations in the MySQL database using JDBC
 * PreparedStatement queries.
 */
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
import com.ems.util.DateTimeUtil;

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
				List<EventRegistrationReport> reports = new ArrayList<>();

				String sql = "select e.title as event_title, u.full_name, t.ticket_type, " +
						"rt.quantity, r.registration_date " +
						"from registrations r " +
						"inner join users u on r.user_id = u.user_id " +
						"inner join registration_tickets rt on r.registration_id = rt.registration_id " +
						"inner join tickets t on rt.ticket_id = t.ticket_id " +
						"inner join events e on r.event_id = e.event_id " +
						"where r.event_id = ? " +
						"  and r.status = 'CONFIRMED'";

				try (Connection con = DBConnectionUtil.getConnection();
						PreparedStatement ps = con.prepareStatement(sql)) {

					ps.setInt(1, eventId);
					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						EventRegistrationReport report = new EventRegistrationReport();
						report.setEventTitle(rs.getString("event_title"));
						report.setUserName(rs.getString("full_name"));
						report.setTicketType(rs.getString("ticket_type"));
						report.setQuantity(rs.getInt("quantity"));
						report.setRegistrationDate(DateTimeUtil.fromTimestamp(rs.getTimestamp("registration_date")));
						reports.add(report);
					}
					rs.close();

				} catch (SQLException e) {
					throw new DataAccessException(
							"Error while fetching event wise registration");
				}

				return reports;
	}

	@Override
	public List<Integer> getRegisteredUserIdsByEvent(int eventId) throws DataAccessException {

				String sql = "select distinct user_id from registrations where event_id=?";
				List<Integer> userIds = new ArrayList<>();

				try (Connection con = DBConnectionUtil.getConnection();
						PreparedStatement ps = con.prepareStatement(sql)) {

					ps.setInt(1, eventId);
					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						userIds.add(rs.getInt("user_id"));
					}

					return userIds;

				} catch (Exception e) {
					throw new DataAccessException("Unable to fetch registered users", e);
				}
			}

	@Override
	public int getEventRegistrationCount(int eventId) throws DataAccessException {
		// Lightweight count query for capacity checks
				String sql = "select count(*) from registrations where event_id=? and status='CONFIRMED' ";
				try (Connection con = DBConnectionUtil.getConnection();
						PreparedStatement ps = con.prepareStatement(sql)) {

					ps.setInt(1, eventId);
					ResultSet rs = ps.executeQuery();
					return rs.next() ? rs.getInt(1) : 0;
				} catch (Exception e) {
					throw new DataAccessException("Unable to fetch registered count");
				}
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
	        if (rows == 0) {
	            throw new DataAccessException("No registration found for ID: " + registrationId);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Unable to update registration status", e);
	    }
	}

	@Override
	public List<RegistrationTicket> getRegistrationTickets(int registrationId) throws DataAccessException {

	    String sql = "SELECT ticket_id, quantity FROM registration_tickets WHERE registration_id = ?";

	    List<RegistrationTicket> tickets = new ArrayList<>();

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, registrationId);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                RegistrationTicket rt = new RegistrationTicket();

	                rt.setTicketId(rs.getInt("ticket_id"));
	                rt.setQuantity(rs.getInt("quantity"));

	                tickets.add(rt);
	            }
	        }

	        return tickets;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error fetching registration tickets", e);
	    }
	}
 
}
