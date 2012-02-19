package com.pongme.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.pongme.PingMeApplication;
import com.pongme.PingMeService;
import com.pongme.model.Category;
import com.pongme.model.POIData;
import com.pongme.utils.Utils;

public class ServerRequestAsyncTask extends AsyncTask<Void, Void, Void> {

	private POIData outData;
	private PingMeService service;
	private double lat;
	private double lng;
	private double radius;
	private Category[] categories;

	public ServerRequestAsyncTask(PingMeService service, double lat, double lng, double radius, Category[] categories) {
		this.service = service;
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
		this.categories = categories;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			if( categories.length > 0 ) outData = fetchPOI();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if(outData == null){
			return;
		}
		service.processServerResponse( outData );	
	}
	
	private static final String SERVER_URL_PROD = "http://pongserver.appspot.com/poi/";
	private static final String SERVER_URL = "http://pingme.cloudfoundry.com/poi/";
	private static final int TIMEOUT_MS = 5000;
	
	public POIData fetchPOI() throws ClientProtocolException, IOException, JSONException {

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,TIMEOUT_MS);

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_MS);

		// Initialize the HTTP client with params
		HttpClient httpclient = new DefaultHttpClient(httpParameters);

        
		// Prepare a request object
		StringBuilder sb = new StringBuilder();
		for( Category category : categories ){
			if( sb.length() > 0 ) sb.append(",");
			if( category.isChecked() ) sb.append( category.getIdSync() );
		}
		
		String serverBaseUrl = PingMeApplication.isTest ? SERVER_URL : SERVER_URL_PROD;
		String serverURL = serverBaseUrl + lat +"+"+ lng+"+"+radius+"+"+sb.toString();
		Log.v( "ServerRequest", "URL: " + serverURL );
		HttpGet httpget = new HttpGet(serverURL);
		HttpResponse httpResponse = httpclient.execute(httpget);

		// Get hold of the response entity
		HttpEntity entity = httpResponse.getEntity();
		
		if (entity != null) 
		{
			InputStream instream = entity.getContent();
			String stringResult = Utils.streamToString(instream);
			
			return parseJSON( stringResult );
					
		}

		return null;

	}
	public static POIData parseJSON(String jsonString) throws JSONException {
		Log.v( "ServerRequest parsing", jsonString );
		
		POIData data = new POIData();
		JSONObject jsonData = new JSONObject(jsonString);
		Object obj = jsonData.opt("poiDatas");
		
		JSONObject jsonObject = null;
		if( obj instanceof JSONArray ){
			jsonObject = ((JSONArray)obj).getJSONObject(0); 
		}
		else if( obj instanceof JSONObject ){
			jsonObject = ((JSONObject)obj);
		}
		
		
		//data.setId( jsonObject.getString("id") );
		data.setTitle( jsonObject.getString("title") );
		data.setDescription( jsonObject.getString("description") );
		data.setAddresse( jsonObject.getString("addresse") );
		data.setLat( jsonObject.getDouble("latitude") );
		data.setLng( jsonObject.getDouble("longitude") );
		data.setCategory( jsonObject.getString("category") );
		//data.setUrl_image( jsonObject.getString(""));
		
		
		return data;		
	}
}
