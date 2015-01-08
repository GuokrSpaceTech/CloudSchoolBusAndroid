package com.android.support.jhf.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * 网络状态工具类
 */
public class NetworkStatusUtils {

	/**
	 * 判断网络是否连接
	 * @param mContext
	 * @return true 有网络，false 没有网络
	 */
	public static boolean networkStatusOK(Context mContext) {
		boolean netStatus = false;
		try {
			ConnectivityManager connectManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectManager
					.getActiveNetworkInfo();
			if (activeNetworkInfo != null) {
				if (activeNetworkInfo.isAvailable()
						&& activeNetworkInfo.isConnected()) {
					netStatus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return netStatus;
	}

}
