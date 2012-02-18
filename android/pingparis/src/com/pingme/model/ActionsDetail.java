package com.pingme.model;

import java.util.LinkedList;
import java.util.List;

import com.pingme.R;

import android.content.Intent;

public class ActionsDetail {

	private static List<ActionsDetail> actions;
	
	private String name;
	private int idRes;
	private int idType;
	private Intent action;
	
	public static List<ActionsDetail> getActions() {
		if(actions == null){
			createActions();
		}
		return actions;
	}

	public static void createActions() {
		actions = new LinkedList<ActionsDetail>();
		actions.add(new ActionsDetail("Route", R.drawable.car_grey, 100, null));
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

	public int getIdType() {
		return idType;
	}

	public void setIdType(int idSync) {
		this.idType = idSync;
	}

	public Intent getAction() {
		return action;
	}

	public void setAction(Intent action) {
		this.action = action;
	}

	public ActionsDetail(String name, int idRes, int idType, Intent action) {
		super();
		this.name = name;
		this.idRes = idRes;
		this.idType = idType;
		this.action = action;
	}
	
	
}
