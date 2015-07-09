package com.android.support.utils;

public class CommonUtils {

	private long lastClickTime;

	public boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	
	public void setLastClickTime() {

		lastClickTime = 0;
	}
}
