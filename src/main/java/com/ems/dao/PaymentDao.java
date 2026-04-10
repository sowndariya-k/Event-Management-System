/*
 * Author : Sowndariya
 * PaymentDao is the DAO interface that defines contract
 * methods for processing and recording event registration
 * payments using various payment methods.
 */
package com.ems.dao;

import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.RegistrationResult;

public interface PaymentDao {


    /**
     * Updates payment status for a registration
     *
     * @param registrationId
     * @throws DataAccessException
     */
    void updatePaymentStatus(int registrationId) throws DataAccessException;

    /**
     * Registers a user for an event and processes payment
     *
     * @param userId
     * @param eventId
     * @param ticketId
     * @param quantity
     * @param price
     * @param paymentMethod
     * @param offerCode
     * @return registration result with status and final amount
     * @throws DataAccessException
     */
    RegistrationResult registerForEvent(
            int userId,
            int eventId,
            int ticketId,
            int quantity,
            double price,
            PaymentMethod paymentMethod,
            String offerCode
    ) throws DataAccessException;
}