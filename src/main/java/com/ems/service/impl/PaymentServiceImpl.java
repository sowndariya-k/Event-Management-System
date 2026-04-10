/*
 * Author : Sowndariya
 * PaymentServiceImpl implements the PaymentService interface
 * and handles the payment processing logic for event
 * registrations, updating payment records and sending
 * payment notifications via the respective DAOs.
 */
package com.ems.service.impl;

import com.ems.dao.EventDao;
import com.ems.dao.NotificationDao;
import com.ems.dao.PaymentDao;
import com.ems.enums.NotificationType;
import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.RegistrationResult;
import com.ems.service.PaymentService;

/*
 * Handles payment and registration processing.
 */
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;
    private final NotificationDao notificationDao;

    public PaymentServiceImpl(PaymentDao paymentDao,
                              NotificationDao notificationDao,
                              EventDao eventDao) {
        this.paymentDao = paymentDao;
        this.notificationDao = notificationDao;
    }

    @Override
    public boolean processRegistration(int userId, int eventId, int ticketId,
                                       int quantity, double price,
                                       PaymentMethod selectedMethod,
                                       String offerCode) {
        try {
            RegistrationResult result = paymentDao.registerForEvent(
                    userId, eventId, ticketId, quantity, price, selectedMethod, offerCode
            );

            if (result.isSuccess()) {

                notificationDao.sendNotification(
                        userId,
                        "Registration confirmed. Amount paid: ₹" + result.getFinalAmount(),
                        NotificationType.EVENT
                );

            } else {
                System.out.println(result.getMessage());
            }

            return result.isSuccess();

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}