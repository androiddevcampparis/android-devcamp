package com.pongme;

import java.util.List;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.pongme.model.Category;
import com.pongme.utils.ImageDownloader;
import com.pongme.utils.Utils;

public class PingMeApplication extends Application {
	
	private static final String SERVICE_STATUS = "pingme.SERVICE_STATUS";
	private static final String NOTIFICATION_SOUND = "pingme.NOTIFICATION_SOUND";
	private static final String CATEGORY_PREFIX = "pingme.CATEGORY.";
	
	private static SharedPreferences preferences;
	private static ImageDownloader imageDownloader;
	
	private static double lat;
	private static double lng;
	private static boolean isPhotoIntentCallable;
	public static boolean isTest = true;
	
	private static boolean firstLaunch = true;

	
	public static boolean getNotificationSound(){
		return preferences.getBoolean( NOTIFICATION_SOUND, PingMeService.NOTIFICATION_SOUND_DEFAULT );
	}
	
	public static void setNotificationSound( Context context, Boolean status ){
		Editor editor = preferences.edit();
		editor.putBoolean( NOTIFICATION_SOUND, status);
		editor.commit();
		
		Intent serviceIntent = new Intent( PingMeService.PING_ACTION_UI_SOUND_NOTIFICATION );
		serviceIntent.setClassName( context, "com.pongme.PingMeService" );

		serviceIntent.putExtra( PingMeService.INTENT_NOTIFICATION_SOUND_EXTRA, status );
		context.startService( serviceIntent );			
	}


	
	public static boolean getServiceStatus(){
		return preferences.getBoolean( SERVICE_STATUS, true );
	}
	
	public static void setServiceStatus( Context context, Boolean status ){
		Editor editor = preferences.edit();
		editor.putBoolean( SERVICE_STATUS, status);
		editor.commit();

		Intent serviceIntent = new Intent( PingMeService.PING_ACTION_LIFECYCLE );
		serviceIntent.setClassName( context, "com.pongme.PingMeService" );
		if( status ){
			context.startService( serviceIntent );			
		}
		else {
			context.stopService( serviceIntent );						
		}
		
		if(status){
			createNotifConfig(context);
		} else{
			removeNotifConfig(context);
		}
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		imageDownloader = new ImageDownloader(getApplicationContext());
		firstLaunch = preferences.getBoolean("firstLaunch", true);
		
		Intent photoIntent = new Intent("com.google.android.radar.SHOW_RADAR");
		photoIntent.putExtra("latitude", 48f);
		photoIntent.putExtra("longitude", 2f);
		isPhotoIntentCallable = Utils.isCallable(photoIntent, this);
		
		setServiceStatus( getApplicationContext(), getServiceStatus() );
		setNotificationSound( getApplicationContext(), getNotificationSound() );
		setCategories( getApplicationContext(), Category.getCategories() );
	}
	
	public static void createNotifConfig(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		 PendingIntent contentIntent = ConfigActivity.getMyLauncher( context );
		 long when = System.currentTimeMillis();
		 
		 Notification notification = new Notification( android.R.drawable.stat_notify_more, context.getString(R.string.open_config), when);
	     notification.setLatestEventInfo(context, context.getString(R.string.open_config), context.getString(R.string.config_running), contentIntent);
	     notificationManager.notify( R.string.app_name, notification);
	}
	
	public static void removeNotifConfig(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}

	public static void setCategories( Context context, List<Category> categories) {
		for(Category category: categories){
			saveCategory(category);
		}
		
		Intent serviceIntent = new Intent( PingMeService.PING_ACTION_UI_CATEGORIES );
		serviceIntent.setClassName( context, "com.pongme.PingMeService" );

		Category[] catArray = categories.toArray(new Category[0]) ;
		serviceIntent.putExtra( PingMeService.INTENT_CATEGORIES_EXTRA,  catArray );
		context.startService( serviceIntent );			
	}
	
	private static void saveCategory(Category category) {
		Editor editor = preferences.edit();
		editor.putBoolean(CATEGORY_PREFIX+category.getIdSync(), category.isChecked());
		editor.commit();
	}
	public static void loadCategory(Category category){
		category.setChecked( preferences.getBoolean(CATEGORY_PREFIX+category.getIdSync(), category.isChecked() ) );
	}

	public static ImageDownloader getImageDownloader() {
		return imageDownloader;
	}

	public static double getLat() {
		return lat;
	}

	public static void setLat(double lat) {
		PingMeApplication.lat = lat;
	}

	public static double getLng() {
		return lng;
	}

	public static void setLng(double lng) {
		PingMeApplication.lng = lng;
	}

	public static boolean isFirstLaunch() {
		return firstLaunch;
	}

	public static void setLaunchedOnce() {
		firstLaunch = false;
		Editor editor = preferences.edit();
		editor.putBoolean( "firstLaunch", false);
		editor.commit();
	}

	public static boolean isPhotoIntentCallable() {
		return isPhotoIntentCallable;
	}

	public static void setPhotoIntentCallable(boolean isPhotoIntentCallable) {
		PingMeApplication.isPhotoIntentCallable = isPhotoIntentCallable;
	}
	
	
}
