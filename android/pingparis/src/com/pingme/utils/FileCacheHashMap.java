package com.pingme.utils;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.util.Log;


class FileCacheHashMap extends LinkedHashMap<String, String> {

	private static final int FILE_CACHE_CAPACITY = 100;
	private static final int FILE_CACHE_SIZE = 1 * 1024 * 1024; // 1 Mbyte

	private static final long serialVersionUID = 2081952637133146372L;
	
	private long fileSizesSum = 0;
	private File baseDirectory;
	
	
	public FileCacheHashMap(){
		super( FILE_CACHE_CAPACITY / 2, 0.75f, false);
	}
		
	public FileCacheHashMap(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
	public long getFileSizesSum() {
		return fileSizesSum;
	}

	public void setFileSizesSum(long fileSizesSum) {
		this.fileSizesSum = fileSizesSum;
	}
	
	
	public File getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public File getCachedFile(String filename) {
		return new File(baseDirectory, filename);
	}

	private long getFileSize(String filename) {
		if (filename != null) {
			File file = getCachedFile(filename);
			if (file.exists())
				return file.length();				
		}
		return 0;
	}		
	
	@Override
	public String put(String key, String value) {
		fileSizesSum += getFileSize(value);
		return super.put(key, value);
	}

	@Override
	public String remove(Object key) {
		fileSizesSum -= getFileSize(get(key));
		return super.remove(key);
	}

	@Override
	protected boolean removeEldestEntry(LinkedHashMap.Entry<String, String> eldest) {
		
		if (fileSizesSum >= FILE_CACHE_SIZE) {
			File file = getCachedFile(eldest.getValue());
			fileSizesSum -= file.length();
			file.delete();
			return true;
		} else
			return false;
	}		
	
	public void clean() {
		long mySize = 0;
		long fileCount = 0;
		Iterator<FileCacheHashMap.Entry<String,String>> iterator = entrySet().iterator();
		
		while (iterator.hasNext()) {
			FileCacheHashMap.Entry<String,String> entry = iterator.next();
			File file = getCachedFile(entry.getValue());
			if (file.exists()) {
				mySize += file.length();
				fileCount++;				
			} else
				iterator.remove();
		}
		fileSizesSum = mySize;
	}
	
	public void refresh(String key, String value) {
		super.remove(key);
		super.put(key, value);
	}
}