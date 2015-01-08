package com.Manga.Activity.myChildren.Shuttlebus;
//Result code=1, 
//content={"allstop":[{"geofenceid":"2","name":"\u67f3\u82b3"}],
//       "currentstop":"\u67f3\u82b3","currentstopid":"2",
//       "notice":[{"time":"12:43"},{"time":"12:43"}]}
public class ShuttlebusStopNoticeDto {
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
