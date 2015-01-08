package com.Manga.Activity.httputils;

public class Result {
	private String code;
	private String content;
	public Result() {
		// TODO Auto-generated constructor stub
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Result code=" + code + ", content=" + content;
	}
}
