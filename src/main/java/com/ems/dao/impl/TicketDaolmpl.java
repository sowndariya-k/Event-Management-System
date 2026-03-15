package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.TicketDao;
import com.ems.model.Ticket;
import com.ems.util.DBConnection;

public class TicketDaoImpl implements TicketDao {

    Connection conn = DBConnection.getConnection();

    public boolean bookTicket(Ticket ticket) {

        try {
            String query = "INSERT INTO tickets(ticket_id, event_id, user_id, seat_number, price) VALUES(?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, ticket.getTicketId());
            ps.setInt(2, ticket.getEventId());
            ps.setInt(3, ticket.getUserId());
            ps.setString(4, ticket.getSeatNumber());
            ps.setDouble(5, ticket.getPrice());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Ticket> getAllTickets() {

        List<Ticket> list = new ArrayList<>();

        try {

            String query = "SELECT * FROM tickets";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ticket ticket = new Ticket();

                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setEventId(rs.getInt("event_id"));
                ticket.setUserId(rs.getInt("user_id"));
                ticket.setSeatNumber(rs.getString("seat_number"));
                ticket.setPrice(rs.getDouble("price"));

                list.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean cancelTicket(int ticketId) {

        try {

            String query = "DELETE FROM tickets WHERE ticket_id=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, ticketId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Ticket> getTicketsByEventId(int eventId) {

        List<Ticket> list = new ArrayList<>();

        try {

            String query = "SELECT * FROM tickets WHERE event_id=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ticket ticket = new Ticket();

                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setEventId(rs.getInt("event_id"));
                ticket.setUserId(rs.getInt("user_id"));
                ticket.setSeatNumber(rs.getString("seat_number"));
                ticket.setPrice(rs.getDouble("price"));

                list.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}