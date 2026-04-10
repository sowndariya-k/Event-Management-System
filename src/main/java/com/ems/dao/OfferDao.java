/*
 * Author : Jagadeep
 * OfferDao is the DAO interface that declares contract
 * methods for offer-related database operations including
 * creating, updating, deleting, and retrieving discount
 * offers linked to events.
 */
package com.ems.dao;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.Offer;

public interface OfferDao {

    /**
     * Retrieves all offers
     *
     * @return list of offers
     * @throws DataAccessException
     */
    List<Offer> getAllOffers() throws DataAccessException;

    /**
     * Creates a new offer for an event
     *
     * @param offer
     * @return generated offer id
     * @throws DataAccessException
     */
    int createOffer(Offer offer) throws DataAccessException;

    /**
     * Updates the validity end date of an offer
     *
     * @param offerId
     * @param validDate
     * @throws DataAccessException
     */
    void updateOfferActiveStatus(int offerId, Instant validDate)
            throws DataAccessException;

    /**
     * Returns usage count for each offer code
     *
     * @return offer usage report
     * @throws DataAccessException
     */
    Map<String, Integer> getOfferUsageReport() throws DataAccessException;

    /**
     * Checks whether a user has already used an offer
     *
     * @param userId
     * @param offerId
     * @return true if already used
     * @throws DataAccessException
     */
    boolean hasUserUsedOfferCode(int userId, int offerId)
            throws DataAccessException;

    /**
     * Retrieves a valid offer for an event using offer code
     *
     * @param eventId
     * @param inputCode
     * @return offer details or null if not found
     * @throws DataAccessException
     */
    Offer getOffer(int eventId, String inputCode)
            throws DataAccessException;
}