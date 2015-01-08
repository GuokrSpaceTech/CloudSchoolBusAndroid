package com.cytx.domain;

/**
 * 问题创建接口返回的信息
 * 
 * @author xilehang
 * 
 */
public class QuestionCreatedDomain extends ErrorDomain{
	private String problem_id;

	public String getProblem_id() {
		return problem_id;
	}

	public void setProblem_id(String problem_id) {
		this.problem_id = problem_id;
	}
}
