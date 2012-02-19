package com.pongme.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.pongme.PingMeApplication;
import com.pongme.R;

public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	private transient static List<Category> categories;
	
	private String name;
	private int idRes;
	private String idSync;
	private boolean checked;
	
	public Category(String name, int idRes, String idSync) {
		super();
		this.name = name;
		this.idRes = idRes;
		this.setIdSync(idSync);
	}

	public static List<Category> getCategories() {
		if(categories == null){
			buildPreferences();
		}
		return categories;
	}
	
	public static void buildPreferences() {
		categories = new LinkedList<Category>();
		categories.add(new Category("Historic Places", R.drawable.fortress, "historic"));
		categories.add(new Category("Museum", R.drawable.museum, "museum"));
		categories.add(new Category("Gardens", R.drawable.tree_1, "garden"));
		categories.add(new Category("Monuments", R.drawable.eiffel200, "monument"));
		
		for(Category category: categories){
			PingMeApplication.loadCategory(category);
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIdRes() {
		return idRes;
	}
	public void setIdRes(int idRes) {
		this.idRes = idRes;
	}

	public String getIdSync() {
		return idSync;
	}

	public void setIdSync(String idSync) {
		this.idSync = idSync;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
}
