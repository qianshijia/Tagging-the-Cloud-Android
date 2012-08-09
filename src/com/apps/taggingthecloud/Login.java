package com.apps.taggingthecloud;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;
public class Login extends Activity implements OnClickListener {
	
	private EditText username;
	private EditText password;
	private Button bt;
	private SharedPreferences sp;
	private ImageButton imageButton;
	private ToggleButton autoLogin;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.main);
		}
		else
		{
			setContentView(R.layout.login_ls);
		}
		sp = getSharedPreferences(TTCConfig.SP_NAME, 0);
		
		autoLogin = (ToggleButton)findViewById(R.id.autologin);
		autoLogin.setChecked(sp.getBoolean("auto_login", false));
		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				buttonView.setChecked(isChecked);
			}
			
		});
		
		bt = (Button) findViewById(R.id.btnLogin);
		bt.setOnClickListener(this);
		username = (EditText) findViewById(R.id.txtUsername);
		password = (EditText) findViewById(R.id.txtPassword);
		
		imageButton = (ImageButton)findViewById(R.id.logoutbtn);
		imageButton.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		
		// TODO Auto-generated method stub
		super.onStart();
		if(sp.getBoolean("auto_login", false))
		{
			DataAdapters.login.clear();
			validateCall();
		}
		
	}

	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.main);
		}
		else
		{
			setContentView(R.layout.login_ls);
		}
		
		
		bt = (Button) findViewById(R.id.btnLogin);
		bt.setOnClickListener(this);
		username = (EditText) findViewById(R.id.txtUsername);
		password = (EditText) findViewById(R.id.txtPassword);
		sp = getSharedPreferences(TTCConfig.SP_NAME, 0);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int bt = arg0.getId();
	
		
		if (bt == R.id.btnLogin) {
			DataAdapters.login.clear();
			if ((!username.getText().toString().equalsIgnoreCase("") && username.getText().toString() != null)
					&& (!password.getText().toString().equalsIgnoreCase("") && password.getText().toString() != null)) {
				validateCall();
			} else {
				if (username.getText().toString().equalsIgnoreCase("")
						|| username.getText().toString() == null) {
					AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
					alertbox.setMessage("Please enter UserName.");
					alertbox.setNeutralButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
								}
							});

					// show the alert box
					alertbox.show();
				}
				else if (password.getText().toString().equalsIgnoreCase("")
						|| password.getText().toString() == null) {
					AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
					alertbox.setMessage("Please enter Password.");
					alertbox.setNeutralButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
								}
							});

					alertbox.show();
				}
			}
		}
		else if(bt == R.id.topScanBtn)
		{
			Intent intent = new Intent();
			intent.setClass(Login.this, MainBarcodeActivity.class);
			startActivity(intent);
			finish();
		}
		else if(bt == R.id.logoutbtn)
		{
			finish();
		}
	}

	private void actionRequired() {
		ArrayList<HashMap<String,String>> login=DataAdapters.getLoginResult();
		String result="";
		result=	login.get(0).get("validLogin");
		Log.i("RESULT",result.toString());
		if (result.equalsIgnoreCase("0")) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			// Set the message to display
			alertbox.setMessage(login.get(0).get("retMessage"));
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
			if(!sp.getBoolean("auto_login", false))
			{
				Editor e = sp.edit();
				e.putString("username", username.getText().toString());
				e.putString("password", password.getText().toString());
				e.commit();
			}
			if(login.get(0).get("retMessage").equals("0"))
			{
				Editor e = sp.edit();
				e.putBoolean("auto_login", autoLogin.isChecked());
				e.commit();
	            finish();
			}
			else
			{
				AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
				// Set the message to display
				alertbox.setMessage(login.get(0).get("retMessage"));
				// Add a neutral button to the alert box and assign a click listener
				alertbox.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {
	
							// Click listener on the neutral button of alert box
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent = new Intent();	
								//intent.putExtra("tag", i.getStringExtra("tag"));
								//intent.putExtra("tagType", i.getStringExtra("tagType"));
					            intent.setClass(Login.this, MainBarcodeActivity.class);
					            startActivity(intent);
					            finish();
								// The neutral button was clicked
	
							}
						});
	
				// show the alert box
				alertbox.show();
			}
			
		}
	}

	private void validateCall() {
			
			if(TTCHelper.check3GNetwork(getApplicationContext()) || TTCHelper.checkWiFi(getApplicationContext()))
			{
				final ProgressDialog dialog = ProgressDialog.show(this, null, null,true);
				dialog.setTitle("Login...");
				final Handler handler = new Handler() {
				public void handleMessage(Message msg) {

						actionRequired();
						dialog.dismiss();

					}
				};
			
				Thread checkUpdate = new Thread() {
					
					public void run() {
						Looper.prepare();
						 try {
							 DateFormat df = DateFormat.getTimeInstance();
							 df.setTimeZone(TimeZone.getTimeZone("gmt"));
							 String gmtTime = df.format(new Date());
							 String un = sp.getBoolean("auto_login", false)?sp.getString("username", ""):username.getText().toString();
							 String pw = sp.getBoolean("auto_login", false)?sp.getString("password", ""):password.getText().toString();
							 TelephonyManager t1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
					  	       String udid = t1.getDeviceId();	
				    	        HttpClient client = new DefaultHttpClient();  
				    	        String postURL = getString(R.string.login_url);
				    	        String hash=md5(un + pw + udid + gmtTime);
				    	        HttpPost post = new HttpPost(postURL);
				    	        List<NameValuePair> params = new ArrayList<NameValuePair>();
				    	            params.add(new BasicNameValuePair("username", un));
				    	            params.add(new BasicNameValuePair("password", pw));
				    	            params.add(new BasicNameValuePair("imei", udid));
				    	            params.add(new BasicNameValuePair("timestamp", gmtTime));
				    	            params.add(new BasicNameValuePair("md5", hash));
				    	            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
				    	            post.setEntity(ent);
				    	            HttpResponse responsePOST = client.execute(post); 
				    	            HttpEntity resEntity = responsePOST.getEntity();  
				    	            if (resEntity != null) {    
				    	               // Log.i("RESPONSE",EntityUtils.toString(resEntity));
				    	                LoginParser lp=new LoginParser();
				    	                lp.getRecords(EntityUtils.toString(resEntity));
				    	            	handler.sendEmptyMessage(0);
				    	            }

				    	    } catch (Exception e) {
				    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
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
				};
				checkUpdate.start();
			}
			else
			{
				AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
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
	
	private String md5(String in) {
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5");
	        digest.reset();
	        digest.update(in.getBytes());
	        byte[] a = digest.digest();
	        int len = a.length;
	        StringBuilder sb = new StringBuilder(len << 1);
	        for (int i = 0; i < len; i++) {
	            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	            sb.append(Character.forDigit(a[i] & 0x0f, 16));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	    return null;
	}
}