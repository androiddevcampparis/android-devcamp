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
import android.view.Menu;
import android.view.MenuItem;

import com.pingme.adapters.POIAdapter;
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
            poiList = list.subList( Math.max(0,list.size()-MAX_POI_DATA_SIZE), list.size() );

            registerReceiver( receiver, new IntentFilter( PingMeService.PING_BROADCAST_POI_DATA ) );
            registerReceiver( receiver, new IntentFilter( PingMeService.PING_BROADCAST_LOCATION ) );
            setDataToList(null);
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
                setDataToList(poiData);
            }
            if( PingMeService.PING_BROADCAST_LOCATION.equals( action ) ){
                currentLocation = (Location)intent.getSerializableExtra( PingMeService.INTENT_LOCATION_EXTRA );
            }
        }
    };
    
    /**
     * Set the liste content, or only add a item
     * @param poiData
     */
    private void setDataToList(POI_Data poiData){
    	if(poiData != null && poiList.size() >= MAX_POI_DATA_SIZE){
    		
    	}
    	
    	getListView().setAdapter(new POIAdapter(poiList));
    }

    
    // ----------------------------------------------------------------------------
	// Activity Lifecycle
    // ----------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(PingMeApplication.isFirstLaunch()){
        	PingMeApplication.setLaunchedOnce();
        	
        	//TODO launch tutorial and add to Menu
        	Intent intent = new Intent(this, ConfigActivity.class);
        	startActivity(intent);
        }
        
        setContentView(R.layout.activity_listplaces);
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
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.menu_configure))) {
			Intent intent = new Intent(this, ConfigActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(R.string.menu_configure).setIcon(android.R.drawable.ic_menu_preferences);
		return super.onPrepareOptionsMenu(menu);
	}
    
}
