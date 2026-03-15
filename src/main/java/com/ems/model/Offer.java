package com.ems.model;

<<<<<<< HEAD
public class Offer {

	

}
=======
import java.time.Instant;

public class Offer {

	private int offerId;
	private int eventId;
	private String code;
	private Integer discountPercentage;
	private Instant validFrom;
	private Instant validTo;

	public Offer(int offerId, int eventId, String code, Integer discountPercentage, Instant validFrom,
			Instant validTo) {
		super();
		this.offerId = offerId;
		this.eventId = eventId;
		this.code = code;
		this.discountPercentage = discountPercentage;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}
	
	public Offer() {
	}
	public int getOfferId() {
		return offerId;
	}

	public void setOfferId(int offerId) {
		this.offerId = offerId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(Integer discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public Instant getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Instant validFrom) {
		this.validFrom = validFrom;
	}

	public Instant getValidTo() {
		return validTo;
	}

	public void setValidTo(Instant validTo) {
		this.validTo = validTo;
	}
}
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433
