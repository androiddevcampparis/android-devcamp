package com.pongme;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.pongme.model.POIData;

public class ZoomImageActivity extends Activity {

	
	public static String EXTRA = "poidata";
	public static String EXTRA_POS = "position";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.image_zoom_activity);
		
		POIData data = (POIData) getIntent().getSerializableExtra(EXTRA);
		int position = getIntent().getIntExtra(EXTRA_POS, 0);
		String url = data.getUrlsImages().get(position);
		
		ImageView view = (ImageView) findViewById(R.id.imageZoom);
		PingMeApplication.getImageDownloader().download(url, view, null, "ZoomActivity");
	}
}
