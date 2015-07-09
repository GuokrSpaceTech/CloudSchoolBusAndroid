package com.android.support.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.Bundle;

/**
 * 用来操作AndroidManifest.xml文件的类
 * 
 * @author lenovo
 * 
 */
public class AndroidManifestUtil {

//	/**
//	 * java代码段：获取友盟渠道号
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public static String getUmengChannelJava(Context context) {
//		ApplicationInfo info = null;
//		try {
//			info = context.getPackageManager().getApplicationInfo(
//					context.getPackageName(), PackageManager.GET_META_DATA);
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		if (null != info && null != info.metaData) {
//			String msg = info.metaData.getString("UMENG_CHANNEL");
//			return msg;
//		}
//		return null;
//	}

	/**
	 * META_DATA
	 * 
	 * @param context
	 * @param metaKey
	 * @return
	 */
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String metaDataString = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				metaDataString = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return metaDataString;
	}

}
