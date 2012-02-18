package com.pingme;

import java.util.List;

import com.pingme.model.Preferences;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PingMeApplication extends Application {
	
	private static final String SERVICE_STATUS = "pingme.SERVER_STATUS";
	private static SharedPreferences preferences;

	public static boolean getServiceStatus(){
		return preferences.getBoolean( SERVICE_STATUS, true );
	}
	
	public static void setServiceStatus( Context context, Boolean status ){
		Editor editor = preferences.edit();
		editor.putBoolean( SERVICE_STATUS, status);
		editor.commit();

		Intent serviceIntent = new Intent( "PING_USER_ACTION" );
		serviceIntent.setClassName( context, "com.pingme.PingMeService" );
		if( status ){
			context.startService( serviceIntent );			
		}
		else {
			context.stopService( serviceIntent );						
		}
	}
	


	@Override
	public void onCreate() {
		super.onCreate();
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		setServiceStatus( this, getServiceStatus() );
		
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
