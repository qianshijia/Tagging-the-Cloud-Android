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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

public class StartQuestions extends Activity {
	private int id;
	private HashMap<String, String> item;
		 ArrayList<HashMap<String,String>> items;
	 ArrayList<HashMap<String,String>> chkitems;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 processQuestion();
		
		
	}
	private void processQuestion() {
		try {
			final ProgressDialog dialog = ProgressDialog.show(this, null, null,
					true);
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					actionRequired();
					dialog.dismiss();

				}
			};
			Thread checkUpdate = new Thread() {
				public void run() {
					//Looper.prepare();
					 try {
						
						 DateFormat df = DateFormat.getTimeInstance();
						 df.setTimeZone(TimeZone.getTimeZone("gmt"));
						 String gmtTime = df.format(new Date());
						 TelephonyManager t1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
				  	       String udid = t1.getDeviceId();	
			    	        HttpClient client = new DefaultHttpClient();  
			    	        String postURL = "http://services.tagit.com.au/TGTService/index/11/";
			    	        String hash=md5(""+ udid + gmtTime + DataAdapters.login.get(0).get("token").toString());
			    	        HttpPost post = new HttpPost(postURL);
			    	            List<NameValuePair> params = new ArrayList<NameValuePair>();
			    	            params.add(new BasicNameValuePair("questionnaireId",Question.questionnaireId));
			    	            params.add(new BasicNameValuePair("imei", udid));
			    	            params.add(new BasicNameValuePair("timestamp", gmtTime));
			    	            params.add(new BasicNameValuePair("md5", hash));
			    	            params.add(new BasicNameValuePair("token", DataAdapters.login.get(0).get("token").toString()));
			    	            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			    	            post.setEntity(ent);
			    	            Log.i("Post",post.toString());
			    	            HttpResponse responsePOST = client.execute(post);  
			    	            HttpEntity resEntity = responsePOST.getEntity();  
			    	            if (resEntity != null) {    
			    	               // Log.i("RESPONSE",EntityUtils.toString(resEntity));
			    	                QuestionDetailParser lp=new QuestionDetailParser();
			    	                lp.getRecords(EntityUtils.toString(resEntity));
			    	            	handler.sendEmptyMessage(0);
			    	            }
			    	    } catch (Exception e) {
			    	    	e.printStackTrace();
			    	    }
				}
			};
			checkUpdate.start();
		} catch (Exception e) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

			// Set the message to display
			alertbox.setMessage("Check your internet connection.");

			// Add a neutral button to the alert box and assign a click listener
			alertbox.setNeutralButton("Ok",
					new DialogInterface.OnClickListener() {

						// Click listener on the neutral button of alert box
						public void onClick(DialogInterface arg0, int arg1) {

							// The neutral button was clicked

						}
					});

			// show the alert box
			alertbox.show();

		}

	}
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	
	private String md5(String in) {
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
	private void actionRequired(){
		if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("textbox")){
			
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, TextBoxCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("tag")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, TagCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photo")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Log.i("action", "photo");
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, ImageCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("checkbox")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, CheckBoxCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("radio")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, RadioCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:video")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, VideoCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("hidden")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, HiddenCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(StartQuestions.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("url"))
		{
			Intent intent=new Intent();
			intent.setClass(StartQuestions.this, UrlCtl.class);
			startActivity(intent);
			finish();
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photooverlay"))
		{
			Intent intent=new Intent();
			intent.setClass(StartQuestions.this, ImageCtl.class);
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