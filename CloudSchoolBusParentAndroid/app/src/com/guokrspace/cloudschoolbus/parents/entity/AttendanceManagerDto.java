package com.guokrspace.cloudschoolbus.parents.entity;

import java.util.List;

/* Example: We only cares about the "record" field under "attendance"
"{
"festival":[
            "2014-10-01,\u56fd\u5e86\u8282",
            "2014-10-02,\u56fd\u5e86\u8282",
            "2014-10-03,\u56fd\u5e86\u8282",
            "2014-10-04,\u56fd\u5e86\u8282",
            "2014-10-05,\u56fd\u5e86\u8282",
            "2014-10-06,\u56fd\u5e86\u8282",
            "2014-10-07,\u56fd\u5e86\u8282"],
 "attendance":[
              {"attendaceday":"141002",
               "attendaceday1":"2014-10-02",
               "record":[{"createtime":"1412217731","imgpath":null},
                         {"createtime":"1412220454","imgpath":null},
                         {"createtime":"1412220455","imgpath":null},
                         {"createtime":"1412220457","imgpath":null},
                         {"createtime":"1412220458","imgpath":null},
                         {"createtime":"1412220459","imgpath":null}]},

              {"attendaceday":"141018",
               "attendaceday1":"2014-10-18",
               "record":[{"createtime":"1413620676","imgpath":"cloud.yunxiaoche.com\/attendance-images\/source_51860_1413536868.jpg"},
                         {"createtime":"1413620781","imgpath":"cloud.yunxiaoche.com\/attendance-images\/source_51719_1413332389.jpg"}]}]}"
*/

public class AttendanceManagerDto {
		
	private List<String> festival;

	private List<AttendanceDto> attendance;
	
	private int total_num_attendance_records;
	
	public List<String> getFestival() {
		return festival;
	}
	
	public void setFestivalList(List<String> festivalList) {
		this.festival = festivalList;
	}
	
	public List<AttendanceDto> getAttendance() {
		return attendance;
	}

	public void setAttendance(List<AttendanceDto> attendanceBeanList) {
		this.attendance = attendanceBeanList;
	}
	
	public void setTotal_num_attendance_records(int num)
	{
		this.total_num_attendance_records = num;
	}
	
	public int getTotal_num_attendance_records()
	{
		return total_num_attendance_records;
	}
}
