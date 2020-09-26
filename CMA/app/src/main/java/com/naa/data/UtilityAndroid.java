package com.naa.data;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UtilityAndroid {

	public static Object removeSettingAll;

    public static String getDefaultPath() {
		return "";
	}
	public static boolean isOnline(Context context){
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	@SuppressLint("MissingPermission")
	public static boolean isBluetoothEnabled() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			return false;
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				// Bluetooth is not enable :)
			}
			return mBluetoothAdapter.isEnabled();
		}
	}
	public static void removeSettingAll(Context context) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("SmartMobile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}

	public static void removeSettingAll() {
	}

	private String checkNetworkStatus(Context context) {
		String networkStatus ="";
		final ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//Check Wifi
		final NetworkInfo wifi = manager.getActiveNetworkInfo();
		//Check for mobile data
		final NetworkInfo mobile = manager.getActiveNetworkInfo();

		if( wifi.getType() == ConnectivityManager.TYPE_WIFI) {
			networkStatus = "wifi";
		}else if(mobile.getType() == ConnectivityManager.TYPE_MOBILE){
			networkStatus = "mobileData";
		}else{
			networkStatus="noNetwork";
		}
		return networkStatus;
	}
	public static boolean isWifiActive(Context context) {
		final ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo wifi = manager.getActiveNetworkInfo();
		if( wifi.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
    public static String getValueFromManifest(Context context, String name){
	   try {
		   ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		   Bundle bundle = ai.metaData;
		   return bundle.getString(name);
	   } catch (PackageManager.NameNotFoundException e) {
		   Log.e("getValueFromManifest", "Failed to load meta-data, NameNotFound: " + e.getMessage());
	   } catch (NullPointerException e) {
		   Log.e("getValueFromManifest", "Failed to load meta-data, NullPointer: " + e.getMessage());
	   }
	   return "";
   }

	public static String getDefaultPath(String fname) {
		return getDefaultPath() + fname;
	}
	public static Number getSettingAsNumber(Context context, String key, Number def) {
		String v = getSetting(context, key, String.valueOf(def));
		try {
			return Double.parseDouble(v);
		}catch ( Exception e){
			return 0;
		}
	}

	public static String getSetting(Context context, String key, String def) {
		if (context!=null) {
			SharedPreferences settings = context.getApplicationContext().getSharedPreferences("SmartMobile", 0);
			//String silent = settings.getString(key, def);
			String s = AES.decrypt(settings.getString(key, ""),  Utility.MD5(key));
			String silent = String.valueOf(s != null ? s :"")  ;
			return silent;
		}else{
			return def;
		}
	}
	public static void setSetting(Context context, String key, String val) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("SmartMobile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, AES.encrypt(val, Utility.MD5(key)));
		//editor.putString(key, val);
		editor.commit();
	}

	public static String encode(String s, String enc) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		StringBuffer ret = new StringBuffer(); // return value
		dOut.writeUTF(s);
		ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
		bIn.read();
		bIn.read();
		int c = bIn.read();
		while (c >= 0) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || c == '.' || c == '-'
					|| c == '*' || c == '_') {
				ret.append((char) c);
			} else if (c == ' ') {
				ret.append('+');
			} else {
				if (c < 128) {
					appendHex(c, ret);
				} else if (c < 224) {
					appendHex(c, ret);
					appendHex(bIn.read(), ret);
				} else if (c < 240) {
					appendHex(c, ret);
					appendHex(bIn.read(), ret);
					appendHex(bIn.read(), ret);
				}
			}
			c = bIn.read();
		}
		return ret.toString();
	}

	private static void appendHex(int arg0, StringBuffer buff) {
		buff.append('%');
		if (arg0 < 16) {
			buff.append('0');
		}
		buff.append(Integer.toHexString(arg0));
	}

	static public String urlEncode(String sUrl) {
		StringBuffer urlOK = new StringBuffer();
		for (int i = 0; i < sUrl.length(); i++) {
			char ch = sUrl.charAt(i);
			switch (ch) {
			case '<':
				urlOK.append("%3C");
				break;
			case '>':
				urlOK.append("%3E");
				break;
			case '/':
				urlOK.append("%2F");
				break;
			case ' ':
				urlOK.append("%20");
				break;
			case ':':
				urlOK.append("%3A");
				break;
			case '-':
				urlOK.append("%2D");
				break;
			default:
				urlOK.append(ch);
				break;
			}
		}
		return urlOK.toString();
	}
	public static View getInflater(Context context, int layout) {
		return inflate(context, layout);
	}
	public static View inflate(Context context, int layout) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layout, null, false);
	}

}
