package com.guokrspace.cloudschoolbus.parents.entity;

import java.util.List;

public class AttendanceDto {
	private String attendaceday1;
	private String attendaceday;
	private List<AttendanceRecord> record;

	public String getAttendaceday() {
		return attendaceday;
	}

	public void setAttendaceday(String attendaceday) {
		this.attendaceday = attendaceday;
	}

	public String getAttendaceday1() {
		return attendaceday1;
	}

	public void setAttendaceday1(String attendaceday1) {
		this.attendaceday1 = attendaceday1;
	}
	
	public List<AttendanceRecord> getRecord() {
		return record;
	}

	public void setRecord(List<AttendanceRecord> records) {
		this.record = records;
	}

}
