package com.cytx.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Shareference存储数据
 * @author xilehang
 *
 */
@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
public class SharePreferencTools {

	private static final String Config = "Server_Config";

	/**
	 * 保存字符串到Share
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean setStringToShares(Context context, String key, String value) {
		if (key == null || value == null)
			return false;
		if (key.trim().equalsIgnoreCase(""))
			return false;
		SharedPreferences shares = context.getSharedPreferences(Config,
				Context.MODE_WORLD_WRITEABLE);
		Editor editor = shares.edit();
		editor.putString(key.trim(), value.trim());
		editor.commit();
		return true;
	}

	/**
	 * 从Share中获取字符串
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getStringFromShares(Context context, String key, String defaultValue) {
		if (key == null || defaultValue == null)
			return null;
		SharedPreferences shares = context.getSharedPreferences(Config,
				Context.MODE_WORLD_READABLE);
		String returnValue = shares.getString(key.trim(), defaultValue);
		return returnValue;
	}
	
	/**
	 * 保存boolean值到Share
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean setBooleanToShares(Context context, String key, boolean value) {
		if (key == null)
			return false;
		if (key.trim().equalsIgnoreCase(""))
			return false;
		SharedPreferences shares = context.getSharedPreferences(Config,
				Context.MODE_WORLD_WRITEABLE);
		Editor editor = shares.edit();
		editor.putBoolean(key, value);
		editor.commit();
		return true;
	}

	/**
	 * 从Share中获取boolean值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean getBooleanFromShares(Context context, String key, boolean defaultValue) {
		if (key == null)
			return false;
		SharedPreferences shares = context.getSharedPreferences(Config,
				Context.MODE_WORLD_READABLE);
		boolean returnValue = shares.getBoolean(key, defaultValue);
		return returnValue;
	}

}
