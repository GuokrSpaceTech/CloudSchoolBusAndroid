package com.cytx.domain;

/**
 * 问题详情：服务器返回的content信息
 * 
 * @author xilehang
 * 
 */
public class QuestionDetailContentDomain {

	private long id;
	private String type;
	private long created_time_ms;
	private String content;

	// private List<QuestionDetailContentItemDomain> content;

	public String getType() {
		return type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getCreated_time_ms() {
		return created_time_ms;
	}

	public void setCreated_time_ms(long created_time_ms) {
		this.created_time_ms = created_time_ms;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	// public List<QuestionDetailContentItemDomain> getContent() {
	// return content;
	// }

	// public void setContent(List<QuestionDetailContentItemDomain> content) {
	// this.content = content;
	// }

}
