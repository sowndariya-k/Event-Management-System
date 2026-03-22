package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ems.dao.FeedbackDao;
import com.ems.exception.DataAccessException;
import com.ems.util.DBConnectionUtil;

public class FeedbackDaoImpl implements FeedbackDao {

    @Override
    public boolean submitRating(int eventId, int userId, int rating, String comments) throws DataAccessException {

        String query = "INSERT INTO feedback (event_id, user_id, rating, comments) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, comments);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataAccessException("Error inserting feedback", e);
        }
    }

    @Override
    public boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException {

        String query = "SELECT COUNT(*) FROM feedback WHERE event_id = ? AND user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, eventId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (Exception e) {
            throw new DataAccessException("Error checking feedback", e);
        }
    }
}