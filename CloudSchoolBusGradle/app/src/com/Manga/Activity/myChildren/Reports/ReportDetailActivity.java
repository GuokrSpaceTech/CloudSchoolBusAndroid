package com.Manga.Activity.myChildren.Reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.umeng.analytics.MobclickAgent;

public class ReportDetailActivity extends BaseActivity {
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private TextView tv_time, tv_name;
	private ListView listview_content;
	private static LayoutInflater mInflater;
	Bundle bundle;
	SerializableMap detailSerMap;
	Map detailMap;
	ExamListAdapter reprotDetailAdapter;
	List<ContentBean> contentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reprot_detail);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_name = (TextView) findViewById(R.id.tv_name);
		listview_content = (ListView) findViewById(R.id.listview_content);
		Intent intent = this.getIntent();
		detailSerMap = (SerializableMap) intent.getSerializableExtra("reportinfo");
		detailMap = detailSerMap.getMap();
		String reprotTime = (String) detailMap.get("reporttime");
		long fooTemp = Long.parseLong(reprotTime) * 1000;
		reprotTime = spl.format(new Date(fooTemp));
		tv_time.setText(reprotTime);
		tv_name.setText((String) detailMap.get("studentname"));
		init();
	}

	private void init() {
		contentList = new ArrayList<ContentBean>();
		reprotDetailAdapter = new ExamListAdapter(new ArrayList<ContentBean>());
		listview_content.setAdapter(reprotDetailAdapter);
		try {
			JSONArray jsonTemp = new JSONArray((String) detailMap.get("content"));
			for (int i = 0; i < jsonTemp.length(); i++) {
				ContentBean cb = new ContentBean();
				JSONObject temp = jsonTemp.getJSONObject(i);
				cb.setAnswer(temp.getString("answer"));
				cb.setTitle(temp.getString("title"));
				contentList.add(cb);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (contentList.size() != 0) {
			reprotDetailAdapter = new ExamListAdapter(contentList);
			reprotDetailAdapter.notifyDataSetChanged();
			listview_content.setAdapter(reprotDetailAdapter);
		}
	}

	public void cancel(View view) {
		finish();
	}

	class ExamListAdapter extends BaseAdapter {

		// 数据源
		private List<ContentBean> data;

		public ExamListAdapter(List<ContentBean> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data != null ? data.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_report_detail, null);
			}
			TextView a = (TextView) convertView.findViewById(R.id.tv_report_detail_title);
			TextView b = (TextView) convertView.findViewById(R.id.tv_report_detail_content);
			a.setText((position + 1) + ". " + data.get(position).getTitle());
			b.setText(data.get(position).getAnswer());
			return convertView;
		}

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
