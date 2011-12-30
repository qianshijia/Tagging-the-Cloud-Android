
package com.apps.TagIt;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CheckBox;

public class CheckBoxifiedTextView extends LinearLayout {
     
     private TextView mText;
     private CheckBox mCheckBox;
     private CheckBoxifiedText mCheckBoxText;
     
     public CheckBoxifiedTextView(Context context, CheckBoxifiedText aCheckBoxifiedText) {
          super(context);

          /* First CheckBox and the Text to the right (horizontal),
           * not above and below (vertical) */
          this.setOrientation(HORIZONTAL);
          mCheckBoxText = aCheckBoxifiedText;
          mCheckBox = new CheckBox(context);
          mCheckBox.setPadding(0, 0, 20, 0); // 5px to the right
          
          /* Set the initial state of the checkbox. */
          mCheckBox.setChecked(aCheckBoxifiedText.getChecked());
          
          
          /* At first, add the CheckBox to ourself
           * (! we are extending LinearLayout) */
          addView(mCheckBox,  new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
          
          mText = new TextView(context);
          mText.setText(aCheckBoxifiedText.getText());
          //mText.setPadding(0, 0, 15, 0);
          addView(mText, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
     }

     public void setText(String words) {
          mText.setText(words);
     }
     public void setCheckBoxState(boolean bool)
     {
    	 mCheckBox.setChecked(mCheckBoxText.getChecked());
    	 mCheckBoxText.setChecked(true);
     }
}