/*
 * Author : Sowndariya
 * User is a model class that represents a registered user
 * in the system, containing user ID, name, email, hashed
 * password, role, status, and timestamps, implementing
 * Comparable for sorting by user attributes.
 */
package com.ems.model;

import java.time.Instant;
import com.ems.enums.UserStatus;

public class User implements Comparable<User> {

	private final int userId;
	private final String fullName;
	private final String email;
	private final String phone;
	private final String passwordHash;
	private final int roleId;
	private final UserStatus status;
	private final Instant createdAt;
	private final Instant updatedAt;
	private final String gender;
	private final int failedAttempts;
	private final Instant lastLogin;

	public User(int userId, String fullName, String email, String phone, String passwordHash, int roleId,
			UserStatus status,
			Instant createdAt, Instant updatedAt, String gender, int failedAttempts, Instant lastLogin) {
		this.userId = userId;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.passwordHash = passwordHash;
		this.roleId = roleId;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.gender = gender;
		this.failedAttempts = failedAttempts;
		this.lastLogin = lastLogin;
	}

	public int getUserId() {
		return userId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public int getRoleId() {
		return roleId;
	}

	public UserStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public String getGender() {
		return gender;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public Instant getLastLogin() {
		return lastLogin;
	}

	@Override
	public int compareTo(User other) {
		return Integer.compare(this.userId, other.userId);
	}
}