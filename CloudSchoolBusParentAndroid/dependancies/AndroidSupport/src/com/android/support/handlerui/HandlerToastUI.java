package com.android.support.handlerui;

import java.lang.ref.WeakReference;

import com.android.support.handlerui.widget.CustomToast;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * 用handler处理UI界面Toast
 * 
 * @author hongfeijia
 * 
 */
public class HandlerToastUI {

	private WeakReference<Context> mContext;

	private HandlerToastUI(Context context) {
		mContext = new WeakReference<Context>(context);
	}

	public static void getHandlerToastUI(Context context, String messageString) {

		HandlerToastUI handlerToastUI = new HandlerToastUI(context);
		handlerToastUI.mHandler.sendMessage(handlerToastUI.mHandler
				.obtainMessage(0, messageString));

	}

	public static void getHandlerToastUI(Context context, String messageString,
			int textColor) {

		HandlerToastUI handlerToastUI = new HandlerToastUI(context);
		Message message = handlerToastUI.mHandler.obtainMessage(0,
				messageString);
		Bundle bundle = new Bundle();
		bundle.putInt("textColor", textColor);
		message.setData(bundle);
		handlerToastUI.mHandler.sendMessage(message);

	}

	private Handler mHandler = new Handler(Looper.getMainLooper(),
			new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {
					switch (msg.what) {
					case 0: {
						String messageString = (String) msg.obj;
						if (null != mContext.get()) {
							Bundle bundle = msg.getData();
							if (null != bundle) {
								int textColor = bundle.getInt("textColor", -1);
								if (-1 != textColor) {
									CustomToast.makeTextColor(
											mContext.get(),
											messageString,
											Toast.LENGTH_SHORT,
											mContext.get().getResources()
													.getColor(textColor))
											.show();
								} else {
									CustomToast.makeText(mContext.get(),
											messageString, Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								CustomToast.makeText(mContext.get(),
										messageString, Toast.LENGTH_SHORT)
										.show();
							}
						}
						break;
					}
					default:
						break;
					}
					return false;
				}
			});

}
