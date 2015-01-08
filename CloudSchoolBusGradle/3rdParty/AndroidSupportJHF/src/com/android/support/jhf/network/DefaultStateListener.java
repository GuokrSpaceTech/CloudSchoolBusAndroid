package com.android.support.jhf.network;

import java.lang.ref.WeakReference;

import com.android.support.jhf.R;
import com.android.support.jhf.dialog.CustomWaitDialog;
import com.android.support.jhf.dialog.CustomWaitDialog.OnKeyCancel;
import com.android.support.jhf.handlerui.HandlerPostUI;
import com.android.support.jhf.handlerui.HandlerToastUI;
import com.android.support.jhf.network.loopj.android.http.AsyncHttpRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;

public class DefaultStateListener extends BaseStateListener {

	private WeakReference<Context> mContextWeakReference;

	private CustomWaitDialog mCustomWaitDialog = null;

	public DefaultStateListener(Context context) {
		mContextWeakReference = new WeakReference<Context>(context);
	}

	@Override
	public void onStart(final AsyncHttpRequest asyncHttpRequest) {
		if (mContextWeakReference.get() instanceof Activity) {
//			Activity activity = (Activity) mContextWeakReference.get();
//			if (activity.isFinishing()) {
//				return;
//			}
			if (null == mCustomWaitDialog
					&& null != mContextWeakReference.get()) {
				HandlerPostUI.getHandlerPostUI(new Runnable() {

					@Override
					public void run() {
						mCustomWaitDialog = new CustomWaitDialog(
								mContextWeakReference.get(),
								R.style.CustomWaitDialog);
						mCustomWaitDialog
								.setOnKeyCancelListener(new OnKeyCancel() {

									@Override
									public void onKeyCancelListener() {
										if (null != asyncHttpRequest) {
											asyncHttpRequest.cancel(true);
										}
										if (null != mCustomWaitDialog) {
											mCustomWaitDialog.cancel();
											mCustomWaitDialog = null;
										}
									}
								});
						mCustomWaitDialog.setMessage(mContextWeakReference.get().getString(R.string.loading));
						mCustomWaitDialog.show();
					}
				});
			}
		}
	}

	@Override
	public void onRetry(int retryNo) {

	}

	@Override
	public void onProgress(int bytesWritten, int totalSize) {

	}

	@Override
	public void onFinish(AsyncHttpRequest asyncHttpRequest) {
		HandlerPostUI.getHandlerPostUI(new Runnable() {

			@Override
			public void run() {
//				if (mContextWeakReference.get() instanceof Activity) {
//					Activity activity = (Activity) mContextWeakReference.get();
//					if (activity.isFinishing()) {
//						return;
//					}
//				}
				if (null != mCustomWaitDialog) {
					mCustomWaitDialog.cancel();
					mCustomWaitDialog = null;
				}
			}
		});
	}

	@Override
	public void onCancel() {

	}

}
