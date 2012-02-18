package com.pingme.model;

import java.util.LinkedList;
import java.util.List;

import com.pingme.PingMeApplication;
import com.pingme.R;

public class Preferences {
	
	private static List<Preferences> preferences;
	
	private String name;
	private int idRes;
	private int idSync;
	private boolean checked;
	
	public Preferences(String name, int idRes, int idSync) {
		super();
		this.name = name;
		this.idRes = idRes;
		this.setIdSync(idSync);
	}

	public static List<Preferences> getPreferences() {
		if(preferences == null){
			buildPreferences();
		}
		return preferences;
	}
	
	public static void buildPreferences() {
		preferences = new LinkedList<Preferences>();
		preferences.add(new Preferences("Historic Places", R.drawable.fortress, 0));
		preferences.add(new Preferences("Museum", R.drawable.museum, 1));
		preferences.add(new Preferences("Gardens", R.drawable.tree_1, 2));
		preferences.add(new Preferences("Monuments", R.drawable.eiffel200, 3));
		
		for(Preferences pref: preferences){
			pref.setChecked(PingMeApplication.getPrefStatus(pref.getName()));
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

	public int getIdSync() {
		return idSync;
	}

	public void setIdSync(int idSync) {
		this.idSync = idSync;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
}
