package com.ems.dao.impl;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import com.ems.util.DBConnectionUtil;
import com.ems.dao.CategoryDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;

public class CategoryDaoImpl implements CategoryDao {

	@Override
	public Category getCategory(int categoryId) throws DataAccessException {

	    String sql = "SELECT category_id, name FROM categories WHERE category_id = ?";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, categoryId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return new Category(
	                rs.getInt("category_id"),
	                rs.getString("name")
	            );
	        }

	    } catch (SQLException e) {
	        throw new DataAccessException("Error fetching category", e);
	    }

	    return null;
	}

	@Override
	public List<Category> getActiveCategories() throws DataAccessException {
		  // List only active categories sorted for UI consistency
        String sql = "select category_id, name from categories where is_active = 1 order by name, category_id";
        List<Category> categories = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("name")
                    )
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while fetching categories", e);
        }

        return categories;
	}

	 @Override
	    public void addCategory(String name) throws DataAccessException {
	        // Inserts a new category, uniqueness enforced at DB level
	        String sql = "insert into categories (name) values (?)";

	        try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setString(1, name.trim());
	            ps.executeUpdate();

	        } catch (SQLException e) {
	            // Converts DB duplicate constraint into a domain error
	            if (isDuplicateKey(e)) {
	                throw new DataAccessException("Category already exists: " + name, e);
	            }
	            throw new DataAccessException("Unable to add category", e);
	        }
	    }
	 

	 @Override
	    public void updateCategoryName(int categoryId, String name)
	            throws DataAccessException {

	        // Updates name only if the category is active
	        String sql = "update categories set name=? where category_id=? and is_active = 1";

	        try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setString(1, name);
	            ps.setInt(2, categoryId);

	            int rows = ps.executeUpdate();

	            // Zero rows means invalid id or inactive category
	            if (rows == 0) {
	                throw new DataAccessException("Category not found");
	            }
	        } catch (SQLException e) {
	            throw new DataAccessException("Unable to update category", e);
	        }
	    }

	

	@Override
    public void deactivateCategory(int categoryId)
            throws DataAccessException {

        // Soft delete using activation flag
        String sql = "update categories set is_active=0 where category_id=? and is_active = 1";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("Failed to deactivate the category");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to deactivate category", e);
        }
    }

	 @Override
	    public void activateCategory(int categoryId)
	            throws DataAccessException {

	        // Re-enables a previously deactivated category
	        String sql = "update categories set is_active=1 where category_id=? and is_active = 0";

	        try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {

	            ps.setInt(1, categoryId);
	            int rows = ps.executeUpdate();

	            if (rows == 0) {
	                throw new DataAccessException("Failed to activate the category");
	            }

	        } catch (SQLException e) {
	            throw new DataAccessException(
	                "Unable to activate category with ID: " + categoryId, e
	            );
	        }
	    }



	 @Override
	    public List<Category> getAllCategories() throws DataAccessException {
	        // Includes both active and inactive records for admin views
	        String sql = "select category_id, name, is_active from categories order by category_id";
	        List<Category> categories = new ArrayList<>();

	        try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {

	            while (rs.next()) {
	                categories.add(
	                    new Category(
	                        rs.getInt("category_id"),
	                        rs.getString("name"),
	                        rs.getInt("is_active")
	                    )
	                );
	            }
	        } catch (SQLException e) {
	            throw new DataAccessException("Error while fetching categories", e);
	        }

	        return categories;
	    }
	 
	 private boolean isDuplicateKey(SQLException e) {
	        // MySQL duplicate key constraint
	        return "23000".equals(e.getSQLState());
	    }


	

}
