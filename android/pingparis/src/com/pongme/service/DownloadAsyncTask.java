package com.pongme.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.pongme.model.POIData;
import com.pongme.utils.Utils;

public class DownloadAsyncTask extends AsyncTask<Void, Void, Void> {

	private final DownloaderCallback _mActivity;
	private int _mErrorMessageId = -1;
	private POIData poiData;
	private List<String> imagesOut;

	public DownloadAsyncTask(DownloaderCallback activity, POIData poiData) {
		_mActivity = activity;
		this.poiData = poiData;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
		try {
			url += URLEncoder.encode( poiData.getTitle(), "ISO-8859-1" );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		launchQuery(url);
		return null;
	}

	private void launchQuery(String baseUrl) {
		final HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		InputStream instream = null;
		_mErrorMessageId = -1;

		try {
			java.net.URLEncoder.encode(baseUrl.toString(), "ISO-8859-1");

			final HttpUriRequest request = new HttpGet(baseUrl);
			request.addHeader("Accept-Encoding", "gzip");
			request.addHeader("Connection", "keep-alive");
			//request.addHeader("User-Agent", NetworkUtils.getUserAgent(_mActivity));

			response = httpClient.execute(request);

			instream = response.getEntity().getContent();
			final Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			try {
				String stringResult = Utils.streamToString(instream);
				imagesOut = parseJSON(stringResult);
			} catch (final Exception e) {
				e.printStackTrace();
				_mErrorMessageId = 1;
			}
		} catch (final Exception e) {
			e.printStackTrace();
			_mErrorMessageId = 1;
		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
				if (response != null && response.getEntity() != null) {
					response.getEntity().consumeContent();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> parseJSON(String jsonString) throws JSONException {
		Log.v( "ServerRequest parsing", jsonString );
		
		List<String> images = new ArrayList<String>(10);
		JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("responseData");
		JSONArray jsonArray = jsonObject.getJSONArray("results");
		
		for(int i=0; i<jsonArray.length(); i++){
			jsonObject = jsonArray.getJSONObject(i);
			images.add(jsonObject.getString("url"));
		}
		
		return images;		
	}


	@Override
	protected void onPostExecute(Void result) {
		// If we have an error message, show it
		if (_mErrorMessageId >= 0) {
			showError();
		}
		// Otherwise, update list
		else if (_mActivity != null && imagesOut != null && imagesOut.size()>0) {
			poiData.setUrlsImages(imagesOut);
			poiData.setUrl_image((String) imagesOut.get(0));
			_mActivity.loadingFinished(new ArrayList<Object>(imagesOut));
		}
	}

	private void showError() {
		if (_mActivity != null) {
			_mActivity.onError(_mErrorMessageId);
		}
	}
}
