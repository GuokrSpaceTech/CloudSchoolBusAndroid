package com.Manga.Activity.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.Manga.Activity.R;

public class CustomDatePickerDialog extends Dialog implements
		View.OnClickListener {

	public interface DatePickerListener {
		public void onClick(View view, int year, int monthOfYear, int dayOfMonth);
	}

	private Context mContext;
	private DatePicker mDatePicker;
	private Button mOkButton;
	private Button mCancelButton;

	private DatePickerListener mOkOnClickListener;
	private View.OnClickListener mCancelOnClickListener;

	public CustomDatePickerDialog(Context context, int theme) {
        super(context,theme);
		init(context);
	}

	protected CustomDatePickerDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public CustomDatePickerDialog(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 创建一个对象
	 *
	 * @param context
	 *            必须是一个Activity对象
	 * @param theme
	 * @return
	 */
	public static CustomDatePickerDialog getCustomDatePickerDialog(Context context,
			int theme) {
		return new CustomDatePickerDialog(context, theme);
	}

	/**
	 * Initialize the state.
	 *
	 * @param year
	 *            The initial year.
	 * @param monthOfYear
	 *            The initial month.
	 * @param dayOfMonth
	 *            The initial day of the month.
	 * @param onDateChangedListener
	 *            How user is notified date is changed by user, can be null.
	 */
	public CustomDatePickerDialog init(int year, int monthOfYear, int dayOfMonth,
			OnDateChangedListener onDateChangedListener) {
		mDatePicker.init(year, monthOfYear, dayOfMonth, onDateChangedListener);
		return this;
	}

	/**
	 * 设置确定按钮的监听 当okString = null，onOKClickListener = null， imageId = -1时不显示按钮
	 *
	 * @param okString
	 *            按钮上的文字 null用默认的 不等于null显示填写的
	 * @param onOkClickListener 年月日，月需要加上1，月是从零开始算的
	 * @param imageId
	 *            -1用默认图片
	 */
	public CustomDatePickerDialog setOkButton(String okString, int imageId,
			DatePickerListener onOkClickListener) {

		mOkOnClickListener = onOkClickListener;

		if (null != okString) {
			mOkButton.setText(okString);
		}

		if (0 != imageId) {
			mOkButton.setBackgroundResource(imageId);
		}


		return this;
	}

	/**
	 * 设置取消按钮的监听 当cancelString = null，onOKClickListener = null， imageId =
	 * -1时不显示按钮
	 *
	 * @param cancelString
	 *            按钮上的文字 null用默认的 不等于null显示填写的
	 * @param onCancelClickListener
	 * @param imageId
	 *            -1用默认图片
	 */
	public CustomDatePickerDialog setCancelButton(String cancelString,
			int imageId, View.OnClickListener onCancelClickListener) {

		mCancelOnClickListener = onCancelClickListener;
		
		if (null != cancelString) {
			mCancelButton.setText(cancelString);
		}
		if (0 != imageId) {
			mCancelButton.setBackgroundResource(imageId);
		}
	
		return this;
	}
	
//	@Override
//	public void setTitle(CharSequence title) {
//		setTitle(-1, title.toString());
//	}
//	
//	@Override
//	public void setTitle(int titleId) {
//		setTitle(mContext.getString(titleId));
//	}

//	/**
//	 * 设置标题
//	 * 
//	 * @param titleImageId
//	 *            -1使用默认图片
//	 * @param titleString
//	 *            null使用默认
//	 * @return
//	 */
//	public CustomDatePickerDialog setTitle(int titleImageId, String titleString) {
//
//		Message message = mHandler.obtainMessage(3);
//		Bundle bundle = new Bundle();
//		bundle.putString("titleString", titleString);
//		bundle.putInt("titleImageId", titleImageId);
//		message.setData(bundle);
//		mHandler.sendMessage(message);
//
//		return this;
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton: {
			mDatePicker.clearFocus();
			if (null != mOkOnClickListener) {
				mOkOnClickListener.onClick(v, mDatePicker.getYear(),
						mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
			}
			CustomDatePickerDialog.this.dismiss();
			break;
		}
		case R.id.cancelButton: {
			if (null != mCancelOnClickListener) {
				mCancelOnClickListener.onClick(v);
			}
			CustomDatePickerDialog.this.dismiss();
			break;
		}
		default:
			break;
		}
	}

	private void init(Context context) {
		mContext = context;

		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		
		setContentView(R.layout.dialog_custom_date_picker);
		mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		mOkButton = (Button) findViewById(R.id.okButton);
		mOkButton.setOnClickListener(this);
		mCancelButton = (Button) findViewById(R.id.cancelButton);
		mCancelButton.setOnClickListener(this);

		setCanceledOnTouchOutside(true);
		
		initDatePicker();
	}

	private void initDatePicker() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mCancelOnClickListener) {
				mCancelOnClickListener.onClick(mCancelButton);
			}
			this.dismiss();
		}
		return false;
	}


}
