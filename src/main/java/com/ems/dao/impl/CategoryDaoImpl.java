package com.ems.dao.impl;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.ems.util.DBConnectionUtil;
import com.ems.dao.CategoryDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;

public class CategoryDaoImpl implements CategoryDao {

	@Override
	public Category getCategory(int categoryId) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getActiveCategories() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
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

	    List<Category> list = new ArrayList<>();

	    try (Connection con = DBConnectionUtil.getConnection()) {

	        String query = "SELECT * FROM categories WHERE is_active = 1";
	        PreparedStatement ps = con.prepareStatement(query);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Category c = new Category(
	                rs.getInt("category_id"),
	                rs.getString("name")
	            );
	            list.add(c);
	        }

	    } catch (Exception e) {
	        e.printStackTrace(); // 👈 ADD THIS
	        throw new DataAccessException("Error fetching categories", e);
	    }

	    return list;
	}

	

}
