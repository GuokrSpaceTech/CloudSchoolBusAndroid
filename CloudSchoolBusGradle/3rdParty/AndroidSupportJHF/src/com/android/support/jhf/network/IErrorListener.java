package com.android.support.jhf.network;

import org.apache.http.Header;

public interface IErrorListener {

	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error);
}
