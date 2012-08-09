package com.apps.taggingthecloud;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Register extends Activity
{
	private WebView wv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.register);
		
		wv = (WebView)findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true);
	    wv.setScrollBarStyle(0);
	    wv.setWebViewClient(new WebViewClient(){
	    	 public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
	            	view.loadUrl(url);
	                return true;   
	            }
	    });
	    wv.loadUrl("http://my.tagit.com.au/register");
	}
	
}
