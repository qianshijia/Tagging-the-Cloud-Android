package com.apps.taggingthecloud;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ImageCtl extends Activity implements OnClickListener {
	private static final int FROM_CAMERA = 11;
	private static final int FROM_Gallary = 12;
	private Thread pingThread;
	protected int _splashTime = 60000;
	protected boolean _active = true;
	private Bitmap thumbnail;
	private String picPath;
	private Button btn, btn1, bt, chooseAgBtn;
	private AlphaAnimation OUT = new AlphaAnimation(1.0f, 0.0f);
	private AlphaAnimation IN = new AlphaAnimation(0.0f, 1.0f);
	private TextView processName;
	private ImageButton backBtn;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image);
		startMonitoringPingService();
		btn=(Button)findViewById(R.id.btnCapture);
	    btn.setOnClickListener(this);
	    btn1=(Button)findViewById(R.id.btnOpen);
	    btn1.setOnClickListener(this);
		bt=(Button)findViewById(R.id.btnNext);
		bt.setOnClickListener(this);	
		chooseAgBtn = (Button)findViewById(R.id.btnChooseAgain);
		chooseAgBtn.setOnClickListener(this);
		TextView tv=(TextView)findViewById(R.id.lblQTitle);
		Log.i("Question Label",DataAdapters.question.get(0).get("questionLabel").toString().trim());
		Log.i("size", DataAdapters.question.size()+"");
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
		OUT.setDuration(350);
		IN.setDuration(500);
		
		processName = (TextView)findViewById(R.id.processName);
		processName.setText(DataAdapters.getProcessName());
		
		backBtn = (ImageButton)findViewById(R.id.back);
		backBtn.setOnClickListener(this);
		
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
		 pingThread = new Thread()
		{
		
			@Override
			public void run()
			{
				 // Looper.prepare();
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
			intent.setClass(ImageCtl.this, MediaCtl.class);
			intent.putExtra("replay", true);
			startActivity(intent);
			finish();
		}
		if(bt==R.id.btnNext){
			if(thumbnail == null)
			{
				Toast.makeText(this, "Please Take a Picture or Select One From Gallary!", Toast.LENGTH_LONG).show();
			}
			else
			{
				if(TTCHelper.check3GNetwork(getApplicationContext()) || TTCHelper.checkWiFi(getApplicationContext()))
				{
					if(pingThread.isAlive()){
						pingThread.interrupt();
					}
					DataAdapters.question.clear();
					Intent intent=new Intent();
					intent.setClass(ImageCtl.this, Processor.class);
					intent.putExtra("value",thumbnail);
					intent.putExtra("currentView", "action:photo");
					intent.putExtra("path", picPath);
					startActivity(intent);
					finish();
				}
				else
				{
					AlertDialog.Builder alertbox = new AlertDialog.Builder(ImageCtl.this);
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
		}		
		if(bt==R.id.btnCapture)
		{			
			//Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
            //Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			Intent intent = new Intent(ImageCtl.this, CustomCamera.class);
			startActivityForResult(intent, FROM_CAMERA);
		}
		if(bt==R.id.btnOpen)
		{
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), FROM_Gallary);

		}
		if(bt==R.id.btnChooseAgain)
		{
			btn.setVisibility(View.VISIBLE);
	        btn1.setVisibility(View.VISIBLE);
	        chooseAgBtn.setVisibility(View.GONE);
	        btn.startAnimation(IN);
	        btn1.startAnimation(IN);
	        chooseAgBtn.startAnimation(OUT);
	        ((ImageView)findViewById(R.id.ImageView02)).setImageBitmap(((BitmapDrawable)this.getResources().getDrawable(R.drawable.image_default)).getBitmap());
		}
		if(bt == R.id.back)
		{
			AlertDialog.Builder alertbox = new AlertDialog.Builder(ImageCtl.this);
			// Set the message to display
			alertbox.setMessage("Are you sure to quit this process?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//DataAdapters.login.clear();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
		        if (requestCode == FROM_Gallary)
		        {
		        	Uri selectedImageUri = data.getData();
		        	String[] projection = { MediaStore.Images.Media.DATA };
		            Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
		            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		            cursor.moveToFirst();
		            BitmapFactory.Options bfo = new BitmapFactory.Options();
		            bfo.inSampleSize = 8;
		            String path=cursor.getString(column_index);
		           
		            thumbnail = BitmapFactory.decodeFile(path, bfo);
		            Log.i("path", path);
		            ((ImageView)findViewById(R.id.ImageView02)).setImageBitmap(thumbnail);
		            findViewById(R.id.ImageView02).setVisibility(0);
		 		  
		        }
		        else if (requestCode == FROM_CAMERA)
		        {
		        	Bundle b = data.getExtras();
		        	thumbnail = (Bitmap)b.get("data");
		        	picPath = b.getString("path");
		        	((ImageView)findViewById(R.id.ImageView02)).setImageBitmap(thumbnail);
		        	findViewById(R.id.ImageView02).setVisibility(0);
		  
		        }
		        btn.setVisibility(View.GONE);
		        btn1.setVisibility(View.GONE);
		        chooseAgBtn.setVisibility(View.VISIBLE);
		        btn.startAnimation(OUT);
		        btn1.startAnimation(OUT);
		        chooseAgBtn.startAnimation(IN);
			}
	}
	
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ImageCtl.this);
			// Set the message to display
			alertbox.setMessage("Are you sure to quit this process?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//DataAdapters.login.clear();
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