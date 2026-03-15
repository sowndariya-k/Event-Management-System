package com.ems.dao;

<<<<<<< HEAD
public class UserDao {

	

}
=======
import java.time.LocalDateTime;
import java.util.List;

import com.ems.enums.UserRole;
import com.ems.enums.UserStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.User;

public interface UserDao {

	/**
	 * Creates a new user account
	 *
	 * @param fullName
	 * @param email
	 * @param phone
	 * @param passwordHash
	 * @param roleId
	 * @param status
	 * @param createdAt
	 * @param updatedAt
	 * @param gender
	 * @return
	 * @throws DataAccessException
	 */
	boolean createUser(String fullName, String email, String phone, String passwordHash, int roleId, UserStatus status,
			LocalDateTime createdAt, LocalDateTime updatedAt, String gender) throws DataAccessException;

	/**
	 * Finds a user by email address
	 *
	 * @param email
	 * @return user details or null if not found
	 * @throws DataAccessException
	 */
	User findByEmail(String email) throws DataAccessException;

	/**
	 * Updates the status of a user account
	 *
	 * @param userId
	 * @param status
	 * @return true if update succeeded
	 * @throws DataAccessException
	 */
	boolean updateUserStatus(int userId, UserStatus status) throws DataAccessException;

	/**
	 * Retrieves all users of a given role
	 *
	 * @param userType
	 * @return list of users
	 * @throws DataAccessException
	 */
	List<User> findAllUsers(String userType) throws DataAccessException;

	/**
	 * Retrieves all users
	 *
	 * @return list of users
	 * @throws DataAccessException
	 */
	List<User> findAllUsers() throws DataAccessException;

	/**
	 * Returns the role of the given user
	 *
	 * @param user
	 * @return user role
	 * @throws DataAccessException
	 */
	UserRole getRole(User user) throws DataAccessException;

}
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433
