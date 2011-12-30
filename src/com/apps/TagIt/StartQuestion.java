package com.apps.TagIt;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

public class StartQuestion extends Activity {
	private int id;
	private HashMap<String, String> item;
		 ArrayList<HashMap<String,String>> items;
	 ArrayList<HashMap<String,String>> chkitems;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// processQuestion();
		 actionRequired();
		
	}
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	private void actionRequired(){
		if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("textbox")){
			
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, TextBoxCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("tag")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, TagCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photo")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, ImageCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("checkbox")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, CheckBoxCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("radio")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, RadioCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:video")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, VideoCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("hidden")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, HiddenCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestion.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("url"))
		{
			Intent intent=new Intent();
			intent.setClass(StartQuestion.this, UrlCtl.class);
			startActivity(intent);
			finish();
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photooverlay"))
		{
			Intent intent=new Intent();
			intent.setClass(StartQuestion.this, ImageCtl.class);
			startActivity(intent);
			finish();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
		setContentView(R.layout.tag);
		
	    switch(requestCode) {
	        case IntentIntegrator.REQUEST_CODE: {
	            if (resultCode != RESULT_CANCELED) {
	                IntentResult scanResult =IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
	                if (scanResult != null) {
	                    String upc = scanResult.getContents();
	                   ((EditText)findViewById(R.id.txtQuestion)).setText(upc);
	                }
	            }
	            break;
	        }
	    }

	}
	
}