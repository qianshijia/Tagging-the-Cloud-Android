package com.apps.taggingthecloud;



import com.apps.taggingthecloud.R;

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
import android.widget.TextView;


public class TagCtl extends Activity implements OnClickListener {
	protected int _splashTime = 60000;
	protected boolean _active = true;
	Thread pingThread;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tag);
		startMonitoringPingService();
		 EditText et=(EditText)findViewById(R.id.txtQuestion);
		et.setText(DataAdapters.question.get(0).get("questionData").toString().trim());
		Button bt=(Button)findViewById(R.id.btnNext);
		bt.setOnClickListener(this);	
		TextView tv=(TextView)findViewById(R.id.lblQTitle);
		Log.i("Question Label",DataAdapters.question.get(0).get("questionLabel").toString().trim());
		tv.setText(DataAdapters.question.get(0).get("questionLabel").toString().trim());
		Button btnBarcode=(Button)findViewById(R.id.btnProductBarcode);
		Button btnQRcode=(Button)findViewById(R.id.btnQRBarcode);
		btnBarcode.setOnClickListener(this);
		 btnQRcode.setOnClickListener(this);
		
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
		pingThread.start();

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
			intent.setClass(TagCtl.this, MediaCtl.class);
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
			intent.setClass(TagCtl.this, Processor.class);
			intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
			intent.putExtra("currentView", "tag");
			startActivity(intent);
			finish();
		}		
		if(bt==R.id.btnProductBarcode){
			
			IntentIntegrator.initiateScan(TagCtl.this,"Barcode","Downlaod xZing","Yes","No","UPC_A,UPC_E,EAN_8,EAN_13,RSS14");
		}
		if(bt==R.id.btnQRBarcode){
			IntentIntegrator.initiateScan(TagCtl.this,"Barcode","Downlaod xZing","Yes","No","QR_CODE");
		}
	}
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(TagCtl.this);
			// Set the message to display
			alertbox.setMessage("Are you sure to quit this process?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					Intent intent = new Intent();
					intent.setClass(TagCtl.this, MainBarcodeActivity.class);
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