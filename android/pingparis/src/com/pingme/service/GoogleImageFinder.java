package com.pingme.service;

import com.pingme.model.POIData;

public class GoogleImageFinder {

	public static void searchImage(POIData data){
		String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
		url += data.getTitle();
	}
}
