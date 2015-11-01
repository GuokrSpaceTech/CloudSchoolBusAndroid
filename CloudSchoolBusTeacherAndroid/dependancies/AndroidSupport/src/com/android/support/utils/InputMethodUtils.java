package com.android.support.utils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * 输入法工具类
 * 
 * @author lenovo
 * 
 */
public class InputMethodUtils {

	/**
	 * 软键盘开关，当软键盘开启的时候调用这个方法关闭，当软键盘没有弹出的时候调用这个方法弹出
	 * 
	 * @param context
	 */
	public static void softKeyboard(Context context) {
		InputMethodManager m = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

	}

	/**
	 * 显示软件盘
	 * 
	 * @param context
	 * @param textView
	 * @return 
	 */
	public static boolean showSoftKeyboard(Context context, TextView textView) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.showSoftInput(textView, 0);
	}

	/**
	 * 隐藏软件盘
	 * 
	 * @param context
	 * @param textView
	 *            编译控件的对象例如：TextView、EditText
	 * @return
	 */
	public static boolean hideSoftKeyboard(Context context, TextView textView) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
	}

}
