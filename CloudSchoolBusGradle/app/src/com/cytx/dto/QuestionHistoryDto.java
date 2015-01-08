package com.cytx.dto;

/**
 * 问题历史
 * @author xilehang
 *
 */
public class QuestionHistoryDto {

	private String user_id;
	private int start_num;
	private int count;
	private String atime;
	private String sign;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getStart_num() {
		return start_num;
	}

	public void setStart_num(int start_num) {
		this.start_num = start_num;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAtime() {
		return atime;
	}

	public void setAtime(String atime) {
		this.atime = atime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
