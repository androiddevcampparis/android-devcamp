package com.pingme;


import android.content.Context;
import android.content.Intent;


import android.app.ListActivity;
import android.app.PendingIntent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pingme.adapters.CategoriesAdapter;

import com.pingme.model.Category;

public class ConfigActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView titleTopbar = (TextView) findViewById(R.id.titleBar);
        titleTopbar.setText(getString(R.string.title_setting));

        //Change notification sound: on/off
        final ToggleButton notificationSoundToggle = (ToggleButton) findViewById(R.id.notificationSound);
        notificationSoundToggle.setChecked( PingMeApplication.getNotificationSound() );
        
        notificationSoundToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	PingMeApplication.setNotificationSound( ConfigActivity.this, notificationSoundToggle.isChecked());
            }
        });
        
        //Change service state: on/off
        final ToggleButton serviceStatusToggle = (ToggleButton) findViewById(R.id.statusService);
        serviceStatusToggle.setChecked( PingMeApplication.getServiceStatus() );
        
        serviceStatusToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	notificationSoundToggle.setEnabled( !notificationSoundToggle.isEnabled() );
            	PingMeApplication.setServiceStatus( ConfigActivity.this, serviceStatusToggle.isChecked());
            }
        });

        
        //Adapter to list of choices
        getListView().setSelector(R.drawable.highlight_pressed);
        setListAdapter(new CategoriesAdapter());

    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Category category = Category.getCategories().get(position);
		category.setChecked(!category.isChecked());
		CategoriesAdapter.setStatusIcon(v, category);
		PingMeApplication.setCategories( this, Category.getCategories() );
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.menu_openlist))) {
			Intent intent = new Intent(this, ListPlaceActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		}
		if (item.getTitle().equals(getString(R.string.menu_tuto))) {
			Intent intent = new Intent(this, CredentialActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(R.string.menu_openlist).setIcon(android.R.drawable.ic_menu_gallery);
		menu.add(R.string.menu_tuto).setIcon(android.R.drawable.ic_menu_help);
		return super.onPrepareOptionsMenu(menu);
	}

    
}