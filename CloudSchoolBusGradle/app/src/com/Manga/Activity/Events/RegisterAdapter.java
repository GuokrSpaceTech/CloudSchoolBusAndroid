package com.Manga.Activity.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.Manga.Activity.Events.ShowEventsRegisterActivity;
import com.Manga.Activity.utils.ActivityUtil;

public class RegisterAdapter extends ArrayAdapter<HashMap<String, String>> {
	private ArrayList<HashMap<String, String>> list;
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
	private long toYear;
	private long nowTime;

	public RegisterAdapter(Context context,
			ArrayList<HashMap<String, String>> list) {
		super(context, R.layout.register_item, list);
		this.list = list;
		try {
			Date date = new Date();
			SimpleDateFormat foo = new SimpleDateFormat("yyyy");
			Date tmp = foo.parse(spl.format(date).split("-")[0]);
			toYear = tmp.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		nowTime = System.currentTimeMillis();
		if (convertView == null || convertView.getTag() == null) {
			convertView = View.inflate(getContext(), R.layout.register_item,
					null);
			holder = new ViewHolder();
			holder.content = (RelativeLayout) convertView
					.findViewById(R.id.item);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.state = (TextView) convertView.findViewById(R.id.state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> map = getItem(position);
		final String mapEvents_id = map.get("events_id");
		final String mapTitle = map.get("title");
		final String mapAddtime = map.get("addtime");
		final String mapSignupStatus = map.get("SignupStatus");
		final String isSignup = map.get("isSignup");
        final String mapHtmlurl = map.get("htmlurl");
		//final String mapHtmlurl = "http://mmbiz.qpic.cn/mmbiz/LVRWo7icaR62ibq3N56VNKVkn6jjW8AYqQGBDhDKXfflh7rqMBxnlpFDs6Tibdx3GLn2kEkkte71nvPTt7IuRFQsg/640?wx_fmt=jpeg";
		holder.content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(),
						ShowEventsRegisterActivity.class);
				intent.putExtra("events_id", mapEvents_id);
				intent.putExtra("htmlurl", mapHtmlurl);
				intent.putExtra("isSignup", isSignup);
				intent.putExtra("title", mapTitle);
				ActivityUtil.startActivity(ActivityUtil.main, intent);
			}
		});
		// 设置标题
		if (mapTitle != null) {
			holder.title.setText(mapTitle);
		}
		// 时间处理
		if (mapAddtime != null) {
			long foo = Long.parseLong(mapAddtime);
			long tmp = nowTime - foo;
			
			if (foo > toYear) {
				if (tmp < 12 * 60 * 60 * 1000) {
					if (tmp < 60 * 60 * 1000) {
						if (tmp <= 60 * 1000) {
							holder.time.setText("1"
									+ getContext().getResources().getString(
											R.string.minute_befor));
						} else {
							holder.time.setText((tmp)
									/ (60 * 1000)
									+ getContext().getResources().getString(
											R.string.minute_befor));
						}
					} else {
						holder.time.setText(tmp
								/ (60 * 60 * 1000)
								+ getContext().getResources().getString(
										R.string.hour_befor));
					}
				} else {
					holder.time.setText(toYearSdf.format(new Date(foo)));
				}
			} else {
				holder.time.setText(spl.format(new Date(foo)));
			}
		}
		// 状态处理
		if (mapSignupStatus != null) {
			switch (Integer.parseInt(mapSignupStatus)) {
			case 1:
				holder.state.setText(getContext().getResources().getString(
						R.string.underway));
				break;
			case -1:
				holder.state.setText(getContext().getResources().getString(
						R.string.dns));
				break;
			case -2:
				holder.state.setText(getContext().getResources().getString(
						R.string.complete));
				break;
			case -3:
				holder.state.setText(getContext().getResources().getString(
						R.string.full));
				break;
			}
		}
		return convertView;
	}

	static class ViewHolder {
		RelativeLayout content;
		TextView title;
		TextView time;
		TextView state;
	}

	public ArrayList<HashMap<String, String>> getList() {
		return list;
	}

	public void setList(ArrayList<HashMap<String, String>> list) {
		this.list = list;
	}
}
