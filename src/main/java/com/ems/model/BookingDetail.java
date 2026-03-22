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

    private int bookingId;
    private int userId;
    private int eventId;
    private double totalAmount;

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

    // Getter and Setter for bookingId
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    // Getter and Setter for userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and Setter for eventId
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    // Getter and Setter for totalAmount
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}