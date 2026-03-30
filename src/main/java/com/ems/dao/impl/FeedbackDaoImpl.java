package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ems.dao.FeedbackDao;
import com.ems.exception.DataAccessException;
import com.ems.util.DBConnectionUtil;

/*
 * Handles database operations related to event feedback.
 *
 * Responsibilities:
 * - Validate user eligibility for feedback submission
 * - Persist ratings and comments for completed events
 */
public class FeedbackDaoImpl implements FeedbackDao {
	
	@Override
	public boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException {
		String sql = "select count(*) from feedback"
				+ " where event_id = ?"
				+ " and user_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
		
			ps.setInt(1, eventId);
	        ps.setInt(2, userId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        }

	        return false;

	    } catch (SQLException e) {
	        throw new DataAccessException("Error while checking rating submission", e);
	    }
	}
	
	@Override
	public boolean submitRating(int eventId, int userId, int rating, String comments)
	        throws DataAccessException {
	
		 String sql =
			        "select count(*) from events e " +
			        "join registrations r on e.event_id = r.event_id " +
			        "where r.user_id = ? " +
			        "and e.event_id = ? " +
			        "and r.status = 'CONFIRMED'";
			
			    try (Connection con = DBConnectionUtil.getConnection();
			         PreparedStatement ps = con.prepareStatement(sql)) {
			
			        ps.setInt(1, userId);
			        ps.setInt(2, eventId);
			
			        try (ResultSet rs = ps.executeQuery()) {
			
			            if (rs.next() && rs.getInt(1) > 0) {
			
			                String insertReview =
			                    "insert into feedback(event_id, user_id, rating, comments, submitted_at) " +
			                    "values(?, ?, ?, ?, utc_timestamp())";
			
			                try (PreparedStatement ps1 = con.prepareStatement(insertReview)) {
			                    ps1.setInt(1, eventId);
			                    ps1.setInt(2, userId);
			                    ps1.setInt(3, rating);
			                    ps1.setString(4, comments);
			
			                    return ps1.executeUpdate() > 0;
			                }
			            }
			        }
			
			        return false;
			
			    } catch (SQLException e) {
			        throw new DataAccessException("Error while submitting rating");
			    }
			}

}