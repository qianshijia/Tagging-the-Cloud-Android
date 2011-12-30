package com.apps.TagIt;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class VideoCtl extends Activity implements OnClickListener {

	final static int REQUEST_VIDEO_CAPTURED = 1;
	Uri uriVideo = null;
	VideoView videoviewPlay;
	File tmpFile;
	Button buttonPlayback;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video);
		
		 Button buttonRecording = (Button)findViewById(R.id.recording);
	      buttonPlayback = (Button)findViewById(R.id.playback);
	      videoviewPlay = (VideoView)findViewById(R.id.videoview);
	      buttonRecording.setOnClickListener(this);
	      buttonPlayback.setOnClickListener(this);
	      
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
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();	
		if(bt==R.id.btnReview){
			Intent intent=new Intent();
			intent.setClass(VideoCtl.this, MediaCtl.class);
			intent.putExtra("replay", true);
			startActivity(intent);
			finish();
		}
		if(bt==R.id.btnNext){
			if(tmpFile == null)
			{
				Toast.makeText(this, "Please Record a Video!", Toast.LENGTH_LONG).show();
			}
			else
			{
				DataAdapters.question.clear();
				Intent intent=new Intent();
				intent.setClass(VideoCtl.this, Processor.class);
				uriVideo=Uri.fromFile(tmpFile);
				intent.putExtra("value",uriVideo.getPath());
				intent.putExtra("currentView", "action:video");
				startActivity(intent);
				finish();
			}
		}		
		if(bt==R.id.recording){
		        Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE );
		        startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
		}
		if(bt==R.id.playback){
			if(tmpFile == null){
				Toast.makeText(VideoCtl.this, 
						"No Video", 
						Toast.LENGTH_LONG)
						.show();
			}else{
				 uriVideo=Uri.fromFile(tmpFile);
//				Log.i("URI",uriVideo.getPath());
//				videoviewPlay.setVideoURI(uriVideo);
//				videoviewPlay.start();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(uriVideo,"video/*");
				startActivity(i);
			}

		}
	}
	
	  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("requestCode", String.valueOf(RESULT_OK));
		Log.i("requestCode", String.valueOf(REQUEST_VIDEO_CAPTURED));
			
		  if(resultCode == RESULT_OK){
			  if(requestCode == REQUEST_VIDEO_CAPTURED){
				  buttonPlayback.setVisibility(View.VISIBLE);
		  try {
			  
		    AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
		    FileInputStream fis = videoAsset.createInputStream();
		    String filename;
            Date date = new Date(0);
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
              filename =  sdf.format(date);
		     tmpFile = new File(Environment.getExternalStorageDirectory(),filename + ".3gp"); 
		    
		    FileOutputStream fos = new FileOutputStream(tmpFile);

		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = fis.read(buf)) > 0) {
		        fos.write(buf, 0, len);
		    }       
		    fis.close();
		    fos.close();
		  } catch (IOException io_e) {
		    // TODO: handle error
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
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(VideoCtl.this);
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
					intent.setClass(VideoCtl.this, MainBarcodeActivity.class);
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