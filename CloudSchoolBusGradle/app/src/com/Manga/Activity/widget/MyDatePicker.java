package com.Manga.Activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.Manga.Activity.R;

public class MyDatePicker extends DatePicker {
	public MyDatePicker(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		changeDatePickerButtons();

	}

	public MyDatePicker(Context context, AttributeSet attrs) {

		super(context, attrs);

		changeDatePickerButtons();

	}

	public MyDatePicker(Context context) {

		super(context);

		changeDatePickerButtons();

	}
	public void changeDatePickerButtons(){

		LinearLayout NumberPickerParent = (LinearLayout)super.getChildAt(0);

		int i = NumberPickerParent.getChildCount();

		LinearLayout NumberPicker = null;

		for(;i>0;i--){

			NumberPicker = (LinearLayout)NumberPickerParent.getChildAt(i-1);

			Object c0 = NumberPicker.getChildAt(0);

			((ImageButton)c0).setBackgroundResource(R.drawable.date_pic_up_selector);
			
			((ImageButton)c0).getLayoutParams().height=60;
			
			Object c2 = NumberPicker.getChildAt(2);

			((ImageButton)c2).setBackgroundResource(R.drawable.date_pic_down_selector);

			((ImageButton)c2).getLayoutParams().height=60;

		}
	}
}
