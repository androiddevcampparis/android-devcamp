package com.pingme.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.pingme.model.ActionsDetail.MapsDriveAction;
import com.pingme.model.ActionsDetail.SearchAction;
import com.pingme.model.ActionsDetail.ShareAction;

public class POI_Data implements Serializable {

	private static final long serialVersionUID = 1446988593172717449L;
	
	public static final int TYPE_LOCATION = 0;
	public static final int TYPE_WIKIPEDIA = 1;

	private String id;
	private String title;
	private String descr;
	private String urlImage;
	private double lat;
	private double lng;
	private int[] typeEnabled;
	private List<ActionsDetail> actions;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getUrlImage() {
		return urlImage;
	}
	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}
	public int[] getTypeEnabled() {
		return typeEnabled;
	}
	public void setTypeEnabled(int[] typeEnabled) {
		this.typeEnabled = typeEnabled;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	
	public List<ActionsDetail> getActions() {
		if(actions == null){
			createActions();
		}
		return actions;
	}

	public void createActions() {
		actions = new LinkedList<ActionsDetail>();
		actions.add(new MapsDriveAction(this));
		actions.add(new SearchAction(this));
		actions.add(new ShareAction(this));
	}
}
