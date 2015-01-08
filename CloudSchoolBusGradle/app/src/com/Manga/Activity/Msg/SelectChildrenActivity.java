package com.Manga.Activity.Msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.MainActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.adapter.ChildrenAdapter;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class SelectChildrenActivity extends BaseActivity {
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
		setContentView(R.layout.select_children);
		listView = (ListView) findViewById(R.id.listview);
		ActivityUtil.selchildren = this;
		dialog = new ProgressDialog(this);
		dialog.setMessage(this.getResources().getString(R.string.current_lodding));
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		listView.setDivider(null);
		showDialog();
		String[] uid_student = getIntent().getStringArrayExtra("uid_student");
		String[] uid_class = getIntent().getStringArrayExtra("uid_class");
		String[] nikename = getIntent().getStringArrayExtra("nikename");
		String[] classname = getIntent().getStringArrayExtra("classname");
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < uid_student.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("uid_student", uid_student[i]);
			map.put("uid_class", uid_class[i]);
			map.put("name", nikename[i]);
			map.put("class", classname[i]);
			list.add(map);
		}
		instantialChildren(list);
	}

	private void instantialChildren(final ArrayList<Map<String, String>> list) {
		Message message = handler.obtainMessage(SELECTCHILDRENSUCCESS, list);
		handler.sendMessage(message);
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
				listView.setAdapter(new ChildrenAdapter(SelectChildrenActivity.this, list));
				Toast.makeText(SelectChildrenActivity.this, R.string.select_children_success_1, Toast.LENGTH_SHORT)
						.show();
				break;
			case SELECTCHILDRENFIAL:
				missDialog();
				Toast.makeText(SelectChildrenActivity.this, R.string.select_children_fail, Toast.LENGTH_SHORT).show();
				break;
			case SELECTCHILDRENSUCCESS1:
				missDialog();
				Toast.makeText(SelectChildrenActivity.this, R.string.select_children_success_1, Toast.LENGTH_SHORT)
						.show();
				break;
			case SELECTCHILDRENFIAL1:
				missDialog();
				Toast.makeText(SelectChildrenActivity.this, R.string.select_children_fail, Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				missDialog();
				Toast.makeText(SelectChildrenActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	public void canel(View view) {
		finish();
	}

	private void showDialog() {
		dialog.show();
	}

	private void missDialog() {
		dialog.dismiss();
	}

	public void jumpView() {
		Intent intent = new Intent(this, MainActivity.class);
		ActivityUtil.startActivity(this, intent);
		finish();
		ActivityUtil.login.close();
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
