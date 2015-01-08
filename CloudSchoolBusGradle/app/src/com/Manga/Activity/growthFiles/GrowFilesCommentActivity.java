package com.Manga.Activity.growthFiles;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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

public class GrowFilesCommentActivity extends BaseActivity {
	private ExpandableListView expandableListView;
	private int nCount = 5;
	private ArrayList<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grow_main);
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
			// 设置组视图的显示文字
			private String[] province = new String[] { "2013.11", "2013.10", "2013.9", "2013.8" };

			// 子视图显示文字

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
				return list.size();
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return list.get(childPosition);
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
				View item = View.inflate(GrowFilesCommentActivity.this, R.layout.grow_group_item, null);
				TextView textView = (TextView) item.findViewById(R.id.textviewmonth);
				textView.setText(getGroup(groupPosition).toString());
				return item;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
					ViewGroup parent) {
				View item = View.inflate(GrowFilesCommentActivity.this, R.layout.grow_child_item_text, null);
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

				Toast.makeText(GrowFilesCommentActivity.this,
						adapter.getChild(groupPosition, childPosition).toString(), Toast.LENGTH_SHORT).show();

				return false;
			}
		});
	}

	public void close(View view) {
		ActivityUtil.close(GrowFilesCommentActivity.this);
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
