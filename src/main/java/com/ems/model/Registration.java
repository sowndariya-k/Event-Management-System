package com.ems.model;
import java.time.Instant;

import com.ems.enums.RegistrationStatus;

public class Registration {
	private int registrationId;
	private int userId;
	private int eventId;
	private Instant registrationDate;
	private Instant eventStartDate;
	private Instant eventEndDate;
	private RegistrationStatus status;
	private String fullName;
	private String email;
	private String eventTitle;
	
	public Registration() {
	}

	public Registration(int registrationId, int userId, int eventId, Instant registrationDate,
			RegistrationStatus status) {
		this.registrationId = registrationId;
		this.userId = userId;
		this.eventId = eventId;
		this.registrationDate = registrationDate;
		this.status = status;
	}
	
	
	public int getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public Instant getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Instant registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Instant getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(Instant eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public Instant getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(Instant eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public RegistrationStatus getStatus() {
		return status;
	}

	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}
}
