package com.Manga.Activity.httputils;

import java.util.HashMap;

public class Params {
	// private String url = "http://v33.service.yunxiaoche.com/";
	private String url = "http://api35.yunxiaoche.com:81/";
	// private String url = "http://222.128.71.186:81/";
	// private String url = "http://apitest.yunxiaoche.com/";
	//private String url = "http://yxc.cloudapp.net/";
	private HashMap<String, String> map;

	/**
	 * @param function
	 *            方法名
	 * @param map
	 *            参数
	 */
	public Params(String function, HashMap<String, String> map) {
		super();
		this.url = this.url + function;
		this.map = map;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "Params url=" + url + ", map=" + map;
	}
}
