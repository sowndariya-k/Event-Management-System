package com.ems.model;

<<<<<<< HEAD
public class Role {

	
=======
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
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433

}
