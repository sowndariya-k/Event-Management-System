package com.ems.dao;

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

    /**
     * Checks whether a user exists by email
     *
     * @param email
     * @return true if user exists
     * @throws DataAccessException
     */
    boolean checkUserExists(String email) throws DataAccessException;
    /**
     * Increments failed login attempt count
     *
     * @param userId
     * @throws DataAccessException
     */
    void incrementFailedAttempts(int userId) throws DataAccessException;

    /**
     * Resets failed login attempts and updates last login time
     *
     * @param userId
     * @throws DataAccessException
     */
    void resetFailedAttempts(int userId) throws DataAccessException;

    /**
     * update the user details
     * 
     * @param user
     * @return true is user updated
     * @throws DataAccessException
     */
    boolean updateUser(User user) throws DataAccessException;
    
}