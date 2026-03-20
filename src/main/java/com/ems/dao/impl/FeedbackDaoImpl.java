package com.ems.dao.impl;

import com.ems.dao.FeedbackDao;
import com.ems.exception.DataAccessException;

/*
 * Handles database operations related to event feedback.
 *
 * Responsibilities:
 * - Validate user eligibility for feedback submission
 * - Persist ratings and comments for completed events
 */
public class FeedbackDaoImpl implements FeedbackDao {

	@Override
	public boolean submitRating(int eventId, int userId, int rating, String comments) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	

}
