package com.ems.actions;

import java.time.Instant;
import java.util.Objects;
import java.util.Scanner;

import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.exception.InvalidPasswordFormatException;
import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.PasswordUtil;

public class UserAction {

    private final Scanner scanner;
    private final UserService userService;

    public UserAction(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    /* ================= LOGIN ================= */

    public User login() throws AuthorizationException, AuthenticationException {

        String email = InputValidationUtil.readNonEmptyString(
            scanner,
            "Enter email: "
        );

        while (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            email = InputValidationUtil.readNonEmptyString(
                scanner,
                "Enter valid Email Address: "
            );
        }

        String password = InputValidationUtil.readNonEmptyString(
            scanner,
            "Enter password: "
        );

        return userService.login(email, password);
    }

    public UserRole getUserRole(User user) {
        return userService.getRole(user);
    }

    /* ================= CREATE ACCOUNT ================= */

    public void createAccount(UserRole role) {

        String fullName;
        do {
            fullName = InputValidationUtil.readNonEmptyString(
                scanner,
                "Enter Full Name (2-30 chars): "
            );

            if (fullName.length() < 2 || fullName.length() > 30) {
                System.out.println("Name must be between 2 and 30 characters.");
            }

        } while (fullName.length() < 2 || fullName.length() > 30);

        String email;
        while (true) {

            email = InputValidationUtil.readNonEmptyString(
                scanner,
                "Enter Email (max 100 chars): "
            ).trim().toLowerCase();

            if (email.length() > 100) {
                System.out.println("Email too long.");
                continue;
            }

            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                System.out.println("Invalid email format.");
                continue;
            }

            if (userExists(email)) {
                System.out.println("Email already registered.");
                continue;
            }

            break;
        }

        String phone = InputValidationUtil.readString(
            scanner,
            "Enter phone (optional): "
        );

        if (phone != null && !phone.trim().isEmpty()) {

            phone = phone.replaceAll("\\D", "");

            while (!phone.matches("^[6-9][0-9]{9}$")) {
                phone = InputValidationUtil.readString(
                    scanner,
                    "Enter valid 10-digit phone: "
                ).replaceAll("\\D", "");
            }

        } else {
            phone = null;
        }

        String passwordPrompt =
            "Create password:\n" +
            "Min 8 chars, 1 upper, 1 lower, 1 number, 1 special\n";

        String password = InputValidationUtil.readNonEmptyString(
            scanner,
            passwordPrompt
        );

        while (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
            password = InputValidationUtil.readNonEmptyString(
                scanner,
                "Enter valid password: "
            );
        }

        int genderChoice;
        do {
            genderChoice = InputValidationUtil.readInt(
                scanner,
                "1. Male\n2. Female\n3. Prefer not to say\n"
            );
        } while (genderChoice < 1 || genderChoice > 3);

        String gender = (genderChoice == 1)
            ? "Male"
            : (genderChoice == 2)
            ? "Female"
            : "Opt-out";

        boolean created = userService.createAccount(
            fullName, email, phone, password, gender, role
        );

        System.out.println(
            created
            ? "\nAccount created successfully!\n"
            : "\nAccount creation failed!\n"
        );
    }

    /* ================= UPDATE PROFILE ================= */

    public boolean updateProfile(User user) {

        System.out.println("\nUpdate Profile (press Enter to skip)");

        String fullName = user.getFullName();
        String phone = user.getPhone();
        String passwordHash = user.getPasswordHash();

        /* ================= FULL NAME ================= */

        String newName = InputValidationUtil.readString(
            scanner,
            "Name (" + fullName + "): "
        );

        if (newName != null && !newName.trim().isEmpty()) {

            while (newName.length() < 2 || newName.length() > 30) {
                newName = InputValidationUtil.readString(
                    scanner,
                    "Name must be 2-30 chars: "
                );
            }

            fullName = newName;
        }

        /* ================= PHONE ================= */
        String newPhone = InputValidationUtil.readString(
            scanner,
            "Phone (" + (phone == null ? "Not set" : phone) + "): "
        );

        if (newPhone != null && !newPhone.trim().isEmpty()) {

            while (true) {
                String cleaned = newPhone.replaceAll("\\D", "");

                if (cleaned.matches("^[6-9][0-9]{9}$")) {
                    phone = cleaned;
                    break;
                }

                newPhone = InputValidationUtil.readString(
                    scanner,
                    "Enter valid phone or press Enter to skip: "
                );

                if (newPhone.trim().isEmpty()) break;
            }
        }

        /* ================= PASSWORD ================= */
        char changePassword = InputValidationUtil.readChar(
            scanner,
            "Change password? (Y/N): "
        );

        if (Character.toUpperCase(changePassword) == 'Y') {

            while (true) {
                String newPassword = InputValidationUtil.readNonEmptyString(
                    scanner,
                    "Enter new password: "
                );

                if (!newPassword.matches(
                    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$"
                )) {
                    System.out.println("Invalid password format.");
                    continue;
                }

                try {
                    passwordHash = PasswordUtil.hashPassword(newPassword);
                    break;
                } catch (InvalidPasswordFormatException e) {
                    System.out.println("Password error. Retry.");
                }
            }
        }

        boolean changed =
            !fullName.equals(user.getFullName()) ||
            !Objects.equals(phone, user.getPhone()) ||
            !passwordHash.equals(user.getPasswordHash());

        if (!changed) {
            System.out.println("No changes made.");
            return false;
        }

        /* ================= CREATE NEW USER OBJECT ================= */
        User updatedUser = new User(
            user.getUserId(),
            fullName,
            user.getEmail(),
            phone,
            passwordHash,
            user.getRoleId(),
            user.getStatus(),
            user.getCreatedAt(),
            Instant.now(),
            user.getGender(),
            user.getFailedAttempts(),
            user.getLastLogin()
        );

        /* ================= SAVE ================= */
    	
	    boolean updated = userService.updateUserProfile(updatedUser);
	
	    if (updated) {
	        System.out.println("\nProfile updated successfully. \n");
	        System.out.println("For security reasons, please log in again.\n");
	    } else {
	        System.out.println("\nProfile update failed.\n");
	    }
		return updated;
	}
	
    public boolean userExists(String email) {
        return userService.checkUserExists(email);
    }
}