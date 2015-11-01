package com.android.support.debug;

import android.util.Log;

/**
 * 打印信息
 * 
 * @author lenovo
 * 
 */
public class DebugLog {

	private static boolean sDebug = true;
	private static String sTag = "DebugLog";

	public static boolean isDebug() {
		return sDebug;
	}

	public static void setDebug(boolean debug) {
		DebugLog.sDebug = debug;
	}

	public static String getTag() {
		return sTag;
	}

	public static void setTag(String tag) {
		DebugLog.sTag = tag;
	}

	public static void logI(String msg) {
		if (sDebug) {
			Log.i(sTag, msg);
		}
	}

	public static void logE(String msg) {
		if (sDebug) {
			Log.e(sTag, msg);
		}
	}

	public static void logW(String msg) {
		if (sDebug) {
			Log.w(sTag, msg);
		}
	}

	public static void logD(String msg) {
		if (sDebug) {
			Log.d(sTag, msg);
		}
	}

}
