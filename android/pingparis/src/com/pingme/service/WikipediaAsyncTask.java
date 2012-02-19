package com.pingme.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

import com.pingme.PingMeApplication;
import com.pingme.model.POIData;
import com.pingme.model.WikiData;

public class WikipediaAsyncTask extends AsyncTask<Void, Void, Void> {

	private final DownloaderCallback _mActivity;
	private int _mErrorMessageId = -1;
	private POIData poiData;
	private List<WikiData> urlOut;

	public WikipediaAsyncTask(DownloaderCallback activity, POIData poiData) {
		_mActivity = activity;
		this.poiData = poiData;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		String url = "http://api.wikilocation.org/articles?limit=30&format=xml&locale=fr&lat="+poiData.getLat()+"&lng="+poiData.getLng();
		//tring url = "http://en.wikipedia.org/w/api.php?format=xml&action=opensearch&limit=5&search=";
//		try {
//			url += URLEncoder.encode( poiData.getTitle(), "ISO-8859-1" );
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		launchQuery(url);
		return null;
	}

	private void launchQuery(String baseUrl) {
		Log.i("WikipediaAsyncTask", baseUrl);
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
				urlOut = parseXML(instream);
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
	
	public List<WikiData> parseXML(InputStream instream) throws JSONException {
		Log.v( "ServerRequest parsing", "" );
		List<WikiData> urls = new ArrayList<WikiData>(5);
		
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document dom = builder.parse(instream);
			
			if (dom != null) {
				final Element itemNode = (Element) getFirstSubNode(dom.getChildNodes().item(0), "articles", true);//"Section"
				final NodeList listeNode = itemNode.getElementsByTagName("article");
					
					for (int i = 0; i < listeNode.getLength(); i++) {
						try {
							final Node node = listeNode.item(i);
							WikiData data = new WikiData();
							
							data.setName(getValueForTag(node, "title", false));
							data.setUrl(getValueForTag(node, "url", false));
							urls.add(data);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
			}
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
		return urls;		
	}


	@Override
	protected void onPostExecute(Void result) {
		// If we have an error message, show it
		if (_mErrorMessageId >= 0) {
			showError();
		}
		// Otherwise, update list
		else if (_mActivity != null && urlOut != null && urlOut.size()>0) {
//			WikiData wiki = (WikiData) urlOut.get(0);
//			poiData.setWiki_link(wiki.getName());
//			poiData.setWiki_url(wiki.getUrl());
			
			poiData.setListWiki(urlOut);
			_mActivity.loadingFinished(new ArrayList<Object>(urlOut));
		}
	}

	private void showError() {
		if (_mActivity != null) {
			_mActivity.onError(_mErrorMessageId);
		}
	}
	
	private Node getFirstSubNode(Node fatherNode, String tag, boolean mandatory) throws Exception {
		final NodeList list = ((Element) fatherNode).getElementsByTagName(tag);
		if (list.getLength() > 0) {
			return list.item(0);
		}
		if (mandatory) {
			throw (new Exception(""));
		}
		return null;
	}
	
	private String getValueForTag(Node fatherNode, String tag, boolean mandatory) throws Exception {
		final Node node = getFirstSubNode(fatherNode, tag, mandatory);
		if (node != null) {
			return node.getFirstChild().getNodeValue();
		}
		return null;
	}
}
