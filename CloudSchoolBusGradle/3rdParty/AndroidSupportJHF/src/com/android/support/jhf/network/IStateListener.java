package com.android.support.jhf.network;

import com.android.support.jhf.network.loopj.android.http.AsyncHttpRequest;

public interface IStateListener {

	public void onStart(AsyncHttpRequest asyncHttpRequest);

	public void onRetry(int retryNo);
	
	public void onProgress(int bytesWritten, int totalSize);
	
	public void onFinish(AsyncHttpRequest asyncHttpRequest);
	
	public void onCancel();
}
