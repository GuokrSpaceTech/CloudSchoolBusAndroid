package com.android.support.jhf.network;

import java.lang.ref.WeakReference;

import android.content.Context;

abstract public class BaseHttpRequest implements IHttpRequest{
	
	protected WeakReference<Context> mContextWeakReference;
	
	private String encodingString = "UTF-8";
	private String charsetString = "UTF-8";

	public BaseHttpRequest(Context context){
		mContextWeakReference = new WeakReference<Context>(context);
	}
	/**
	 * 上传参数的编码需要urlencoding
	 * @param encodingString
	 */
	public void setEncoding(String encodingString){
		this.encodingString = encodingString;
	}
	/**
	 * 返回客户端接收字符串得编码,设置默认值
	 * @param charsetString
	 */
	public void setCharset(String charsetString) {
		this.charsetString = charsetString;
	}
	
	/**
	 *上传参数的编码需要urlencoding
	 * @return
	 */
	public String getEncoding() {
		return encodingString;
	}
	
	/**
	 * 返回客户端接收字符串得编码,设置默认值
	 * @return
	 */
	public String getCharset() {
		return charsetString;
	}
}
