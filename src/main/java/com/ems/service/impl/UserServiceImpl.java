/*
 * Author : Sowndariya
 * UserServiceImpl implements the UserService interface
 * and provides business logic for user authentication,
 * new user registration, profile updates, and password
 * management using the UserDao and RoleDao.
 */
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

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;

    public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    /* ================= LOGIN ================= */

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
                    "Your account is suspended. Contact admin."
                );
            }

            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {

                int attempts = user.getFailedAttempts() + 1;
                userDao.incrementFailedAttempts(user.getUserId());

                throw new AuthenticationException(
                    "Invalid password. Attempt " + attempts + " of 3"
                );
            }

            // reset attempts after success (you forgot this)
            userDao.resetFailedAttempts(user.getUserId());

            System.out.println("\nWelcome, " + user.getFullName());
            return user;

        } catch (DataAccessException e) {
            throw new AuthenticationException("Login failed");
        }
    }

    /* ================= CREATE ACCOUNT ================= */

    @Override
    public boolean createAccount(
            String fullName,
            String email,
            String phone,
            String password,
            String gender,
            UserRole role) {

        try {
            List<Role> roles = roleDao.getRoles();

            Role selectedRole = roles.stream()
                .filter(r -> r.getRoleName().equalsIgnoreCase(role.toString()))
                .findFirst()
                .orElse(null);

            if (selectedRole == null) {
                System.out.println("Invalid role.");
                return false;
            }

            String hashedPassword = PasswordUtil.hashPassword(password);

            return userDao.createUser(
                fullName,
                email,
                phone,
                hashedPassword,
                selectedRole.getRoleId(),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                gender
            );

        } catch (DataAccessException e) {
            System.out.println("Database error during account creation");
        } catch (InvalidPasswordFormatException e) {
            System.out.println("Password format error");
        }

        return false;
    }

    /* ================= ROLE ================= */

    @Override
    public UserRole getRole(User user) {
        try {
            return userDao.getRole(user);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /* ================= EXISTS ================= */

    @Override
    public boolean checkUserExists(String email) {
        try {
            return userDao.checkUserExists(email);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /* ================= UPDATE PROFILE ================= */


	@Override
	public boolean updateUserProfile(User user) {
		try {
			return userDao.updateUser(user);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}