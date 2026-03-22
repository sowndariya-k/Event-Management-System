package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.VenueDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Venue;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;
/*
 * Handles database operations related to venues.
 *
 * Responsibilities:
 * - Retrieve venue details and availability information
 * - Persist venue creation and updates
 * - Manage venue activation state
 */
public class VenueDaoImpl implements VenueDao {

	@Override
	public String getVenueName(int venueId) throws DataAccessException {
		 String sql = "select name from venues where venue_id=?";
	        try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setInt(1, venueId);

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getString("name");
	                }
	            }
	        } catch (SQLException e) {
	            throw new DataAccessException("Error fetching venue name");
	        }
	        throw new DataAccessException("No venue found!");
	}

	@Override
	public String getVenueAddress(int venueId) throws DataAccessException {
		 String sql = "select street, city, state, pincode from venues where venue_id=?";
	        try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setInt(1, venueId);

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getString("street") + ",\n"
	                         + rs.getString("city") + ",\n"
	                         + rs.getString("state") + " - "
	                         + rs.getString("pincode");
	                }
	            }
	        } catch (SQLException e) {
	            throw new DataAccessException("Error fetching venue address");
	        }
	        throw new DataAccessException("No venue found!");
	}

	@Override
	public Map<Integer, String> getAllCities() throws DataAccessException {
		 // Used for venue selection filters
        String sql = "select venue_id, city from venues where is_active = 1 order by city";
        Map<Integer, String> cities = new HashMap<>();

        try (Connection con = DBConnectionUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.put(rs.getInt("venue_id"), rs.getString("city"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching cities");
        }
        return cities;
	}

	@Override
	public List<Venue> getActiveVenues() throws DataAccessException {
		String sql = "select * from venues where is_active = 1";
		List<Venue> venues = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	ResultSet rs = ps.executeQuery();
        	while(rs.next()) {
        		Venue venue = new Venue();
        		venue.setName(rs.getString("name"));
        		
        		venue.setCity(rs.getString("city"));
        		venue.setVenueId(rs.getInt("venue_id"));
        		venue.setStreet(rs.getString("street"));
        		venue.setState(rs.getString("state"));
        		venue.setPincode(rs.getString("pincode"));
        		venue.setMaxCapacity(rs.getInt("max_capacity"));
        		venue.setCreatedAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("created_at")));
        		if(rs.getTimestamp("updated_at") != null) {
        			venue.setUpdateAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("updated_at")));
        		}
        		venues.add(venue);
        	}
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venues");
        }
        return venues;
	}

	@Override
	public boolean isVenueAvailable(int venueId, Timestamp to, Timestamp from) throws DataAccessException {
		 // Checks for overlapping events within the given time window

		String sql =
		        "SELECT COUNT(*) " +
		        "FROM events " +
		        "WHERE venue_id = ? " +
		        "AND status IN (?, ?) " +
		        "AND start_datetime < ? " +
		        "AND end_datetime > ?";

		    try (Connection conn = DBConnectionUtil.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql)) {

		        ps.setInt(1, venueId);
		        ps.setString(2, EventStatus.APPROVED.toString());
		        ps.setString(3, EventStatus.PUBLISHED.toString());;
		        ps.setTimestamp(4, to);
		        ps.setTimestamp(5, from);

		        try (ResultSet rs = ps.executeQuery()) {
		            if (rs.next()) {
		                return rs.getInt(1) == 0;
		            }
		        }

		    } catch (SQLException e) {
	            throw new DataAccessException("Error fetching venues");
	        }

		    return false;
	}

	@Override
	public Venue getVenueById(int venueId) throws DataAccessException {
		String sql = "select * from venues where is_active = 1 and venue_id = ?";
		Venue venue = new Venue();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	ps.setInt(1, venueId);
        	ResultSet rs = ps.executeQuery();
        	if(rs.next()) {
        		venue.setCity(rs.getString("city"));
        		venue.setVenueId(rs.getInt("venue_id"));
        		venue.setStreet(rs.getString("street"));
        		venue.setState(rs.getString("state"));
        		venue.setPincode(rs.getString("pincode"));
        		venue.setMaxCapacity(rs.getInt("max_capacity"));
        		venue.setCreatedAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("created_at")));
        		Timestamp updatedTs = rs.getTimestamp("updated_at");
        		if (updatedTs != null) {
        			venue.setUpdateAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("updated_at")));
        		}

        	}
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venues");
        }
        return venue;
	}

	@Override
	public void addVenue(Venue venue) throws DataAccessException {
		 // Stores created timestamp in UTC

	    String sql =
	        "insert into venues (name, street, city, state, pincode, max_capacity, created_at, is_active) " +
	        "values (?, ?, ?, ?, ?, ?, ?, 1)";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, venue.getName());
	        ps.setString(2, venue.getStreet());
	        ps.setString(3, venue.getCity());
	        ps.setString(4, venue.getState());
	        ps.setString(5, venue.getPincode());
	        ps.setInt(6, venue.getMaxCapacity());
	        ps.setTimestamp(7, DateTimeUtil.toTimestamp(DateTimeUtil.nowUtc()));
	        ps.executeUpdate();
	    } catch (Exception e) {
	        throw new DataAccessException("Failed to add venue");
	    }
		
	}

	@Override
	public void updateVenue(Venue venue) throws DataAccessException {
		 // Updates only active venues

	    String sql =
	        "update venues set name=?, street=?, city=?, state=?, pincode=?, max_capacity=? " +
	        "where venue_id=? and is_active=1";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, venue.getName());
	        ps.setString(2, venue.getStreet());
	        ps.setString(3, venue.getCity());
	        ps.setString(4, venue.getState());
	        ps.setString(5, venue.getPincode());
	        ps.setInt(6, venue.getMaxCapacity());
	        ps.setInt(7, venue.getVenueId());

	        ps.executeUpdate();
	    } catch (Exception e) {
	        throw new DataAccessException("Failed to update venue");
	    }
		
	}

	@Override
	public void deactivateVenue(int venueId) throws DataAccessException {
		 String sql = "update venues set is_active=0 where venue_id=?";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setInt(1, venueId);
		        ps.executeUpdate();
		    } catch (Exception e) {
		        throw new DataAccessException("Failed to remove venue");
		    }
		
	}

	@Override
	public List<Venue> getAllVenues() throws DataAccessException {
		String sql = "select name, city, venue_id , street, state, pincode, max_capacity , created_at, updated_at, is_active from venues";
		List<Venue> venues = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	ResultSet rs = ps.executeQuery();
        	while(rs.next()) {
        		Venue venue = new Venue();
        		venue.setName(rs.getString("name"));
        		
        		venue.setCity(rs.getString("city"));
        		venue.setVenueId(rs.getInt("venue_id"));
        		venue.setStreet(rs.getString("street"));
        		venue.setState(rs.getString("state"));
        		venue.setPincode(rs.getString("pincode"));
        		venue.setMaxCapacity(rs.getInt("max_capacity"));
        		venue.setCreatedAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("created_at")));

        		if(rs.getTimestamp("updated_at") != null) {
            		venue.setUpdateAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("updated_at")));
        		}
        		venue.setStatus(rs.getInt("is_active") == 1);
        		venues.add(venue);
        	}
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching venues");
        }
        return venues;
	}

	@Override
	public void activateVenue(int venueId) throws DataAccessException {
		  String sql = "update venues set is_active=1 where venue_id=?";

		    try (Connection con = DBConnectionUtil.getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {

		        ps.setInt(1, venueId);
		        ps.executeUpdate();
		    } catch (Exception e) {
		        throw new DataAccessException("Failed to activate venue");
		    }
		
	}

}
