package com.apps.TagIt;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class RadioCtl extends Activity implements OnClickListener,RadioGroup.OnCheckedChangeListener {
	 ArrayList<HashMap<String,String>> items;
	 protected int _splashTime = 60000;
	protected boolean _active = true;
	Thread pingThread;
	 private int optionVal;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.options);
		startMonitoringPingService();
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
        
        
		Button bt=(Button)findViewById(R.id.btnNext);
		bt.setOnClickListener(this);	
		TextView tv=(TextView)findViewById(R.id.lblQTitle);
		Log.i("Question Label",DataAdapters.question.get(0).get("questionLabel").toString().trim());
		tv.setText(DataAdapters.question.get(0).get("questionLabel").toString().trim());
		
		
		Button btReview=(Button)findViewById(R.id.btnReview);
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
		}
        }
		
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
		if(bt==R.id.btnReview){
			try{
				pingThread.interrupt();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			Intent intent=new Intent();
			intent.setClass(RadioCtl.this, MediaCtl.class);
			intent.putExtra("replay", true);
			startActivity(intent);
			finish();
		}
		if(bt==R.id.btnNext){
			try{
				pingThread.interrupt();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			DataAdapters.question.clear();
			Intent intent=new Intent();
			intent.setClass(RadioCtl.this, Processor.class);
			intent.putExtra("value", String.valueOf(optionVal));
			intent.putExtra("currentView", "radio");
			startActivity(intent);
			finish();
		}		
	}
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
        Log.i("Value checked",String.valueOf(checkedId));
        optionVal=checkedId;
        //optionVal=
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(RadioCtl.this);
			// Set the message to display
			alertbox.setMessage("Are you sure to quit this process?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					PingService.stopService();
					Intent intent = new Intent();
					intent.setClass(RadioCtl.this, MainBarcodeActivity.class);
					startActivity(intent);
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

	
}