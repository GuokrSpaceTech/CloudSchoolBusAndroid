package com.android.support.jhf.network;

import com.android.support.jhf.network.loopj.android.http.AsyncHttpRequest;

abstract public class BaseStateListener implements IStateListener {

	public BaseStateListener() {
	}

	@Override
	public void onStart(AsyncHttpRequest asyncHttpRequest) {

	}

	@Override
	public void onRetry(int retryNo) {

	}

	@Override
	public void onProgress(int bytesWritten, int totalSize) {

	}

	@Override
	public void onFinish(AsyncHttpRequest asyncHttpRequest) {

	}
	
	@Override
	public void onCancel() {
		
	}

}
