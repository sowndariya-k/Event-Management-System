/*
 * Author : Mythily
 * UserEventRegistration is a model class that represents
 * a combined view of a user's event registration including
 * event details, ticket type, payment info, and registration
 * status, used for displaying booking history.
 */
package com.ems.model;

import java.time.Instant;

public class UserEventRegistration {

	private int registrationId;
    private Instant registrationDate;
    private String registrationStatus;
    private int eventId;
    private String title;
    private String category;
    private Instant startDateTime;
    private Instant endDateTime;
    private int ticketsPurchased;
    private double amountPaid;
    private String ticketType;

	public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public Instant getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Instant registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getRegistrationStatus() {
		return registrationStatus;
	}
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Instant getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(Instant startDateTime) {
		this.startDateTime = startDateTime;
	}
	public Instant getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(Instant endDateTime) {
		this.endDateTime = endDateTime;
	}
	public int getTicketsPurchased() {
		return ticketsPurchased;
	}
	public void setTicketsPurchased(int ticketsPurchased) {
		this.ticketsPurchased = ticketsPurchased;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	

}
