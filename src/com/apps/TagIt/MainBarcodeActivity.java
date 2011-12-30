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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainBarcodeActivity extends Activity implements OnClickListener, OnTouchListener, OnGestureListener
{
	private Button btnBarcode;
	private Button btnQRcode;
	private EditText txt;
	private SharedPreferences sp;
	private String tagType;
	private GestureDetector mGestureDetector;
	private ProgressDialog dialog;
	private static Thread pingThread;

	private static int msgLen=0;
	protected boolean _active = true;
	protected static MainBarcodeActivity me;
	protected int _splashTime = 15000;
	private HashMap<String, String> item;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me=this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.barcodemain);
		btnBarcode=(Button)findViewById(R.id.btnProductBarcode);
		btnQRcode=(Button)findViewById(R.id.btnQRBarcode);
		btnBarcode.setOnClickListener(this);
		btnQRcode.setOnClickListener(this);
		txt=(EditText)findViewById(R.id.txtBarcode);

		mGestureDetector = new  GestureDetector(this);
		LinearLayout mLinearLayout = (LinearLayout)findViewById(R.id.barcode_layout);
		mLinearLayout.setOnTouchListener(this);
		mLinearLayout.setLongClickable(true);
		
		sp = getSharedPreferences(TagItConfig.SP_NAME, 0);
		
		if(sp.getBoolean("first_time", true))
		{
			createConfigFile();
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();
		if(bt==R.id.btnProductBarcode){
			IntentIntegrator.initiateScan(MainBarcodeActivity.this,"Barcode","Downlaod xZing","Yes","No","UPC_A,UPC_E,EAN_8,EAN_13,RSS14");
		}
		if(bt==R.id.btnQRBarcode){
			IntentIntegrator.initiateScan(MainBarcodeActivity.this,"Barcode","Downlaod xZing","Yes","No","QR_CODE");
		}

	}
	
	@Override
	protected void onResume() {
		PingService.ignoreGpsPingResponses = false;
		// TODO Auto-generated method stub
		if(!DataAdapters.login.isEmpty())
		{
			item=DataAdapters.login.get(0);
		}
		super.onResume();
	}
	
	public void onStart()
	{
		if(pingThread == null)
		{
			startMonitoringPingService();
		}
		else if(!pingThread.isAlive())
		{
			startMonitoringPingService();
		}
		super.onStart();
	}
	
	//*Create Options Menu Start*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, Menu.NONE, "Settings").setIcon(R.drawable.settings_menu);
		menu.add(0, Menu.FIRST + 1, Menu.NONE, "Log Off").setIcon(R.drawable.logoff);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == Menu.FIRST)
		{
			Intent i = new Intent();
			i.setClass(this, TagItConfig.class);
			startActivity(i);
		}
		else if(item.getItemId() == Menu.FIRST +1)
		{
			AlertDialog.Builder alertbox = new AlertDialog.Builder(me);
			alertbox.setMessage("Do you want to Log Off?");
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					finish();
				}
			});
			alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) 
				{
				}
			});
			alertbox.show();
		}
		return true;
		
	}
	//*Create Options Menu End*
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent.getStringExtra("RFIDTagId") != null)
		{
			txt.setText(intent.getStringExtra("RFIDTagId"));
			tagType = "RFID Tag";
			processTag("RFID Tag");
		}
	}
	
	//Show an Alert Dialog if the server returns a message
	private static void showAlert(String str){
		AlertDialog.Builder alertbox = new AlertDialog.Builder(me);
		alertbox.setMessage(str);
		alertbox.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				msgLen+=1;
				PerformRespectiveAction();
			}
		});
		alertbox.show();
	}
	
	private static void actionRequiredForPing(){
		if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("textbox")){

			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(me, TextBoxCtl.class);
				me.startActivity(intent);
				me.finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(me, MediaCtl.class);
				me.startActivity(intent);
				me.finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("tag")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(me, TagCtl.class);
				me.startActivity(intent);
				me.finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(me, MediaCtl.class);
				me.startActivity(intent);
				me.finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photo")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(me, ImageCtl.class);
				me.startActivity(intent);
				me.finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(me, MediaCtl.class);
				me.startActivity(intent);
				me.finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("checkbox")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(me, CheckBoxCtl.class);
				me.startActivity(intent);
				me.finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(me, MediaCtl.class);
				me.startActivity(intent);
				me.finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("radio")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(me, RadioCtl.class);
				me.startActivity(intent);
				me.finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(me, MediaCtl.class);
				me.startActivity(intent);
				me.finish();
			}
		}
		else if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:video")){
			if(DataAdapters.question.get(0).get("mediaData").toString().trim().equalsIgnoreCase("")){
				Intent intent=new Intent();
				intent.setClass(me, VideoCtl.class);
				me.startActivity(intent);
				me.finish();
			}
			else{
				Intent intent=new Intent();
				intent.setClass(me, MediaCtl.class);
				me.startActivity(intent);
				me.finish();
			}
		}
	}
	
	//Perform actions after receiving the return data from the server
	public static void PerformRespectiveAction(){
		if(DataAdapters.messages!=null)
		{
			if(msgLen< DataAdapters.messages.length)
				showAlert(DataAdapters.messages[msgLen]);
			else{
				if(DataAdapters.question!=null){
					if(DataAdapters.question.size()>0){
						PingService.ignoreGpsPingResponses=true;
						actionRequiredForPing();
					}
				}
				 if(DataAdapters.questions!=null){
					if(DataAdapters.questions.size()>0){
						PingService.ignoreGpsPingResponses=true;
						Intent intent=new Intent();
						intent.setClass(me, QuestionsListActivity.class);
						me.startActivity(intent);
						me.finish();
					}
				}
			}
		}
		else{
				if(DataAdapters.question.size()>0 && !DataAdapters.question.get(0).get("questionId").equals("-1")){
					PingService.ignoreGpsPingResponses=true;
					actionRequiredForPing();
				}
				else if(DataAdapters.questions.size()>0){
					Intent intent=new Intent();
					PingService.ignoreGpsPingResponses=true;
					intent.setClass(me, QuestionsListActivity.class);
					me.startActivity(intent);
					me.finish();
				}
		}

		if(Question.questionId.trim().contains("-1"))
		{
			if(pingThread == null)
			{
				startMonitoringPingService();
			}
			else if(!pingThread.isAlive())
			{
				startMonitoringPingService();
			}
		}
	}
	
	private static void startMonitoringPingService()
	{
		pingThread = new Thread()
		{
			Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					PingService.CallFrom="MainBarcodeActivity";
					PingService.runService(me.getApplicationContext(),((EditText)me.findViewById(R.id.txtBarcode)).getText().toString());
				}
			};
			@Override
			public void run()
			{
				int waited = 0;
				while(me._active && (waited < me._splashTime))
				{
					try {

						sleep(1000);
					}
					catch (InterruptedException e) {
						return;
					}
					if(me._active) 
					{
						waited += 1000;
						Log.i("Seconds",String.valueOf(waited));
					}
				} 
				waited=0;
				handler.sendEmptyMessage(0);
			}
		};
		if(!pingThread.isAlive())
		{
			pingThread.start();
		}

	}
	
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
		switch(requestCode) 
		{
			case IntentIntegrator.REQUEST_CODE: 
			{
				if (resultCode != RESULT_CANCELED) {
					IntentResult scanResult =
						IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
					if (scanResult != null) {
						String upc = scanResult.getContents();
						((EditText)findViewById(R.id.txtBarcode)).setText(upc);
						if(scanResult.getFormatName().equals(IntentIntegrator.QR_CODE_TYPES))
						{
							tagType = "QR Tag";
							processTag(tagType);
						}
						else
						{
							tagType = "Bar Code";
							processTag(tagType);
						}
					}
				}
				break;
			}
		}
	} 

	private void processTag(final String tagType) {
		Question.clear();
		if(!DataAdapters.login.isEmpty() && DataAdapters.login.get(0).get("validLogin").equals("1"))
		{
			PingService.CallFrom="MainBarcodeActivity";
		}
		if(!sp.getBoolean("enhance_action", true))
		{
			autoAction(tagType);
		}
		else
		{
			actionByTagIt(tagType);
		}

	}
	
	private void actionByTagIt(String tagType)
	{
		final String tp = tagType;
		final String tagCont = ((EditText)findViewById(R.id.txtBarcode)).getText().toString();
		
		if(TagItHelper.check3GNetwork(getApplicationContext()) || TagItHelper.checkWiFi(getApplicationContext()))
		{
			if(dialog == null)
			{
				dialog = ProgressDialog.show(this, null, null,
						true);
				dialog.setTitle("Processing...");
			}
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					DataAdapters.repeatTag = "";
					if(tagCont.contains("tagit.com.au"))
					{
						DataAdapters.repeatTag = tagCont.substring(tagCont.lastIndexOf("/") + 1);
					}
					else
					{
						DataAdapters.repeatTag = tagCont;
					}
					DataAdapters.repeatTagType = tp;
					
					if(DataAdapters.questions.size()>0)
					{
						dialog.dismiss();
						actionRequired();
					}
					else if(DataAdapters.question.size() != 0 && !DataAdapters.question.get(0).get("questionId").equals("-2"))
					{
						dialog.dismiss();
						actionRequired();
					}
					else
					{
						Toast.makeText(MainBarcodeActivity.this, "Server Error", Toast.LENGTH_LONG).show();
						dialog.dismiss();
						((EditText)findViewById(R.id.txtBarcode)).setText("");
					}
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
						String postURL = "http://services.tagit.com.au/TGTService/index/10";
						String hash = null;
						String token = null;
						if(item == null)
						{
							hash = md5(""+ udid + gmtTime);
							token = "";
						}
						else
						{
							hash = md5(""+ udid + gmtTime + item.get("token").toString());
							token = item.get("token").toString();
						}							
						
						HttpPost post = new HttpPost(postURL);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						Log.i("TAG", ((EditText)findViewById(R.id.txtBarcode)).getText().toString());
						if(tagCont.contains("tagit.com.au"))
						{
							params.add(new BasicNameValuePair("uid", tagCont.substring(tagCont.lastIndexOf("/") + 1)));
						}
						else
						{
							params.add(new BasicNameValuePair("uid", tagCont));
						}
						//params.add(new BasicNameValuePair("uid", tagCont));
						params.add(new BasicNameValuePair("imei", udid));
						params.add(new BasicNameValuePair("timestamp", gmtTime));
						params.add(new BasicNameValuePair("md5", hash));
						params.add(new BasicNameValuePair("token", token));
						params.add(new BasicNameValuePair("tagtype", tp));
						
						UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
						post.setEntity(ent);
						HttpResponse responsePOST = client.execute(post);  
						HttpEntity resEntity = responsePOST.getEntity();  
						String res = EntityUtils.toString(resEntity);
						if (resEntity != null) {
							//if the result length is larger than 500, that means an error page is returned
							if(res.length() < 500)
							{
								if(res.contains("questionairelist"))
								{
									DataAdapters.questions.clear();
									QuestionParser lp=new QuestionParser();
									lp.getRecords(res);
								}
								else
								{
									DataAdapters.question.clear();
									QuestionDetailParser qdp = new QuestionDetailParser();
									qdp.getRecords(res);
								}
							}
							handler.sendEmptyMessage(0);
						}
					} 
					catch (Exception e) 
					{
						AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBarcodeActivity.this);
						alertbox.setMessage("Check your internet settings.");
						alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
						alertbox.show();
					}
				}
			};
			checkUpdate.start();
		}
		else
		{
				AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBarcodeActivity.this);
    			alertbox.setMessage("Check your internet settings.");
    			alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface arg0, int arg1) {
    							// The neutral button was clicked
    						}
    					});
    			alertbox.show();
		}
	}
	
	private void autoAction(final String tagType)
	{
		String tagContent = ((EditText)findViewById(R.id.txtBarcode)).getText().toString().toLowerCase();
		
		String[] splitContent = tagContent.split(":");
		if(splitContent[0].equalsIgnoreCase("http"))
		{
			((EditText)findViewById(R.id.txtBarcode)).setText("");
			Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(tagContent));
			startActivity(intent);
		}
		else if(splitContent[0].equalsIgnoreCase("tel"))
		{
			((EditText)findViewById(R.id.txtBarcode)).setText("");
			Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse(tagContent));
			startActivity(intent);
		}
		else if(splitContent[0].equalsIgnoreCase("smsto"))
		{
			((EditText)findViewById(R.id.txtBarcode)).setText("");
			String smsTo = tagContent.substring(0, tagContent.lastIndexOf(":"));
			Intent intent=new Intent(Intent.ACTION_SENDTO, Uri.parse(smsTo));
			intent.putExtra("sms_body", tagContent.substring(tagContent.lastIndexOf(":")+1));
			startActivity(intent);
		}
		else
		{
			actionByTagIt(tagType);
		}
	}
	
	@Override    
	protected void onDestroy() { 
		PingService.stopService();
		if(pingThread != null && pingThread.isAlive())
		{
			pingThread.interrupt();
		}
		super.onDestroy();
	}
	
	private void actionRequired(){
		Thread.interrupted();
		if(DataAdapters.questions.size()>0){
			Intent intent=new Intent();
			intent.setClass(MainBarcodeActivity.this, QuestionsListActivity.class);
			startActivity(intent);
			finish();
		}
		else if(DataAdapters.question.size()>0)
		{
			if(DataAdapters.question.get(0).get("questionId").equals("-1"))
			{
				String tagContent = ((EditText)findViewById(R.id.txtBarcode)).getText().toString().toLowerCase();
			    String[] splitContent = tagContent.split(":");
			    if(splitContent[0].equalsIgnoreCase("http"))
				{
					Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(tagContent));
					startActivity(intent);
				}
				else if(splitContent[0].equalsIgnoreCase("tel"))
				{
					Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse(tagContent));
					startActivity(intent);
				}
				else if(splitContent[0].equalsIgnoreCase("smsto"))
				{
					String smsTo = tagContent.substring(0, tagContent.lastIndexOf(":"));
					Intent intent=new Intent(Intent.ACTION_SENDTO, Uri.parse(smsTo));
					intent.putExtra("sms_body", tagContent.substring(tagContent.lastIndexOf(":")+1));
					startActivity(intent);
				}
				else
				{
					Toast.makeText(this, "There is no processes comes with this tag", Toast.LENGTH_LONG).show();
				}
			    dialog.dismiss();
				
			}
			//login required
//			else if(DataAdapters.question.get(0).get("questionId").equals("-2"))
//			{	
//				if(!sp.getBoolean("auto_login", false))
//				{
//					Toast.makeText(this, "Login is required to perform further actions!", Toast.LENGTH_LONG).show();
//					Intent intent = new Intent();
//					intent.putExtra("tag", ((EditText)findViewById(R.id.txtBarcode)).getText().toString());
//					intent.putExtra("tagType", tagType);
//					intent.setClass(MainBarcodeActivity.this, TagIt.class);
//					startActivity(intent);
//				}
//				else
//				{
//					validateCall();
//				}
//			}
			else
			{
					Intent intent=new Intent();
					intent.setClass(MainBarcodeActivity.this, StartQuestion.class);
					startActivity(intent);
					finish();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		    if (keyCode == KeyEvent.KEYCODE_BACK) {
		    	AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBarcodeActivity.this);
				// Set the message to display
				alertbox.setMessage("Are you sure to quit TagIt?");
				// Add a positive button to the alert box and assign a click listener
				alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						DataAdapters.login.clear();
						DataAdapters.question.clear();
						DataAdapters.questions.clear();
						finish();
					}
				});
				alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) 
					{
					}
				});
				alertbox.show();
		    }
		return false;
	}
	
	private void createConfigFile()
	{
		Editor e = sp.edit();
		e.putBoolean("first_time", false);
		e.putBoolean("download_media", true);
		e.putBoolean("auto_login", false);
		e.putBoolean("enhance_action", true);
		e.commit();
	}
	
	/*private void validateCall() {
		
		if(TagItHelper.check3GNetwork(getApplicationContext()) || TagItHelper.checkWiFi(getApplicationContext()))
		{
			final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
					loginActionRequired();
				}
			};
		
			Thread checkUpdate = new Thread() {	
				public void run() {
					Looper.prepare();
					 try {
						 DateFormat df = DateFormat.getTimeInstance();
						 df.setTimeZone(TimeZone.getTimeZone("gmt"));
						 String gmtTime = df.format(new Date());
						 String un = sp.getString("username", "");
						 String pw = sp.getString("password", "");
						 TelephonyManager t1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
				  	       String udid = t1.getDeviceId();	
			    	        HttpClient client = new DefaultHttpClient();  
			    	        String postURL = "http://services.tagit.com.au/TGTService/login";
			    	        String hash=md5(un + pw + udid + gmtTime);
			    	        HttpPost post = new HttpPost(postURL);
			    	        List<NameValuePair> params = new ArrayList<NameValuePair>();
			    	            params.add(new BasicNameValuePair("username", un));
			    	            params.add(new BasicNameValuePair("password", pw));
			    	            params.add(new BasicNameValuePair("imei", udid));
			    	            params.add(new BasicNameValuePair("timestamp", gmtTime));
			    	            params.add(new BasicNameValuePair("md5", hash));
			    	            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			    	            post.setEntity(ent);
			    	            HttpResponse responsePOST = client.execute(post); 
			    	            HttpEntity resEntity = responsePOST.getEntity();  
			    	            if (resEntity != null) {    
			    	            	String res = EntityUtils.toString(resEntity);
			    	                LoginParser lp=new LoginParser();
			    	                lp.getRecords(res);
			    	            	handler.sendEmptyMessage(0);
			    	            }

			    	    } catch (Exception e) {
			    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBarcodeActivity.this);
	    	    			// Set the message to display
	    	    			alertbox.setMessage("Check your internet settings.");
	    	    			// Add a neutral button to the alert box and assign a click listener
	    	    			alertbox.setNeutralButton("Ok",
	    	    					new DialogInterface.OnClickListener() {

	    	    						public void onClick(DialogInterface arg0, int arg1) {

	    	    						}
	    	    					});
	    	    			alertbox.show();
			    	    }
				}
			};
			checkUpdate.start();
		}
		else
		{
			AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBarcodeActivity.this);
			// Set the message to display
			alertbox.setMessage("Check your internet settings.");
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
	
	private void loginActionRequired() {
		ArrayList<HashMap<String,String>> login=DataAdapters.getLoginResult();
		String result="";
		result=	login.get(0).get("validLogin");
		Log.i("RESULT",result.toString());
		if (result.equalsIgnoreCase("0")) {
			dialog.dismiss();
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			// Set the message to display
			alertbox.setMessage(login.get(0).get("retMessage"));
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
		else{
			item=DataAdapters.login.get(0);
			processTag(tagType);
			
		}
	}
	*/
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if  (e1.getX()-e2.getX() > 50 && Math.abs(velocityX) > 0) {    
          Intent intent = new Intent(MainBarcodeActivity.this, TagItConfig.class);   
          startActivity(intent);  
          overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
          Toast.makeText(this ,  "Settings" , Toast.LENGTH_SHORT).show();  
          finish();
        } else   if  (e2.getX()-e1.getX() > 50  
                && Math.abs(velocityX) > 0) {  
 
          Intent intent = new Intent(MainBarcodeActivity.this, Options.class);   
          startActivity(intent);   
          overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
          Toast.makeText(this ,  "Options" , Toast.LENGTH_SHORT).show();  
          finish();
        }  
          
        return   false ;     
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return  mGestureDetector.onTouchEvent(event);
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
}

