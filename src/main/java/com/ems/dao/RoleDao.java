package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Role;

public interface RoleDao {
	/**
	 * Retrieves all active roles from the system
	 *
	 * @return list of roles
	 * @throws DataAccessException
	 */
	List<Role> getRoles() throws DataAccessException;
}
