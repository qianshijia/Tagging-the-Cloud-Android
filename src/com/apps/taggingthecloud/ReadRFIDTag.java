package com.apps.taggingthecloud;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ReadRFIDTag extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		readRFIDTagId(getIntent());
		finish();
		/*if(!DataAdapters.login.isEmpty())
		{
			if(DataAdapters.login.get(0).get("validLogin").equals("1"))
			{
				readRFIDTagId(getIntent());
				finish();
			}
			else
			{
				Toast.makeText(this, "Please Login", Toast.LENGTH_LONG).show();
				Intent it = new Intent();
				it.setClass(ReadRFIDTag.this, TagIt.class);
				startActivity(it);
				finish();
			}
			
		}
		else
		{
			Toast.makeText(this, "Please Login", Toast.LENGTH_LONG).show();
			Intent it = new Intent();
			it.setClass(ReadRFIDTag.this, TagIt.class);
			startActivity(it);
			finish();
		}*/
	}
	
	private void readRFIDTagId(Intent intent)
	{
			Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Intent it = new Intent();
			it.putExtra("RFIDTagId", getHexString(tag.getId()));
			it.setClass(ReadRFIDTag.this, MainBarcodeActivity.class);
			startActivity(it);
			
	}
	
	private String getHexString(byte[] b)
	{
		String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}
	
}
