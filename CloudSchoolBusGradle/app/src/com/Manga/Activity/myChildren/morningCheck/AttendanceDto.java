package com.Manga.Activity.myChildren.morningCheck;

import java.util.List;

public class AttendanceDto {
	private String attendaceday1;
	private String attendaceday;
	private List<AttendanceRecordDto> record;

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
	
	public List<AttendanceRecordDto> getRecord() {
		return record;
	}

	public void setRecord(List<AttendanceRecordDto> records) {
		this.record = records;
	}

}
