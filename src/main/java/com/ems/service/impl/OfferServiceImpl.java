/*
 * Author : Jagadeep
 * OfferServiceImpl implements the OfferService interface
 * and provides business logic for managing event offers,
 * including validation of offer codes and discount
 * calculations using the OfferDao.
 */
package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.OfferDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Offer;
import com.ems.service.OfferService;
import com.ems.util.DateTimeUtil;

/*
 * Handles offer and promotion related business operations.
 *
 * Responsibilities:
 * - Create and manage promotional offers
 * - Control offer activation and validity
 * - Generate offer usage reports
 */
public class OfferServiceImpl implements OfferService {

	private final OfferDao offerDao;


	/*
	 * Initializes offer service with required data access dependency.
	 */
	public OfferServiceImpl(OfferDao offerDao) {
		this.offerDao = offerDao;
	}

	/*
	 * Retrieves all offers available in the system.
	 *
	 * Used by admin for offer management and reporting.
	 */
	@Override
	public List<Offer> getAllOffers() {
		try {
			return offerDao.getAllOffers();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());;
		}
		return new ArrayList<>();
	}

	/*
	 * Creates a new promotional offer for an event.
	 *
	 * Rules: - Offer validity is defined by from and to dates - Discount is applied
	 * as a percentage
	 */
	@Override
	public boolean createOffer(int eventId, String code, Integer discount, LocalDateTime from, LocalDateTime to) {
		Offer offer = new Offer();
		offer.setEventId(eventId);
		offer.setCode(code);
		offer.setDiscountPercentage(discount);
		offer.setValidFrom(DateTimeUtil.toUtcInstant(from));
		offer.setValidTo(DateTimeUtil.toUtcInstant(to));

		try {
			int offerId = offerDao.createOffer(offer);

			if(offerId != 0 ) {
				return true;
			}
			return false;

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Activates or deactivates an offer by updating its validity.
	 *
	 * Rule: - Valid date is stored in UTC for consistency
	 */
	@Override
	public void toggleOfferStatus(int offerId, LocalDateTime validDate) {
		try {
			offerDao.updateOfferActiveStatus(
				    offerId,
				    DateTimeUtil.toUtcInstant(validDate)
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());;
		}
	}

	/*
	 * Generates a usage report for all offers.
	 *
	 * Used to analyze offer effectiveness.
	 */
	@Override
	public Map<String, Integer> getOfferUsageReport() {
		try {
			return offerDao.getOfferUsageReport();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());;
		}
		return new HashMap<>();
	}
	
	@Override
	public Offer getOffer(int eventId, String offerCode) {
		try {
			return offerDao.getOffer(eventId, offerCode);
		}catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public boolean hasUserUsedOfferCode(int userId, int offerId) {
		try {
			return offerDao.hasUserUsedOfferCode(userId, offerId);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}
