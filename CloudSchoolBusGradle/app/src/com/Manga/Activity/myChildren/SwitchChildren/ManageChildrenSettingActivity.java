package com.Manga.Activity.myChildren.SwitchChildren;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.adapter.ManageChildrenAdapterSetting;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class ManageChildrenSettingActivity extends Activity {
	private ListView listView;
	private static final int SELECTCHILDRENSUCCESS = 0;
	private static final int SELECTCHILDRENFIAL = 1;
	private static final int NETISNOTWORKING = 2;
	private static final int SELECTCHILDRENSUCCESS1 = 3;
	private static final int SELECTCHILDRENFIAL1 = 4;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_children);
		listView = (ListView) findViewById(R.id.listview);
		// listView.setDivider(getResources().getDrawable(R.drawable.manage_children_line));
		ActivityUtil.managechildrenset = this;
		dialog = new ProgressDialog(this);
		dialog.setMessage(this.getResources().getString(R.string.children_manage_read));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		showDialog();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DB db = new DB(ManageChildrenSettingActivity.this);
				SQLiteDatabase sql = db.getReadableDatabase();
				Cursor cursor = sql.query("signin", null, null, null, null, null, null);
				ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
				if (cursor != null) {
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("uid_student", cursor.getString(cursor.getColumnIndex("uid_student")));
						map.put("uid_class", cursor.getString(cursor.getColumnIndex("uid_class")));
						map.put("nikename", cursor.getString(cursor.getColumnIndex("nikename")));
						map.put("classname", cursor.getString(cursor.getColumnIndex("classname")));
						list.add(map);
					}
				}
				cursor.close();
				sql.close();
				db.close();
				handler.sendMessage(handler.obtainMessage(SELECTCHILDRENSUCCESS, list));
			}
		});
		thread.start();

	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case SELECTCHILDRENSUCCESS:
				missDialog();
				ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
				list = (ArrayList<Map<String, String>>) message.obj;
				listView.setAdapter(new ManageChildrenAdapterSetting(ManageChildrenSettingActivity.this, list));
				break;
			case SELECTCHILDRENFIAL:
				missDialog();
				Toast.makeText(ManageChildrenSettingActivity.this, R.string.children_manage_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			case SELECTCHILDRENSUCCESS1:
				missDialog();
				Toast.makeText(ManageChildrenSettingActivity.this, R.string.children_manage_success, Toast.LENGTH_SHORT)
						.show();
				break;
			case SELECTCHILDRENFIAL1:
				missDialog();
				Toast.makeText(ManageChildrenSettingActivity.this, R.string.children_manage_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			case NETISNOTWORKING:
				missDialog();
				Toast.makeText(ManageChildrenSettingActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	public void close(View view) {
		ActivityUtil.close(ManageChildrenSettingActivity.this);
	}

	private void showDialog() {
		dialog.show();
	}

	private void missDialog() {
		dialog.dismiss();
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
