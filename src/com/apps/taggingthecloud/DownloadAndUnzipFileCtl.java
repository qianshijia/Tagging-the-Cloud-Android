package com.apps.taggingthecloud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.apps.taggingthecloud.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DownloadAndUnzipFileCtl extends Activity 
{
	private Button finishButton;
	private final String url = "";
	private ProgressBar pb;
	private int fileSize;
	private int downloadedFileSize;
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (!Thread.currentThread().isInterrupted())
			{
				switch (msg.what)
				{
					case 0:
						pb.setMax(fileSize);
						pb.setVisibility(View.VISIBLE);
					case 1:
						pb.setProgress(downloadedFileSize);
						break;
					case 2:
						pb.setVisibility(View.GONE);
						Toast.makeText(DownloadAndUnzipFileCtl.this, "Download Finish", 1).show();
						break;
					case -1:
						String error = msg.getData().getString("error");
						Toast.makeText(DownloadAndUnzipFileCtl.this, error, 1).show();
						break;
				}
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dlandunzip);
		finishButton = (Button)findViewById(R.id.btnFinish);
		pb = (ProgressBar)findViewById(R.id.down_pb);
		new Thread(){
			public void run()
			{
				downloadMediaFiles("");
			}
		}.start();
		super.onCreate(savedInstanceState);
	}
	
	private String downloadMediaFiles(String url)
	{
		try
		{
			URL fileUrl = new URL(url);
			HttpURLConnection urlConnection = (HttpURLConnection) fileUrl.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
			File SDCardRoot = Environment.getExternalStorageDirectory();
			String dir = SDCardRoot.getPath() + "/" + getString(R.string.app_name) + "/ZipFiles";
			File dlDir = new File(dir);
			if(!dlDir.exists())
			{
				dlDir.mkdirs();
			}
			
			String fileName = "zipfile.zip";
			File file = new File(dir,fileName);
			if(file.createNewFile())
			{
				file.createNewFile();
			}
			FileOutputStream fileOutput = new FileOutputStream(file);
			InputStream inputStream = urlConnection.getInputStream();
			fileSize = urlConnection.getContentLength();
			downloadedFileSize = 0;
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			handler.sendEmptyMessage(0);
			while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedFileSize += bufferLength;
				handler.sendEmptyMessage(1);
			}
			fileOutput.close();
			if(downloadedFileSize==fileSize)
				handler.sendEmptyMessage(2);
				return file.getPath();
		}
		catch(Exception e)
		{
			
		}
		return "";
	}
	
	private void unZipFile(String zipFilePath)
	{
		try
		{
			File zipFile = new File(zipFilePath);
			ZipFile zf = new ZipFile(zipFile);
			for(Enumeration<?> entries = zf.entries(); entries.hasMoreElements();)
			{
				ZipEntry entry = ((ZipEntry)entries.nextElement());
				InputStream is = zf.getInputStream(entry);
				String str = Environment.getExternalStorageDirectory().getPath() + "/"
						+ getString(R.string.app_name) + "/" 
						+ "downloadedMedias" + "/" + entry.getName();
				str = new String(str.getBytes("8859_1"), "GB2312");
				File desFile = new File(str);
				if(!desFile.exists())
				{
					File fileParentDir = desFile.getParentFile();
					if(!fileParentDir.exists())
						fileParentDir.mkdirs();
					desFile.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(desFile);
				byte buffer[] = new byte[1024*1024];
				int readLength;
				while((readLength = is.read(buffer)) > 0)
				{
					out.write(buffer, 0, readLength);
				}

				is.close();
				out.close();
			}
		}
		catch(Exception e)
		{
			
		}
	}
}
