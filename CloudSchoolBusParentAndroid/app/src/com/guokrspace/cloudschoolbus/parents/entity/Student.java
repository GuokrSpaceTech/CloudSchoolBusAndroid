package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;

/**
 * 学生
 * 
 * @author lenovo
 * 
 */
//              "cnname":"王弘毅",
//              "birthday":"2010-10-10",
//              "sex":"1",
//              "avatar":"http:\/\/cloud.yunxiaoche.com\/avatar-tea-stu\/39724_39741.jpg",
//              "nickname":"大顺",
//              "studentid":"39738"}
public class Student implements Serializable {
	String cnname;
	String birthday;
	String sex;
	String avatar;
	String nickname;
	String studentid;

	public String getCnname() {
		return cnname;
	}

	public void setCnname(String cnname) {
		this.cnname = cnname;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getStudentid() {
		return studentid;
	}

	public void setStudentid(String studentid) {
		this.studentid = studentid;
	}
}

