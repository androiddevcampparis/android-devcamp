package com.pingme.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pingme.PingMeApplication;
import com.pingme.model.ActionsDetail.LocatedPhotosAction;
import com.pingme.model.ActionsDetail.MapsDriveAction;
import com.pingme.model.ActionsDetail.SearchAction;
import com.pingme.model.ActionsDetail.ShareAction;
import com.pingme.model.ActionsDetail.WikipediaAction;

import com.pingme.utils.Utils;

/*
 * Check https://github.com/androiddevcampparis/android-devcamp-server/blob/master/src/main/java/com/devcamp/server/resources/ResponseData.java
 */


public class POIData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id = "uid-"+System.currentTimeMillis();
	private String title;
	private String description;
	private String addresse;
	private String category;
	private double latitude;
	private double longitude;
	
	private String url_image;
	private List<String> urlsImages;
	private String wiki_link;
	private String wiki_url;
	private List<WikiData> listWiki;
	
	private String credential;
	
	private boolean plus;
	private int plusSum = 32;

	public POIData copy() {
		POIData data = new POIData();
		data.id = id;
		data.title = title;
		data.description = description;
		data.addresse = addresse;
		data.category = category;
		data.latitude = latitude;
		data.longitude = longitude;
		data.url_image = url_image;
		data.wiki_link = wiki_link;
		data.wiki_url = wiki_url;
		data.credential = credential;
		data.plus = plus;
		data.plusSum = plusSum;
		return data;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		//RULES POUR CORRIGER OPEN DATA
		if("immeuble".equalsIgnoreCase(title)){
			return description.substring(0, Math.min(40, description.length()));
		}
		
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


	public void setPlus( boolean plus ){
		this.plus = plus;
	}
	public boolean isPlus(){
		return this.plus;
	}

	public void setPlusSum( int plusSum ){
		this.plusSum = plusSum;
	}
	public int getPlusSum(){
		return this.plusSum;
	}

	private List<ActionsDetail> actions;


	public List<ActionsDetail> getActions() {
		List<ActionsDetail> actions = new ArrayList<ActionsDetail>();
		actions.add(new MapsDriveAction(this));
		
		if(!Utils.isEmpty(wiki_url)){
			actions.add(new WikipediaAction(this));
		} 
		
		if(PingMeApplication.isPhotoIntentCallable()){
			actions.add(new LocatedPhotosAction(this));
		}
		
		//actions.add(new YoutubeAction(this));
		actions.add(new SearchAction(this));
		actions.add(new ShareAction(this));
		
		return actions;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public List<WikiData> getListWiki() {
		return listWiki;
	}

	public void setListWiki(List<WikiData> listWiki) {
		this.listWiki = listWiki;
	}

	public List<String> getUrlsImages() {
		return urlsImages;
	}

	public void setUrlsImages(List<String> urlsImages) {
		this.urlsImages = urlsImages;
	}
		
}
