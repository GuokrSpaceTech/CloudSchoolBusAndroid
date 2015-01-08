package com.Manga.Activity.myChildren.Reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.adapter.ReportAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.DoctorConsult.MyAsyncTask;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class ReportSearchActivity extends BaseActivity {
	private ListView myListview;
	public ReportAdapter reportAdapter;
	private RelativeLayout noData;
	List<Map<String, String>> reportList;

	public void initReport() {
		if (reportList.size() == 0) {
			noData.setVisibility(View.VISIBLE);
		} else {
			noData.setVisibility(View.GONE);
			reportAdapter.getList().clear();
			reportAdapter.getList().addAll(reportList);
			reportAdapter.notifyDataSetChanged();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_search);
		myListview = (ListView) findViewById(R.id.search_listview);
		noData = (RelativeLayout) findViewById(R.id.no_data);
		reportList = new ArrayList<Map<String, String>>();
		reportAdapter = new ReportAdapter(this, new ArrayList<Map<String, String>>());
		myListview.setAdapter(reportAdapter);
		init();
		ActivityUtil.reportSearchActivity = this;
		myListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(ActivityUtil.reportActivity, ReportDetailActivity.class);
				SerializableMap tmpmap = new SerializableMap();
				tmpmap.setMap(reportList.get(arg2));
				Bundle bundle = new Bundle();
				bundle.putSerializable("reportinfo", tmpmap);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});
	}

	public void init() {
		MyAsyncTask postSubmitReportTask = new MyAsyncTask(ReportSearchActivity.this, false) {
			Result result;

			@Override
			protected void onPostExecute(Void vod) {
				initReport();
				super.onPostExecute(vod);
			}

			@Override
			protected Void doInBackground(Void... params) {
				if (HttpUtil.isNetworkConnected(ReportSearchActivity.this)) {

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("content", ActivityUtil.reportActivity.searchContent);
					map.put("starttime", "0");
					map.put("endtime", "0");
					result = HttpUtil.httpGet(ReportSearchActivity.this, new Params("search", map));
					if ("1".equals(result.getCode())) {
						reportList.clear();
						try {
							JSONArray myJson = new JSONArray(result.getContent());
							for (int i = 0; i < myJson.length(); i++) {
								JSONObject temp = myJson.getJSONObject(i);
								Map<String, String> tempMap = new HashMap<String, String>();
								tempMap.put("id", temp.getString("id"));
								tempMap.put("title", temp.getString("title"));
								tempMap.put("cnname", temp.getString("cnname"));
								tempMap.put("reportname", temp.getString("reportname"));
								tempMap.put("studentlist", temp.getString("studentlist"));
								tempMap.put("reporttime", temp.getString("reporttime"));
								tempMap.put("createtime", temp.getString("createtime"));
								tempMap.put("type", temp.getString("type"));
								tempMap.put("adduserid", temp.getString("adduserid"));
								tempMap.put("teachername", temp.getString("teachername"));
								tempMap.put("studentlistid", temp.getString("studentlistid"));
								tempMap.put("studentname", temp.getString("studentname"));
								tempMap.put("content", temp.getString("content"));
								reportList.add(tempMap);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				return super.doInBackground(params);
			}
		};
		postSubmitReportTask.execute();

	}

	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void close(View v) {
		ActivityUtil.reportActivity.isSearch = false;
		ActivityUtil.close(ActivityUtil.reportSearchActivity);
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
