package com.pingme.service;

import java.util.List;

public interface DownloaderCallback {

	public void loadingFinished(List<String> datas);
	
	public void onError(int code);
}
