/*
 * Author : Sowndariya
 * Role is a model class that represents a user role
 * entity with a role ID, role name, and creation
 * timestamp, mapped to the roles table in the database.
 */
package com.ems.model;

import java.time.Instant;

public class Role {

	private int roleId;
	private String roleName;
	private Instant createdAt;

	public Role(int roleId, String roleName, Instant createdAt) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.createdAt = createdAt;
	}

	public Role() {
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

}
