package com.apps.taggingthecloud;

import java.util.ArrayList;
import java.util.HashMap;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Options extends Activity implements OnItemClickListener, OnTouchListener, OnGestureListener
{
	private GridView gv;
	private GestureDetector mGestureDetector;
	private Button tagitBtn;
	private Button settingsBtn;
	private Options me;
	private ImageButton backBtn;
	
	private SharedPreferences sp;
	
	private String[] itemText = new String[]{"Register", "Profile", "History", "Settings"};
	private int[] itemImg = new int[]{R.drawable.login, R.drawable.tag_create, R.drawable.login, R.drawable.settings};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.homepage);
		}
		else
		{
			setContentView(R.layout.homepage_ls);
		}
		sp = getSharedPreferences(TTCConfig.SP_NAME, 0);
		me = this;
		
		tagitBtn = (Button)findViewById(R.id.tagit);
		tagitBtn.getBackground().setAlpha(0);
		tagitBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
				
			}
			
		});
		settingsBtn = (Button)findViewById(R.id.settings);
		settingsBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(me, TTCConfig.class);
				startActivity(i);
				finish();				
			}
			
		});
		settingsBtn.getBackground().setAlpha(0);
		
		backBtn = (ImageButton)findViewById(R.id.back);
		backBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
				
			}
			
		});
		
		gv = (GridView)findViewById(R.id.gridview);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i < itemText.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", itemImg[i]);
			map.put("ItemText", itemText[i]);
			listItem.add(map);
		}
		
		SimpleAdapter sa = new SimpleAdapter(this,
											listItem,
											R.layout.home_page_cell,
											new String[]{"ItemImage", "ItemText"},
											new int[]{R.id.ItemImage, R.id.ItemText});
		gv.setAdapter(sa);
		gv.setOnItemClickListener(this);

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.homepage);
		}
		else
		{
			setContentView(R.layout.homepage_ls);
		}
		
		sp = getSharedPreferences(TTCConfig.SP_NAME, 0);
		
		
		gv = (GridView)findViewById(R.id.gridview);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i < itemText.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", itemImg[i]);
			map.put("ItemText", itemText[i]);
			listItem.add(map);
		}
		
		SimpleAdapter sa = new SimpleAdapter(this,
											listItem,
											R.layout.home_page_cell,
											new String[]{"ItemImage", "ItemText"},
											new int[]{R.id.ItemImage, R.id.ItemText});
		gv.setAdapter(sa);
		gv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		// TODO Auto-generated method stub
		switch(position)
		{
			case 0:
				//initPopupWindow();
				intent.setClass(Options.this, Register.class);
				startActivity(intent);
				break;
			case 1:
				intent.setClass(Options.this, Profile.class);
				startActivity(intent);
				break;
			case 2:
				intent.setClass(Options.this, History.class);
				startActivity(intent);
				break;
			case 3:
				intent.setClass(Options.this, TTCConfig.class);
				startActivity(intent);
				break;
		}
		
	}
	
	private void initPopupWindow()
	{
		View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.login_popup, null);
		contentView.setBackgroundResource(R.drawable.login_popup);
		final PopupWindow popupWindow = new PopupWindow(gv, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true); 
		popupWindow.setContentView(contentView);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(gv.getChildAt(0), 20, 0);
		
		Button loginBtn = (Button)contentView.findViewById(R.id.popupLogin);
		loginBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Options.this, Login.class);
				startActivity(intent);
				popupWindow.dismiss();
			}
			
		});
		
		Button regBtn = (Button)contentView.findViewById(R.id.popupReg);
		regBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
			
		});
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
          Intent intent = new Intent(Options.this, MainBarcodeActivity.class);   
          startActivity(intent);  
          overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
          Toast.makeText(this ,  "Scanner" , Toast.LENGTH_SHORT).show();  
          finish();
        } else   if  (e2.getX()-e1.getX() > 50  
                && Math.abs(velocityX) > 0) {  
              
//          切换Activity   
          Intent intent = new Intent(Options.this, TTCConfig.class);   
          startActivity(intent);   
          overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
          Toast.makeText(this ,  "Settings" , Toast.LENGTH_SHORT).show();  
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
