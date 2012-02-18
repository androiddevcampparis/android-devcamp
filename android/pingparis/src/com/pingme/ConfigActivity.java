package com.pingme;

import android.content.Context;
import android.content.Intent;

import android.app.ListActivity;
import android.app.PendingIntent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.pingme.adapters.PreferencesAdapter;
import com.pingme.model.POI_Data;
import com.pingme.model.Preferences;

public class ConfigActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        //Change service state: on/off
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.statusService);
        togglebutton.setChecked( PingMeApplication.getServiceStatus() );
        
        togglebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	PingMeApplication.setServiceStatus( ConfigActivity.this, togglebutton.isChecked());
            }
        });
        
        //Adapter to list of choices
        getListView().setSelector(R.drawable.highlight_pressed);
        setListAdapter(new PreferencesAdapter());

    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Preferences pref = Preferences.getPreferences().get(position);
		pref.setChecked(!pref.isChecked());
		PreferencesAdapter.setStatusIcon(v, pref);
		PingMeApplication.savePref(Preferences.getPreferences());
	}
    
	
	/**
	 * Get the Intent for notification to launch the ConfigActivity
	 * @param context
	 * @param data
	 * @return
	 */
	public static PendingIntent getMyLauncher(Context context){
		Intent intent = new Intent(context, ConfigActivity.class);		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return contentIntent;
	}
    
}