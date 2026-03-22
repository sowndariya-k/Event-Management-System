package com.ems.actions;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.util.ApplicationUtil;

public class AdminUserManagementAction {
	private final AdminService adminService;

	public AdminUserManagementAction() {
		this.adminService = ApplicationUtil.adminService();
	}
	
	public List<User> getUsersByRole(String role) throws DataAccessException {
		return adminService.getUsersList(role);
	}

	public void listUsersByRole(String role) {
		
		
		
	}
	public List<User> getAllUsers() throws DataAccessException {
		return adminService.getAllUsers();
	}

	public void changeUserStatus(String status) {
		
		
	}

	

}
