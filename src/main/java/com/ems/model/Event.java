package com.ems.model;

import java.time.Instant;

import com.ems.enums.EventStatus;

public class Event {
	private int eventId;
	private int organizerId;
	private String title;
	private String description;
	private int categoryId;
	private int venueId;
	private Instant startDateTime;
	private Instant endDateTime;
	private Instant approvedAt;
	private Instant createdAt;
	private Instant updatedAt;
	private int capacity;
	private EventStatus status;
	private int approvedBy;
	
	
	public Event(int eventId, int organizerId, String title, String description, int categoryId, int venueId,
			Instant startDateTime, Instant endDateTime, Instant approvedAt, Instant createdAt, Instant updatedAt,
			int capacity, EventStatus status, int approvedBy) {
		this.eventId = eventId;
		this.organizerId = organizerId;
		this.title = title;
		this.description = description;
		this.categoryId = categoryId;
		this.venueId = venueId;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.approvedAt = approvedAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.capacity = capacity;
		this.status = status;
		this.approvedBy = approvedBy;
	}


	public int getEventId() {
		return eventId;
	}


	public void setEventId(int eventId) {
		this.eventId = eventId;
	}


	public int getOrganizerId() {
		return organizerId;
	}


	public void setOrganizerId(int organizerId) {
		this.organizerId = organizerId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}


	public int getVenueId() {
		return venueId;
	}


	public void setVenueId(int venueId) {
		this.venueId = venueId;
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


	public Instant getApprovedAt() {
		return approvedAt;
	}


	public void setApprovedAt(Instant approvedAt) {
		this.approvedAt = approvedAt;
	}


	public Instant getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}


	public Instant getUpdatedAt() {
		return updatedAt;
	}


	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}


	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}


	public EventStatus getStatus() {
		return status;
	}


	public void setStatus(EventStatus status) {
		this.status = status;
	}


	public int getApprovedBy() {
		return approvedBy;
	}


	public void setApprovedBy(int approvedBy) {
		this.approvedBy = approvedBy;
	}


	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", organizerId=" + organizerId + ", title=" + title + ", description="
				+ description + ", categoryId=" + categoryId + ", venueId=" + venueId + ", startDateTime="
				+ startDateTime + ", endDateTime=" + endDateTime + ", approvedAt=" + approvedAt + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", capacity=" + capacity + ", status=" + status
				+ ", approvedBy=" + approvedBy + "]";
	}

}
