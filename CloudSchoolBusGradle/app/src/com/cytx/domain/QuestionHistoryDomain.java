package com.cytx.domain;

/**
 * 获取问题历史信息
 * @author xilehang
 *
 */
public class QuestionHistoryDomain {

	// problem
	private QuestionHistoryProblemDomain problem;
	// doctor：由于此处的信息和问题详情的信息一样，所以直接就引用了
	private QuestionDetailDoctorDomain doctor;

	public QuestionHistoryProblemDomain getProblem() {
		return problem;
	}

	public void setProblem(QuestionHistoryProblemDomain problem) {
		this.problem = problem;
	}

	public QuestionDetailDoctorDomain getDoctor() {
		return doctor;
	}

	public void setDoctor(QuestionDetailDoctorDomain doctor) {
		this.doctor = doctor;
	}

}
