package com.pingme;

import java.util.List;


import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pingme.adapters.POIAdapter;
import com.pingme.model.Coordinate;
import com.pingme.model.POIData;
import com.pingme.utils.POIListUtil;


public class ListPlaceActivity extends ListActivity {

	private final int MAX_POI_DATA_SIZE = 10;
	private List<POIData> poiList;
	

	// ----------------------------------------------------------------------------
	// Service Binding
    // ----------------------------------------------------------------------------
	private PingMeService pingMeService;
	private void reloadAdapterDataFromService() {
		if( pingMeService != null ){
			List<POIData> list = pingMeService.getPOIList();
			poiList = list.subList( Math.max(0,list.size()-MAX_POI_DATA_SIZE), list.size() );
			getListView().setAdapter(new POIAdapter(poiList));			
		}
	}
	
    private ServiceConnection onService = new ServiceConnection(){
        public void onServiceConnected( ComponentName className, IBinder rawBinder ){
            pingMeService = ( (PingMeService.LocalBinder) rawBinder ).getService();
            reloadAdapterDataFromService();
        }
        public void onServiceDisconnected( ComponentName className ){
        	pingMeService = null;
        }
    };

    // ----------------------------------------------------------------------------
	// Broadcaster Reciever
    // ----------------------------------------------------------------------------
    
    private BroadcastReceiver poiDataReceiver = new BroadcastReceiver() {
        public void onReceive( Context context, Intent intent ){
            POIData poiData = (POIData)intent.getSerializableExtra( PingMeService.INTENT_POI_DATA_EXTRA );
            POIListUtil.enqueuePOI( poiList, poiData, MAX_POI_DATA_SIZE );
            
            getListView().setAdapter(new POIAdapter(poiList));
        }
    };
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive( Context context, Intent intent ){
            Coordinate currentLocation = (Coordinate)intent.getSerializableExtra( PingMeService.INTENT_LOCATION_EXTRA );
            PingMeApplication.setLat(currentLocation.lat);
            PingMeApplication.setLng(currentLocation.lng);
        }
    };
    
    
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
        getListView().setSelector(R.drawable.highlight_pressed);
        
        final TextView titleTopbar = (TextView) findViewById(R.id.titleBar);
        titleTopbar.setText(getString(R.string.titleApp));
        
        bindService( new Intent( this, PingMeService.class ), onService, BIND_AUTO_CREATE );
        
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Intent intent = new Intent(this, DetailsActivity.class);
			POIData poiData = poiList.get(position);
			intent.putExtra(PingMeService.INTENT_POI_DATA_EXTRA, poiData);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onResume(){
    	
        super.onResume();
        
        reloadAdapterDataFromService();
        
        registerReceiver( poiDataReceiver, new IntentFilter( PingMeService.PING_BROADCAST_POI_DATA ) );
        registerReceiver( locationReceiver, new IntentFilter( PingMeService.PING_BROADCAST_LOCATION ) );
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver( poiDataReceiver );
        unregisterReceiver( locationReceiver );

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    	unbindService( onService );
    }

    
    // ----------------------------------------------------------------------------
	// Menu
    // ----------------------------------------------------------------------------
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.menu_configure))) {
			Intent intent = new Intent(this, ConfigActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
