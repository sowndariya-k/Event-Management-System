/*
 * Author : Mythily
 * TicketAction provides helper actions for fetching and
 * displaying available ticket types for a selected event,
 * used during the registration and booking process.
 */
package com.ems.actions;

import java.util.List;

import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.exception.DataAccessException;

public class TicketAction {

    private final EventService eventService;

    public TicketAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public List<Ticket> getTicketsForEvent(int eventId) throws DataAccessException {
        return eventService.getTicketTypes(eventId);
    }
}