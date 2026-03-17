package com.ems.menu;

import java.util.List;
import java.util.Scanner;
import com.ems.dao.UserDao;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.util.InputValidationUtil;

public class AdminMenu {
	Scanner scanner;
	User loggedInUser;
	public AdminMenu(Scanner scanner, User user) throws DataAccessException{
		this.scanner = scanner;
		this.loggedInUser = user;
		this.start();
	}
	public void start() throws DataAccessException {
		UserDao userDao = new UserDaoImpl();
		while(true) {
			System.out.println("\nAdmin Menu"
					+ "\n\nEnter your choice:\r\n"
			        + "1. View all users\r\n"
			        + "2. View all organizers\r\n"
			        + "3. Activate user account\r\n"
			        + "4. Suspend user account\r\n"
			        + "5. View all events\r\n"
			        + "6. Approve event\r\n"
			        + "7. Cancel event\r\n"
			        + "8. View event-wise registrations\r\n"
			        + "9. View organizer-wise performance\r\n"
			        + "10. View revenue reports\r\n"
			        + "11. Send system-wide notification\r\n"
			        + "12. Logout"
			        + "\n>");
			
			int input = InputValidationUtil.readInt(scanner, "");
			
			switch(input) {
				case 1:
					List<User> users = userDao.findAllUsers();
					users.stream().forEach(u -> System.out.println(u));
					break;
				case 2:
					break;
				case 3:
				    //int userId = InputValidationUtil.readInt(scanner, "Enter the user id to be activated: ");
				    //userDao.updateUserStatus(userId, "ACTIVE"); // works now
				    break;
				case 4:
				   // int uid = InputValidationUtil.readInt(scanner, "Enter the user id to be suspended: ");
				   // userDao.updateUserStatus(uid, "SUSPENDED"); // works now
				    break;
			}
		
	     }

	}

}
