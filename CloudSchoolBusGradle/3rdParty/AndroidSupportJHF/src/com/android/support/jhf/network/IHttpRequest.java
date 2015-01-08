package com.android.support.jhf.network;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import com.android.support.jhf.network.loopj.android.http.RequestParams;

import android.content.Context;

public interface IHttpRequest {

	public Context getContext();
	
	public String getAbsoluteUrl();
	
	public Header[] getHeaders();
	
	public RequestParams getRequestParams();
	
	public HttpEntity getRequestEntity();
	
	public String getPostContentType();
	
}
