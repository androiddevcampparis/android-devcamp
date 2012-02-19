package com.pingme.model;

import java.io.Serializable;

public class POI_Data implements Serializable {

	private static final long serialVersionUID = 1446988593172717449L;
	
	public static final int TYPE_LOCATION = 0;
	public static final int TYPE_WIKIPEDIA = 1;

	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private String title;
	private String descr;
	private String urlImage;
	private int[] typeEnabled;
	
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
	
	
}
