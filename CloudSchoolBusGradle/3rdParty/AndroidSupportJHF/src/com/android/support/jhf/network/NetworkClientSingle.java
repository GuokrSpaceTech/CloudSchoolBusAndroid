package com.android.support.jhf.network;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.Proxy;
import android.text.TextUtils;

import com.android.support.jhf.network.loopj.android.http.AsyncHttpClient;
import com.android.support.jhf.network.loopj.android.http.RequestHandle;
import com.android.support.jhf.network.loopj.android.http.ResponseHandlerInterface;
/**
 * 队列式的一个一个发送网络请求
 * @author lenovo
 *
 */
public class NetworkClientSingle {

	private static AsyncHttpClient sAsyncHttpClient = new AsyncHttpClient();
	private static NetworkClientSingle sNetworkClient = null;

	private static String sLoginToken = null;

	private NetworkClientSingle() {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
				0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						100000), new ThreadPoolExecutor.CallerRunsPolicy());
		sAsyncHttpClient.setThreadPool(threadPoolExecutor);
	}

	synchronized private static void newInstance() {
		if (null == sNetworkClient) {
			sNetworkClient = new NetworkClientSingle();
		}
	}

	public static NetworkClientSingle getNetworkClient() {
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

	public RequestHandle PostRequest(IHttpRequest iHttpRequest,
			ResponseHandlerInterface responseHandlerInterface) {
		return PostRequest(iHttpRequest, responseHandlerInterface, null, null);
	}

	public RequestHandle PostRequest(IHttpRequest iHttpRequest,
			ResponseHandlerInterface responseHandlerInterface,
			IStateListener iStateListener, IErrorListener iErrorListener) {
		RequestHandle requestHandle = null;
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
			requestHandle = sAsyncHttpClient
					.post(iHttpRequest.getContext(),
							iHttpRequest.getAbsoluteUrl(),
							iHttpRequest.getHeaders(),
							iHttpRequest.getRequestParams(),
							iHttpRequest.getPostContentType(),
							responseHandlerInterface);
		} else {
			requestHandle = sAsyncHttpClient
					.post(iHttpRequest.getContext(),
							iHttpRequest.getAbsoluteUrl(),
							iHttpRequest.getHeaders(),
							iHttpRequest.getRequestEntity(),
							iHttpRequest.getPostContentType(),
							responseHandlerInterface);
		}
		return requestHandle;
	}

	/**
	 * Cancels any pending (or potentially active) requests associated with the
	 * passed Context.
	 * <p>
	 * &nbsp;
	 * </p>
	 * <b>Note:</b> This will only affect requests which were created with a
	 * non-null android Context. This method is intended to be used in the
	 * onDestroy method of your android activities to destroy all requests which
	 * are no longer required.
	 * 
	 * @param context
	 *            the android Context instance associated to the request.
	 * @param mayInterruptIfRunning
	 *            specifies if active requests should be cancelled along with
	 *            pending requests.
	 */
	public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		sAsyncHttpClient.cancelRequests(context, mayInterruptIfRunning);
	}

	/**
	 * 保存登陆返回的全局token
	 * 
	 * @param token
	 */
	public void setLoginToken(String token) {
		sLoginToken = token;
	}

	/**
	 * 返回登陆返回的全局token
	 * 
	 * @return
	 */
	public String getLoginToken() {
		return sLoginToken;
	}

}
