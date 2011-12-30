package com.apps.TagIt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class TextBoxCtl extends Activity implements OnClickListener {

	protected int _splashTime = 60000;
	protected boolean _active = true;
	Thread pingThread;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.textbox);
		EditText et=(EditText)findViewById(R.id.txtQuestion);
		startMonitoringPingService();
		et.setText(DataAdapters.question.get(0).get("questionData").toString().trim());
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
	public boolean numberValidation(String numberstring)
	{
		Pattern pattern=Pattern.compile("[0-9]*");
		Matcher matcher=pattern.matcher(numberstring);  
		if(!matcher.matches())       // on Success
			return false;
		else
			return true;
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
			intent.setClass(TextBoxCtl.this, MediaCtl.class);
			intent.putExtra("replay", true);
			startActivity(intent);
			finish();
		}
		if(bt==R.id.btnNext){
			if(pingThread.isAlive()){
				pingThread.interrupt();
			}
			if(DataAdapters.question.get(0).get("validationType").toString().trim().equalsIgnoreCase("blank^dd/mm/yyyy")){
				Log.i("validation in","blank^dd/mm/yyyy");
				String value=((EditText)findViewById(R.id.txtQuestion)).getText().toString().trim();
				Pattern datePattern = Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})");
				Matcher dateMatcher = datePattern.matcher(value);


				if(value==""){
					AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
					if (DataAdapters.question.get(0).get("validationMsg").toString().trim()=="")
						alertbox.setMessage("Field must be filled");
					else
						alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
				else if (!dateMatcher.matches()) {
					AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
					if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
						alertbox.setMessage("Please enter valid date format (dd/mm/yyyy)");
					else
						alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(TextBoxCtl.this, Processor.class);
					intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
					intent.putExtra("currentView", "textbox");
					startActivity(intent);
					finish();
				}
			}
			else if(DataAdapters.question.get(0).get("validationType").toString().trim().equalsIgnoreCase("dd/mm/yyyy")){
				Log.i("validation in","dd/mm/yyyy");
				String value=((EditText)findViewById(R.id.txtQuestion)).getText().toString().trim();
				Pattern datePattern = Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})");
				Matcher dateMatcher = datePattern.matcher(value);
				if(value!=""){

					if (!dateMatcher.matches()) {
						AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
						if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
							alertbox.setMessage("Please enter valid date format (dd/mm/yyyy)");
						else
							alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
				else{
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(TextBoxCtl.this, Processor.class);
					intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
					intent.putExtra("currentView", "textbox");
					startActivity(intent);
					finish();
				}
			}
			else if(DataAdapters.question.get(0).get("validationType").toString().trim().equalsIgnoreCase("blank^int")){
				String value=((EditText)findViewById(R.id.txtQuestion)).getText().toString().trim();
				Log.i("validation in","blank^int");
				Log.i("validation val====",value);
				if(value.equalsIgnoreCase("") || value==null){
					AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
					if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
						alertbox.setMessage("Field must be filled");
					else
						alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
				else if (numberValidation(value)==false) {
					AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
					if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
						alertbox.setMessage("Please enter valid number value.");
					else
						alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(TextBoxCtl.this, Processor.class);
					intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
					intent.putExtra("currentView", "textbox");
					startActivity(intent);
					finish();
				}
			}
			else if(DataAdapters.question.get(0).get("validationType").toString().trim().equalsIgnoreCase("int")){
				Log.i("validation in","int");
				String value=((EditText)findViewById(R.id.txtQuestion)).getText().toString();
				Log.i("value",value);
				if(value!=""){
					if (!numberValidation(value)) {
						AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
						if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
							alertbox.setMessage("Please enter valid number value.");
						else
							alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
						DataAdapters.question.clear();
						Intent intent=new Intent();
						intent.setClass(TextBoxCtl.this, Processor.class);
						intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
						intent.putExtra("currentView", "textbox");
						startActivity(intent);
						finish();
					}
				}
				else{
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(TextBoxCtl.this, Processor.class);
					intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
					intent.putExtra("currentView", "textbox");
					startActivity(intent);
					finish();
				}
			}
			else if(DataAdapters.question.get(0).get("validationType").toString().trim().equalsIgnoreCase("blank")){
				Log.i("validation in","blank");
				String value=((EditText)findViewById(R.id.txtQuestion)).getText().toString();
				if(value==""){
					AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
					if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
						alertbox.setMessage("Field must be filled.");
					else
						alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(TextBoxCtl.this, Processor.class);
					intent.putExtra("value", ((EditText)findViewById(R.id.txtQuestion)).getText().toString());
					intent.putExtra("currentView", "textbox");
					startActivity(intent);
					finish();
				}
			}
			else	
			{
				String value = ((EditText)findViewById(R.id.txtQuestion)).getText().toString();
				if(value.equalsIgnoreCase("") || value==null){
					AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
					if (DataAdapters.question.get(0).get("validationMsg").toString().trim().equalsIgnoreCase(""))
						alertbox.setMessage("Field must be filled");
					else
						alertbox.setMessage(DataAdapters.question.get(0).get("validationMsg").toString());
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
				else
				{
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(TextBoxCtl.this, Processor.class);
					intent.putExtra("value", value);
					intent.putExtra("currentView", "textbox");
					startActivity(intent);
					finish();
				}
			}
		}
	}		

	@Override    
	protected void onDestroy() {        
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(TextBoxCtl.this);
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
					intent.setClass(TextBoxCtl.this, MainBarcodeActivity.class);
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