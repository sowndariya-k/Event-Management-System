package com.ems.actions;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import com.ems.dao.UserDao;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.enums.UserStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.User;

public class UserAction {

    private UserDao userDao;

    public UserAction(Connection connection) {
        userDao = new UserDaoImpl(connection);
    }

    public boolean registerUser(String fullName, String email, String phone,
                                String passwordHash, int roleId, String gender) {

        try {

            return userDao.createUser(
                    fullName,
                    email,
                    phone,
                    passwordHash,
                    roleId,
                    UserStatus.ACTIVE,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    gender);

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {

        try {
            return userDao.findByEmail(email);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<User> getAllUsers() {

        try {
            return userDao.findAllUsers();
        } catch (DataAccessException e) {
            return null;
        }
    }
}