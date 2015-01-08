package com.android.support.jhf.dialog;

import com.android.support.jhf.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.*;

/**
 * 等待对话框
 * 
 * @author hongfeijia
 * 
 */
public class CustomWaitDialog extends Dialog implements View.OnClickListener {

	public interface OnKeyCancel {
		public void onKeyCancelListener();
	}

	private Context mContext;
	private OnKeyCancel mOnKeyCancel;
	private ProgressBar mProgressBar;
	private TextView mWaitTextView;

	public CustomWaitDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	protected CustomWaitDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public CustomWaitDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		
		getWindow().setGravity(Gravity.CENTER);
		
		setContentView(R.layout.android_support_jhf_dialog_custom_wait);
		
		mProgressBar = (ProgressBar) findViewById(R.id.waitProgressBar);
		mWaitTextView = (TextView) findViewById(R.id.waitTextView);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(mContext.getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		setMessage(title.toString());
	}

	public void setMessage(String messageString) {
		mWaitTextView.setText(messageString);
	}

	public void setOnKeyCancelListener(OnKeyCancel onKeyCancel) {
		mOnKeyCancel = onKeyCancel;
	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cancel() {
		try {
			super.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void dismiss() {
		try {
			super.dismiss();
		} catch (Exception e) {
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			this.dismiss();
			if (null != mOnKeyCancel) {
				mOnKeyCancel.onKeyCancelListener();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		if (null != mOnKeyCancel) {
			mOnKeyCancel.onKeyCancelListener();
		}
	}

}
