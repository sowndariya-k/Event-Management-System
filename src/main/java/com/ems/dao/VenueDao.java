package com.ems.dao;

import com.ems.exception.DataAccessException;

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
}
