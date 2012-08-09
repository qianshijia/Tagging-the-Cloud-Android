package com.apps.taggingthecloud;


import java.util.ArrayList;
import java.util.HashMap;

import com.apps.taggingthecloud.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QuestionsListActivity extends ListActivity  {
    /** Called when the activity is first created. */
	
	   String search;
	   ImageButton backBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
         setContentView(R.layout.questionlist);
         backBtn = (ImageButton)findViewById(R.id.back);
         backBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(QuestionsListActivity.this);
				// Set the message to display
				alertbox.setMessage("Are you sure to quit this process?");
				// Add a positive button to the alert box and assign a click listener
				alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//DataAdapters.login.clear();
						DataAdapters.question.clear();
						DataAdapters.questions.clear();
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
				
        	 
         });
         
		   final ListView lv1 = getListView();
		   lv1.setTextFilterEnabled(true);
		   lv1.setDivider(new ColorDrawable(Color.TRANSPARENT));
		   	 lv1.setOnItemClickListener(new OnItemClickListener() {
		   	 @Override
		  	 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		   		 Intent myintent=new Intent(QuestionsListActivity.this,StartQuestions.class);	
		   		 Question.questionnaireId = DataAdapters.questions.get(position).get("Qcode").toString();
		   		 DataAdapters.setProcessName(DataAdapters.questions.get(position).get("Question").toString());
			     startActivity(myintent);
			     finish();
		   	 }
		   	 });
		   	 
		   	 ArrayList<HashMap<String,String>> items=DataAdapters.getQuestionsList();
		   	 String itemsmm[] = new String[items.size()];
		   	 for(int i=0 ; i< items.size();i++){
		   		 Log.i("Itemcode==",items.get(i).get("Qcode").toString());
		   		Log.i("ItemQ==",items.get(i).get("Question").toString());
		   		itemsmm[i]="Item" + String.valueOf(1);
		   	 }
		     
		   	 SimpleAdapter ilist = new SimpleAdapter(this, DataAdapters.getQuestionsList(),R.layout.questionrow,new String[] {"Question"},new int[] {R.id.txtName});
		    
		     
		     setListAdapter(ilist);
  }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(QuestionsListActivity.this);
			// Set the message to display
			alertbox.setMessage("Are you sure to quit this process?");
			// Add a positive button to the alert box and assign a click listener
			alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//DataAdapters.login.clear();
					DataAdapters.question.clear();
					DataAdapters.questions.clear();
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
