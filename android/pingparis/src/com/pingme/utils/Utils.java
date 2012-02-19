package com.pingme.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Utils {
	
	
	public static boolean isCallable(Intent intent, Context context) {  
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,   
            PackageManager.MATCH_DEFAULT_ONLY);  
        return list.size() > 0;  
	}

	static public Object readFromFile(String fileName, Context activity) {
		Object res = null;
		try {
			FileInputStream fis = activity.openFileInput(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			try {
				// désérialisation : lecture de l'objet depuis le flux d'entrée
				res = (Object) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					ois.close();
				} finally {
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return res;
	}

	static public void writeToFile(String fileName, Context activity, Object toSave) {
		try {
			FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);

			// création d'un "flux objet" avec le flux fichier
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			try {
				// sérialisation : écriture de l'objet dans le flux de sortie
				oos.writeObject(toSave);
				// on vide le tampon
				oos.flush();
			} finally {
				// fermeture des flux
				try {
					oos.close();
				} finally {
					fos.close();
				}
			}
		} catch (FileNotFoundException ioe) {
			ioe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the network is available
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isConnected(Activity activity) {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/**
	 * Hash an md5 string
	 * 
	 * @param s
	 *            the string to hash
	 * @return The hash
	 */
	public static String md5(String s) {
		if (s == null) {
			return null;
		}
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getGetParamFromUrl(String url, String param) {
		if (url == null || param == null) {
			return null;
		}
		param += "=";

		int firstId = url.lastIndexOf(param) + param.length();
		int lastId = url.indexOf('&', firstId);
		if (lastId == -1) {
			lastId = url.length();
		}
		if (firstId == -1) {
			return "";
		}
		return url.substring(firstId, lastId);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.equals("");
	}
}
