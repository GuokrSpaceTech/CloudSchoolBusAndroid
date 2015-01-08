package com.android.support.jhf.network;

import android.content.Context;
import android.net.Proxy;
import android.text.TextUtils;

import com.android.support.jhf.network.loopj.android.http.AsyncHttpClient;
import com.android.support.jhf.network.loopj.android.http.ResponseHandlerInterface;

public class NetworkClient {

	private static AsyncHttpClient sAsyncHttpClient = new AsyncHttpClient();
	private static NetworkClient sNetworkClient = null;
	
	private static String sLoginToken = null;

	private NetworkClient() {

	}

	synchronized private static void newInstance() {
		if (null == sNetworkClient) {
			sNetworkClient = new NetworkClient();
		}
	}

	public static NetworkClient getNetworkClient() {
		if (null == sNetworkClient) {
			newInstance();
		}

		return sNetworkClient;
	}

	/**
	 * set default gloab user agent
	 * 
	 * @param defaultUserAgentString
	 */
	public void setDefaultUserAgent(String defaultUserAgentString) {
		sAsyncHttpClient.setUserAgent(defaultUserAgentString);
	}

	public void GetRequest(IHttpRequest iHttpRequest,
			ResponseHandlerInterface responseHandlerInterface) {
		GetRequest(iHttpRequest, responseHandlerInterface, null, null);
	}

	public void GetRequest(IHttpRequest iHttpRequest,
			ResponseHandlerInterface responseHandlerInterface,
			IStateListener iStateListener, IErrorListener iErrorListener) {
		if (null != iStateListener) {
			responseHandlerInterface.setStateListener(iStateListener);
		} else {
			responseHandlerInterface.setStateListener(new DefaultStateListener(
					iHttpRequest.getContext()));
		}
		if (null != iErrorListener) {
			responseHandlerInterface.setErrorListener(iErrorListener);
		} else {
			responseHandlerInterface.setErrorListener(new DefaultErrorListener(
					iHttpRequest.getContext()));
		}

		try {
			String host = Proxy.getDefaultHost();// 此处Proxy源自android.net
			int port = Proxy.getPort(iHttpRequest.getContext());// 同上
			if (!TextUtils.isEmpty(host)) {
				sAsyncHttpClient.setProxy(host, port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		sAsyncHttpClient.get(iHttpRequest.getContext(),
				iHttpRequest.getAbsoluteUrl(), iHttpRequest.getHeaders(),
				iHttpRequest.getRequestParams(), responseHandlerInterface);
	}

	public void PostRequest(IHttpRequest iHttpRequest,
			ResponseHandlerInterface responseHandlerInterface) {
		PostRequest(iHttpRequest, responseHandlerInterface, null, null);
	}

	public void PostRequest(IHttpRequest iHttpRequest,
			ResponseHandlerInterface responseHandlerInterface,
			IStateListener iStateListener, IErrorListener iErrorListener) {
		if (null != iStateListener) {
			responseHandlerInterface.setStateListener(iStateListener);
		} else {
			responseHandlerInterface.setStateListener(new DefaultStateListener(
					iHttpRequest.getContext()));
		}
		if (null != iErrorListener) {
			responseHandlerInterface.setErrorListener(iErrorListener);
		} else {
			responseHandlerInterface.setErrorListener(new DefaultErrorListener(
					iHttpRequest.getContext()));
		}

		try {
			String host = Proxy.getDefaultHost();// 此处Proxy源自android.net
			int port = Proxy.getPort(iHttpRequest.getContext());// 同上
			if (!TextUtils.isEmpty(host)) {
				sAsyncHttpClient.setProxy(host, port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != iHttpRequest.getRequestParams()) {
			sAsyncHttpClient
					.post(iHttpRequest.getContext(),
							iHttpRequest.getAbsoluteUrl(),
							iHttpRequest.getHeaders(),
							iHttpRequest.getRequestParams(),
							iHttpRequest.getPostContentType(),
							responseHandlerInterface);
		} else {
			sAsyncHttpClient
					.post(iHttpRequest.getContext(),
							iHttpRequest.getAbsoluteUrl(),
							iHttpRequest.getHeaders(),
							iHttpRequest.getRequestEntity(),
							iHttpRequest.getPostContentType(),
							responseHandlerInterface);
		}
	}
	
	/**
     * Cancels any pending (or potentially active) requests associated with the passed Context.
     * <p>&nbsp;</p> <b>Note:</b> This will only affect requests which were created with a non-null
     * android Context. This method is intended to be used in the onDestroy method of your android
     * activities to destroy all requests which are no longer required.
     *
     * @param context               the android Context instance associated to the request.
     * @param mayInterruptIfRunning specifies if active requests should be cancelled along with
     *                              pending requests.
     */
    public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
    	sAsyncHttpClient.cancelRequests(context, mayInterruptIfRunning);
    }
    
    /**
     * 保存登陆返回的全局token
     * @param token
     */
    public void setLoginToken(String token){
    	sLoginToken = token;
    }
    
    /**
     * 返回登陆返回的全局token
     * @return
     */
    public String getLoginToken() {
		return sLoginToken;
	}

}
