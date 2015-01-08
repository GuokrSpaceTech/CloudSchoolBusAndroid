package com.Manga.Activity.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Manga.Activity.R;

@SuppressLint("SimpleDateFormat")
public class ReportAdapter extends ArrayAdapter<Map<String, String>> {
	private ArrayList<Map<String, String>> list;
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toY = new SimpleDateFormat("yyyy");
	private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
	private long toYear;
	{
		try {
			toYear = toY.parse(toY.format(new Date())).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ReportAdapter(Context context, ArrayList<Map<String, String>> list) {
		super(context, R.layout.item_report, list);
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Map<String, String> map = getItem(position);
		final ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = View.inflate(getContext(), R.layout.item_report, null);
			holder.tv_report_title = (TextView) convertView.findViewById(R.id.tv_report_title);
			holder.tv_report_time = (TextView) convertView.findViewById(R.id.tv_report_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String title = map.get("reportname");
		String reprotTime = map.get("reporttime");
		long fooTemp = Long.parseLong(reprotTime) * 1000;
		reprotTime = spl.format(new Date(fooTemp));
		String mapAddtime = map.get("createtime");
		String tempTime = "";
		if (mapAddtime != null) {
			long foo = Long.parseLong(mapAddtime) * 1000;
			long tmp = System.currentTimeMillis() - foo;
			if (foo > toYear) {
				if (tmp < 12 * 60 * 60 * 1000) {

					if (tmp < 60 * 60 * 1000) {
						if (tmp <= 60 * 1000) {
							tempTime = "1" + getContext().getResources().getString(R.string.minute_befor);
						} else {
							tempTime = "1" + getContext().getResources().getString(R.string.minute_befor);
						}
					} else {
						tempTime = tmp / (60 * 60 * 1000) + getContext().getResources().getString(R.string.hour_befor);
					}
				} else {
					tempTime = toYearSdf.format(new Date(foo));
				}
			} else {
				tempTime = toYearSdf.format(new Date(foo));
			}
		}
		holder.tv_report_title.setText(title +"  "+ reprotTime);
		holder.tv_report_time.setText(tempTime);
		return convertView;
	}

	static class ViewHolder {
		TextView tv_report_title;
		TextView tv_report_time;

	}

	public ArrayList<Map<String, String>> getList() {
		return list;
	}

	public void setList(ArrayList<Map<String, String>> list) {
		this.list = list;
	}
}