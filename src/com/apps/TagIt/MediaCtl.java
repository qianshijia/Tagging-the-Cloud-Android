package com.apps.TagIt;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class MediaCtl extends Activity implements OnClickListener {

	final static int REQUEST_VIDEO_CAPTURED = 1;
	Uri uriVideo = null;
	protected int _splashTime = 60000;
	protected boolean _active = true;
	VideoView videoviewPlay;
	File tmpFile;
	String drawable=null;
	Thread pingThread;
	TextView tv;
	SharedPreferences sp;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.media);
		startMonitoringPingService();
		Button buttonAudio = (Button)findViewById(R.id.btnPlayAudio);
		Button buttonVideo = (Button)findViewById(R.id.btnPlayVideo);
		videoviewPlay = (VideoView)findViewById(R.id.videoview);
		buttonAudio.setOnClickListener(this);
		buttonVideo.setOnClickListener(this);
		tv = (TextView)findViewById(R.id.lblMsg);

		Button bt=(Button)findViewById(R.id.btnNext);
		bt.setOnClickListener(this);	

		final ImageView img=(ImageView)findViewById(R.id.imgView);
		
		sp = getSharedPreferences(TagItConfig.SP_NAME, 0);
				
		if(DataAdapters.question.get(0).get("mediaData").toString().trim().contains("img:")){
			img.setVisibility(0);
			final ProgressDialog dialog = ProgressDialog.show(this, null, null, true);
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					Bitmap bmp=BitmapFactory.decodeFile(drawable);
					img.setImageBitmap(bmp);   
					dialog.dismiss();
				}
			}; 
			Thread checkUpdate = new Thread() {  
				public void run() {
					drawable = Download(DataAdapters.question.get(0).get("mediaData").toString().trim().substring(4), "img");

					handler.sendEmptyMessage(0);
				}
			};
			checkUpdate.start();	
		}
		if(DataAdapters.question.get(0).get("mediaData").toString().trim().contains("vid:")){
			tv.setText(getString(R.string.video_page_msg));
			tv.setVisibility(0);
			buttonVideo.setVisibility(0);
		}
		if(DataAdapters.question.get(0).get("mediaData").toString().trim().contains("aud:")){
			tv.setText(getString(R.string.audio_page_msg));
			tv.setVisibility(0);
			buttonAudio.setVisibility(0);
		}


		//img.setImageDrawable(drawable)
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
	public String Download(String Url, String mediaType)
	{
		String filepath=null;
		try {
			URL url = new URL(Url);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			File SDCardRoot = Environment.getExternalStorageDirectory();
			
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String dir = SDCardRoot.getPath() + File.separator + getString(R.string.app_name) + File.separator + DataAdapters.question.get(0).get("questionId");
			File dlDir = new File(dir);
			if(!dlDir.exists())
			{
				dlDir.mkdirs();
			}
			
			String filename= "DownloadedFile";
			if(mediaType.equals("img"))
			{
				filename +=".jpg";
			}
			else if(mediaType.equals("vid"))
			{
				filename +=".3gp";
			}
			else if(mediaType.equals("aud"))
			{
				filename +=".mp3";
			}
			Log.i("Local filename:",""+filename);
			File file = new File(dir,filename);
			if(file.createNewFile())
			{
				file.createNewFile();
			}
			FileOutputStream fileOutput = new FileOutputStream(file);
			InputStream inputStream = urlConnection.getInputStream();
			int totalSize = urlConnection.getContentLength();
			int downloadedSize = 0;
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;

			}
			fileOutput.close();
			if(downloadedSize==totalSize) 
				filepath=file.getPath();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			filepath=null;
			e.printStackTrace();
		}
		return filepath;

	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();		
		if(bt==R.id.btnNext){
			//if(pingThread.isAlive()){
			try{
				pingThread.interrupt();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			//}
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("textbox")){
				Intent intent=new Intent();
				intent.setClass(MediaCtl.this, TextBoxCtl.class);
				startActivity(intent);
				finish();
			}
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("radio")){
				Intent intent=new Intent();
				intent.setClass(MediaCtl.this, RadioCtl.class);
				startActivity(intent);
				finish();
			}
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("checkbox")){
				Intent intent=new Intent();
				intent.setClass(MediaCtl.this, CheckBoxCtl.class);
				startActivity(intent);
				finish();
			}
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("tag")){
				Intent intent=new Intent();
				intent.setClass(MediaCtl.this, TagCtl.class);
				startActivity(intent);
				finish();
			}
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photo")){
				Intent intent=new Intent();
				intent.setClass(MediaCtl.this, ImageCtl.class);
				startActivity(intent);
				finish();
			}
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:video")){
				Intent intent=new Intent();
				intent.setClass(MediaCtl.this, VideoCtl.class);
				startActivity(intent);
				finish();
			}	
		}		
		if(bt==R.id.btnPlayVideo){
			if(sp.getBoolean("download_media", true))
			{
				if(drawable == null && !getIntent().getBooleanExtra("replay", false))
				{
					final ProgressDialog dialog = ProgressDialog.show(this, null, "Downloading...", true);
					final Handler handler = new Handler() {
						public void handleMessage(Message msg) {
							tmpFile = new File(drawable);
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setDataAndType(Uri.fromFile(tmpFile),"video/*");
							startActivity(i);
							dialog.dismiss();
						}
					}; 
					Thread checkUpdate = new Thread() {  
						public void run() {
							drawable = Download(DataAdapters.question.get(0).get("mediaData").toString().trim().substring(4), "vid");
							handler.sendEmptyMessage(0);
						}
					};
					checkUpdate.start();
				}
				else
				{
					String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator 
							+ getString(R.string.app_name) + File.separator + DataAdapters.question.get(0).get("questionId");
					File dir = new File(filePath);
					File[] files = dir.listFiles();
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.fromFile(files[0]),"video/*");
					startActivity(i);
				}
			}
			else
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse(DataAdapters.question.get(0).get("mediaData").toString().trim().substring(4)),"video/*");
				startActivity(i);
			}
		}
		if(bt==R.id.btnPlayAudio){
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse(DataAdapters.question.get(0).get("mediaData").toString().trim().substring(4)),"audio/*");
			startActivity(i);
		}
		if(bt==R.id.playback){
			if(tmpFile == null){
				Toast.makeText(MediaCtl.this, 
						"No Video", 
						Toast.LENGTH_LONG)
						.show();
			}else{
				uriVideo=Uri.fromFile(tmpFile);
				Log.i("URI",uriVideo.getPath());
				videoviewPlay.setVideoURI(uriVideo);
				videoviewPlay.start();
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
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(MediaCtl.this);
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
					intent.setClass(MediaCtl.this, MainBarcodeActivity.class);
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