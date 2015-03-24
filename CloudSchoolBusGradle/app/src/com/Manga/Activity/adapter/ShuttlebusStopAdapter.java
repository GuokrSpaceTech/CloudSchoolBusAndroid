package com.Manga.Activity.adapter;

import java.util.List;

import com.Manga.Activity.R;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusStopDto;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ShuttlebusStopAdapter extends BaseExpandableListAdapter{
	private List<ShuttlebusStopDto> mShuttlebusStopList;
	private int mCurrentBusStopId;

    public int getmCurrentBusLineId() {
        return mCurrentBusLineId;
    }

    public void setmCurrentBusLineId(int mCurrentBusLineId) {
        this.mCurrentBusLineId = mCurrentBusLineId;
    }

    private int mCurrentBusLineId;
	private Context cntx;

	public ShuttlebusStopAdapter(Context context)
	{
		super();
		cntx= context;
		setmShuttlebusStopList(null);
	}
	
    @Override
    public int getGroupCount() {
        return mShuttlebusStopList==null?0:mShuttlebusStopList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mShuttlebusStopList==null?0:mShuttlebusStopList.get(groupPosition).getStop().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mShuttlebusStopList==null?null:mShuttlebusStopList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mShuttlebusStopList==null?null:mShuttlebusStopList.get(groupPosition).getStop().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = LayoutInflater.from(cntx).inflate(R.layout.list_shuttlebusstop_itemgroup, parent, false);
            holder.text_view_shuttlebusline_name = (TextView)convertView.findViewById(R.id.shuttlebusline_name);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        if(getmShuttlebusStopList()!=null)
        {
            if(getmShuttlebusStopList().size()!=0)
            {
                String busLineName = getmShuttlebusStopList().get(groupPosition).getLinename();
                holder.text_view_shuttlebusline_name.setText("校车路线： "+busLineName);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(cntx).inflate(R.layout.list_shuttlebusstop_item, parent, false);
            holder.text_view_shuttlebusstop_name = (TextView)convertView.findViewById(R.id.shuttlebusstop_name);
            holder.text_view_shuttlebusstop_icon = (TextView)convertView.findViewById(R.id.shuttlebusstop_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        if(getmShuttlebusStopList()!=null)
        {
            if(getmShuttlebusStopList().size()!=0)
            {
                String busStopName = getmShuttlebusStopList().get(groupPosition).getStop().get(childPosition).getName();
                holder.text_view_shuttlebusstop_name.setText(busStopName);
                if(getmShuttlebusStopList().get(groupPosition).getStop().get(childPosition).getGeofenceid() == mCurrentBusStopId)
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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ChildViewHolder {
		public TextView  text_view_shuttlebusstop_name;
		public TextView  text_view_shuttlebusstop_icon;
	}

    class GroupViewHolder {
        public TextView text_view_shuttlebusline_name;
//        public TextView text_view_shuttlebusline_icon;
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
