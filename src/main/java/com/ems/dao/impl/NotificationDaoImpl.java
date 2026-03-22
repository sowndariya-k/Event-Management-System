package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.enums.UserStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

/*
 * Handles database operations related to notifications.
 *
 * Responsibilities:
 * - Persist system, role based, and user specific notifications
 * - Retrieve user notifications
 * - Update notification read state
 */
public class NotificationDaoImpl implements NotificationDao {

	@Override
	public void sendSystemWideNotification(String message, NotificationType notificationType)
			throws DataAccessException {

		// Inserts one notification row per active user
		String sql = "insert into notifications (user_id, message, type,"
				+ " created_at, read_status) select u.user_id"
				+ ", ? , ?, utc_timestamp(), false from users u"
				+ " where u.status = ? ";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, message);
			ps.setString(2, notificationType.name());
			ps.setString(3, UserStatus.ACTIVE.name());
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Database error while sending system notification");
		}
	}

	@Override
	public List<Notification> getUnreadNotifications(int userId)
			throws DataAccessException {
		// Fetches unread notifications ordered by newest first
		String sql = "select * from notifications where user_id = ? and read_status = FALSE order by created_at desc";
		List<Notification> notifications = new ArrayList<>();
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Notification notification = new Notification();
				notification.setNotificationId(rs.getInt("notification_id"));
				notification.setUserId(rs.getInt("user_id"));
				notification.setMessage(rs.getString("message"));
				notification.setType(rs.getString("type"));
				notification.setCreatedAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("created_at")));
				notification.setReadStatus(rs.getBoolean("read_status"));
				notifications.add(notification);
			}
			rs.close();

		} catch (SQLException e) {
			throw new DataAccessException("Database error while reading notification");
		}

		return notifications;
	}

	@Override
	public void markAsRead(int notificationId) throws DataAccessException {
		String sql = "update notifications set read_status = 1 where notification_id = ?";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, notificationId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Database error while marking notification as read");
		}
	}

	@Override
	public List<Notification> getAllNotifications(int userId) throws DataAccessException {
		List<Notification> notifications = new ArrayList<>();
		String sql = "select * from notifications where user_id = ? order by created_at desc";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Notification notification = new Notification();
				notification.setNotificationId(rs.getInt("notification_id"));
				notification.setUserId(rs.getInt("user_id"));
				notification.setMessage(rs.getString("message"));
				notification.setType(rs.getString("type"));
				notification.setCreatedAt(DateTimeUtil.fromTimestamp(rs.getTimestamp("created_at")));
				notification.setReadStatus(rs.getBoolean("read_status"));
				notifications.add(notification);
			}
			rs.close();
			return notifications;

		} catch (SQLException e) {
			throw new DataAccessException("Database error while reading notifications");
		}

	}

	@Override
	public void markAllAsRead(int userId) throws DataAccessException {

		// Bulk update to avoid per row updates
		String sql = "UPDATE notifications " +
				"SET read_status = TRUE " +
				"WHERE user_id = ? AND read_status = FALSE";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Database error while updating notifications");
		}
	}

	@Override
	public boolean sendNotification(int userId, String message, NotificationType notificationType)
			throws DataAccessException {
		String sql = "insert into notifications (user_id, message, type,"
				+ " created_at, read_status) values (?"
				+ ", ? , ?, ?, ?)";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setString(2, message);
			ps.setString(3, notificationType.name());
			ps.setTimestamp(4, DateTimeUtil.toTimestamp(DateTimeUtil.nowUtc()));
			ps.setBoolean(5, false);

			int affectedRows = ps.executeUpdate();

			return affectedRows > 0;

		} catch (SQLException e) {
			throw new DataAccessException("Database error while sending notification");
		}
	}

	@Override
	public void sendNotificationByRole(String message, NotificationType notificationType, UserRole role)
			throws DataAccessException {

		// Sends notification only to active users matching the role
		String sql = "insert into notifications (user_id, message, type, created_at, read_status) "
				+ "select u.user_id, ? , ?, utc_timestamp(), false from users u "
				+ "inner join roles r on u.role_id = r.role_id "
				+ "where u.status = ? and role_name = ?";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, message);
			ps.setString(2, notificationType.name());
			ps.setString(3, UserStatus.ACTIVE.name());
			ps.setString(4, role.name());
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Database error while sending role-based notification");
		}
	}
}