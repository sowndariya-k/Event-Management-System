/*
 * Author : Jagadeep
 * OfferService is the service interface that defines
 * business logic contracts for managing event discount
 * offers including creation, validation, and retrieval.
 */
package com.ems.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.model.Offer;

public interface OfferService {

    // offers
    List<Offer> getAllOffers();

    boolean createOffer(
        int eventId,
        String code,
        Integer discount,
        LocalDateTime from,
        LocalDateTime to
    );

    void toggleOfferStatus(int offerId, LocalDateTime validDate);

    // reports
    Map<String, Integer> getOfferUsageReport();

	Offer getOffer(int eventId, String offerCode);

	boolean hasUserUsedOfferCode(int userId, int offerId);
}