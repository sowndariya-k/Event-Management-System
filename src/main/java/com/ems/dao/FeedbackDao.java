/*
 * Author : Jagadeep
 * FeedbackDao is the DAO interface that declares the
 * contract for saving and retrieving user feedback
 * records associated with attended events.
 */
package com.ems.dao;

import com.ems.exception.DataAccessException;

public interface FeedbackDao {

    /**
     * Submits rating and feedback for a completed event
     *
     * @param eventId
     * @param userId
     * @param rating
     * @param comments
     * @return true if feedback was saved successfully
     * @throws DataAccessException
     */
    boolean submitRating(int eventId, int userId, int rating, String comments)
            throws DataAccessException;
    
    /**
     * Checks that the user have already submitted feedback for the event
     * 
     * @param eventId
     * @param userId
     * @return true if feedback already submitted for the event
     * @throws DataAccessException
     */
	boolean isRatingAlreadySubmitted(int eventId, int userId) throws DataAccessException;
}