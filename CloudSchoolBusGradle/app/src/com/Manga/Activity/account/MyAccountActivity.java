package com.Manga.Activity.account;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.bindingPhone.BindingPhoneActivity;
import com.Manga.Activity.bindingPhone.BindingPhoneChangeActivity;
import com.Manga.Activity.myChildren.DoctorConsult.DoctorActivity;
import com.Manga.Activity.myChildren.SwitchChildren.ManageChildrenActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.cytx.ConsultActivity;
import com.umeng.analytics.MobclickAgent;

public class MyAccountActivity extends BaseActivity {
	private String strBindingPhone;
	String accountName;
	TextView accounthealthlabel, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		ActivityUtil.accountActivity = this;
		initform();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		initform();
	}

	public void close(View view) {
		ActivityUtil.main.move();
	}

	public void clickphone(View view) {
		if (strBindingPhone.equals("1")) {
			Intent intent = new Intent(MyAccountActivity.this, BindingPhoneChangeActivity.class);
			ActivityUtil.main.comeIn(intent);
		} else {
			Intent intent = new Intent(MyAccountActivity.this, BindingPhoneActivity.class);
			ActivityUtil.main.comeIn(intent);
		}
	}

	public void health(View view) {
		if ("0".equals(Student_Info.chunyuisopen)) {
			Intent intent = new Intent(MyAccountActivity.this, DoctorActivity.class);
			ActivityUtil.main.comeIn(intent);
		} else {
			Intent intent = new Intent(MyAccountActivity.this, ConsultActivity.class);
			intent.putExtra("user_id", Student_Info.username);
			startActivity(intent);
		}
	}

	public void clickchildren(View view) {
		Intent intent = new Intent(MyAccountActivity.this, ManageChildrenActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	private void initform() {
		accounthealthlabel = (TextView) findViewById(R.id.accounthealthlabel);
		name = (TextView) findViewById(R.id.accountnametxt);
		TextView phone = (TextView) findViewById(R.id.accountphonetxt);
		TextView phonetext = (TextView) findViewById(R.id.accountphonetext);
		TextView phonelabel = (TextView) findViewById(R.id.accountphonelabel);
		TextView children = (TextView) findViewById(R.id.accountchildrenlabel);
		// if ("0".equals(Student_Info.chunyuisopen)) {
		// accounthealthlabel.setText(R.string.no_buy_server);
		// } else {
		//
		// if (!"".equals(Student_Info.chunyu_entime)) {
		// String temp1 = getResources().getString(R.string.buy_server);
		// String temp2 = getResources().getString(R.string.period);
		// SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
		// long fooTemp = Long.parseLong(Student_Info.chunyu_entime) * 1000;
		// String temp3 = spl.format(new Date(fooTemp));
		// accounthealthlabel.setText(Html.fromHtml(temp1 + "<br>" + temp2 + temp3));
		// } else {
		// accounthealthlabel.setText(R.string.no_buy_server);
		// }
		// }
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			handler.sendEmptyMessage(1);
		} else {
			cursor.moveToFirst();
			strBindingPhone = cursor.getString(cursor.getColumnIndex("ischeck_mobile"));
			if ("1".equals(cursor.getString(cursor.getColumnIndex("ischeck_mobile")))) {
				accountName = cursor.getString(cursor.getColumnIndex("username"));
				name.setText(accountName);
				phone.setText(cursor.getString(cursor.getColumnIndex("mobile")));
				String str = cursor.getString(cursor.getColumnIndex("mobile"));
				if (str.length() == 11) {
					phone.setText(str.substring(0, 4) + "****" + str.substring(8, 11));
				}
				phonelabel.setText(getResources().getString(R.string.account_phone_label_yes));
				phonetext.setText(getResources().getString(R.string.account_phone_text_change));
			} else {
				name.setText(cursor.getString(cursor.getColumnIndex("username")));
				phone.setText("");
				phonelabel.setText(getResources().getString(R.string.account_phone_label_not));
				phonetext.setText(getResources().getString(R.string.account_phone_text_binding));
			}

		}
		if (cursor != null) {
			cursor.close();
		}
		Cursor cursor1 = sql.query("signin", null, null, null, null, null, null);
		if (cursor1 == null || cursor1.getCount() == 0) {
			handler.sendEmptyMessage(1);
		} else {
			children.setText(getResources().getString(R.string.account_children_label_prev) + cursor1.getCount()
					+ getResources().getString(R.string.account_children_label_next));
		}
		if (cursor1 != null) {
			cursor1.close();
		}
		sql.close();
		db.close();
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				Toast.makeText(MyAccountActivity.this, R.string.myaccount_fail, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	public void onResume() {
		super.onResume();
		if ("0".equals(Student_Info.chunyuisopen)) {
			accounthealthlabel.setText(R.string.no_buy_server);
		} else {

			if (!"".equals(Student_Info.chunyu_entime)) {
				String temp1 = getResources().getString(R.string.buy_server);
				String temp2 = getResources().getString(R.string.period);
				SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
				long fooTemp = Long.parseLong(Student_Info.chunyu_entime) * 1000;
				String temp3 = spl.format(new Date(fooTemp));
				accounthealthlabel.setText(Html.fromHtml(temp1 + "<br>" + temp2 + temp3));
			} else {
				accounthealthlabel.setText(R.string.no_buy_server);
			}
		}
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
