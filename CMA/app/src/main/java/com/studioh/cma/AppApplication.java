package com.studioh.cma;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.StrictMode;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.naa.data.Dson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by rkrzmail on 13/06/2017.
 */

public class AppApplication extends Application {
    private static AppApplication appSystem;
    @Override
    public void onCreate() {
        super.onCreate();
        appSystem = this;

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public static AppApplication getInstance(){
        return appSystem;
    }

    public static String getBaseUrl(String name){
        return "https://apidev.xxx.co.id/api/"+name;
    }
    public static String getBaseUrlV2(String name){
        return  getBaseUrl(name);
    }
    public static String getUrl(String name){
        return getBaseUrl(name)+"?key="+UtilityAndroid.getSetting(getInstance().getApplicationContext(), "KEY", "");
    }


    private static String slocation = "0,0";
    public static String getLastCurrentLocation(){
        return UtilityAndroid.getSetting(getInstance().getApplicationContext(), "LOCATION", "0,0");
    }
    public static void setLastCurrentLocation(Location location){
        if (location!=null){
            slocation = location.getLatitude() + "," + location.getLongitude();
            UtilityAndroid.setSetting(getInstance().getApplicationContext(), "LOCATION", slocation);
        }
    }

    public HashMap<String, String> getArgsData() {
        HashMap<String, String> hashtable = new HashMap();

        hashtable.put("user", UtilityAndroid.getSetting(getApplicationContext(), "user", ""));
        hashtable.put("session", UtilityAndroid.getSetting(getApplicationContext(), "session", ""));
        hashtable.put("CID",    UtilityAndroid.getSetting(getApplicationContext(), "CID", ""));
        hashtable.put("FCM",    UtilityAndroid.getSetting(getApplicationContext(), "FCMID", ""));
        hashtable.put("NOW",    Utility.Now());
        hashtable.put("Location", AppApplication.getLastCurrentLocation());

        int vi = TimeZone.getDefault().getRawOffset() / 1000;
        hashtable.put("xzona", String.valueOf(vi / 60 / 60));//gmt
        hashtable.put("time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        try {
            PackageInfo pInfo = AppApplication.getInstance().getPackageManager().getPackageInfo(AppApplication.getInstance().getPackageName(), 0);
            hashtable.put("version", pInfo.versionName);
        } catch (Exception e) {
            hashtable.put("version", UtilityAndroid.getSetting(getApplicationContext(), "VERSION", ""));
        }
        hashtable.put("xcount", "1");
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            hashtable.put("imei", "");
            hashtable.put("imsi", "");
            hashtable.put("simsn", "");
            hashtable.put("line","");
        }else{
            hashtable.put("imei", String.valueOf(mTelephonyMgr.getDeviceId()) );
            hashtable.put("imsi", String.valueOf(mTelephonyMgr.getSubscriberId()));
            hashtable.put("simsn", String.valueOf(mTelephonyMgr.getSimSerialNumber()) );
            hashtable.put("line", String.valueOf(mTelephonyMgr.getLine1Number()) );
        }


        /*try {
            TelephonyManager telephonyManager = (TelephonyManager) AppApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            hashtable.put("imei", telephonyManager.getDeviceId() );
        } catch (Exception e) { hashtable.put("imei", ""); }*/


        return hashtable;
    }

}
