package com.apps.TagIt;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;

import android.media.MediaRecorder;

public class AudioCtl extends Activity implements OnClickListener {
	private static  Uri fname;
	protected static TextBoxCtl me;
	protected int _splashTime = 60000;
	protected boolean _active = true;
	private Thread pingThread;
	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".mp3";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	private AudioRecord recorder = null;
	private int bufferSize = 0;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	final static int REQUEST_AUDIO_CAPTURED = 1;
	Uri uriAudio = null;
	File tmpFile;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio);
		//startMonitoringPingService();
		
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
		 setButtonHandlers();
	        enableButtons(false);
	        
	        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
	        enableButton(R.id.btnPlay, false);
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
		pingThread.start();

	}

	private void setButtonHandlers() {
		((Button)findViewById(R.id.btnStart)).setOnClickListener(AudioCtl.this);
        ((Button)findViewById(R.id.btnStop)).setOnClickListener(AudioCtl.this);
        ((Button)findViewById(R.id.btnPlay)).setOnClickListener(AudioCtl.this);
	}
	
	private void enableButton(int id,boolean isEnable){
		((Button)findViewById(id)).setEnabled(isEnable);
	}
	
	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart,!isRecording);
		enableButton(R.id.btnStop,isRecording);
	
	}
	
	private String getFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);
		
		if(!file.exists()){
			file.mkdirs();
		}
		File f2=new File(file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
		
		fname=Uri.fromFile(f2);
		return (fname.getPath().toString());
	}
	
	private String getTempFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
		
		if(tempFile.exists())
			tempFile.delete();
		
		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}
	
	private void startRecording(){
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
		
		recorder.startRecording();
		
		isRecording = true;
		
		recordingThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				writeAudioDataToFile();
			}
		},"AudioRecorder Thread");
		
		recordingThread.start();
	}
	
	private void writeAudioDataToFile(){
		byte data[] = new byte[bufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;
		
		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int read = 0;
		
		if(null != os){
			while(isRecording){
				read = recorder.read(data, 0, bufferSize);
				
				if(AudioRecord.ERROR_INVALID_OPERATION != read){
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void stopRecording(){
		if(null != recorder){
			isRecording = false;
			
			recorder.stop();
			recorder.release();
			
			recorder = null;
			recordingThread = null;
		}
		
		copyWaveFile(getTempFilename(),getFilename());
		deleteTempFile();
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());
		
		file.delete();
	}
	
	private void copyWaveFile(String inFilename,String outFilename){
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
		
		byte[] data = new byte[bufferSize];
                
		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			
			
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);
			
			while(in.read(data) != -1){
				out.write(data);
			}
			
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(
			FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels,
			long byteRate) throws IOException {
		
		byte[] header = new byte[44];
		
		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;  // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8);  // block align
		header[33] = 0;
		header[34] = RECORDER_BPP;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
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
			intent.setClass(AudioCtl.this, MediaCtl.class);
			startActivity(intent);
			finish();
		}
		if(bt==R.id.btnNext){
			if(pingThread.isAlive()){
				pingThread.interrupt();
			}
			DataAdapters.question.clear();
			Intent intent=new Intent();
			intent.setClass(AudioCtl.this, Processor.class);
			intent.putExtra("value",fname.getPath());
			intent.putExtra("currentView", "action:audio");
			startActivity(intent);
			
			finish();
		}		
		if(bt==R.id.btnStart){
			enableButton(R.id.btnPlay, false);
			enableButtons(true);
			startRecording();
		}
		if(bt==R.id.btnStop){
			enableButtons(false);
			stopRecording();
			enableButton(R.id.btnPlay, true);
		}
		if(bt==R.id.btnPlay){
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(fname,"audio/*");
			startActivity(i);
		}
		
	}
	
	  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("requestCode", String.valueOf(RESULT_OK));
		Log.i("requestCode", String.valueOf(REQUEST_AUDIO_CAPTURED));
		  if(resultCode == RESULT_OK){
			  if(requestCode == REQUEST_AUDIO_CAPTURED){
		  try {
		    AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
		    FileInputStream fis = videoAsset.createInputStream();
		    String filename;
            Date date = new Date(0);
            SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
              filename =  sdf.format(date);
		     tmpFile = new File(Environment.getExternalStorageDirectory(),filename + ".wav"); 
		    
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
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(AudioCtl.this);
			// Set the message to display
			alertbox.setMessage("Do you want quit TagIt?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
					PingService.stopService();
					Intent intent = new Intent();
					intent.setClass(AudioCtl.this, MainBarcodeActivity.class);
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