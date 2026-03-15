package com.ems.dao.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.UserDao;
import com.ems.enums.UserRole;
import com.ems.enums.UserStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.User;

public class UserDaoImpl implements UserDao {

    private Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean createUser(String fullName, String email, String phone, String passwordHash,
                              int roleId, UserStatus status, LocalDateTime createdAt,
                              LocalDateTime updatedAt, String gender) throws DataAccessException {

        String sql = "INSERT INTO users (full_name,email,phone,password_hash,role_id,status,created_at,updated_at,gender) VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, passwordHash);
            ps.setInt(5, roleId);
            ps.setString(6, status.name());
            ps.setTimestamp(7, Timestamp.valueOf(createdAt));
            ps.setTimestamp(8, Timestamp.valueOf(updatedAt));
            ps.setString(9, gender);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error creating user", e);
        }
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {

        String sql = "SELECT * FROM users WHERE email=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding user", e);
        }
    }

    @Override
    public boolean updateUserStatus(int userId, UserStatus status) throws DataAccessException {

        String sql = "UPDATE users SET status=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Error updating status", e);
        }
    }

    @Override
    public List<User> findAllUsers(String userType) throws DataAccessException {
        return findAllUsers();
    }

    @Override
    public List<User> findAllUsers() throws DataAccessException {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

            return users;

        } catch (SQLException e) {
            throw new DataAccessException("Error fetching users", e);
        }
    }

    @Override
    public UserRole getRole(User user) throws DataAccessException {
        return UserRole.values()[user.getRoleId()];
    }

    private User mapUser(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRoleId(rs.getInt("role_id"));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        user.setGender(rs.getString("gender"));

        return user;
    }
}