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
import com.ems.util.DBConnectionUtil;

public class UserDaoImpl implements UserDao {

	@Override
	public boolean createUser(String fullName, String email, String phone, String passwordHash, int roleId,
			UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, String gender)

			throws DataAccessException {

		String sql = "insert into users(full_name, email, phone, password_hash, role_id, created_at, status, "
				+ "updated_at, gender) values (?,?,?,?,?,?,?,?,?)";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, fullName);
			ps.setString(2, email.toLowerCase());
			ps.setString(3, phone);
			ps.setString(4, passwordHash);
			ps.setInt(5, roleId);
			ps.setTimestamp(6, Timestamp.valueOf(createdAt));
			ps.setString(7, status.name());
			ps.setTimestamp(8, null);
			ps.setString(9, gender);
			ps.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("Error while creating user account");
		}
	}

	@Override
	public User findByEmail(String email) throws DataAccessException {

		// Email lookup is normalized to lowercase

		User user = null;
		String sql = "select * from users where email = ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, email.toLowerCase());

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					user = new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
							rs.getString("phone"), rs.getString("password_hash"), rs.getInt("role_id"),
							UserStatus.valueOf(rs.getString("status")),
							rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null,
							rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toInstant() : null,
							rs.getString("gender"), rs.getInt("failed_attempts"),
							rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toInstant() : null);
				}
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching user");
		}

		return user;
	}

	@Override
	public List<User> findAllUsers(String userType) throws DataAccessException {
		String sql = "select u.* from users u inner join roles r on u.role_id = r.role_id " + "where r.role_name = ?";
		List<User> users = new ArrayList<>();

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, userType);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User user = new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
						rs.getString("phone"), rs.getString("password_hash"), rs.getInt("role_id"),
						UserStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null,
						rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toInstant() : null,
						rs.getString("gender"), rs.getInt("failed_attempts"),
						rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toInstant() : null);
				users.add(user);
			}
			rs.close();

		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching users:");
		}

		return users;
	}

	@Override
	public UserRole getRole(User user) throws DataAccessException {

		String query = "SELECT role_name FROM roles WHERE role_id = ?";

		try (Connection conn = DBConnectionUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, user.getRoleId());

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				String roleName = rs.getString("role_name").toUpperCase();

				if (roleName.equals("USER")) {
					roleName = "ATTENDEE";
				}

				return UserRole.valueOf(roleName);
			}

		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching role of user");
		}

		throw new DataAccessException("Role not found");
	}

	@Override
	public List<User> findAllUsers() throws DataAccessException {
		String sql = "select * from users order by role_id";
		List<User> users = new ArrayList<>();

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User user = new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
						rs.getString("phone"), rs.getString("password_hash"), rs.getInt("role_id"),
						UserStatus.valueOf(rs.getString("status")),
						rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null,
						rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toInstant() : null,
						rs.getString("gender"), rs.getInt("failed_attempts"),
						rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toInstant() : null);
				users.add(user);
			}
			rs.close();

		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching users");
		}

		return users;
	}

	@Override
	public boolean checkUserExists(String email) throws DataAccessException {
		String sql = "SELECT COUNT(1) FROM users WHERE email = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, email.toLowerCase().trim());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching users");
		}
		return false;
	}

	@Override
	public void incrementFailedAttempts(int userId) throws DataAccessException {

		// Used for account lock or security checks

		String sql = "update users set failed_attempts = failed_attempts + 1 where id = ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException("Error while updating the failed attempts");
		}
	}

}