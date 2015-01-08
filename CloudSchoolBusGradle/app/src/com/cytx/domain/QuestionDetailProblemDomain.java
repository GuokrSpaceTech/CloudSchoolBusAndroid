package com.cytx.domain;

/**
 * 问题详情：服务器返回的problem信息
 * 
 * @author xilehang
 * 
 */
public class QuestionDetailProblemDomain {
	private long id;
	private String ask;
	private int star;
	private String clinic_no;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getClinic_no() {
		return clinic_no;
	}

	public void setClinic_no(String clinic_no) {
		this.clinic_no = clinic_no;
	}

}
