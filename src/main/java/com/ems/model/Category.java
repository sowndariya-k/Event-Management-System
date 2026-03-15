package com.ems.model;

public class Category {
<<<<<<< HEAD

	

=======
	private int categoryId;
	private String name;
	private int isActive;
	
	public Category(int categoryId, String name) {
		this.categoryId = categoryId;
		this.name = name;
	}

	public Category(int categoryId, String name, int isActive) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.isActive = isActive;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433
}
