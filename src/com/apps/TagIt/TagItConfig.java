package com.apps.TagIt;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

public class TagItConfig extends Activity implements OnCheckedChangeListener, OnTouchListener, OnGestureListener
{
	public static final String SP_NAME = "Settings";
	
	private ToggleButton autoLogin;
	private ToggleButton dlMedia;
	private ToggleButton enhanceAction;
	private SharedPreferences sp;
	private EditText uName;
	private EditText pswd;
	private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  
		setContentView(R.layout.config);
		autoLogin = (ToggleButton)findViewById(R.id.autologin);
		dlMedia = (ToggleButton)findViewById(R.id.dlmedia);
		enhanceAction = (ToggleButton)findViewById(R.id.enhance);
		sp = getSharedPreferences(SP_NAME, 0);
		autoLogin.setChecked(sp.getBoolean("auto_login", false));
		dlMedia.setChecked(sp.getBoolean("download_media", true));
		enhanceAction.setChecked(sp.getBoolean("enhance_action", true));
		
		
		uName = (EditText)findViewById(R.id.uname);
		pswd = (EditText)findViewById(R.id.pswd);
		if(sp.getBoolean("auto_login", false) == false)
		{
			if(DataAdapters.login != null)
			{
					uName.setText(sp.getString("username", ""));
					pswd.setText(sp.getString("password", ""));
			}
		}
		else
		{
			uName.setText(sp.getString("username", ""));
			uName.setEnabled(false);
			pswd.setText(sp.getString("password", ""));
			pswd.setEnabled(false);
		}
		
		autoLogin.setOnCheckedChangeListener(this);
		dlMedia.setOnCheckedChangeListener(this);
		enhanceAction.setOnCheckedChangeListener(this);
		
		mGestureDetector = new  GestureDetector(this);
		ScrollView mScrollView = (ScrollView)findViewById(R.id.config_layout);
		mScrollView.setOnTouchListener(this);
		mScrollView.setLongClickable(true);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		Editor e = sp.edit();
		// TODO Auto-generated method stub
		if(id == R.id.autologin)
		{
			if ((!uName.getText().toString().equalsIgnoreCase("") && uName.getText().toString() != null)
					&& (!pswd.getText().toString().equalsIgnoreCase("") && pswd.getText().toString() != null))
			{
				buttonView.setChecked(isChecked);
				e.putBoolean("auto_login", isChecked);
				e.putString("username", uName.getText().toString());
				e.putString("password", pswd.getText().toString());
				e.commit();
				if(isChecked)
				{
					uName.setEnabled(false);
					pswd.setEnabled(false);
				}
				else
				{
					uName.setEnabled(true);
					pswd.setEnabled(true);
				}
			}
			else
			{
				buttonView.setChecked(!isChecked);
				if (uName.getText().toString().equalsIgnoreCase("") || uName.getText().toString() == null)
				{
					Toast.makeText(this, "Please enter your username", Toast.LENGTH_LONG).show();
				}
				else if(pswd.getText().toString().equalsIgnoreCase("") || pswd.getText().toString() == null)
				{
					Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
				}
			}
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
  
//          切换Activity   
          Intent intent = new Intent(TagItConfig.this, Options.class);   
          startActivity(intent);  
          overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
          Toast.makeText(this ,  "Options" , Toast.LENGTH_SHORT).show(); 
          finish();
        } else   if  (e2.getX()-e1.getX() > 50  
                && Math.abs(velocityX) > 0) {  
              
//          切换Activity   
          Intent intent = new Intent(TagItConfig.this, MainBarcodeActivity.class);   
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		    if (keyCode == KeyEvent.KEYCODE_BACK) {
		    	Intent intent = new Intent();
		    	intent.setClass(this, MainBarcodeActivity.class);
		    	startActivity(intent);
		    	finish();
		    }
		return false;
	}
}
