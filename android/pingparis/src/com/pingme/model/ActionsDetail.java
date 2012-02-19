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
	
	protected int nameId;
	protected int idRes;
	protected int idType;
	protected POIData data;

	public int getName() {
		return nameId;
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
	
	public ActionsDetail(int name, int idRes, int idType, POIData data) {
		super();
		this.nameId = name;
		this.idRes = idRes;
		this.idType = idType;
		this.data = data;
	}
	
	
	public static class MapsDriveAction extends ActionsDetail{

		public MapsDriveAction(POIData data) {
			super(R.string.maps, R.drawable.maps, 100,data);
		}
		
		@Override
		public  void execute(Context context) {
			String uriMaps = "http://maps.google.com/maps?saddr="+PingMeApplication.getLat()+","+PingMeApplication.getLng()+"&daddr="+data.getLat()+","+data.getLng();
			Log.i("ActionDetails", "uri:"+ uriMaps);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriMaps));
			
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			context.startActivity(intent);
		}
	}
	
	public static class SearchAction extends ActionsDetail{

		public SearchAction(POIData data) {
			super(R.string.search, R.drawable.search, 102,data);
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
			super(R.string.share, R.drawable.share_img, 103,data);
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
		public WikiData wikidata;
		
		public String getNameStr(Context context){
			return context.getString(R.string.wikipediaExt) + " "+wikidata.getName();
		}

		public WikipediaAction(POIData data) {
			super(R.string.wikipedia, R.drawable.wikipedia, 104, data);
		}
		
		public WikipediaAction(POIData data, WikiData wikiData) {
			this(data);
			this.wikidata = wikiData;
		}

		@Override
		public void execute(Context context) {
			if(wikidata==null && wikidata==null){
				return;
			}
			
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(wikidata==null?data.getWiki_url():wikidata.getUrl()));
			context.startActivity(browserIntent);
		}
	}
	
	public static class LocatedPhotosAction extends ActionsDetail{

		public LocatedPhotosAction(POIData data) {
			super(R.string.photos, R.drawable.pictures_intent, 105, data);
		}

		@Override
		public void execute(Context context) {
			Intent i = new Intent("com.google.android.radar.SHOW_RADAR");
			i.putExtra("latitude", data.getLat());
			i.putExtra("longitude", data.getLng());
			context.startActivity(i);
		}
	}
	
	public static class YoutubeAction extends ActionsDetail{

		public YoutubeAction(POIData data) {
			super(R.string.youtube, R.drawable.youtube, 106, data);
		}

		@Override
		public void execute(Context context) {
			Intent intent = new Intent(Intent.ACTION_SEARCH, Uri.parse("http://m.youtube.com/results?q="+data.getTitle()));
			//intent.putExtra(SearchManager.QUERY, data.getTitle());
			//intent.setClassName("com.google.android.youtube", "com.google.android.youtube.PlayerActivity");
			
			context.startActivity(intent);
		}
	}
}
