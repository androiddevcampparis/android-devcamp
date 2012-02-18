package com.pingme.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import com.pingme.model.ActionsDetail.MapsDriveAction;
import com.pingme.model.ActionsDetail.SearchAction;
import com.pingme.model.ActionsDetail.ShareAction;

/*
 * Check https://github.com/androiddevcampparis/android-devcamp-server/blob/master/src/main/java/com/devcamp/server/resources/ResponseData.java
 */


public class POIData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private String description;
	private String addresse;
	private String category;
	private double latitude;
	private double longitude;
	
	private String url_image;
	private String wiki_link;
	private String wiki_url;
	

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddresse() {
		return addresse;
	}

	public void setAddresse(String addresse) {
		this.addresse = addresse;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUrl_image() {
		return url_image;
	}

	public void setUrl_image(String url_image) {
		this.url_image = url_image;
	}

	public String getWiki_link() {
		return wiki_link;
	}

	public void setWiki_link(String wiki_link) {
		this.wiki_link = wiki_link;
	}

	public double getLat() {
		return latitude;
	}

	public void setLat(double latitude) {
		this.latitude = latitude;
	}

	public double getLng() {
		return longitude;
	}

	public void setLng(double longitude) {
		this.longitude = longitude;
	}

	public String getWiki_url() {
		return wiki_url;
	}

	public void setWiki_url(String wiki_url) {
		this.wiki_url = wiki_url;
	}


	private List<ActionsDetail> actions;

	public List<ActionsDetail> getActions() {
		if(actions == null){
			createActions();
		}
		return actions;
	}

	public void createActions() {
		actions = new ArrayList<ActionsDetail>();
		actions.add(new MapsDriveAction(this));
		actions.add(new SearchAction(this));
		actions.add(new ShareAction(this));
	}
		
}
