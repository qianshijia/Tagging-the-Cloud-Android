package com.apps.taggingthecloud;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

public class TTCConfig extends Activity implements OnCheckedChangeListener, OnTouchListener, OnGestureListener
{
	public static final String SP_NAME = "Settings";
	
	private ToggleButton autoLogin;
	private ToggleButton dlMedia;
	private ToggleButton enhanceAction;
	private ToggleButton gpsPing;
	private SharedPreferences sp;
	private GestureDetector mGestureDetector;
	private ImageButton backBtn;
	private Button tagitBtn;
	private Button optionsBtn;
	private TTCConfig me;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
		setContentView(R.layout.config);
		me = this;
		
		tagitBtn = (Button)findViewById(R.id.tagit);
		tagitBtn.getBackground().setAlpha(0);
		tagitBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
				
			}
			
		});
		optionsBtn = (Button)findViewById(R.id.options);
		optionsBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(me, Options.class);
				startActivity(i);
				finish();				
			}
			
		});
		optionsBtn.getBackground().setAlpha(0);
		
		backBtn = (ImageButton)findViewById(R.id.back);
		backBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		autoLogin = (ToggleButton)findViewById(R.id.autologin);
		dlMedia = (ToggleButton)findViewById(R.id.dlmedia);
		enhanceAction = (ToggleButton)findViewById(R.id.enhance);
		gpsPing = (ToggleButton)findViewById(R.id.gpsping);
		sp = getSharedPreferences(SP_NAME, 0);
		autoLogin.setChecked(sp.getBoolean("auto_login", false));
		dlMedia.setChecked(sp.getBoolean("download_media", true));
		enhanceAction.setChecked(sp.getBoolean("enhance_action", true));
		gpsPing.setChecked(sp.getBoolean("gps_ping", true));
		
		autoLogin.setOnCheckedChangeListener(this);
		dlMedia.setOnCheckedChangeListener(this);
		enhanceAction.setOnCheckedChangeListener(this);
		gpsPing.setOnCheckedChangeListener(this);
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		Editor e = sp.edit();
		// TODO Auto-generated method stub
		if(id == R.id.autologin)
		{
			buttonView.setChecked(isChecked);
			e.putBoolean("auto_login", isChecked);
			e.commit();
		}
		else if(id == R.id.dlmedia)
		{
			buttonView.setChecked(isChecked);
			e.putBoolean("download_media", isChecked);
			e.commit();
		}
		else if(id == R.id.enhance)
		{
			buttonView.setChecked(isChecked);
			e.putBoolean("enhance_action", isChecked);
			e.commit();
		}
		else if(id == R.id.gpsping)
		{
			buttonView.setChecked(isChecked);
			e.putBoolean("gps_ping", isChecked);
			e.commit();
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if  (e1.getX()-e2.getX() > 50  
                && Math.abs(velocityX) > 0) {  
    
          Intent intent = new Intent(TTCConfig.this, Options.class);   
          startActivity(intent);  
          overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
          Toast.makeText(this ,  "Options" , Toast.LENGTH_SHORT).show(); 
          finish();
        } else   if  (e2.getX()-e1.getX() > 50  
                && Math.abs(velocityX) > 0) {  
                
          Intent intent = new Intent(TTCConfig.this, MainBarcodeActivity.class);   
          startActivity(intent);   
          overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
          Toast.makeText(this ,  "Scanner" , Toast.LENGTH_SHORT).show();  
          finish();
        }  
          
        return   false ;     
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return  mGestureDetector.onTouchEvent(event);
	}
}
