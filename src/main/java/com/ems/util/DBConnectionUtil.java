/*
 * Author : Sowndariya
 * DBConnectionUtil is a utility class that manages the
 * JDBC database connection to MySQL, providing a static
 * method to obtain a Connection instance using the
 * configured driver, URL, username, and password.
 */
package com.ems.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

	private static final String URL = "jdbc:mysql://localhost:3306/event_management_system";
	private static final String USER = "root";
	private static final String PASSWORD = "Sow@911!kavin";

	private DBConnectionUtil() {
	}

	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("MySQL JDBC Driver not found.", e);
		}
		return DriverManager.getConnection(URL, USER, PASSWORD);

	}

}
