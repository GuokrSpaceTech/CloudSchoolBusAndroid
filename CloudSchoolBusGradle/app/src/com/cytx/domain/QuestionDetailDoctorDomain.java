package com.cytx.domain;

/**
 * 问题详情：服务器返回的doctor信息
 * 
 * @author xilehang
 * 
 */
public class QuestionDetailDoctorDomain {

	private String id;
	private String name;
	private String image;
	private String title;
	private String clinic;
	private String hospital;
	private String level_title;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getClinic() {
		return clinic;
	}

	public void setClinic(String clinic) {
		this.clinic = clinic;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getLevel_title() {
		return level_title;
	}

	public void setLevel_title(String level_title) {
		this.level_title = level_title;
	}

}
