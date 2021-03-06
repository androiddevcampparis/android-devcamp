package com.pongme.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.pongme.model.POIData;

public class ServerRequestPlusOneAsyncTask extends AsyncTask<Void, Void, Void> {

	@SuppressWarnings("unused")
	private POIData poiData;
	private String md5;

	public ServerRequestPlusOneAsyncTask( POIData poiData, String md5 ) {
		this.poiData = poiData;
		this.md5 = md5;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			pushPlusOne();
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
	}
	
	private static final String SERVER_URL = "http://pingme.cloudfoundry.com/poi/";
	private static final int TIMEOUT_MS = 5000;
	
	public void pushPlusOne() throws ClientProtocolException, IOException, JSONException {

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,TIMEOUT_MS);

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_MS);

		// Initialize the HTTP client with params
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		
		String serverURL = SERVER_URL + poiData.getId() + "+" + md5 + "+" + poiData.isPlus();
		Log.v( "ServerRequest", "URL: " + serverURL );
		HttpPost httpPost = new HttpPost(serverURL);
		httpclient.execute(httpPost);

	}
}
