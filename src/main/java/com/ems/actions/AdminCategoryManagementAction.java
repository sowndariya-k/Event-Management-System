package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.service.AdminService;
import com.ems.util.AdminMenuHelper;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;

public class AdminCategoryManagementAction {
	private final AdminService adminService;
	private final Scanner scanner;

	public AdminCategoryManagementAction(Scanner scanner) {
		this.scanner = scanner;
		this.adminService = ApplicationUtil.adminService();
	}

	/**
	 * prints all the categories
	 */
	public void listAllCategories() {
		try {
			List<Category> categories = adminService.getAllCategories();
			if (categories.isEmpty()) {
				System.out.println("No categories found.");
				return;
			}
			AdminMenuHelper.printCategories(categories);

		} catch (DataAccessException e) {
			System.out.println("Error retrieving categories: " + e.getMessage());
		}
	}

	/**
	 * Gets user input and add a new category
	 * Category name must be between 3 and 30 characters
	 */
	public void addCategory() {
		String name = InputValidationUtil.readNonEmptyString(scanner, "Enter category name: ");
		while (name.length() < 3 || name.length() > 30) {
			name = InputValidationUtil.readNonEmptyString(scanner,
					"Enter category name (3 - 30 characters): ");
		}
		try {
			adminService.addCategory(name.trim());
			System.out.println("Category added successfully.");
		} catch (DataAccessException e) {
			System.out.println("Error adding category: " + e.getMessage());
		}
	}


	/**
	 * It prints the existing categories
	 * user select a category to update
	 */
	public void updateCategory() {
		try {
			Category selectedCategory = selectCategory();
			if (selectedCategory == null)
				return;

			if (selectedCategory.getIsActive() == 1) {
				String newName = InputValidationUtil.readNonEmptyString(scanner,
						"Enter new category name: ");
				adminService.updateCategory(selectedCategory.getCategoryId(), newName);

			} else {
				char confirm = InputValidationUtil.readChar(scanner,
						"Are you sure you want to activate this category (Y/N): ");

				if (Character.toUpperCase(confirm) != 'Y') {
					System.out.println("Category updation cancelled.");
					return;
				}
				adminService.updateCategory(selectedCategory.getCategoryId());
			}
			System.out.println("Category updated successfully");
		} catch (DataAccessException e) {
			System.out.println("Error updating category: " + e.getMessage());
		}
	}

	/**
	 * Prints all active category and admin select any
	 * and the category is deactivated, not completely deleted
	 */
	public void deleteCategory() {
		try {
			Category selectedCategory = selectCategory();
			if (selectedCategory == null)
				return;

			if (selectedCategory.getIsActive() == 1) {
				char confirm = InputValidationUtil.readChar(scanner,
						"Are you sure you want to delete this category (Y/N): ");

				if (Character.toUpperCase(confirm) != 'Y') {
					System.out.println("Category deletion cancelled.");
					return;
				}
				adminService.deleteCategory(selectedCategory.getCategoryId());
				System.out.println("Category deleted successfully");
			} else {
				System.out.println("The category is already deleted");
			}
		} catch (DataAccessException e) {
			System.out.println("Error deleting category: " + e.getMessage());
		}
	}

	/**
	 * It returns the list of all available categories
	 */
	public List<Category> getAllCategories() throws DataAccessException {
		return adminService.getAllCategories();
	}

	
	/*
	 * Helper function to display the categories
	 */
	private Category selectCategory() throws DataAccessException {

		List<Category> categories = getAllCategories();

		if (categories.isEmpty()) {
			System.out.println("No categories found.");
			return null;
		}
		AdminMenuHelper.printCategories(categories);

		int choice = InputValidationUtil.readInt(
				scanner,
				"Select a category (1-" + categories.size() + "): ");

		while (choice < 1 || choice > categories.size()) {
			choice = InputValidationUtil.readInt(
					scanner,
					"Enter a valid choice: ");
		}

		return categories.get(choice - 1);
	}
}
