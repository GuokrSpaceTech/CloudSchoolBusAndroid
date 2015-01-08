package com.cytx.domain;

/**
 * 服务器返回的信息error 和 error_msg
 * @author xilehang
 *
 */
public class ErrorDomain {
	
	private int error;
	private String error_msg;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}


}
