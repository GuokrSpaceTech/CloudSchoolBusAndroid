package com.android.support.jhf.network;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.HttpHostConnectException;

import android.R.string;
import android.content.Context;
import android.text.TextUtils;

/**
 * 错误异常处理类
 * 
 * @author Administrator
 * 
 */
public class ErrorExceptionHandler {

	public static final String ERR_NET_CONN = "你的网络不给力";
	public static final String ERR_NET_TIMEOUT = ERR_NET_CONN;
	public static final String ERR_SERVER_RETURN = "服务器返回数据错误";

	public ErrorExceptionHandler() {
	}

	/**
	 * 网络异常处理类
	 * 
	 * @param errorString
	 *            用于输出错误字符串
	 * @param error
	 */
	public synchronized static void NetworkExceptionHandler(
			StringBuffer errorString, Throwable error) {

		errorString.delete(0, errorString.length());

		if (null != error) {
			if (error instanceof HttpResponseException) {
				// 返回错误
				errorString.append(error.getMessage());
			} else if (error instanceof UnknownHostException) {
				// 主机名错误
				errorString.append(ERR_NET_CONN);
			} else if (error instanceof UnsupportedEncodingException) {
				// errorString.append("字符串解码错误");
				errorString.append(ERR_NET_CONN);

			} else if (error instanceof ConnectException) {
				// errorString.append("IP地址错误");
				errorString.append(ERR_NET_CONN);

				// 原因:指定ip地址的机器不能找到（也就是说从当前机器不存在到指定ip路由），
				// 或者是该ip存在，但找不到指定的端口进行监听。应该首先检查客户端的ip和port是否写错了，
				// 假如正确则从客户端ping一下服务器看是否能ping通，假如能ping通（服务服务器端把ping禁掉则需要另外的办法），
				// 则看在服务器端的监听指定端口的程序是否启动。
			} else if (error instanceof IllegalStateException) {
				// errorString.append("不是有效的URL地址");
				errorString.append(ERR_NET_CONN);

				// 1）同一个页面中再次调用response.sendRedirect()方法。
				// 2）提交的URL错误，即不是个有效的URL。
			} else if (error instanceof IllegalArgumentException) {
				// errorString.append("参数不正确");
				errorString.append(ERR_NET_CONN);
				// 抛出的异常表明向方法传递了一个不合法或不正确的参数。

			} else if (error instanceof SocketException) {
				errorString.append(ERR_NET_CONN);

				// 切换连接方式，返回false一般表明之前有切换尝试过，
				// 仍失败应该是手机当前没有可用连接,故返回错误回复.
			} else if (error instanceof ClientProtocolException) {
				// errorString.append("网络协议错误");
				errorString.append(ERR_NET_CONN);

				// 协议错误 在HTTP协议发出错误信号。
			} else if (error instanceof SocketTimeoutException) {
				errorString.append(ERR_NET_TIMEOUT);

				// 这个异常比较常见，socket超时。
				// 一般有2个地方会抛出这个，一个是connect的时候，
				// 这个超时参数由connect(SocketAddress endpoint,int timeout)中的后者来决定，
				// 还有就是setSoTimeout(int timeout)，这个是设定读取的超时时间。它们设置成0均表示无限大。
				// 超时
			} else if (error instanceof MalformedURLException) {
				// "No valid URI scheme was provided"
				// errorString.append("无效的URL");
				errorString.append(ERR_NET_CONN);

			} else if (error instanceof InterruptedIOException) {
				errorString.append(ERR_NET_CONN);
			} else if (error instanceof IOException) {
				errorString.append(ERR_NET_CONN);
			} else if (error instanceof Exception) {
				errorString.append(ERR_NET_CONN);
			} else if (error instanceof Throwable) {
				errorString.append(ERR_NET_CONN);
			} else {
				errorString.append(ERR_NET_CONN);
			}
			error.printStackTrace();
		} else {

		}

	}

}
