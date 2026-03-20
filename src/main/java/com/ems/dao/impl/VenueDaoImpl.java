package com.ems.dao.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.ems.dao.VenueDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Venue;

public class VenueDaoImpl implements VenueDao {

	@Override
	public String getVenueName(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVenueAddress(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, String> getAllCities() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Venue> getActiveVenues() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVenueAvailable(int venueId, Timestamp to, Timestamp from) throws DataAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Venue getVenueById(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addVenue(Venue venue) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateVenue(Venue venue) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivateVenue(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Venue> getAllVenues() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activateVenue(int venueId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

}
