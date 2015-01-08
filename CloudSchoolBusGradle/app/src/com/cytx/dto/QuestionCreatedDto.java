package com.cytx.dto;

import java.util.List;

/**
 * 问题创建Dto
 * 
 * @author xilehang
 * 
 */
public class QuestionCreatedDto {

	private String user_id;
	private List<Object> content;
	private String sign;
	private String atime;
	private String clinic_no;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAtime() {
		return atime;
	}

	public void setAtime(String atime) {
		this.atime = atime;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getClinic_no() {
		return clinic_no;
	}

	public void setClinic_no(String clinic_no) {
		this.clinic_no = clinic_no;
	}

}
