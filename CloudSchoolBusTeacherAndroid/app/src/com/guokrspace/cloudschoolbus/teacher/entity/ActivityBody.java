package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 通知
 * @author Yang Kai
 *
 */
public class ActivityBody implements Serializable{
	public List<String> PList;

	public List<String> getPList() {
		return PList;
	}

	public void setPList(List<String> PList) {
		this.PList = PList;
	}
}
