package com.android.support.jhf.network;

import java.lang.ref.WeakReference;

import org.apache.http.Header;

import com.android.support.jhf.handlerui.HandlerToastUI;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class DefaultErrorListener extends BaseErrorListener {

	private WeakReference<Context> mContextWeakReference;

	public DefaultErrorListener(Context context) {
		mContextWeakReference = new WeakReference<Context>(context);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {
		super.onFailure(statusCode, headers, responseBody, error);
		if (mContextWeakReference.get() instanceof Activity) {
			StringBuffer stringBuffer = new StringBuffer();
			ErrorExceptionHandler.NetworkExceptionHandler(stringBuffer, error);
			if (!TextUtils.isEmpty(stringBuffer.toString())) {
				HandlerToastUI.getHandlerToastUI(mContextWeakReference.get(),
						stringBuffer.toString());
			}
		}
	}

}
