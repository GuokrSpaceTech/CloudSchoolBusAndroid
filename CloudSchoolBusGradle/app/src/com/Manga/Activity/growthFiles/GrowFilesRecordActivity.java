package com.Manga.Activity.growthFiles;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class GrowFilesRecordActivity extends BaseActivity {
	private ExpandableListView expandableListView;
	private ExpandableListAdapter adapter;
	private TextView textViewHead;
	private int nCount = 5;
	private int nExpandIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grow_main);
		textViewHead = (TextView) findViewById(R.id.textviewmonth);
		adapter = new BaseExpandableListAdapter() {
			// 设置组视图的显示文字
			private String[] province = new String[] { "河南省", "河北省", "山东省", "山西省" };
			// 子视图显示文字
			private String[][] city = new String[][] {
					{ "郑州市", "开封市", "新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市", "开封市",
							"新乡市", "安阳市", "南阳市" },
					{ "石家庄市", "邯郸市", "保定市", "廊坊市", "廊坊市", "开封市", "新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市",
							"开封市", "新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市" },
					{ "济南市", "青岛市", "日照市", "烟台市", "威海市", "开封市", "新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市", "开封市",
							"新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市" },
					{ "太原市", "大同市", "晋城市", "吕梁市", "长治市", "开封市", "新乡市", "安阳市", "南阳市", "开封市", "新乡市", "安阳市", "南阳市", "开封市",
							"新乡市", "安阳市", "南阳市" } };

			// 自己定义一个获得文字信息的方法
			TextView getTextView() {
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
				TextView textView = new TextView(GrowFilesRecordActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(36, 0, 0, 0);
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				return textView;
			}

			// 重写ExpandableListAdapter中的各个方法
			@Override
			public int getGroupCount() {
				return province.length;
			}

			@Override
			public Object getGroup(int groupPosition) {
				return province[groupPosition];
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return city[groupPosition].length;
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return city[groupPosition][childPosition];
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
				View item = View.inflate(GrowFilesRecordActivity.this, R.layout.grow_group_item, null);
				TextView textView = (TextView) item.findViewById(R.id.textviewmonth);
				textView.setText(getGroup(groupPosition).toString());
				return item;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
					ViewGroup parent) {
				View item = View.inflate(GrowFilesRecordActivity.this, R.layout.childrenitem, null);

				Button button = (Button) item.findViewById(R.id.btn_select_children);
				button.setText(getChild(groupPosition, childPosition).toString());
				return item;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				return true;
			}

		};

		expandableListView = (ExpandableListView) findViewById(R.id.province);
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(adapter);
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
				return false;
			}
		});
		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int arg0) {
				// TODO Auto-generated method stub
				for (int ii = 0; ii < nCount; ii++) {
					if (arg0 != ii) {
						expandableListView.collapseGroup(ii);
					}
				}
			}
		});
		// 设置item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				Toast.makeText(GrowFilesRecordActivity.this, adapter.getChild(groupPosition, childPosition).toString(),
						Toast.LENGTH_SHORT).show();

				return false;
			}
		});
	}

	public void close(View view) {
		ActivityUtil.close(GrowFilesRecordActivity.this);
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
