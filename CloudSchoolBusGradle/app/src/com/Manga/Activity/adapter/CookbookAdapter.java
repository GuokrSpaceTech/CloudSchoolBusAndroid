package com.Manga.Activity.adapter;

import java.util.List;
import java.util.Map;

import com.Manga.Activity.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CookbookAdapter extends ArrayAdapter <Map<String,String>>{
	private List<Map<String,String>> list;
	public CookbookAdapter(Context context,List<Map<String,String>> list) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.cookbook_item, list);
		this.list=list;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View item=View.inflate(getContext(), R.layout.cookbook_item, null);
		Map<String,String> map=getItem(position);
		ImageView image=(ImageView) item.findViewById(R.id.image);
		TextView content=(TextView) item.findViewById(R.id.content);
		TextView text=(TextView) item.findViewById(R.id.text);
		String menu_type_name=map.get("menu_type_name");
		String content_=map.get("content");
		if(menu_type_name!=null){
			if(menu_type_name.equals("早餐")){
				image.setBackgroundResource(R.drawable.breakfast);
			}else if(menu_type_name.equals("加餐")){
				image.setBackgroundResource(R.drawable.extra_meal);
			}else if(menu_type_name.equals("午餐")){
				image.setBackgroundResource(R.drawable.lunch);
			}else if(menu_type_name.equals("午点")){
				image.setBackgroundResource(R.drawable.snack);
			}else if(menu_type_name.equals("晚餐")){
				image.setBackgroundResource(R.drawable.supper);
			}else {
				image.setBackgroundResource(R.drawable.extra_meal);
			}
		}
		content.setText(content_);
		text.setText(menu_type_name);
		return item;
	}
	
	public List<Map<String, String>> getList() {
		return list;
	}
	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}
	
}
