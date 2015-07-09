package com.guokrspace.cloudschoolbus.parents.entity;

import com.android.support.utils.DateUtils;

import java.io.Serializable;

/**
 * 学生
 * 
 * @author lenovo
 * 
 */
public class Student implements Serializable {

//	[{"uid_student":"53834","uid_class":"49519","inactive":"0","birthday":"2001-02-10","cnname":"Ryan","nikename":"Ryan","sex":"1","classname":"掌声有请一期A班","schoolid":"105"}]

	public String uid_student;
	public String uid_class;
	public String inactive;
	public String birthday;
	public String cnname;
	public String nikename;
	public String sex;
	public String classname;
	public String schoolid;

	public String getUid_student() {
		return uid_student;
	}

	public void setUid_student(String uid_student) {
		this.uid_student = uid_student;
	}

	public String getUid_class() {
		return uid_class;
	}

	public void setUid_class(String uid_class) {
		this.uid_class = uid_class;
	}

	public String getInactive() {
		return inactive;
	}

	public void setInactive(String inactive) {
		this.inactive = inactive;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCnname() {
		return cnname;
	}

	public void setCnname(String cnname) {
		this.cnname = cnname;
	}

	public String getNikename() {
		return nikename;
	}

	public void setNikename(String nikename) {
		this.nikename = nikename;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}
}

