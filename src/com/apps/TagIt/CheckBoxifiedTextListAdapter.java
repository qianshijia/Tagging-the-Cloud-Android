
package com.apps.TagIt;

import java.util.ArrayList;
import android.util.Log;
import java.util.List;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CheckBoxifiedTextListAdapter extends BaseAdapter {

     /** Remember our context so we can use it when constructing views. */
     private Context mContext;

     private List<CheckBoxifiedText> mItems = new ArrayList<CheckBoxifiedText>();

     public CheckBoxifiedTextListAdapter(Context context) {
          mContext = context;
     }

     public void addItem(CheckBoxifiedText it) { mItems.add(it); }

     public void setListItems(List<CheckBoxifiedText> lit) { mItems = lit; }
     
     public ArrayList<String> getListItems() { 
    	 ArrayList<String> item=new ArrayList<String>();
    	 
    	  for(CheckBoxifiedText cboxtxt: mItems)
          {
    		  if(cboxtxt.getChecked()==true){
    			  item.add(cboxtxt.getText());
    		  }
         	 
              
          }		
    	 return item; 
     }

     /** @return The number of items in the */
     public int getCount() { return mItems.size(); }

     public Object getItem(int position) { return mItems.get(position); }
     
     public void setChecked(boolean value, int position){
         mItems.get(position).setChecked(value);
     }
     public void selectAll(){
    	 
         for(CheckBoxifiedText cboxtxt: mItems)
         {
        	 
              cboxtxt.setChecked(true);
         }		
         /* Things have changed, do a redraw. */
         this.notifyDataSetInvalidated();
     }
     public void deselectAll()
     {
         for(CheckBoxifiedText cboxtxt: mItems)
             cboxtxt.setChecked(false);
        /* Things have changed, do a redraw. */
        this.notifyDataSetInvalidated();
     }

     public boolean areAllItemsSelectable() { return false; }

     /** Use the array index as a unique id. */
     public long getItemId(int position) {
          return position;
     }

     /** @param convertView The old view to overwrite, if one is passed
      * @returns a CheckBoxifiedTextView that holds wraps around an CheckBoxifiedText */
     public View getView(int position, View convertView, ViewGroup parent){
          CheckBoxifiedTextView btv;
          if (convertView == null) {
               btv = new CheckBoxifiedTextView(mContext, mItems.get(position));
          } else { // Reuse/Overwrite the View passed
               // We are assuming(!) that it is castable!
        	   CheckBoxifiedText src = mItems.get(position);
               btv = (CheckBoxifiedTextView) convertView;
               btv.setCheckBoxState(src.getChecked()); 
               btv = (CheckBoxifiedTextView) convertView;
               btv.setText(mItems.get(position).getText());
          }
          return btv;
     }
}