package com.ems.dao;

<<<<<<< HEAD
public class RoleDao {

	

=======
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
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433
}
