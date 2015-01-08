package com.cytx.domain;

/**
 * 医生信息：各种指数
 * 
 * @author xilehang
 * 
 */
public class IndexDomain {

	private boolean trend;
	private int rate;
	private String name;
	private String hint;

	public boolean isTrend() {
		return trend;
	}

	public void setTrend(boolean trend) {
		this.trend = trend;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

}
