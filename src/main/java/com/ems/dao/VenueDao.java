package com.ems.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.Venue;

public interface VenueDao {

    /**
     * Returns venue name by venue id
     *
     * @param venueId
     * @return venue name or null if not found
     * @throws DataAccessException
     */
    String getVenueName(int venueId) throws DataAccessException;

    /**
     * Returns formatted venue address
     *
     * @param venueId
     * @return full address string or null if not found
     * @throws DataAccessException
     */
    String getVenueAddress(int venueId) throws DataAccessException;

    /**
     * Retrieves active venues mapped by venue id and city
     *
     * @return map of venue id to city
     * @throws DataAccessException
     */
    Map<Integer, String> getAllCities() throws DataAccessException;

    /**
     * Retrieves all active venues
     *
     * @return list of active venues
     * @throws DataAccessException
     */
    List<Venue> getActiveVenues() throws DataAccessException;

    /**
     * Checks venue availability for a given time range
     *
     * @param venueId
     * @param to
     * @param from
     * @return true if venue has no overlapping events
     * @throws DataAccessException
     */
    boolean isVenueAvailable(int venueId, Timestamp to, Timestamp from) throws DataAccessException;

    /**
     * Retrieves venue details by id
     *
     * @param venueId
     * @return venue details
     * @throws DataAccessException
     */
    Venue getVenueById(int venueId) throws DataAccessException;

    /**
     * Adds a new venue
     *
     * @param venue
     * @throws DataAccessException
     */
    void addVenue(Venue venue) throws DataAccessException;

    /**
     * Updates an active venue
     *
     * @param venue
     * @throws DataAccessException
     */
    void updateVenue(Venue venue) throws DataAccessException;

    /**
     * Deactivates a venue
     *
     * @param venueId
     * @throws DataAccessException
     */
    void deactivateVenue(int venueId) throws DataAccessException;

    /**
     * Retrieves all venues regardless of status
     *
     * @return list of venues
     * @throws DataAccessException
     */
    List<Venue> getAllVenues() throws DataAccessException;

    /**
     * Activates a previously deactivated venue
     *
     * @param venueId
     * @throws DataAccessException
     */
    void activateVenue(int venueId) throws DataAccessException;
}