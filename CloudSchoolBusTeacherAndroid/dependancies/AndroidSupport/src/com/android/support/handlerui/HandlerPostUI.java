package com.android.support.handlerui;


import android.os.Handler;
import android.os.Looper;

/**
 * 在线程中处理UI事件
 * 
 * @author hongfeijia
 * 
 */
public class HandlerPostUI {

	private static Handler mHandler = new Handler(Looper.getMainLooper());

	private HandlerPostUI() {
	}

	public static void getHandlerPostUI(Runnable r) {

		HandlerPostUI.mHandler.post(r);

	}

}
