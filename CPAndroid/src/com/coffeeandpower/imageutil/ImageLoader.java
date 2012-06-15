package com.coffeeandpower.imageutil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

	CacheMemory memoryCache = new CacheMemory();
	CacheFile fileCache;

	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

	ExecutorService executorService;

	private int defaultImage;
	private int IMAGE_SIZE = 70;
	
	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;

	public ImageLoader(Context context) {
		fileCache = new CacheFile(context);
		executorService = Executors.newFixedThreadPool(5);
		
		checkMediaState();
                
                

	}

	public void DisplayImage(String url, ImageView imageView, int defaultImageRes, int size) {
		this.defaultImage = defaultImageRes;
		this.IMAGE_SIZE = size;
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);

		if (bitmap != null) {
			//Log.d("ImageLoader","Got image from cache: " + url);
			imageView.setImageBitmap(bitmap);
		} else {
			//Log.d("ImageLoader","Queueing image: " + url);
			queuePhoto(url, imageView);
			imageView.setImageResource(defaultImageRes);
		}
	}
	
	private void checkMediaState() {
		String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    mExternalStorageAvailable = mExternalStorageWriteable = true;
                } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    mExternalStorageAvailable = true;
                    mExternalStorageWriteable = false;
                } else {
                    mExternalStorageAvailable = mExternalStorageWriteable = false;
                }
                //Log.d("ImageLoader","Media State: avail - " + mExternalStorageAvailable + ", writeable - " + mExternalStorageWriteable + ", canwrite: " + Environment.getExternalStorageDirectory().canWrite());
	}

	private void queuePhoto(String url, ImageView imageView) {

		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {

		File f = fileCache.getFile(url);
		
		//Log.d("ImageLoader","getBitmap: File: " + f.exists() + " " + f.canWrite() + " - " + f.getPath());
		

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from http
		Bitmap imgBitmap = null;
		ByteArrayOutputStream buffer = null;
		
		try {
			//Bitmap bitmap = null;
			//Log.d("ImageLoader","Loading URL: " + url.hashCode());
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			
			// Need to read inputstream into memory, not directly into file
			// in case file is not writeable
			buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] tmpData = new byte[16384];
			while ((nRead = is.read(tmpData, 0, tmpData.length)) != -1) {
			  buffer.write(tmpData, 0, nRead);
			}
			buffer.flush();
		}
		catch (Exception e) {
			Log.e("ImageLoader","Exception in img download: " + e);
		}
		try {

			byte[] imgBytes = buffer.toByteArray();
			imgBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
			
			//Log.d("ImageLoader","Read image into memory: " + imgBytes.length);
			OutputStream os = new FileOutputStream(f);
			os.write(imgBytes);
			os.close();
			
			return imgBitmap;
		} catch (Exception e) {
			Log.e("ImageLoader","Could not save image: " + e);
			 //e.printStackTrace();
			if (imgBitmap != null)
				return imgBitmap;
			else
				return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power
			// of 2.

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < IMAGE_SIZE || height_tmp / 2 < IMAGE_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {

		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {

		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(defaultImage);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public void copyStream(InputStream is, OutputStream os) {

		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

}
