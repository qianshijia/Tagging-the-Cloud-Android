package com.apps.taggingthecloud;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CustomCamera extends Activity implements SurfaceHolder.Callback, OnClickListener, SensorListener, AutoFocusCallback
{
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private Boolean mPreviewRunning;
	private Button okBtn;
	private Button cancelBtn;
	private Button retakeBtn;
	private LayoutInflater mLayoutInflater;
	private ImageView iv;
	private boolean isPortrait = true;
	protected SensorManager sensorManager;
	protected Sensor sensor;
	private ImageButton captureBtn;
	private float yAcc, zAcc;
	private Bitmap newImage, thumbNail;
	private String picPath, overlayImgPath;
	private Drawable drawable;
	
	private static AlphaAnimation OUT = new AlphaAnimation(1.0f, 0.0f);
	private static AlphaAnimation IN = new AlphaAnimation(0.0f, 1.0f);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		setContentView(R.layout.customcamera);
		mPreviewRunning = false;
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		mSurfaceView = (SurfaceView)findViewById(R.id.surface_camera);
		//mSurfaceView.setOnClickListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		captureBtn = (ImageButton)findViewById(R.id.capture);
		captureBtn.setOnClickListener(this);
		
		okBtn = (Button)findViewById(R.id.okbtn);
		cancelBtn = (Button)findViewById(R.id.cancelbtn);
		retakeBtn = (Button)findViewById(R.id.retakebtn);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		retakeBtn.setOnClickListener(this);
		mLayoutInflater = LayoutInflater.from(getBaseContext());
		View v = mLayoutInflater.inflate(R.layout.overlay_img, null);
		LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(v, layoutParamsControl);
		iv = (ImageView)findViewById(R.id.crossimg);
		
		
		if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photooverlay"))
		{
			final ProgressDialog dialog = ProgressDialog.show(this, null, null,true);
			dialog.setTitle("...");
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					dialog.dismiss();
					try
					{
					File file = new File(overlayImgPath);
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					Bitmap bm = BitmapFactory.decodeStream(bis);
					bis.close(); 
					drawable = new BitmapDrawable(bm);
					iv.setImageDrawable(drawable);
					}
					catch(Exception e)
					{
						
					}
					
				}
			};
			Thread downloadThread = new Thread()
			{
			
				@Override
				public void run()
				{
					Looper.prepare();
					try
					{
						String imgUrl = DataAdapters.question.get(0).get("mediaLength");
						URL url = new URL(imgUrl);
						HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
						urlConnection.setRequestMethod("GET");
						urlConnection.setDoOutput(true);
						urlConnection.connect();
						
						File SDCardRoot = Environment.getExternalStorageDirectory();
						

						String dir = SDCardRoot.getPath() + "/" + getString(R.string.app_name) + "/OverlayImage";
						File dlDir = new File(dir);
						if(!dlDir.exists())
						{
							dlDir.mkdirs();
						}
						
						String filename= "OverlayImg.png";

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
							overlayImgPath=file.getPath();
					}
					catch(Exception e)
					{
						
					}
					handler.sendEmptyMessage(0);
					
					
				}
			};
			downloadThread.start();
			
		}

		OUT.setDuration(350);
		IN.setDuration(500);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try
		{
		 	mCamera = Camera.open();
		 	mCamera.setDisplayOrientation(90);
		 	Camera.Parameters parameters = mCamera.getParameters();
		 	List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		    List<Camera.Size> picturewSizes = parameters.getSupportedPictureSizes();
		    parameters.setPreviewSize(previewSizes.get(1).width, previewSizes.get(1).height);
		    parameters.setPreviewFrameRate(3);
		    parameters.setPictureFormat(PixelFormat.JPEG);
		    parameters.set("jpeg-quality", 85);
		    parameters.setPictureSize(picturewSizes.get(1).width, picturewSizes.get(1).height);  
		    mCamera.setParameters(parameters);
		    mCamera.setPreviewDisplay(mSurfaceView.getHolder());
		    mCamera.startPreview();
		    mPreviewRunning = true;
		}
		catch (IOException e) {
		   
		   }
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		sensorManager.registerListener(this, 
        		SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		sensorManager.unregisterListener(this);
		super.onStop();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mPreviewRunning=false;
		mCamera.release();
	}
	
	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				
			//Bitmap newImage = Bitmap.createBitmap(cameraBitmap.getWidth(), cameraBitmap.getHeight(), Bitmap.Config.ARGB_8888);
			
			Matrix matrix = new Matrix();
			matrix.postScale(1024f/cameraBitmap.getWidth(), 768f/cameraBitmap.getHeight());
			Log.i("width",cameraBitmap.getWidth()+"");
			Log.i("height",cameraBitmap.getHeight()+"");
			if(isPortrait)
			{
				matrix.postRotate(90);
				
			}
			newImage = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), matrix, true);	
			
			if(DataAdapters.question.get(0).get("questionType").toString().trim().equalsIgnoreCase("action:photooverlay"))
			{
				Canvas canvas = new Canvas(newImage);
					
				canvas.drawBitmap(newImage, 0f, 0f, null);
					 
				Bitmap temp = ((BitmapDrawable)drawable).getBitmap();
				drawable.setBounds((newImage.getWidth() - temp.getWidth())/2, 
						(newImage.getHeight() - temp.getHeight())/2, 
						(newImage.getWidth() + temp.getWidth())/2, 
						(newImage.getHeight() + temp.getHeight())/2);
				drawable.draw(canvas);
			}
			Matrix m = new Matrix();
			m.postScale(0.2f, 0.2f);
			thumbNail = Bitmap.createBitmap(newImage, 0, 0, newImage.getWidth(), newImage.getHeight(), m, true);
			//camera.startPreview();
				
//			drawable = null;	
//			cameraBitmap.recycle();
//			cameraBitmap = null;
							
			}
	};
	
	private void resetCamera()
	{
		if(mCamera != null && mPreviewRunning)
		{
			mCamera.startPreview();
			mCamera.release();
			mCamera = null;
			mPreviewRunning = false;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.capture:
				mCamera.autoFocus(this);
				//iv.setVisibility(View.GONE);
				captureBtn.startAnimation(OUT);
				captureBtn.setVisibility(View.GONE);				
				
				break;
			case R.id.okbtn:
				//mCamera.startPreview();
				captureBtn.setVisibility(View.VISIBLE);
				//iv.setVisibility(View.VISIBLE);
				okBtn.setVisibility(View.GONE);			
				retakeBtn.setVisibility(View.GONE);
				savePicture();
				newImage.recycle();
				newImage = null;
				
				Intent intent = new Intent();
				intent.putExtra("path", picPath);
				intent.putExtra("data", thumbNail);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.retakebtn:
				mCamera.startPreview();
				captureBtn.startAnimation(IN);
				captureBtn.setVisibility(View.VISIBLE);
				//iv.setVisibility(View.VISIBLE);
				okBtn.startAnimation(OUT);
				okBtn.setVisibility(View.GONE);
				retakeBtn.setVisibility(View.GONE);
				retakeBtn.startAnimation(OUT);
				break;
			case R.id.cancelbtn:
				finish();
				break;
		}
		
	}
	
	private void savePicture()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
		String date = sdf.format(new Date());
		String path = Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.app_name) + "/" + date.substring(0, 8);
		File dir = new File(path);
		if(!dir.isDirectory())
		{
			dir.mkdirs();
		}
		File pic = new File(dir, date.substring(8) + ".jpg");
			
		try
		{
			FileOutputStream out = new FileOutputStream(pic);
			newImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
			
			out.flush();
			out.close();
		}
		catch(FileNotFoundException e)
		{
			Log.d("In Saving File", e + "");    
		}
		catch(IOException e)
		{
			Log.d("In Saving File", e + "");
		}
		picPath = pic.getPath();
	}


	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		synchronized (this) {
	        yAcc = values[1];
	        zAcc = values[2];
            
            if(yAcc < -4.5)
            {
            	isPortrait = true;
            	
            }
            else
            {
            	if(zAcc< -4.5)
            	{
            		isPortrait = true;
            	}
            	else
            	{
            		isPortrait = false;
            	}
            }
        }
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		if(success)
		{
			camera.takePicture(null, null, mPictureCallback);
			okBtn.setVisibility(View.VISIBLE);
			okBtn.startAnimation(IN);
			retakeBtn.setVisibility(View.VISIBLE);
			retakeBtn.startAnimation(IN);
		}
	}
	
	

}
