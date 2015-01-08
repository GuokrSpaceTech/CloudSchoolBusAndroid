package com.Manga.Activity.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;

public abstract class MyAdapter {
	private List<Map<String,String>> list;
	private Context context;
	private int size;
	public abstract View getView(int position);
	public MyAdapter(Context context,List<Map<String,String>> list) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.list=list;
		this.size=list.size();
	}
	public List<Map<String, String>> getList() {
		return list;
	}
	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}
	public Context getContext() {
		return context;
	}
	public int getSize() {
		return size;
	}
}
