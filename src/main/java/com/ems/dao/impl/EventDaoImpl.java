package com.ems.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.EventDao;
import com.ems.enums.EventStatus;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.util.DBConnectionUtil;

public class EventDaoImpl implements EventDao {

    Connection con;

    public EventDaoImpl() {
        try {
            con = DBConnectionUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> viewEvents() {

        List<Event> events = new ArrayList<>();

        try {

            String query = "SELECT * FROM events";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Event event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    // 2 View Event Details
    @Override
    public Event getEventById(int eventId) {

        Event event = null;

        try {

            String query = "SELECT * FROM events WHERE event_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setCategoryId(rs.getInt("category_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));

                Timestamp approvedAt = rs.getTimestamp("approved_at");
                if (approvedAt != null) {
                    event.setApprovedAt(approvedAt.toInstant());
                }

                event.setApprovedBy(rs.getInt("approved_by"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    event.setCreatedAt(createdAt.toInstant());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    event.setUpdatedAt(updatedAt.toInstant());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return event;
    }

 // 3 View Ticket Options
    @Override
    public List<Ticket> getTicketsByEventId(int eventId) {

        List<Ticket> tickets = new ArrayList<>();

        try {

            String query = "SELECT * FROM tickets WHERE event_id=?";  // FIXED: ticket → tickets
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ticket ticket = new Ticket();

                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setTicketType(rs.getString("ticket_type"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setTotalQuantity(rs.getInt("total_quantity"));
                ticket.setAvailableQuantity(rs.getInt("available_quantity"));

                tickets.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // 4 List Available Events
    @Override
    public List<Event> listAvailableEvents() {

        List<Event> events = new ArrayList<>();

        try {

            String query = "SELECT * FROM events WHERE status IN ('PUBLISHED', 'APPROVED') AND start_datetime > NOW()";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Event event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }
}