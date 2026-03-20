package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Ticket;

public interface TicketDao {

    /**
     * Returns total available ticket count for an event
     *
     * @param eventId
     * @return available ticket count
     * @throws DataAccessException
     */
    int getAvailableTickets(int eventId) throws DataAccessException;

    /**
     * Retrieves all ticket types with available quantity for an event
     *
     * @param eventId
     * @return list of available ticket types
     * @throws DataAccessException
     */
    List<Ticket> getTicketTypes(int eventId) throws DataAccessException;


    /**
     * Updates available quantity of a ticket
     *
     * @param ticketId
     * @param quantity delta to apply to available quantity
     * @return true if update succeeded
     * @throws DataAccessException
     */
    boolean updateAvailableQuantity(int ticketId, int quantity) throws DataAccessException;

    // organizer functions

    /**
     * Creates a new ticket type for an event
     *
     * @param ticket
     * @return true if ticket was created
     * @throws DataAccessException
     */
    boolean createTicket(Ticket ticket) throws DataAccessException;

    /**
     * Updates price of a ticket
     *
     * @param ticketId
     * @param price
     * @return true if update succeeded
     * @throws DataAccessException
     */
    boolean updateTicketPrice(int ticketId, double price) throws DataAccessException;

    /**
     * Updates total and available quantity of a ticket
     *
     * @param ticketId
     * @param quantity
     * @return true if update succeeded
     * @throws DataAccessException
     */
    boolean updateTicketQuantity(int ticketId, int quantity) throws DataAccessException;

    /**
     * Retrieves all tickets for an event
     *
     * @param eventId
     * @return list of tickets
     * @throws DataAccessException
     */
    List<Ticket> getTicketsByEvent(int eventId) throws DataAccessException;
}