package com.pingme;

import java.util.List;


import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.pingme.model.POI_Data;
import com.pingme.utils.POIListUtil;





public class ListPlaceActivity extends ListActivity {

	private final int MAX_POI_DATA_SIZE = 10;
	private List<POI_Data> poiList;

	private Location currentLocation;
	

	// ----------------------------------------------------------------------------
	// Service Binding
    // ----------------------------------------------------------------------------
    private ServiceConnection onService = new ServiceConnection(){
        public void onServiceConnected( ComponentName className, IBinder rawBinder ){
            PingMeService pingMeService = ( (PingMeService.LocalBinder) rawBinder ).getService();
            List<POI_Data> list = pingMeService.getPOIList();
            poiList = list.subList( list.size()-MAX_POI_DATA_SIZE, list.size() );

            registerReceiver( receiver, new IntentFilter( PingMeService.PING_BROADCAST_POI_DATA ) );
            registerReceiver( receiver, new IntentFilter( PingMeService.PING_BROADCAST_LOCATION ) );

        }
        public void onServiceDisconnected( ComponentName className ){
            unregisterReceiver( receiver );
        }
    };

    // ----------------------------------------------------------------------------
	// Broadcaster Reciever
    // ----------------------------------------------------------------------------
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive( Context context, Intent intent ){
        	String action = intent.getAction() ;
            if( PingMeService.PING_BROADCAST_POI_DATA.equals( action ) ){
                POI_Data poiData = (POI_Data)intent.getSerializableExtra( PingMeService.INTENT_POI_DATA_EXTRA );
                POIListUtil.enqueuePOI( poiList, poiData, MAX_POI_DATA_SIZE );
            }
            if( PingMeService.PING_BROADCAST_LOCATION.equals( action ) ){
                currentLocation = (Location)intent.getSerializableExtra( PingMeService.INTENT_LOCATION_EXTRA );
            }
        }
    };

    
    // ----------------------------------------------------------------------------
	// Activity Lifecycle
    // ----------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        bindService( new Intent( this, PingMeService.class ), onService, BIND_AUTO_CREATE );
    }

    @Override
    public void onPause(){
        super.onPause();
        unbindService( onService );
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    
}
