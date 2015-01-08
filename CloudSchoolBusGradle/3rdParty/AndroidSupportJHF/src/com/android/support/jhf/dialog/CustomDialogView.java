package com.android.support.jhf.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;

/**
 * 自定义DialogView，可以将TextView
 * Layout等显示在当前的界面上，可以实现点击一个Button在当前界面上显示这个View，松开Button这个view消失
 * 
 * @author hongfeijia
 * 
 */
public class CustomDialogView {

	private WindowManager mWindowManager;
	private Context mContext;
	private Handler mHandler = new Handler();
	private View mDialogView;
	private View mContentView;

	public CustomDialogView(Context context) {
		mContext = context;
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
	}

	/**
	 * 设置弹出框内容
	 * @param view 显示的View
	 * @param contentView 用来设置显示和隐藏的layout
	 */
	public void setContentView(View view, View contentView) {
		mContentView = contentView;
		mDialogView = view;
		mContentView.setVisibility(View.INVISIBLE);
		mHandler.post(new Runnable() {

			public void run() {
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				mWindowManager.addView(mDialogView, lp);
			}
		});
	}

	/**
	 * 设置弹出对话框的内容
	 * 
	 * @param event
	 */
	public void showDialog() {
		mContentView.setVisibility(View.VISIBLE);
	}
	
	public void hideDialog(){
		mContentView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 当调用下面这两个方法的时候将mWindowManager删除
	 * 
	 * @Override protected void onPause() { super.onPause();
	 *           mWindowManager.removeView(mDialogText); }
	 * @Override protected void onDestroy() { super.onDestroy();
	 *           mWindowManager.removeView(mDialogText); }
	 */
	public void removeDialogText() {
		mWindowManager.removeView(mDialogView);
	}
}
