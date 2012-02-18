package com.pingme;

import android.app.Activity;
import android.content.Intent;

import android.app.ListActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.pingme.adapters.PreferencesAdapter;
import com.pingme.model.Preferences;

public class ConfigActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        Intent serviceIntent = new Intent( "PING_USER_ACTION" );
        serviceIntent.setClassName( this, "com.pingme.PingMeService" );
        startService( serviceIntent );

        
        //Change service state: on/off
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.statusService);
        togglebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (togglebutton.isChecked()) {
                    PingMeApplication.startApp();
                } else {
                	PingMeApplication.stopApp();
                }
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
    
    
}