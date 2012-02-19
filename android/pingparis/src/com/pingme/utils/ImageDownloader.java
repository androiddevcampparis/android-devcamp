/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pingme.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;


/**
 * This helper class download images from the Internet and binds those with the
 * provided ImageView.
 * 
 * <p>
 * It requires the INTERNET permission, which should be added to your
 * application's manifest file.
 * </p>
 * 
 * A local cache of downloaded images is maintained internally to improve
 * performance.
 */
public class ImageDownloader {
	private static final String LOG_TAG = "ImageDownloader";

	private static Class ANDROID_CLIENT_CLASS = null;
	private static Method ANDROID_CLIENT_CONSTRUCTOR = null;
	private static Method ANDROID_CLIENT_CLOSE = null;
	static {
		try {
			ANDROID_CLIENT_CLASS = Class.forName("android.net.http.AndroidHttpClient");
			ANDROID_CLIENT_CONSTRUCTOR = ANDROID_CLIENT_CLASS.getMethod("newInstance", String.class);
			ANDROID_CLIENT_CLOSE = ANDROID_CLIENT_CLASS.getMethod("close");

		} catch (Exception cne) {

		}
	}

	public enum Mode {
		NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT
	}

	private Mode mode = Mode.CORRECT;

	
	/**
	 * The execution content of the cache. 
	 * 
	 * Allows retrieving the cached version of the files that are locally stored.
	 */
	private Context context;

	public static HashMap<BitmapDownloaderTask, String> taches = new HashMap<BitmapDownloaderTask, String>();
	
	
	public ImageDownloader(Context context) {
		this.context = context;
		
		// Load the list of cached files with the URLS
		sFileCache = (FileCacheHashMap) Utils.readFromFile("fileCache", context);
		if (sFileCache == null) {
			sFileCache = new FileCacheHashMap(getCacheDirectory());
		} else {
			// Since files in the cache may be deleted, we need to clean the cache on each start
			sFileCache.clean();			
		}
	}	
	
	public void saveCache() {
			Utils.writeToFile("fileCache", context, sFileCache);
	}

	public void showUsedMegs() {
		int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
		String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
		Log.i(LOG_TAG, usedMegsString);
	}

	protected File getCacheDirectory() {
		File file = new File(this.context.getCacheDir(), "imagesCache");
		if (!file.exists())
			file.mkdir();
		return file;
	}
	
	/**
	 * cancel and destroy tasks which are not concerned
	 * 
	 * @param classSimpleName
	 */
	public static void cancelOtherTasks(String classSimpleName) {
		Iterator<HashMap.Entry<BitmapDownloaderTask,String>> iterator = taches.entrySet().iterator();
		while (iterator.hasNext()) {
			HashMap.Entry<BitmapDownloaderTask,String> entry = iterator.next();
			String activity = entry.getValue();
			if (activity != null && !activity.equals(classSimpleName)) {
				entry.getKey().cancel(true);
				// Canceling the task should remove it
				//iterator.remove();
			}
		}

	}

	/**
	 * Download the specified image from the Internet and binds it to the
	 * provided ImageView. The binding is immediate if the image is found in the
	 * cache and will be done asynchronously otherwise. A null bitmap will be
	 * associated to the ImageView if an error occurs.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 * @param tempImage
	 */
	public void download(String url, ImageView imageView, BitmapDrawable image, String activityName) {
		resetPurgeTimer();

		Bitmap bitmap = getBitmapFromCache(url, imageView, image);
		// Utils.log("IMAGE "+image+","+image.getBitmap().getHeight() +
		// " imageView"+imageView.getHeight()+","+imageView.getMeasuredHeight());

		if (bitmap == null) {
			BitmapDownloaderTask task = forceDownload(url, imageView, image);
			if (task != null)
				taches.put(task, activityName);
			
		} else {
			cancelPotentialDownload(url, imageView);
			imageView.setImageBitmap(bitmap);
		}
	}

	/*
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear. private
	 * void forceDownload(String url, ImageView view) { forceDownload(url, view,
	 * null); }
	 */

	/**
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear.
	 */
	private BitmapDownloaderTask forceDownload(String url, ImageView imageView, BitmapDrawable tempImage) {
		// State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
		BitmapDownloaderTask task = null;

		if (url == null) {
			imageView.setImageDrawable(null);
			// imageView.setImageResource(R.drawable.home_img_download);
			return null;
		}
		
		int w =0,h=0;
		LayoutParams params = imageView.getLayoutParams();
		if(params != null){
			w=  params.width;
			h = params.height;
		}

		// Change Task attributes if exist intead of Cancel

		if (refactorPotentialDownload(url, imageView, w, h)) {
			switch (mode) {
			case NO_ASYNC_TASK:
				Bitmap bitmap = downloadBitmap(url, w, h);
				addBitmapToCache(url, bitmap);
				imageView.setImageBitmap(bitmap);
				break;

			case NO_DOWNLOADED_DRAWABLE:
				imageView.setMinimumHeight(96);
				imageView.setMinimumWidth(128);
				task = new BitmapDownloaderTask(imageView, w, h);
				task.execute(url);
				break;

			case CORRECT:
				task = new BitmapDownloaderTask(imageView, w, h);
				DownloadedDrawable downloadedDrawable = null;
				if (tempImage != null) {
					downloadedDrawable = new DownloadedDrawable(task, tempImage);
				} else {
					downloadedDrawable = new DownloadedDrawable(task);
				}

				int minWith = 0;
				int minheight = 0;
				if(params != null){
					minWith = params.width;
					minheight = params.height;
				}
				
				imageView.setImageDrawable(downloadedDrawable);
				imageView.setMinimumWidth(minWith);
				imageView.setMinimumHeight(minheight);

				task.execute(url);
				break;
			}
		}

		return task;
	}

	/**
	 * Returns true if the current download has been canceled or if there was no
	 * download in progress on this image view. Returns false if the download in
	 * progress deals with the same url. The download is not stopped in that
	 * case.
	 */
	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	/**
	 * @param url
	 * @param imageView
	 * @return true if the Thread need to be launch
	 */
	private static boolean refactorPotentialDownload(String url, ImageView imageView, int w, int h) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;

			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
//				if (!bitmapDownloaderTask.isHasAsyncStarted()) {
//					// = Refactor the pending Thread =
//					bitmapDownloaderTask.resetData(imageView, w, h);
//					return false;
//				} else {
//					// = Cancel the running thread =
					bitmapDownloaderTask.cancel(true);
					return true;
				//}
			} else {
				// = The same URL is already being downloaded. =
				return false;
			}
		}
		return true;
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active download task (if any) associated
	 *         with this imageView. null if there is no such task.
	 */
	public static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	protected HttpClient getHttpClient() {
		HttpClient client = null;
		if (mode != Mode.NO_ASYNC_TASK || ANDROID_CLIENT_CLASS != null) {

			try {
				client = (HttpClient) ANDROID_CLIENT_CONSTRUCTOR.invoke(null, "Android");

			} catch (Exception e) {

			}
		} else
			client = new DefaultHttpClient();

		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "USER-AGENT");

		return client;

	}

	Bitmap downloadBitmap(String url, int width, int height) {
		// final int IO_BUFFER_SIZE = 4 * 1024;

		// AndroidHttpClient is not allowed to be used from the main thread
		HttpClient client = null;
		HttpGet getRequest = null;

		try {
			getRequest = new HttpGet(url);
			client = getHttpClient();

			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {					
				String md5 = Utils.md5(url);
				File outputFile = sFileCache.getCachedFile(md5);
				FileOutputStream stream = new FileOutputStream(outputFile);
				entity.writeTo(stream);
				stream.close();
				Bitmap res = getBitmapFromFile(outputFile, width, height);
				if (res != null) {
					synchronized (sFileCache) {
						sFileCache.put(url, md5);
						saveCache();						
					}
				}
				return res;
			}
		} catch (IOException e) {
			if (getRequest != null)
				getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			if (getRequest != null)
				getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			if (getRequest != null)
				getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			if (client == null) {
				return null;
			}
			if (ANDROID_CLIENT_CLASS != null && ANDROID_CLIENT_CLASS.isAssignableFrom(client.getClass())) {
				try {
					ANDROID_CLIENT_CLOSE.invoke(client);
				} catch (Exception e) {

				}
			}
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private WeakReference<ImageView> imageViewReference;
		private int imageTempWidth;
		private int imageTempHeight;
		private boolean finished = false;

		public BitmapDownloaderTask(ImageView imageView, int w, int h) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			imageTempWidth = w;
			imageTempHeight = h;
		}

		public void resetData(ImageView imageView, int w, int h) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			imageTempWidth = w;
			imageTempHeight = h;
		}

		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			// Remove me from the list of tasks
			taches.remove(this);
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			Bitmap bitmap = downloadBitmap(url, imageTempWidth, imageTempHeight);
			bitmap = resizeToFit(bitmap, imageTempWidth, imageTempHeight);
			return bitmap;
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			// bitmap = resizeToFit(bitmap, imageTempWidth, imageTempHeight);
			addBitmapToCache(url, bitmap);
			
			// Remove me from list of pending task
			taches.remove(this);

			if (imageViewReference != null && imageViewReference.get() != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

				// Change bitmap only if this process is still associated with it
				// Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
				if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {
					if (bitmap != null) {
						//imageView.setImageBitmap(bitmap);
						Drawable drawableOld = imageView.getDrawable();
						imageView.setImageBitmap(bitmap);

					}
				}
			}
			
			finished = true;
		}

		public boolean isFinished() {
			return finished;
		}
	}

	/**
	 * A fake Drawable that will be attached to the imageView while the download
	 * is in progress.
	 * 
	 * <p>
	 * Contains a reference to the actual download task, so that a download task
	 * can be stopped if a new binding is required, and makes sure that only the
	 * last started download process can bind its result, independently of the
	 * download finish order.
	 * </p>
	 */
	static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, BitmapDrawable bitmapDrawable) {
			super(bitmapDrawable.getBitmap());
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
		}

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		clearCache();
	}

	/*
	 * Cache-related fields and methods.
	 * 
	 * We use a hard and a soft cache. A soft reference cache is too
	 * aggressively cleared by the Garbage Collector.
	 */
	private static int size_big_img = 0;
	private static final int HARD_CACHE_CAPACITY = 25;
	private static final int HARD_CACHE_CAPACITY_BIG_FILE = 6;
	private static final int SOFT_CACHE_CAPACITY = 30;
	private static final int DELAY_BEFORE_PURGE = 60 * 1000 * 60 * 24; // in  milliseconds

	
	private FileCacheHashMap sFileCache;


	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2,
			0.75f, false) {
		/**
				 * 
				 */
		private static final long serialVersionUID = 2081952637133151372L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to soft reference cache
				super.removeEldestEntry(eldest);
				sSoftBitmapCache.put(eldest.getKey(), new WeakReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	// Hard cache big Images, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCacheBigImg = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY_BIG_FILE, 0.75f, false) {
		/**
				 * 
				 */
		private static final long serialVersionUID = -8500138771478713419L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY_BIG_FILE) {
				super.removeEldestEntry(eldest);
				sSoftBitmapCache.put(eldest.getKey(), new WeakReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};


	private final HashMap<String, WeakReference<Bitmap>> sSoftBitmapCache = new LinkedHashMap<String, WeakReference<Bitmap>>(
			SOFT_CACHE_CAPACITY, 0.75f, false) {
		private static final long serialVersionUID = -2301811094449480086L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, WeakReference<Bitmap>> eldest) {
			if (size() > SOFT_CACHE_CAPACITY - 1) {
				try {
					eldest.getValue().clear();
				} catch (Exception e) {
				}

				super.removeEldestEntry(eldest);
				return true;
			} else
				return false;
		}
	};

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable() {
		public void run() {
			clearCache();
		}
	};

	/**
	 * Adds this bitmap to the cache.
	 * 
	 * @param bitmap
	 *            The newly downloaded bitmap.
	 */
	private void addBitmapToCache(String url, Bitmap bitmap) {
		// never use memory cache
		return;
	}

	private Bitmap getBitmapFromFile(File file, int width, int height){
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (width != 0 && height != 0) {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			float heightRatio = (float)options.outHeight / (float)height ;
			float widthRatio = (float)options.outWidth / (float)width;
			float maxRatio = Math.max(widthRatio, heightRatio);
			if (maxRatio > 6)
				options.inSampleSize = 8;
			else if (maxRatio > 3)
				options.inSampleSize = 4;
			else if (maxRatio > 1.5f) 
				options.inSampleSize = 2;
			options.inJustDecodeBounds = false;
		}
		options.inTempStorage = new byte[16 * 1024];
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		return bitmap;
	}
	
	
	/**
	 * @param url
	 *            The URL of the image that will be retrieved from the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String url, ImageView imageView, BitmapDrawable tempImage) {
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}

		// Then try the hard reference cache for big Images
		synchronized (sHardBitmapCacheBigImg) {
			final Bitmap bitmap = sHardBitmapCacheBigImg.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCacheBigImg.remove(url);
				sHardBitmapCacheBigImg.put(url, bitmap);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		WeakReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
			}
		}
		
		synchronized (sFileCache) {

			String filename = sFileCache.get(url);
			if (filename != null) {
				LayoutParams params = imageView.getLayoutParams();
				int width = 0;
				int height = 0;
				if(params != null){
					width = params.width;
					height = params.height;
				}
				
				
				Bitmap bitmap = getBitmapFromFile(sFileCache.getCachedFile(filename), width, height);
				if (bitmap != null) {
					sFileCache.refresh(url, filename);
					addBitmapToCache(url, bitmap);
					return bitmap;
				}
			}
		}

		return null;
	}

	/**
	 * Resize the bitmap to save Memory when put in cache
	 * 
	 * @param bitmap
	 *            based big downloaded bitmap
	 * @param imageTempWidth
	 *            new Width in pixels
	 * @param imageTempHeight
	 *            new Height in pixels
	 * @return the new resized image
	 */
	private Bitmap resizeToFit(Bitmap bitmap, int imageTempWidth, int imageTempHeight) {
		if (bitmap == null) {
			return null;
		}

		if (imageTempWidth > 0 && imageTempHeight > 0 && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
			float deltaX = ((float) imageTempWidth) / ((float) bitmap.getWidth());
			float deltaY = ((float) imageTempHeight) / ((float) bitmap.getHeight());
			float delta = Math.max(deltaX, deltaY);

			if (delta < 1) {
				bitmap = Bitmap.createScaledBitmap(bitmap, (int) (delta * bitmap.getWidth()),
						(int) (delta * bitmap.getHeight()), false);

				// Bitmap bitmap2 = ImageUtils.resizeBitmap(bitmap, delta);
				// bitmap = bitmap2;
			}
		}

		return bitmap;
	}

	/**
	 * Clears the image cache used internally to improve performance. Note that
	 * for memory efficiency reasons, the cache will automatically be cleared
	 * after a certain inactivity delay.
	 */
	public void clearCache() {
		sHardBitmapCache.clear();
		sHardBitmapCacheBigImg.clear();
		sSoftBitmapCache.clear();
	}

	/**
	 * Allow a new delay before the automatic cache clear is done.
	 */
	private void resetPurgeTimer() {
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}


	/**
	 * Clear all the images in SD cache
	 */
	public void clearSdCache() {
		File cacheDir = getCacheDirectory();

		if (cacheDir.exists()) {
			File[] files = cacheDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}
}