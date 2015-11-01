package com.android.support.utils;
/**
 * 用于延时点击操作
 * @author jiahongfei
 *
 */
public class DelayClickUtils {

	private static long lastClickTime;

	/**
	 * 返回true表示快速点击，返回false表示可以点击
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 2000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 清零
	 */
	public static void setLastClickTime() {

		lastClickTime = 0;
	}
}
