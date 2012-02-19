package com.pingme;




import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import org.json.JSONObject;

import com.pingme.model.Category;
import com.pingme.model.Coordinate;
import com.pingme.model.POIData;
import com.pingme.service.ServerRequest;
import com.pingme.utils.POIListUtil;

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

	public static String PING_BROADCAST_LOCATION = "com.pingme.PingMeService.PING_BROADCAST_LOCATION";
	public static String INTENT_LOCATION_EXTRA = "com.pingme.PingMeService.INTENT_LOCATION_EXTRA";
	
	public static String PING_BROADCAST_POI_DATA = "com.pingme.PingMeService.PING_BROADCAST_MESSAGE";
	public static String INTENT_POI_DATA_EXTRA = "com.pingme.PingMeService.INTENT_DATA_EXTRA";
	
	public static String PING_ACTION_UI_CATEGORIES = "com.pingme.PingMeService.PING_ACTION_UI_CATEGORIES";
	public static String INTENT_CATEGORIES_EXTRA = "com.pingme.PingMeService.INTENT_CATEGORIES_EXTRA";
	
	
	public static String INTENT_IS_NOTIF_EXTRA = "com.pingme.PingMeService.INTENT_IS_NOTIF_EXTRA";

	public static String INTENT_NOTIFICATION_SOUND_EXTRA = "com.pingme.PingMeService.INTENT_NOTIFICATION_SOUND_EXTRA";

	public static String PING_ACTION_LIFECYCLE = "com.pingme.PingMeService.PING_ACTION_LIFECYCLE";
	public static String PING_ACTION_UI_SOUND_NOTIFICATION = "com.pingme.PingMeService.PING_ACTION_UI_SOUND_NOTIFICATION";
	public static String PING_ACTION_MOCK_LOCATION = "com.pingme.PingMeService.PING_ACTION_MOCK_LOCATION";
	
	public static final boolean NOTIFICATION_SOUND_DEFAULT = false;
	private boolean notificationSound = NOTIFICATION_SOUND_DEFAULT; 
	
	public static final Category[] CATEGORIES_DEFAULT = new Category[0];
	private Category[] categories = CATEGORIES_DEFAULT; 
	
    private final Binder binder = new LocalBinder();
    
    // ----------------------------------------------------------------------------
    // Binder MGMT
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
	
	// ------------------------------------------------------------
	// POI_DATA List
	// ------------------------------------------------------------
	private final int MAX_POI_DATA_SIZE = 10;
	private List<POIData> pois = new ArrayList<POIData>();
	/*
	 * Return a copy of local pois (we assume that pois are immutable)
	 */
	public synchronized List<POIData> getPOIList(){
		return new ArrayList<POIData>( pois );
	}

	
    // ----------------------------------------------------------------------------
	// Notification MGMT
    // ----------------------------------------------------------------------------
    private void messageNotification( CharSequence tickerText, POIData data, int count ){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        long when = System.currentTimeMillis();

        Notification notification = new Notification( android.R.drawable.stat_sys_warning, tickerText, when);
                
        PendingIntent contentIntent = DetailsActivity.getMyLauncher( this, data );
        notification.setLatestEventInfo(this, tickerText, data.getTitle(), contentIntent);
        
        if( notificationSound )
        	notification.defaults |= Notification.DEFAULT_SOUND;

    	notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = Color.GREEN; 
        notification.ledOffMS = 500; 
        notification.ledOnMS = 500;
        if(count>1){
        	notification.number = count;
        }
        
        notificationManager.notify( R.string.app_name, notification);
    }

	
    // ----------------------------------------------------------------------------
    // Network IO
    // ----------------------------------------------------------------------------
    private static final float FETCH_RANGE_RADIUS = 200; // in meter
	private void queryLatLng( double lat, double lng ){
		Log.v("PingMeService", "queryLatLng lat:"+ lat +" lng:" + lng );
		try{
			POIData data = ServerRequest.fetchPOI( lat, lng, FETCH_RANGE_RADIUS, categories );
			if( data != null )
				processServerResponse( data );			
		}
		catch( Exception e ){
			Log.e( "PingMeService", e.getMessage(), e );
		}
		
	}
	
	private void processServerResponse( POIData data ){
		/*
        data = new POIData();
        data.setId("uid"+System.currentTimeMillis());
        data.setTitle("Tour Effeil");
        data.setDescription("La plus grand tour de Paris");
        data.setUrl_image("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg/220px-Tour_Eiffel_Wikimedia_Commons.jpg");
        */
        
        synchronized( pois ){ 
        	if( !POIListUtil.contains( pois, data )  )
        		messageNotification( getString(R.string.titleApp), data, 1 );
        	POIListUtil.enqueuePOI(pois, data, MAX_POI_DATA_SIZE);
        }
        
		
		Intent broadCastIntent = new Intent( PING_BROADCAST_POI_DATA );
	    broadCastIntent.putExtra( INTENT_POI_DATA_EXTRA, data );
	    sendBroadcast( broadCastIntent );
	}
	
    // ----------------------------------------------------------------------------
	// Location Manager
    // ----------------------------------------------------------------------------
	private static final int MIN_DISTANCE_MOVEMENT_TRIGGER = 20;
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
				if( distance > MIN_DISTANCE_MOVEMENT_TRIGGER ){
					prevLat = location.getLatitude();
					prevLng = location.getLongitude();
					queryLatLng( location.getLatitude(), location.getLongitude() );
										
					Intent broadCastIntent = new Intent( PING_BROADCAST_LOCATION );
				    broadCastIntent.putExtra( INTENT_LOCATION_EXTRA, new Coordinate( prevLat, prevLng ) );
				    sendBroadcast( broadCastIntent );
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
	    
	    //Add permanent icon in status bar
//	    Notification notifStatusBar = new Notification(R.drawable.status_icon, getString(R.string.app_name), System.currentTimeMillis());
//	    notifStatusBar.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
//	    NotificationManager notifier = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//	    notifier.notify(1, notifStatusBar);
	}
	
    // ----------------------------------------------------------------------------
	// Service Lifecycle
    // ----------------------------------------------------------------------------
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		
		Log.v("PingMeService", "StartCommand with action " + action );
		
		if( PING_ACTION_UI_SOUND_NOTIFICATION.equals( action ) ){
			notificationSound = intent.getBooleanExtra( INTENT_NOTIFICATION_SOUND_EXTRA, false );
			Toast.makeText( this, "Sound Notification " + notificationSound, Toast.LENGTH_SHORT).show();
		}
		else if( PING_ACTION_UI_CATEGORIES.equals( action ) ){
			Object[] array = (Object[])intent.getSerializableExtra( INTENT_CATEGORIES_EXTRA );
			categories = new Category[array.length];
			for( int i = 0; i< array.length; i++ ) categories[i] = (Category)array[i];
			Toast.makeText( this, "Categories " + Arrays.asList(categories), Toast.LENGTH_SHORT).show();			
		}
		else if( PING_ACTION_MOCK_LOCATION.equals( action ) ){
			double lat = intent.getDoubleExtra("lat", 0 );
			double lng = intent.getDoubleExtra("lng", 0 );
			Toast.makeText( this, "GeoLoc "+ lat + "/"+lng , Toast.LENGTH_SHORT).show();
			queryLatLng( lat, lng );
		}
		else if( PING_ACTION_LIFECYCLE.equals( action ) ){			
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
    	//remove permanent icon in status bar
    	//((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }


    
    

}
