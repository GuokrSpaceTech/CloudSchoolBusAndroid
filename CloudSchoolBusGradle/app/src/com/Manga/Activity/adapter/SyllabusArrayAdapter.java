package com.Manga.Activity.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Manga.Activity.R;

public class SyllabusArrayAdapter extends ArrayAdapter <Map<String,String>>{
	private List<Map<String,String>> list;
	public SyllabusArrayAdapter(Context context,List<Map<String,String>> list) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.syllabus_item, list);
		this.list=list;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=View.inflate(getContext(), R.layout.syllabus_item, null);
		TextView time=(TextView) view.findViewById(R.id.time);
		TextView content=(TextView) view.findViewById(R.id.content);
		time.setText(getItem(position).get("scheduletime"));
		content.setText(getItem(position).get("cnname"));
		return view;
	}
	public List<Map<String, String>> getList() {
		return list;
	}
	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}
	
}
