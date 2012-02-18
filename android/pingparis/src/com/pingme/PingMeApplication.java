package com.pingme;

import java.util.List;

import com.pingme.model.Preferences;

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

	public static void savePref(List<Preferences> preferences2) {
		for(Preferences pref: preferences2){
			savePref(pref);
		}
	}
	
	public static void savePref(Preferences pref) {
		Editor editor = preferences.edit();
		editor.putBoolean(pref.getName(), pref.isChecked());
	}
	
	public static boolean getPrefStatus(String name){
		return preferences.getBoolean(name, false);
	}
	
	
}
