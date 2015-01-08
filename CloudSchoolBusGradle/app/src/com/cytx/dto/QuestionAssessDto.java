package com.cytx.dto;

/**
 * 问题评价Dto
 * 
 * @author xilehang
 * 
 */
public class QuestionAssessDto {

	private String user_id;
	private String problem_id;
	private int star;
	private String content;
	private String atime;
	private String sign;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getProblem_id() {
		return problem_id;
	}

	public void setProblem_id(String problem_id) {
		this.problem_id = problem_id;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
