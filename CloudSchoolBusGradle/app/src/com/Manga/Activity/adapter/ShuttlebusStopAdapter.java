package com.Manga.Activity.adapter;

import java.util.List;

import com.Manga.Activity.R;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusStopDto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShuttlebusStopAdapter extends BaseAdapter {
	private List<ShuttlebusStopDto> mShuttlebusStopList;
	private int mCurrentBusStopId;
	private Context cntx;

	public ShuttlebusStopAdapter(Context context)
	{
		super();
		cntx= context;
		setmShuttlebusStopList(null);
	}
	
	@Override
	public int getCount() {
		if (getmShuttlebusStopList() == null) {
			return 0;
		}
		return getmShuttlebusStopList().size();
	}

	@Override
	public Object getItem(int position) {
		//
		return getmShuttlebusStopList() != null ? getmShuttlebusStopList().get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		//
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(cntx).inflate(R.layout.list_shuttlebusstop_item, parent, false);
			holder.text_view_shuttlebusstop_name = (TextView)convertView.findViewById(R.id.shuttlebusstop_name);
			holder.text_view_shuttlebusstop_icon = (TextView)convertView.findViewById(R.id.shuttlebusstop_item_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}		
		
		if(getmShuttlebusStopList()!=null)
		{
			if(getmShuttlebusStopList().size()!=0)
			{
		        String busStopName = getmShuttlebusStopList().get(position).getName();
		        holder.text_view_shuttlebusstop_name.setText(busStopName);
		        if(getmShuttlebusStopList().get(position).getGeofenceid() == mCurrentBusStopId)
		        {
		        	holder.text_view_shuttlebusstop_icon.setBackgroundResource(0);
		        	holder.text_view_shuttlebusstop_icon.setBackgroundResource(R.drawable.class_share_button_left_up);
		        	holder.text_view_shuttlebusstop_name.setTextColor(Color.BLUE);
		        	holder.text_view_shuttlebusstop_name.setTextSize(28);
		        }
		        else
		        {
		        	holder.text_view_shuttlebusstop_icon.setBackgroundResource(0);
		        	holder.text_view_shuttlebusstop_icon.setBackgroundResource(R.drawable.account_btn_bg);
		        	holder.text_view_shuttlebusstop_name.setTextColor(Color.BLACK);
		        	holder.text_view_shuttlebusstop_name.setTextSize(18);
		        }
			}
		}
		return convertView;
    }

	class ViewHolder {
		public TextView  text_view_shuttlebusstop_name;
		public TextView  text_view_shuttlebusstop_icon;
	}
	
	public List<ShuttlebusStopDto> getmShuttlebusStopList() {
		return mShuttlebusStopList;
	}

	public void setmShuttlebusStopList(List<ShuttlebusStopDto> mShuttlebusStopList) {
		this.mShuttlebusStopList = mShuttlebusStopList;
	}

	public int getmCurrentBusStopId() {
		return mCurrentBusStopId;
	}

	public void setmCurrentBusStopId(int mCurrentBusStopId) {
		this.mCurrentBusStopId = mCurrentBusStopId;
	}
}	
