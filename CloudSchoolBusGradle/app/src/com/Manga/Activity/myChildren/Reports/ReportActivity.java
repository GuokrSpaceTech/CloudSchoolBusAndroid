package com.Manga.Activity.myChildren.Reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.adapter.ReportAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.widget.ModifiListView;
import com.Manga.Activity.widget.ModifiListView.MyBackCall;
import com.umeng.analytics.MobclickAgent;

public class ReportActivity extends BaseActivity {
	private ModifiListView myListview;
	public ReportAdapterreportAdapter;
	private RelativeLayout noData;
	private EditText ed_search;
	List<Map<String, String>> reportList;
	List<Map<String, String>> tempList;

	public static String searchContent = "";
	private boolean isheader = false;
	private boolean isfooter = false;
	public static boolean isSearch = false;
	/**
	 * 昵称过长
	 */
	private static final int OUTLENGTH = 0;
	/**
	 * 无网络
	 */
	private static final int HASNTNETWORK = 1;
	/**
	 * 超时
	 */
	private static final int OUTTIME = 2;
	/**
	 * 修改成功
	 */
	private static final int MODIFIOK = 3;
	/**
	 * xialai shuaxin
	 */
	private static final int MODIFIFAILE = 4;
	/**
	 * 修改界面
	 */
	private static final int CHANGEUI = 5;
	private static final int EMPTY = 6;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case OUTLENGTH:
				Toast.makeText(ReportActivity.this, R.string.nikename_out_length, Toast.LENGTH_SHORT).show();
				break;
			case HASNTNETWORK:
				Toast.makeText(ReportActivity.this, R.string.no_network_cannt_uploding_nikename, Toast.LENGTH_SHORT)
						.show();
				myListview.cancelHeader();
				myListview.cancelFooter();
				break;
			case OUTTIME:
				Toast.makeText(ReportActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIOK:
				initReport(reportList);
				break;
			case MODIFIFAILE:
				initReport(tempList);
				break;
			case EMPTY:
				Toast.makeText(ReportActivity.this, R.string.feekback_content_null, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	public void initReport(List<Map<String, String>> list) {
		if (isheader == false && isfooter == false) {
			if (list.size() != 0) {
				reportAdapter.getList().addAll(list);
				reportAdapter.notifyDataSetChanged();
			}
		} else if (isheader == true && isfooter == false) {
			reportAdapter.getList().clear();
			reportAdapter.getList().addAll(list);
			reportAdapter.notifyDataSetChanged();
			myListview.cancelHeader();
		} else if (isheader == false && isfooter == true) {
			reportAdapter.getList().addAll(list);
			reportAdapter.notifyDataSetChanged();
			myListview.cancelFooter();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		myListview = (ModifiListView) findViewById(R.id.report_listview);
		noData = (RelativeLayout) findViewById(R.id.no_data);
		ed_search = (EditText) findViewById(R.id.ed_search);
		myListview.setLongClickable(true);
		myListview.setDivider(null);
		reportList = new ArrayList<Map<String, String>>();
		tempList = new ArrayList<Map<String, String>>();
		reportAdapter = new ReportAdapter(this, new ArrayList<Map<String, String>>());
		myListview.setAdapter(reportAdapter);
		myListview.cancelHeader();
		myListview.cancelFooter();

		init();
		ActivityUtil.reportActivity = this;
		ed_search.setOnKeyListener(new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_ENTER) {
							System.out.println("keyCode == KeyEvent.KEYCODE_ENTER");
							// 先隐藏键盘
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
									ReportActivity.this.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
							search(v);
						}
						return false;
					}
				});
		myListview.setBackCall(new MyBackCall() {

			@Override
			public void executeHeader() {
				if (reportAdapter.getList().isEmpty()) {
					isheader = false;
					isfooter = false;
					init("0", "0");
				} else {
					isheader = true;
					isfooter = false;
					init("0", "0");
				}

			}

			@Override
			public void executeFooter() {
				if (reportAdapter.getList().isEmpty()) {
					isheader = false;
					isfooter = false;
					init("0", "0");
				} else {
					isheader = false;
					isfooter = true;
					init(reportAdapter.getItem(reportAdapter.getCount() - 1).get("reporttime"), "0");
				}
			}
		});
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

	public void search(View v) {
		if (!isSearch) {
			if ("".equals(ed_search.getText().toString())) {
				handler.sendEmptyMessage(EMPTY);
			} else {
				isSearch = true;
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						ReportActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				searchContent = ed_search.getText().toString();
				Intent intent = new Intent(ActivityUtil.reportActivity, ReportSearchActivity.class);
				startActivity(intent);
			}
		}
	}

	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	public void init() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ReportActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("starttime", "0");
					map.put("endtime", "0");
					Result result = HttpUtil.httpGet(ReportActivity.this, new Params("report", map));
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
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
						} catch (Exception e) {
						}
						handler.sendEmptyMessage(MODIFIOK);
					}
				} else {
					handler.sendEmptyMessage(HASNTNETWORK);
				}
			}
		});
		thread.start();
	}

	public void init(final String startTime, final String endTime) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ReportActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("starttime", startTime);
					map.put("endtime", endTime);
					Result result = HttpUtil.httpGet(ReportActivity.this, new Params("report", map));
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						tempList.clear();
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
								tempList.add(tempMap);
							}
						} catch (Exception e) {

						}
						handler.sendEmptyMessage(MODIFIFAILE);
					}
				} else {
					handler.sendEmptyMessage(HASNTNETWORK);
				}
			}
		});
		thread.start();
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
