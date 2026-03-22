package com.ems.model;

import java.time.Instant;

public class EventRegistrationReport {
	private String eventTitle;
	private String userName;
	private String ticketType;
	private int quantity;
	private Instant registrationDate;

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Instant getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Instant registrationDate) {
		this.registrationDate = registrationDate;
	}
}