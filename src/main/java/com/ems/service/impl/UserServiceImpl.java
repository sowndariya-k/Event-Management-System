package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.ems.dao.RoleDao;
import com.ems.dao.UserDao;
import com.ems.enums.UserRole;
import com.ems.enums.UserStatus;
import com.ems.exception.AuthorizationException;
import com.ems.exception.DataAccessException;
import com.ems.exception.InvalidPasswordFormatException;
import com.ems.exception.AuthenticationException;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.util.PasswordUtil;


/*
 * Handles user authentication and account management.
 *
 * Responsibilities:
 * - Authenticate users during login
 * - Create new user accounts with role assignment
 * - Validate user credentials and account status
 * - Provide user role and existence checks
 */

public class UserServiceImpl implements UserService{

	private final UserDao userDao;
	private final RoleDao roleDao;

	public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
		this.userDao = userDao;
		this.roleDao = roleDao;
	}

	/*
	 * Authenticates a user using email and password.
	 *
	 * Rules: - Email must exist - Password must match stored hash - Suspended
	 * accounts are not allowed to log in
	 */
	@Override
	public User login(String emailId, String password)
			throws AuthorizationException, AuthenticationException {

		try {
			User user = userDao.findByEmail(emailId.toLowerCase());

			if (user == null) {
				throw new AuthorizationException("Account not found. Please register first.");
			}

			if (UserStatus.SUSPENDED.name().equals(user.getStatus())) {
				throw new AuthorizationException(
						"\nYour account has been suspended!\ncontact admin@ems.com for more info");
			}

			if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {

				int attempts = user.getFailedAttempts() + 1;
				userDao.incrementFailedAttempts(user.getUserId());

				throw new AuthenticationException(
						"Invalid password. Attempt " + attempts + " of 3");
			}


			System.out.println("\nWelcome, " + user.getFullName());
			return user;

		} catch (DataAccessException e) {
			throw new AuthenticationException("Login failed");
		}
	}

	/*
	 * Creates a new user account with the specified role.
	 *
	 * Rules: - Role must exist in the system - Password must meet security
	 * requirements before hashing
	 */
	@Override
	public boolean createAccount(String fullName, String email, String phone, String password, String gender,
			UserRole role) {

		try {
			List<Role> roles = roleDao.getRoles();
			Role selectedRole = roles.stream().filter(r -> r.getRoleName().equalsIgnoreCase(role.toString()))
					.findFirst().orElse(null);

			if (selectedRole != null) {

				String hashedPassword = PasswordUtil.hashPassword(password);

				boolean isUserCreated = userDao.createUser(fullName, email, phone, hashedPassword,
						selectedRole.getRoleId(), UserStatus.ACTIVE,
						LocalDateTime.now(), null, gender);
				return isUserCreated;

			} else {
				System.out.println("Error: The role '" + role.toString() + "' was not found in the database.");
			}
		} catch (DataAccessException e) {
		    System.out.println("Database Error while creating account");
		    e.printStackTrace();
		}
		catch (InvalidPasswordFormatException e) {
		    System.out.println("Password format error");
		    e.printStackTrace();
		}
		catch (Exception e) {
		    System.out.println("Unexpected error while creating account");
		    e.printStackTrace();
		}
		return false;
	}

	/*
	 * Retrieves the role associated with the given user.
	 */
	@Override
	public UserRole getRole(User user) {
		try {
			return userDao.getRole(user);
		} catch (DataAccessException e) {
		    throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 * Checks whether a user already exists based on email.
	 */
	@Override
	public boolean checkUserExists(String email) {
		try {
			return userDao.checkUserExists(email);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	

}