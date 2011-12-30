package com.apps.TagIt;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class History extends Activity
{
	private WebView wv;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.history);
		sp = getSharedPreferences(TagItConfig.SP_NAME, 0); 
		String url = "http://my.tagit.com.au/appLogin";
		
		wv = (WebView)findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true);
	    wv.setScrollBarStyle(0);
	    wv.setWebViewClient(new WebViewClient(){
	    	 public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
	            	view.loadUrl(url);
	                return true;   
	            }
	    });
	    DateFormat df = DateFormat.getTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("gmt"));
		String gmtTime = df.format(new Date());
		String un = sp.getBoolean("auto_login", false)?sp.getString("username", "") : "";
		String pw = sp.getBoolean("auto_login", false)?sp.getString("password", "") : "";
		TelephonyManager t1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
 	    String imei = t1.getDeviceId();
 	    String postData = "username=" + un + "&password=" + pw +
 	    		"&time=" + gmtTime + "&imei=" + imei + "&page=history";
 	    wv.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
	    //wv.loadUrl(url);
	}
	
	
}
