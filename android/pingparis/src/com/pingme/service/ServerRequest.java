package com.pingme.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.pingme.model.POIData;

public class ServerRequest {

	private static final String SERVER_URL = "http://";
	
	public static POIData fetchPOI() throws ClientProtocolException, IOException, JSONException {
		final int timeoutConnection = 45000;

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		final int timeoutSocket = 45000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		// Initialize the HTTP client with params
		HttpClient httpclient = new DefaultHttpClient(httpParameters);

        
		// Prepare a request object
		HttpGet httpget = new HttpGet(SERVER_URL);
		HttpResponse httpResponse = httpclient.execute(httpget);

		// Get hold of the response entity
		HttpEntity entity = httpResponse.getEntity();
		
		if (entity != null) 
		{
			InputStream instream = entity.getContent();
			String stringResult = streamToString(instream);
			
			return parseJSON( stringResult );
					
		}

		return null;

	}
	public static POIData parseJSON(String jsonString) throws JSONException {
		Log.v( "ServerRequest parsing", jsonString );
		JSONObject rootObject = new JSONObject(jsonString);
		
		return new POIData();		
	}
	
	private final static int bufferSize = 8192;

	public static String streamToString(InputStream is)
			throws UnsupportedEncodingException {

		BufferedReader reader = new BufferedReader( new InputStreamReader(is, "UTF-8"), bufferSize );
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				reader.close();
			} catch (IOException e) {
				Log.e("ServerRequest", e.getMessage(), e );
			}
		}
		return sb.toString();
	}

}
