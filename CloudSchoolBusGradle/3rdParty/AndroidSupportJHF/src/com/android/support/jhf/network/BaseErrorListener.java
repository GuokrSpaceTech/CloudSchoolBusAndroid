package com.android.support.jhf.network;

import org.apache.http.Header;

abstract public class BaseErrorListener implements IErrorListener {

	public BaseErrorListener() {
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {
		
	}

}
