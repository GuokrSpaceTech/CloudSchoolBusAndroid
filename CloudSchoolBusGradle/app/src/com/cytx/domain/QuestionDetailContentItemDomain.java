package com.cytx.domain;

/**
 * 每一项问题回答内容：有可能是图片或文字，有可能是医生回答或病人追问的
 * 
 * @author xilehang
 * 
 */
public class QuestionDetailContentItemDomain {

	private String type;// 内容类型：image、text
	private String text;// 文字内容
	private String file;// 图片url

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
