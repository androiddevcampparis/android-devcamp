package com.pingme.model;

import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.pingme.PingMeApplication;
import com.pingme.R;

public abstract class ActionsDetail {

	private static List<ActionsDetail> actions;
	
	protected String name;
	protected int idRes;
	protected int idType;
	protected POIData data;

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

	public abstract void execute(Context context);
	
	public ActionsDetail(String name, int idRes, int idType, POIData data) {
		super();
		this.name = name;
		this.idRes = idRes;
		this.idType = idType;
		this.data = data;
	}
	
	
	public static class MapsDriveAction extends ActionsDetail{

		public MapsDriveAction(POIData data) {
			super("Maps", R.drawable.maps, 100,data);
		}
		
		@Override
		public  void execute(Context context) {
			String uriMaps = "http://maps.google.com/maps?saddr="+PingMeApplication.getLat()+","+PingMeApplication.getLat()+"&daddr="+data.getLat()+","+data.getLng();
			Log.i("ActionDetails", "uri:"+ uriMaps);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriMaps));
			context.startActivity(intent);
		}
	}
	
	public static class SearchAction extends ActionsDetail{

		public SearchAction(POIData data) {
			super("Search", R.drawable.search, 102,data);
		}

		@Override
		public void execute(Context context) {
			Intent intent = new Intent(Intent.ACTION_SEARCH);
			intent.putExtra(SearchManager.QUERY, data.getTitle());
			context.startActivity(intent);
		}
	}
	
	public static class ShareAction extends ActionsDetail{

		public ShareAction(POIData data) {
			super("Share", R.drawable.share_img, 103,data);
		}

		@Override
		public void execute(Context context) {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "I recommand you "+data.getTitle());
			shareIntent.putExtra(Intent.EXTRA_TEXT, data.getDescription());
			
			context.startActivity(shareIntent);
		}
	}

	public static class WikipediaAction extends ActionsDetail{

		public WikipediaAction(POIData data) {
			super("wikipedia", R.drawable.wikipedia, 104, data);
		}

		@Override
		public void execute(Context context) {
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(data.getWiki_url()));
			context.startActivity(browserIntent);
		}
	}
	
	public static class LocatedPhotosAction extends ActionsDetail{

		public LocatedPhotosAction(POIData data) {
			super("photos", R.drawable.pictures_intent, 105, data);
		}

		@Override
		public void execute(Context context) {
			Intent i = new Intent("com.google.android.radar.SHOW_RADAR");
			i.putExtra("latitude", data.getLat());
			i.putExtra("longitude", data.getLng());
			context.startActivity(i);
		}
	}
}
