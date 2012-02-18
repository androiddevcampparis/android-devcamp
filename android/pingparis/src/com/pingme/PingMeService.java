package com.pingme;



import com.pingme.model.POI_Data;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class PingMeService extends Service {

	public static String PING_LIFECYCLE_ACTION = "PING_LIFECYCLE_ACTION";
	public static String PING_USER_ACTION = "PING_USER_ACTION";
	public static String PING_MOCK_LOCATION = "PING_MOCK_LOCATION";
	
	
    private final Binder binder = new LocalBinder();
    
    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------
    public class LocalBinder extends Binder {
        public PingMeService getService() {
            return PingMeService.this;
        }
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	
    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------
    private void messageNotification( CharSequence tickerText, POI_Data data ){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        long when = System.currentTimeMillis();

        Notification notification = new Notification( android.R.drawable.stat_sys_warning, tickerText, when);
        
        /*
        Intent intent = new Intent( this, ConfigActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );


        PendingIntent contentIntent = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        */
        
        
        PendingIntent contentIntent = DetailsActivity.getMyLauncher( this, data );
        notification.setLatestEventInfo(this, tickerText, data.getTitle(), contentIntent);
        
        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = Color.GREEN; 
        notification.ledOffMS = 500; 
        notification.ledOnMS = 500; 
        
        notificationManager.notify( R.string.app_name, notification);
    }

	
    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------
	private void queryLatLng( double lat, double lng ){
		Log.v("PingMeService==========>", "queryLatLng lat:"+ lat +" lng:" + lng );
	}
	
    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------
	private double prevLat = 0;
	private double prevLng = 0;
	
	private void startLocation(){
	    LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	    locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				float[] results = new float[1];
				Location.distanceBetween( prevLat, prevLng, location.getLatitude(), location.getLongitude(), results );
				float distance = results[0];
				if( distance > 15 ){
					prevLat = location.getLatitude();
					prevLng = location.getLongitude();
					queryLatLng( location.getLatitude(), location.getLongitude() );
				}
			}

			@Override
			public void onProviderDisabled(String str) {
			}

			@Override
			public void onProviderEnabled(String str) {
			}

			@Override
			public void onStatusChanged(String str, int arg1, Bundle arg2) {				
			}	    	
		} );

	}
	
    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		
		Log.v("PingMeService", "StartCommand with action " + action );
		Toast.makeText( this, action, Toast.LENGTH_SHORT).show();
		
        POI_Data data = new POI_Data();
        data.setTitle("Tour Effeil");
        data.setDescr("La plus grand tour de Paris");
        data.setUrlImage("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg/220px-Tour_Eiffel_Wikimedia_Commons.jpg");
		messageNotification( getString(R.string.titleApp), data );
		
		if( PING_USER_ACTION.equals( action ) ){
			
		}
		else if( PING_MOCK_LOCATION.equals( action ) ){
			double lat = intent.getDoubleExtra("lat", 0 );
			double lng = intent.getDoubleExtra("lon", 0 );
			queryLatLng( lat, lng );
		}

		return START_STICKY;
	}


	@Override
    public void onCreate(){
        super.onCreate();
        
        startLocation();
    }
    
    @Override
    public void onDestroy(){
    }


    
    

}
