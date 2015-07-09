package com.android.support.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;

/**
 * 复制粘贴工具类
 * 
 * @author jiahongfei
 * 
 */
public class ClipboardUtils {

	/**
	 * 实现文本复制功能
	 * 
	 * @param content
	 */
	@SuppressLint("NewApi")
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			// TODO:如果当前版本小于HONEYCOMB版本，即3.0版本
			android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(content.trim());
		} else {
			android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			// cmb.setText(content.trim());
			cmb.setPrimaryClip(ClipData.newPlainText(content, content));
		}

	}

	/**
	 * 实现粘贴功能
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String paste(Context context) {
		// 得到剪贴板管理器
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return cmb.getText().toString().trim();
		} else {
			// 如果是文本信息

			android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			if (cmb.getPrimaryClipDescription().hasMimeType(
					ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				ClipData cdText = cmb.getPrimaryClip();
				Item item = cdText.getItemAt(0);
				// 此处是TEXT文本信息
				if (item.getText() == null) {
					return null;
				} else {
					return item.getText().toString();
				}
			}
		}
		return null;

	}
}
