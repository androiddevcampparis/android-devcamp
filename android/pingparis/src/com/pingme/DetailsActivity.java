package com.pingme;

import com.pingme.model.POI_Data;
import com.pingme.utils.ImageDownloader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	

	
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
		
		title.setText(poiData.getTitle());
		descr.setText(poiData.getDescr());
		
		new ImageDownloader(this).download(poiData.getUrlImage(), image, null, "DetailsActivity");
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
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return contentIntent;
	}

	
}
