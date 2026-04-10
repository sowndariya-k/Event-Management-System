/*
 * Author : Mythily
 * RegistrationDao is the DAO interface that defines
 * contract methods for managing event registrations
 * including saving, cancelling, and fetching registration
 * records and reports.
 */
package com.ems.dao;

import java.util.List;
import com.ems.enums.RegistrationStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;
import com.ems.model.Registration;
import com.ems.model.RegistrationTicket;

public interface RegistrationDao {
        List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException;

        List<Integer> getRegisteredUserIdsByEvent(int eventId) throws DataAccessException;

        int getEventRegistrationCount(int eventId) throws DataAccessException;

        Registration getById(int registrationId) throws DataAccessException;

        void updateStatus(int registrationId, RegistrationStatus status) throws DataAccessException;

        List<RegistrationTicket> getRegistrationTickets(int registrationId) throws DataAccessException;
}