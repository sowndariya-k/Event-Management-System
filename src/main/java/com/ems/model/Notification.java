/*
 * Author : Sowndariya
 * Notification is a model class that represents a system
 * or event notification, containing recipient details,
 * notification type, message content, and read status,
 * mapped to the notifications table.
 */
package com.ems.model;

import java.time.Instant;

public class Notification {

	private int notificationId;
	private int userId;
	private String message;
	private String type;
	private Instant createdAt;
	private boolean readStatus;

	public Notification(int notificationId, int userId, String message, String type, Instant createdAt,
			boolean readStatus) {
		super();
		this.notificationId = notificationId;
		this.userId = userId;
		this.message = message;
		this.type = type;
		this.createdAt = createdAt;
		this.readStatus = readStatus;
	}

	public Notification() {
		
	}


	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isReadStatus() {
		return readStatus;
	}

	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}

}
