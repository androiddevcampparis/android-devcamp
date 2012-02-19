package com.pongme.model;

import java.io.Serializable;

public class Coordinate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public double lat;
	public double lng;
	
	public Coordinate(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
}
