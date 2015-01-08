package com.cytx.dto;

import java.util.List;

/**
 * 问题追问Dto
 * 
 * @author xilehang
 * 
 */
public class QuestionAskedDto {

	private String problem_id;
	private String user_id;
	private String sign;
	private List<Object> content;
	private String atime;

	public String getProblem_id() {
		return problem_id;
	}

	public void setProblem_id(String problem_id) {
		this.problem_id = problem_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

	public String getAtime() {
		return atime;
	}

	public void setAtime(String atime) {
		this.atime = atime;
	}

}
