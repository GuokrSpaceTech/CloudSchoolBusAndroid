package com.Manga.Activity.myChildren.Shuttlebus;

//Result code=1, 
//content={"allstop":[{"geofenceid":"2","name":"\u67f3\u82b3"}],
//         "currentstop":"\u67f3\u82b3","currentstopid":"2",
//         "notice":[{"time":"12:43"},{"time":"12:43"}]}
import java.util.List;

public class ShuttlebusStopListDto {
	private List<ShuttlebusStopDto> allstop;
	private String currentstop;
	private int currentstopid;
    private List<ShuttlebusStopNoticeDto> notice;
    
	public List<ShuttlebusStopDto> getAllstop() {
		return allstop;
	}
	
	public void setAllstop(List<ShuttlebusStopDto> allstop) {
		this.allstop = allstop;
	}
	
	public List<ShuttlebusStopNoticeDto> getNotice() {
		return notice;
	}
	
	public void setNotice(List<ShuttlebusStopNoticeDto> notice) {
		this.notice = notice;
	}

	public String getCurrentstop() {
		return currentstop;
	}

	public void setCurrentstop(String currentstop) {
		this.currentstop = currentstop;
	}

	public int getCurrentstopid() {
		return currentstopid;
	}

	public void setCurrentstopid(int currentstopid) {
		this.currentstopid = currentstopid;
	}
}
