package com.cytx.domain;

import java.util.List;

/**
 * 问题详情
 * @author xilehang
 *
 */
public class QuestionDetailDomain {

	private QuestionDetailProblemDomain problem;
	private List<QuestionDetailContentDomain> content;
	private QuestionDetailDoctorDomain doctor;

	public QuestionDetailProblemDomain getProblem() {
		return problem;
	}

	public void setProblem(QuestionDetailProblemDomain problem) {
		this.problem = problem;
	}

	public List<QuestionDetailContentDomain> getContent() {
		return content;
	}

	public void setContent(List<QuestionDetailContentDomain> content) {
		this.content = content;
	}

	public QuestionDetailDoctorDomain getDoctor() {
		return doctor;
	}

	public void setDoctor(QuestionDetailDoctorDomain doctor) {
		this.doctor = doctor;
	}

}
