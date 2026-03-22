package com.ems.model;

import java.time.Instant;

public class BookingDetail {

	private String eventName;
	private Instant startDateTime;
	private String venueName;
	private String city;
	private String ticketType;
	private int quantity;
	private double totalCost;

	public BookingDetail(String eventName, Instant startDateTime, String venueName, String city, String ticketType,
			int quantity, double totalCost) {
		this.eventName = eventName;
		this.startDateTime = startDateTime;
		this.venueName = venueName;
		this.city = city;
		this.ticketType = ticketType;
		this.quantity = quantity;
		this.totalCost = totalCost;
	}

	public String getEventName() {
		return eventName;
	}

	public Instant getStartDateTime() {
		return startDateTime;
	}

	public String getVenueName() {
		return venueName;
	}

	public String getCity() {
		return city;
	}

	public String getTicketType() {
		return ticketType;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getTotalCost() {
		return totalCost;
	}
}