package com.android.support.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 和屏幕相关的
 * 
 * @author jiahongfei
 * 
 */
public class ScreenUtils {

	/**
	 * 获取屏幕宽高
	 * @param context
	 * @return 数组第一个元素宽，第二个元素高
	 */
	public static int[] getScreenBounds(Context context) {

		int[] screenBounds = new int[2];

		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		screenBounds[0] = displayMetrics.widthPixels;
		screenBounds[1] = displayMetrics.heightPixels;
		return screenBounds;
	}

}
