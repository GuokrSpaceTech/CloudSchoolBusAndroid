package com.guokrspace.cloudschoolbus.parents.entity;

public class AttendanceRecordDto {
	private Long createtime;
	private String imgpath;

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long attendanceTime) {
		this.createtime = attendanceTime;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String picUrl) {
		this.imgpath = picUrl;
	}
}