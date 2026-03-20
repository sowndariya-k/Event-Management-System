package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;

public interface CategoryDao {
	/**
	 * Gets the category details using the category id.
	 *
	 * @param categoryId the unique identifier of the category
	 * @return the category data object for the given id
	 * @throws DataAccessException if the category cannot be retrieved
	 */
	Category getCategory(int categoryId)  throws DataAccessException;

	/**
	 * Gets all active categories
	 * 
	 * @return List<Category>
	 * @throws DataAccessException
	 */
	List<Category> getActiveCategories()  throws DataAccessException;
	
	/**
	 * To add new category
	 * 
	 * @param name
	 * @throws DataAccessException
	 */
	void addCategory(String name) throws DataAccessException;

	/**
	 * Updates the existing category
	 * 
	 * @param categoryId
	 * @param new name
	 * @throws DataAccessException
	 */
	void updateCategoryName(int categoryId, String name) throws DataAccessException;
	
	/**
	 * Gets all active and inactive categories
	 * 
	 * @return List<Category>
	 * @throws DataAccessException
	 */
	List<Category> getAllCategories() throws DataAccessException;
		
	/**
	 * Deactivate the category-mark as inactive.
	 *
	 * @param categoryId
	 * @throws DataAccessException
	 */
	void deactivateCategory(int categoryId) throws DataAccessException;

	/**
	 * Activate the deactivated category-reactivates a category.
	 *
	 * @param categoryId
	 * @throws DataAccessException
	 */
	void activateCategory(int categoryId) throws DataAccessException;

	void listAllCategory();
}