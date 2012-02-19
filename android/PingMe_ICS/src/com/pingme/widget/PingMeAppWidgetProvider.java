package com.pingme.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.pingme.R;

public class PingMeAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		for (int i = 0; i < appWidgetIds.length; ++i) {
	        
	        // Set up the intent that starts the StackViewService, which will
	        // provide the views for this collection.
	        Intent intent = new Intent(context, PingmeWidgetService.class);
	        // Add the app widget ID to the intent extras.
	        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
	        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
	        // Instantiate the RemoteViews object for the App Widget layout.
	        
	        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget);
	        // Set up the RemoteViews object to use a RemoteViews adapter. 
	        // This adapter connects
	        // to a RemoteViewsService  through the specified intent.
	        // This is how you populate the data.
	        rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
	        
	        // The empty view is displayed when the collection has no items. 
	        // It should be in the same layout used to instantiate the RemoteViews
	        // object above.
	        rv.setEmptyView(R.id.stack_view, R.id.empty_view);

	        //
	        // Do additional processing specific to this app widget...
	        //
	        
	        appWidgetManager.updateAppWidget(appWidgetIds[i], rv);   
	    }
		
	    super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
