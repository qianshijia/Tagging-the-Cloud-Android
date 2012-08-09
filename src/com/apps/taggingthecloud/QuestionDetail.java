package com.apps.taggingthecloud;


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

import com.apps.taggingthecloud.R;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class QuestionDetail extends ListActivity implements OnClickListener,RadioGroup.OnCheckedChangeListener {
	private int id;
	private HashMap<String, String> item;
	private HashMap<String, String> iteml;
	private CheckBoxifiedTextListAdapter cbla;
	private static final int FROM_CAMERA = 11;
	private static final int FROM_Gallary = 12;

	private String currentView;
	private int optionVal;
	 ArrayList<HashMap<String,String>> items;
	 ArrayList<HashMap<String,String>> chkitems;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 Bundle b = getIntent().getExtras();
		 id=Integer.valueOf(b.get("id").toString());
		 item=DataAdapters.questions.get(id);
		 processQuestion();
		
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();		
		if(bt==R.id.btnNext){
			
			processNextQuestion();
			Log.i("NEXT","Process Next Question");
		}
		if(bt==R.id.btnProductBarcode){
			
			IntentIntegrator.initiateScan(QuestionDetail.this,"Barcode","Downlaod xZing","Yes","No","UPC_A,UPC_E,EAN_8,EAN_13,RSS14");
		}
		if(bt==R.id.btnQRBarcode){
			IntentIntegrator.initiateScan(QuestionDetail.this,"Barcode","Downlaod xZing","Yes","No","QR_CODE");
		}
		if(bt==R.id.btnCapture)
		{
			Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(intent, FROM_CAMERA);
		}
		if(bt==R.id.btnOpen)
		{
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), FROM_Gallary);

		}
		
}
	
	private void processNextQuestion() {
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
			    	        String postURL = "http://services.tagit.com.au/TGTService/index/12/";
			    	        String hash=md5(""+ udid + gmtTime + DataAdapters.login.get(0).get("token").toString());
			    	        HttpPost post = new HttpPost(postURL);
			    	            List<NameValuePair> params = new ArrayList<NameValuePair>();
			    	  
			    	            params.add(new BasicNameValuePair("answersetid",DataAdapters.question.get(0).get("answersetid").toString()));
			    	            params.add(new BasicNameValuePair("questionId",DataAdapters.question.get(0).get("questionId").toString()));
			    	            if(currentView.equalsIgnoreCase("textview")){
			    	            	 params.add(new BasicNameValuePair("value",((EditText)findViewById(R.id.txtQuestion)).getText().toString()));
			    	            }
			    	            if(currentView.equalsIgnoreCase("radio")){
			    	            	 params.add(new BasicNameValuePair("value",String.valueOf(optionVal)));
			    	            }
			    	            if(currentView.equalsIgnoreCase("checkbox")){
			    	            	
			    	            	 ArrayList<String> sll=	cbla.getListItems();
			    	         		for(String s :sll){
			    	         			
			    	         			for(HashMap<String,String> it:chkitems){
			    	         				if(s.trim().equalsIgnoreCase(it.get("title").trim().toString())){
			    	         					Log.i("equal and value is==",it.get("value").trim().toString());
			    	         					 params.add(new BasicNameValuePair("value",String.valueOf(it.get("value").trim().toString())));
			    	         				}
			    	         			}
			    	         		}
			    	            }
			    	           
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
			    	            	DataAdapters.question.clear();
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
			    	            Log.i("QCODEEEDD=",item.get("Qcode").toString());
			    	            params.add(new BasicNameValuePair("questionnaireId",item.get("Qcode").toString()));
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
		Log.i("Type===",DataAdapters.question.get(0).get("questionType").toString());
		if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("textbox")){
			setContentView(R.layout.textbox);
			currentView="textbox";
			
			
			EditText et=(EditText)findViewById(R.id.txtQuestion);
			et.setText(DataAdapters.question.get(0).get("questionData").toString().trim());
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("tag")){
			setContentView(R.layout.tag);
			Button btnBarcode=(Button)findViewById(R.id.btnProductBarcode);
			Button btnQRcode=(Button)findViewById(R.id.btnQRBarcode);
			btnBarcode.setOnClickListener(this);
			 btnQRcode.setOnClickListener(this);
			EditText txt=(EditText)findViewById(R.id.txtQuestion);
			txt.setText(DataAdapters.question.get(0).get("questionData").toString().trim());
			currentView="tag";
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photo")){
			setContentView(R.layout.image);
			  Button btn=(Button)findViewById(R.id.btnCapture);
		        btn.setOnClickListener(this);
		        Button btn1=(Button)findViewById(R.id.btnOpen);
		        btn1.setOnClickListener(this);
		        
			currentView="action:photo";
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("checkbox")){
			setContentView(R.layout.checkbox);
			String splitter1 = "\\^";
	    	String splitter2 = "\\~";
	    	 String st=DataAdapters.question.get(0).get("questionData").toString().trim();
	        String arr[]=st.split(splitter1);
	        HashMap<String,String> item;
	       chkitems=new ArrayList<HashMap<String,String>>();
	        cbla = new CheckBoxifiedTextListAdapter(this);
	        for(int i=0;i<arr.length;i++){
	        	String arr2[]=arr[i].split(splitter2);
	        	item=new HashMap<String,String>();
	        	 Log.i("Array1=", arr2[0]);
	        	 Log.i("Array2=", arr2[1]);
	       
	        		item.put("value", arr2[0]);
	        		item.put("title", arr2[1]);
	        		chkitems.add(item);
	        cbla.addItem(new CheckBoxifiedText(arr2[1], true));
	        }
	 
	      //  
	       
	        // Display it
	        setListAdapter(cbla);
	        currentView="checkbox";
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("radio")){
			setContentView(R.layout.options);
			String splitter1 = "\\^";
	    	String splitter2 = "\\~";
	        String st=DataAdapters.question.get(0).get("questionData").toString();
	        st=st.substring(3);
	        String arr[]=st.split(splitter1);
	        HashMap<String,String> item;
	        items=new ArrayList<HashMap<String,String>>();
	       for(int i=0;i<arr.length;i++){
	        	String arr2[]=arr[i].split(splitter2);
	        	item=new HashMap<String,String>();
	        	if(arr2[1].equalsIgnoreCase("default")){
	        		item.put("title", arr2[0]);
	        		item.put("isdefault", "Y");
	        		item.put("value", "0");
	        	}
	        	else{
	        		item.put("title", arr2[1]);
	        		item.put("isdefault", "N");
	        		item.put("value", arr2[0]);
	        	}
	        items.add(item);
	        }
	 
	        for(int j=items.size()-1; j>=0;j--){
	      	  Log.i("title=", items.get(j).get("title"));
	      	  Log.i("value=", items.get(j).get("value"));
	      	  Log.i("isdefault=", items.get(j).get("isdefault"));
	      
	      	  RadioGroup   mRadioGroup = (RadioGroup) findViewById(R.id.radiobtns);
	        RadioButton newRadioButton = new RadioButton(this);
	        
	        newRadioButton.setText(items.get(j).get("title").toString());
	        newRadioButton.setId(Integer.valueOf(items.get(j).get("value")));
	        if(items.get(j).get("isdefault").toString().trim().equalsIgnoreCase("Y")){
	        	newRadioButton.setChecked(true);
	        }
	       
	       
	        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
	                RadioGroup.LayoutParams.WRAP_CONTENT,
	                RadioGroup.LayoutParams.WRAP_CONTENT);
	        mRadioGroup.addView(newRadioButton, 0, layoutParams);
	       
	        mRadioGroup.setOnCheckedChangeListener(this);
	        currentView="radio";
	         }
		      
		  

		}
		else{
			
			setContentView(R.layout.question);
		}
		Button bt=(Button)findViewById(R.id.btnNext);
		bt.setOnClickListener(this);
		TextView tv=(TextView)findViewById(R.id.lblQTitle);
		Log.i("Question Label",DataAdapters.question.get(0).get("questionLabel").toString().trim());
		tv.setText(DataAdapters.question.get(0).get("questionLabel").toString().trim());
		
	
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
	
	
	@Override
	 public void onCheckedChanged(RadioGroup group, int checkedId) {

		// TODO Auto-generated method stub
		
	            Log.i("Value checked",String.valueOf(checkedId));
	            optionVal=checkedId;
	            //optionVal=
	} 


	
}