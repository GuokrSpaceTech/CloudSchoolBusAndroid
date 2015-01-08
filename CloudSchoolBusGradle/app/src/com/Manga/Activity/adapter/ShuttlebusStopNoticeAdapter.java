package com.Manga.Activity.adapter;

import java.util.List;

import com.Manga.Activity.R;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusStopNoticeDto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShuttlebusStopNoticeAdapter extends BaseAdapter{
	private Context mContext;
	private List<ShuttlebusStopNoticeDto> mListNotice;
	
	public ShuttlebusStopNoticeAdapter(Context cntx)
	{
		mContext = cntx;
	}
	
	@Override
	public int getCount() {
		if (getmListNotice() == null) {
			return 0;
		}
		return getmListNotice().size();
	}

	@Override
	public Object getItem(int position) {
		return getmListNotice() != null ? getmListNotice().get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_shuttlebusstop_notice_item, parent, false);
			holder.text_view_shuttlebusstop_notice = (TextView) convertView.findViewById(R.id.shuttlebusstop_notice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}		
		
		if(getmListNotice()!=null)
		{
			if(getmListNotice().size()!=0)
			{
		        String busArrivalNotice = getmListNotice().get(position).getTime();
		        holder.text_view_shuttlebusstop_notice.setText(busArrivalNotice + " 校车即将进站");
			}
		}
		return convertView;
    }
	
	public List<ShuttlebusStopNoticeDto> getmListNotice() {
		return mListNotice;
	}

	public void setmListNotice(List<ShuttlebusStopNoticeDto> mListNotice) {
		this.mListNotice = mListNotice;
	}

	class ViewHolder {
		public TextView text_view_shuttlebusstop_notice;
	}
}
