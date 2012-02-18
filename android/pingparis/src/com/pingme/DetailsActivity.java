package com.pingme;

import com.pingme.adapters.ActionsAdapter;
import com.pingme.adapters.PreferencesAdapter;
import com.pingme.model.POI_Data;
import com.pingme.utils.ImageDownloader;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailsActivity extends ListActivity {
	
	private static String INTENT_DATA = "data";
	private static String IS_NOTIF = "isNotif";
	
	private POI_Data poiData;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		// get data from intent and set To View
		poiData = (POI_Data) getIntent().getSerializableExtra(INTENT_DATA);
		
		final TextView title = (TextView) findViewById(R.id.titleEvent);
		final TextView descr = (TextView) findViewById(R.id.descrEvent);
		final ImageView image = (ImageView) findViewById(R.id.imageEvent);
		
		title.setText(poiData.getTitle());
		descr.setText(poiData.getDescr());
		
		new ImageDownloader(this).download(poiData.getUrlImage(), image, null, "DetailsActivity");
		
		 //Adapter to list of actions
        getListView().setSelector(R.drawable.highlight_pressed);
        setListAdapter(new ActionsAdapter(poiData));
        
        //Reset Location notif to Main Notif
        if(PingMeApplication.getServiceStatus() && getIntent().getExtras().getBoolean(IS_NOTIF, false)){
        	 PingMeApplication.createNotifConfig(this);
        }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
	}
	
	/**
	 * Get the Intent for notification to launch the DetailsActivity
	 * @param context
	 * @param data
	 * @return
	 */
	public static PendingIntent getMyLauncher(Context context, POI_Data data){
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(INTENT_DATA, data);
		intent.putExtra(IS_NOTIF, true);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return contentIntent;
	}

	
}
