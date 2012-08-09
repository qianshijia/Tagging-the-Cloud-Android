package com.apps.taggingthecloud;

import java.util.ArrayList;
import java.util.HashMap;

import com.apps.taggingthecloud.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class CheckBoxCtl extends ListActivity implements OnClickListener {
	 ArrayList<HashMap<String,String>> chkitems;
	 private CheckBoxifiedTextListAdapter cbla;
		
		protected int _splashTime = 60000;
		protected boolean _active = true;
		ImageButton backBtn;
		TextView processName;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.checkbox);
		processName = (TextView)findViewById(R.id.processName);
		processName.setText(DataAdapters.getProcessName());
		
		backBtn = (ImageButton)findViewById(R.id.back);
		backBtn.setOnClickListener(this);
		
		String splitter1 = "\\^";
    	String splitter2 = "\\~";
    	 String st=DataAdapters.question.get(0).get("questionData").toString().trim();
        String arr[]=st.split(splitter1);
        HashMap<String,String> item;
        //startMonitoringPingService();
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
        cbla.addItem(new CheckBoxifiedText(arr2[1], false));
        }
        setListAdapter(cbla);
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
	private void startMonitoringPingService()

	{
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				PingService.CallFrom="Other";
				PingService.runService(getApplicationContext(),"");
				//PerformRespectiveAction();
				//dialog.dismiss();
				startMonitoringPingService();
			}
		};
		Thread splashTread = new Thread()
		{
		
			@Override
			public void run()
			{
				 // Looper.prepare();
				int waited = 0;
				try {
					
					while(_active && (waited < _splashTime))
					{

						sleep(1000);
						if(_active) 
						{
							waited += 1000;
							Log.i("Seconds",String.valueOf(waited));
						}
						
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				finally
				{    
					
					waited=0;
					handler.sendEmptyMessage(0);
				
				}
			}
		};
		//splashTread.start();

	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();		
		if(bt==R.id.btnReview){
			Intent intent=new Intent();
			intent.setClass(CheckBoxCtl.this, MediaCtl.class);
			intent.putExtra("replay", true);
			startActivity(intent);
			finish();
		}
		if(bt==R.id.btnNext){
			DataAdapters.question.clear();
			Intent intent=new Intent();
			intent.setClass(CheckBoxCtl.this, Processor.class);
			
			 ArrayList<String> sll=	cbla.getListItems();
			 ArrayList<String> val=new ArrayList<String>();
      		for(String s :sll){
      			
      			for(HashMap<String,String> it:chkitems){
      				if(s.trim().equalsIgnoreCase(it.get("title").trim().toString())){
      					Log.i("equal and value is==",it.get("value").trim().toString());
      					 val.add(String.valueOf(it.get("value").trim().toString()));
      				}
      			}
      		}
      		intent.putExtra("value", val);
			intent.putExtra("currentView", "checkbox");
			startActivity(intent);
			finish();
		}	
		if(bt == R.id.back)
		{
			AlertDialog.Builder alertbox = new AlertDialog.Builder(CheckBoxCtl.this);
			// Set the message to display
			alertbox.setMessage("Do you want quit TagIt?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					PingService.stopService();
					DataAdapters.clearProcessName();
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
	}
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(CheckBoxCtl.this);
			// Set the message to display
			alertbox.setMessage("Do you want quit TagIt?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					PingService.stopService();
					DataAdapters.clearProcessName();
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