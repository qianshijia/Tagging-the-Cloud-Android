package com.apps.TagIt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UrlCtl extends Activity implements OnClickListener
{

	private TextView tv;
	private Button nextBtn;
	protected int _splashTime = 60000;
	protected boolean _active = true;
	Thread pingThread;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hidden);
		
		tv = (TextView)findViewById(R.id.lblQTitle);
		tv.setText(DataAdapters.question.get(0).get("questionLabel").toString().trim());
		
		nextBtn = (Button)findViewById(R.id.btnNext);
		nextBtn.setOnClickListener(this);
		
		Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(DataAdapters.question.get(0).get("questionData").toString().trim()));
		startActivity(intent);
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
							Log.i("Seconds",String.valueOf(waited));
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.btnNext)
		{
			DataAdapters.question.clear();
			Intent intent=new Intent();
			intent.setClass(UrlCtl.this, Processor.class);
			intent.putExtra("value", "");
			intent.putExtra("currentView", "hidden");
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(UrlCtl.this);
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
					intent.setClass(UrlCtl.this, MainBarcodeActivity.class);
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
