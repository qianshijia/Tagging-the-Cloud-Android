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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


public class ThankYouCtl extends Activity implements OnClickListener {
	protected int _splashTime = 60000;
	protected boolean _active = true;
	ImageButton backBtn;
	TextView processName;
	Thread pingThread;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.thankyou);
		startMonitoringPingService();
		Button bt=(Button)findViewById(R.id.btnNext);
		Button repeatBtn = (Button)findViewById(R.id.btnRepeat);
		if(!Question.questionnaireId.equals(""))
		{
			Button backToList = (Button)findViewById(R.id.btnBacktoList);
			backToList.setOnClickListener(this);
			backToList.setVisibility(View.VISIBLE);
		}
		repeatBtn.setOnClickListener(this);
		bt.setOnClickListener(this);	
		TextView tv=(TextView)findViewById(R.id.lblQTitle);
		//Log.i("Question Label",DataAdapters.question.get(0).get("questionLabel").toString().trim());
		//tv.setText(DataAdapters.question.get(0).get("questionLabel").toString().trim());
		tv.setText("Process Finished! Thank You!");
		processName = (TextView)findViewById(R.id.processName);
		processName.setText(DataAdapters.getProcessName());
		
		backBtn = (ImageButton)findViewById(R.id.back);
		backBtn.setOnClickListener(this);
		/*Button btReview=(Button)findViewById(R.id.btnReview);
		btReview.setOnClickListener(this);
		if(DataAdapters.question.get(0).get("mediaData").toString().trim().contains("img:")){
			btReview.setText("Reveiw Image");
			btReview.setVisibility(0);
		}
		if(DataAdapters.question.get(0).get("mediaData").toString().trim().contains("vid:")){
			btReview.setVisibility(0);
			btReview.setText("Replay Movie");
		}
		if(DataAdapters.question.get(0).get("mediaData").toString().trim().contains("aud:")){
			btReview.setVisibility(0);
			btReview.setText("Replay Recording");
		}*/
	}
	private void startMonitoringPingService()

	{
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				PingService.CallFrom="Other";
				PingService.runService(getApplicationContext(),"");
				//PerformRespectiveAction();
				//dialog.dismiss();
				if(pingThread.isAlive()){
					pingThread.interrupt();
				}
				startMonitoringPingService();
			}
		};
		pingThread = new Thread()
		{

			@Override
			public void run()
			{
				if(pingThread.isInterrupted()==false){
					int waited = 0;
						while(_active && (waited < _splashTime))
						{
							try{
							sleep(1000);
							}catch (InterruptedException  i)
							{
								return;
							}
							if(_active) 
							{
								waited += 1000;
								Log.i("Seconds-media",String.valueOf(waited));
							}
						}
						waited=0;
						handler.sendEmptyMessage(0);

				}
			}
		};
		//pingThread.start();

	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();	
		/*if(bt==R.id.btnReview){
			try{
				pingThread.interrupt();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			Intent intent=new Intent();
			intent.setClass(ThankYouCtl.this, MediaCtl.class);
			startActivity(intent);
			finish();
		}*/
		if(bt==R.id.btnNext){
			try{
				pingThread.interrupt();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			PingService.ignoreGpsPingResponses=false;
			DataAdapters.question.clear();
			finish();
		}
		if(bt==R.id.btnBacktoList){
			Intent myintent=new Intent(ThankYouCtl.this,QuestionsListActivity.class);	
		    startActivity(myintent);
		    finish();
		}
		if(bt == R.id.btnRepeat)
		{
			if(!Question.questionnaireId.equals(""))
			{
				Intent myintent=new Intent(ThankYouCtl.this,StartQuestions.class);	
			    startActivity(myintent);
			    finish();
			}
			else
			{
				final ProgressDialog dialog = ProgressDialog.show(this, null, null,true);
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						dialog.dismiss();
						Intent i = new Intent();
						i.setClass(ThankYouCtl.this, StartQuestion.class);
						startActivity(i);
						finish();
						
					}
				};
				Thread checkUpdate = new Thread() {

					public void run() {
							Looper.prepare();
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
							if(DataAdapters.login.get(0) == null)
							{
								hash = md5(""+ udid + gmtTime);
								token = "";
							}
							else
							{
								hash = md5(""+ udid + gmtTime + DataAdapters.login.get(0).get("token").toString());
								token = DataAdapters.login.get(0).get("token").toString();
							}
							HttpPost post = new HttpPost(postURL);
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("uid", DataAdapters.repeatTag));
							params.add(new BasicNameValuePair("imei", udid));
							params.add(new BasicNameValuePair("timestamp", gmtTime));
							params.add(new BasicNameValuePair("md5", hash));
							params.add(new BasicNameValuePair("token", token));
							params.add(new BasicNameValuePair("tagtype", DataAdapters.repeatTagType));
							
							Log.i("TAGGGGGGGG", DataAdapters.repeatTag);
							
							UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
							post.setEntity(ent);
							HttpResponse responsePOST = client.execute(post);  
							HttpEntity resEntity = responsePOST.getEntity();  
							String res = EntityUtils.toString(resEntity);
							if (resEntity != null) {    
								DataAdapters.question.clear();
								QuestionDetailParser qdp = new QuestionDetailParser();
								qdp.getRecords(res);
								handler.sendEmptyMessage(0);
							}
						} catch (Exception e) {
							//e.printStackTrace();
							AlertDialog.Builder alertbox = new AlertDialog.Builder(ThankYouCtl.this);
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
				};
				checkUpdate.start();
			}
		}
		if(bt == R.id.back)
		{
			DataAdapters.question.clear();
			DataAdapters.questions.clear();
			DataAdapters.clearProcessName();
			finish();
		}
	}
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
					//DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					DataAdapters.clearProcessName();
					finish();
	    }
		return false;
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