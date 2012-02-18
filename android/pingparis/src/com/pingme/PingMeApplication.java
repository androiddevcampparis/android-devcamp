package com.pingme;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PingMeApplication extends Application {
	
	private static SharedPreferences preferences;

	public static void stopApp(){
		setStatusService(false);
		//TODO Stop service
	}
	
	public static void startApp(){
		setStatusService(true);
		//TODO start service
	}
	
	private static void setStatusService(boolean status){
		Editor editor = preferences.edit();
		editor.putBoolean("SERVICE_STATUS", status);
		editor.commit();
	}
	

	@Override
	public void onCreate() {
		super.onCreate();
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	}
	
	
}
