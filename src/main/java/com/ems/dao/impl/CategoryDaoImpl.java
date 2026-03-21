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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCategoryName(int categoryId, String name) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void deactivateCategory(int categoryId) throws DataAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateCategory(int categoryId) throws DataAccessException {
		// TODO Auto-generated method stub
		
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

	

}
