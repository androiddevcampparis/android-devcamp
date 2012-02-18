package com.pingme;

import com.pingme.adapters.ActionsAdapter;
import com.pingme.model.ActionsDetail;
import com.pingme.model.POI_Data;
import com.pingme.utils.ImageDownloader;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailsActivity extends ListActivity {
	
	
	private POI_Data poiData;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		// get data from intent and set To View
		poiData = (POI_Data) getIntent().getSerializableExtra(PingMeService.INTENT_POI_DATA_EXTRA);
		
		final TextView title = (TextView) findViewById(R.id.titleEvent);
		final TextView descr = (TextView) findViewById(R.id.descrEvent);
		final ImageView image = (ImageView) findViewById(R.id.imageEvent);
		final TextView titleTopbar = (TextView) findViewById(R.id.titleBar);
		
		title.setText(poiData.getTitle());
		descr.setText(poiData.getDescr());
		titleTopbar.setText(getString(R.string.detail_place));
		
		new ImageDownloader(this).download(poiData.getUrlImage(), image, null, "DetailsActivity");
		
		 //Adapter to list of actions
        getListView().setSelector(R.drawable.highlight_pressed);
        setListAdapter(new ActionsAdapter(poiData));
        
        //Reset Location notif to Main Notif
        if( PingMeApplication.getServiceStatus() && getIntent().getExtras().getBoolean(PingMeService.INTENT_IS_NOTIF_EXTRA, false) ){
        	 PingMeApplication.createNotifConfig(this);
        }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			ActionsDetail details = poiData.getActions().get(position);
			details.execute(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the Intent for notification to launch the DetailsActivity
	 * @param context
	 * @param data
	 * @return
	 */
	public static PendingIntent getMyLauncher(Context context, POI_Data data){
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(PingMeService.INTENT_POI_DATA_EXTRA, data);
		intent.putExtra(PingMeService.INTENT_IS_NOTIF_EXTRA, true);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return contentIntent;
	}

	
}
