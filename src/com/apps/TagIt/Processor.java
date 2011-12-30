package com.apps.TagIt;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;


public class Processor extends Activity {
	private String contentView;
	private String value;
	protected boolean _active = true;
	protected int _splashTime = 1000;
	private  Bundle b;
		 ArrayList<HashMap<String,String>> items;
	 ArrayList<HashMap<String,String>> chkitems;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.processor);
		b = getIntent().getExtras();
		 contentView=b.getString("currentView");
		 Log.i("CurrentView==========",contentView);
		 startSplashing();
	}
	
	private void startSplashing()

	{
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if(Integer.valueOf(Question.mediaId.toString().trim())==-1){
					if(contentView.equalsIgnoreCase("action:photo")){
						
						AlertDialog.Builder alertbox = new AlertDialog.Builder(Processor.this);
						// Set the message to display
						alertbox.setMessage(DataAdapters.ErrorMsg);
						// Add a neutral button to the alert box and assign a click listener
						alertbox.setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {

							// Click listener on the neutral button of alert box
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent=new Intent();
								intent.setClass(Processor.this, ImageCtl.class);
								startActivity(intent);
								finish();
							}
						});

						// show the alert box
						alertbox.show();
						
		        	}
		        	else if(contentView.equalsIgnoreCase("action:video")){
		        		AlertDialog.Builder alertbox = new AlertDialog.Builder(Processor.this);
						// Set the message to display
						alertbox.setMessage(DataAdapters.ErrorMsg);
						// Add a neutral button to the alert box and assign a click listener
						alertbox.setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {

							// Click listener on the neutral button of alert box
							public void onClick(DialogInterface arg0, int arg1) {
								
				        		Intent intent=new Intent();
								intent.setClass(Processor.this, VideoCtl.class);
								startActivity(intent);
								finish();
							}
						});
						alertbox.show();
						// show the alert box
						
		        	}
		        	else if(contentView.equalsIgnoreCase("action:audio")){
		        		AlertDialog.Builder alertbox = new AlertDialog.Builder(Processor.this);
						// Set the message to display
						alertbox.setMessage(DataAdapters.ErrorMsg);
						// Add a neutral button to the alert box and assign a click listener
						alertbox.setNeutralButton("Ok",
								new DialogInterface.OnClickListener() {

							// Click listener on the neutral button of alert box
							public void onClick(DialogInterface arg0, int arg1) {
								
								Intent intent=new Intent();
								intent.setClass(Processor.this, AudioCtl.class);
								startActivity(intent);
								finish();
							}
						});
						alertbox.show();
		        		
		        	}
				}
			}
		};
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {

						sleep(100);

						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					handler.sendEmptyMessage(0);

				} finally {
					try{
					processNextQuestion();
					}
					catch (Exception ee){
						handler.sendEmptyMessage(0);
					}
				}
			}
		};
		splashTread.start();

	}
		
	private void processNextQuestion() {
		
					 try {
						 DateFormat df = DateFormat.getTimeInstance();
						 df.setTimeZone(TimeZone.getTimeZone("gmt"));
						 String gmtTime = df.format(new Date());
						 TelephonyManager t1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
				  	       String udid = t1.getDeviceId();	
			    	        HttpClient client = new DefaultHttpClient();  
			    	        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			    	        String postURL;
			    	        if(contentView.equalsIgnoreCase("action:photo")){
			    	        	postURL= "http://services.tagit.com.au/TGTService/index/21/";
			    	        }
			    	        else if(contentView.equalsIgnoreCase("action:video")){
			    	        	postURL= "http://services.tagit.com.au/TGTService/index/21/";
			    	        }
			    	        else if(contentView.equalsIgnoreCase("action:audio")){
			    	        	postURL= "http://services.tagit.com.au/TGTService/index/21/";
			    	        }
			    	        else 	{
			    	        postURL= "http://services.tagit.com.au/TGTService/index/12/";
			    	        }
			    	        
			    	        String hash=md5(""+ udid + gmtTime + DataAdapters.login.get(0).get("token").toString());
			    	        HttpPost post = new HttpPost(postURL);
			    	         List<NameValuePair> params = new ArrayList<NameValuePair>();
			    	         reqEntity.addPart("answersetid", new StringBody(Question.answreId));
			    	         reqEntity.addPart("questionId", new StringBody(Question.questionId));
			    	            //params.add(new BasicNameValuePair("answersetid", Question.answreId));
			    	            //params.add(new BasicNameValuePair("questionId",Question.questionId));
			    	            if(contentView.equalsIgnoreCase("textbox")){
			    	            	value=b.getString("value");
			    	            	reqEntity.addPart("value", new StringBody(value));
			    	            	// params.add(new BasicNameValuePair("value",value));
			    	            }
			    	            else if(contentView.equalsIgnoreCase("radio")){
			    	            	value=b.getString("value");
			    	            	// params.add(new BasicNameValuePair("value",value));
			    	            	reqEntity.addPart("value", new StringBody(value));
			    	            }
			    	            else if(contentView.equalsIgnoreCase("checkbox")){
			    	            	ArrayList<String> val=b.getStringArrayList("value");
			    	            	for(String s :val){
			    	            		// params.add(new BasicNameValuePair("value",s));
			    	            		reqEntity.addPart("value", new StringBody(s));
			    	          		}
			    	            }
			    	          
			    	         
			    	            else if(contentView.equalsIgnoreCase("action:photo")){
				    	            /*File file = new File(b.getString("path"));
				    				FileInputStream fis = new FileInputStream(file);
				    				BufferedInputStream bis = new BufferedInputStream(fis);
				    				Bitmap bm = BitmapFactory.decodeStream(bis);
				    				bis.close();
				    					
		                            File uploadFile = new File(b.getString("path"));
		                            FileOutputStream fout = new FileOutputStream(uploadFile);
		                            bm.compress(Bitmap.CompressFormat.JPEG, 100, fout);
		                            fout.flush();
		                            fout.close();
		                            FileBody bin = new FileBody(uploadFile);*/
		                                
			    	            	File file = new File(b.getString("path"));
			    	            	FileBody bin = new FileBody(file);
		                            reqEntity.addPart("fil", bin);
		                            Log.i("TYPE", reqEntity.getContentType().toString());
		                              //post.setEntity(reqEntity);  
			    	            }
			    	            else if(contentView.equalsIgnoreCase("action:video")){
			    	            	Log.i("ASDFFFFFFFFFFFFFFFFFFFFFFFF==",b.get("value").toString());
		                              File file = new File(b.get("value").toString());
		                  
		                              FileBody bin = new FileBody(file);
		                           
		                              reqEntity.addPart("fil", bin);
		                              post.setEntity(reqEntity);  
	    	            
			    	            }
			    	            else if(contentView.equalsIgnoreCase("action:audio")){
			    	            	Log.i("ASDFFFFFFFFFFFFFFFFFFFFFFFF==",b.get("value").toString());
		                              File file = new File(b.get("value").toString());
		                  
		                              FileBody bin = new FileBody(file);
		                           
		                              reqEntity.addPart("fil", bin);
		                              post.setEntity(reqEntity);  
	    	            
			    	            }
			    	            else if(contentView.equalsIgnoreCase("media")){
		    	            		// params.add(new BasicNameValuePair("value",value));
		    	            	Log.i("IN MEDIA","IN YES");
		    	            	reqEntity.addPart("value", new StringBody(value));
			    	            }
			    	           
			    	     
			    	            reqEntity.addPart("imei", new StringBody(udid));
			    	            reqEntity.addPart("timestamp", new StringBody(gmtTime));
			    	            reqEntity.addPart("md5", new StringBody(hash));
			    	            reqEntity.addPart("token", new StringBody(DataAdapters.login.get(0).get("token").toString()));
			    	           // UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			    	           
			    	            post.setEntity(reqEntity);
			    	           
			    	            HttpResponse responsePOST = client.execute(post);  
			    	            HttpEntity resEntity = responsePOST.getEntity();  
			    	            if (resEntity != null) {
			    	            	if(contentView.equalsIgnoreCase("action:photo")){
			    	            		Log.i("ASDFFFFFFFFFFFFFFFFFFFFFFFF==","in photo");
			    	            		 MediaResultParser lp=new MediaResultParser();
					    	                lp.getRecords(EntityUtils.toString(resEntity));
					    	                callAgain();
			    	            	}
			    	            	else if(contentView.equalsIgnoreCase("action:video")){
			    	            		 MediaResultParser lp=new MediaResultParser();
					    	                lp.getRecords(EntityUtils.toString(resEntity));
					    	                callAgain();
			    	            	}
			    	            	else if(contentView.equalsIgnoreCase("action:audio")){
			    	            		 MediaResultParser lp=new MediaResultParser();
					    	                lp.getRecords(EntityUtils.toString(resEntity));
					    	                callAgain();
			    	            	}
			    	            	else{
			    	                QuestionDetailParser lp=new QuestionDetailParser();
			    	                lp.getRecords(EntityUtils.toString(resEntity));
			    	                actionRequired();
			    	            	}
			    	            }
			    	    } catch (Exception e) {
			    	    	e.printStackTrace();
			    	    }

	}
	
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	
	private void callAgain(){
		Log.i("Media id===",Question.mediaId.toString());
		if(Integer.valueOf(Question.mediaId.toString().trim())!=-1){
			value=Question.mediaId;
			contentView="media";
			processNextQuestion();
		}
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
		Log.i("ACTION REQUIRED",DataAdapters.question.get(0).get("questionType").toString());
		if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("textbox")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, TextBoxCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
			
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("tag")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, TagCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("checkbox")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, CheckBoxCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("radio")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, RadioCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
	         }
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photo")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, ImageCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:video")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, VideoCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:audio")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, AudioCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("hidden")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, HiddenCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("url"))
		{
			Intent intent=new Intent();
			intent.setClass(Processor.this, UrlCtl.class);
			startActivity(intent);
			finish();
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photooverlay"))
		{
			Intent intent=new Intent();
			intent.setClass(Processor.this, ImageCtl.class);
			startActivity(intent);
			finish();
		}
		else{
			if(DataAdapters.question.get(0).get("questionId").toString().trim().equalsIgnoreCase("-1"))
			{
				Intent intent=new Intent();
				intent.setClass(Processor.this, ThankYouCtl.class);
				startActivity(intent);
				finish();
			}
			else if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(Processor.this, ThankYouCtl.class);
				startActivity(intent);
				finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(Processor.this, MediaCtl.class);
				startActivity(intent);
				finish();
			}
		}
		}
	
	
	
	
}