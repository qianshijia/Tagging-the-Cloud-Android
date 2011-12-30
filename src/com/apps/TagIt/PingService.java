package com.apps.TagIt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.ContentResolver;
import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;


public class PingService extends Object  {
	/** Called when the activity is first created. */
	public static boolean gpsStoped = true;
	public static boolean ignoreGpsPingResponses=false;
	public static double mylat;
	public static double mylang;
	public static int serverID=0;
	public static double myalt;
	public static long gpsTime;
	private static LocationManager  mlocManager;
	private static  LocationListener mlocListener;
	private static String uidl;
	private static Context contextl;
	public static String CallFrom="";
	public static  void runService(Context context, String uid) {
		// TODO Auto-generated method stub
		contextl=context;
		uidl=uid;
		gpsStoped = false;
		try{
			mlocManager = (LocationManager)  context.getSystemService(Context.LOCATION_SERVICE);
			mlocListener = new MyLocationListener();
			mlocManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					mlocListener);
			Log.i("calling services","true1");
		}
		catch (Exception e){
			try{
				mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				mlocListener = new MyLocationListener();
				mlocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0,
						mlocListener);
				Log.i("calling services","true2");

			}
			catch (Exception e1){
				Log.i("Error","YES");
				e.printStackTrace();
			}
		}
	}
	private static void trigerPingServiceCall() {
		try {
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if(PingService.CallFrom.trim().equalsIgnoreCase("MainBarcodeActivity")){
						Log.i("#############", "trigerPingCall");
						if(PingService.ignoreGpsPingResponses==false)
							MainBarcodeActivity.PerformRespectiveAction();
					}


					//actionRequiredForPingService();
				}
			};
			Thread checkUpdate = new Thread() {

				public void run() {
					Looper.prepare();
					try {
						DateFormat df = DateFormat.getTimeInstance();
						df.setTimeZone(TimeZone.getTimeZone("gmt"));
						String gmtTime = df.format(new Date());
						TelephonyManager t1 = (TelephonyManager) contextl.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
						String udid = t1.getDeviceId();	
						HttpClient client = new DefaultHttpClient();  
						String postURL = "http://services.tagit.com.au/TGTService/index/42/";
						String hash=md5(udid + gmtTime + DataAdapters.login.get(0).get("token").toString());
						HttpPost post = new HttpPost(postURL);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("alt", String.valueOf(myalt)));
						params.add(new BasicNameValuePair("gpstime", String.valueOf(gpsTime)));
						params.add(new BasicNameValuePair("imei", String.valueOf(udid)));
						params.add(new BasicNameValuePair("lat", String.valueOf(mylat)));
						params.add(new BasicNameValuePair("long", String.valueOf(mylang)));
						params.add(new BasicNameValuePair("md5", hash));
						params.add(new BasicNameValuePair("timestamp", gmtTime));
						params.add(new BasicNameValuePair("token", DataAdapters.login.get(0).get("token").toString()));
						params.add(new BasicNameValuePair("uid",""));
						Log.i("Lat", String.valueOf(PingService.mylat));
						Log.i("Lang", String.valueOf(PingService.mylang));
						UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
						post.setEntity(ent);
						HttpResponse responsePOST = client.execute(post);  
						HttpEntity resEntity = responsePOST.getEntity();  
						if (resEntity != null) {    
							String res = EntityUtils.toString(resEntity);
							//Log.i("RESPONSE", res);
							if(res.length() > 500)
							{
								Log.i("GPS Server Error", "GPS Server Error");
								handler.sendEmptyMessage(0);
							}
							if(PingService.ignoreGpsPingResponses==false){
								PingServiceParser lp=new PingServiceParser();
								lp.getRecords(res);
							}
							handler.sendEmptyMessage(0);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			checkUpdate.start();
			
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	private static String md5(String in) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		return null;
	}
	public static void stopService() {
		if(!gpsStoped)
		{
			mlocManager.removeUpdates(mlocListener);
			gpsStoped = true;
		}
	}
	private static void DoAction() {
		gpsStoped = true;
		mlocManager.removeUpdates(mlocListener);
		trigerPingServiceCall();

	}
	/* Class My Location Listener */

	public static class MyLocationListener implements LocationListener

	{

		@Override
		public void onLocationChanged(Location loc)

		{

			mylat = loc.getLatitude();
			mylang = loc.getLongitude();
			myalt = loc.getAltitude();
			gpsTime= loc.getTime();
			DoAction();

		}

		@Override
		public void onProviderDisabled(String provider)

		{

		}

		@Override
		public void onProviderEnabled(String provider)

		{

			// Toast.makeText(
			// getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)

		{

		}

	}/* End of Class MyLocationListener */
}