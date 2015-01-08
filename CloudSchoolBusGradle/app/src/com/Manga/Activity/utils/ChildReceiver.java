package com.Manga.Activity.utils;

import android.graphics.Bitmap;

public class ChildReceiver {

	private String id;
	private String pid;
	private String filePath;
	private String relationship;
	private Bitmap fileBitmap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public Bitmap getFileBitmap() {
		return fileBitmap;
	}

	public void setFileBitmap(Bitmap fileBitmap) {
		this.fileBitmap = fileBitmap;
	}

}
