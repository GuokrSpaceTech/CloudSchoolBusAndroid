package com.Manga.Activity.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.MainActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;

public class ManageChildrenAdapterSetting extends ArrayAdapter<Map<String, String>> {
	private static final int SELECTCHILDRENSUCCESS = 0;
	private static final int SELECTCHILDRENFIAL = 1;
	private static final int NETISNOTWORKING = 2;
	private static final int MISSDIALOG = 3;
	private ProgressDialog dialog;

	public ManageChildrenAdapterSetting(Context context, List<Map<String, String>> list) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.childrenitem, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View item = View.inflate(getContext(), R.layout.children_manage_item, null);
		Map<String, String> map = getItem(position);
		TextView textview = (TextView) item.findViewById(R.id.textview);
		RelativeLayout relativeLayout = (RelativeLayout) item
				.findViewById(R.id.relativeLayout);
		final String strName = map.get("uid_student");
		final String strClass = map.get("uid_class");
		String strFinalname = map.get("nikename");
		if(strFinalname.length()>=10){
			strFinalname = strFinalname.substring(0, 9) + "...";
		}
		final String strClassname = map.get("classname");
		final String strNikename = strFinalname;
		textview.setText(strNikename+"  "+strClassname);
		View viewselect =  item.findViewById(R.id.viewselect);
		SharedPreferences shp1 = getContext().getSharedPreferences(
				"sid", Context.MODE_PRIVATE);
		DB db=new DB(getContext());
		SQLiteDatabase sql=db.getReadableDatabase();
		Cursor cursor=sql.query("signin", null, "u_id=?", new String[] {shp1.getString("id", "")}, null, null, null);
		String studentNameClass = "";
		String studentName = "";
		if(cursor==null||cursor.getCount()==0){
		}else{
			cursor.moveToFirst();
			studentNameClass= (cursor.getString(cursor.getColumnIndex("uid_class")));
			studentName = (cursor.getString(cursor.getColumnIndex("uid_student")));
		}
		if(cursor!=null){
			cursor.close();
		}
		sql.close();
		db.close();
		if (strName.equals(studentName)&&strClass.equals(studentNameClass)
				) {
			viewselect.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.children_manage_view));
		}else{
			viewselect.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.children_manage_view_not));
		}
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences shp1 = getContext().getSharedPreferences(
						"nikename", Context.MODE_PRIVATE);
				if (strNikename.equals(shp1.getString("nikename", ""))) {
					jumpView();
				} else {
					dialog = new ProgressDialog(getContext());
					dialog.setMessage(getContext().getResources().getString(
							R.string.children_manage_change));
					dialog.setIndeterminate(false);
					dialog.setCancelable(true);
					showDialog();
					final HashMap<String, String> map = new HashMap<String, String>();
					map.put("uid_student", strName);
					map.put("uid_class", strClass);
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Result result = HttpUtil.httpPost(getContext(),
									new Params("unit", map));
							if (result != null) {
								String tmp = result.getCode();
								if ("1".equals(tmp)) {
									handler.sendEmptyMessage(SELECTCHILDRENSUCCESS);
									try {
										SharedPreferences shp = getContext()
												.getSharedPreferences("sid",
														Context.MODE_PRIVATE);
										Editor editor = shp.edit();
										JSONObject jso = new JSONObject(result
												.getContent());
										editor.putString("id",
												strClass+strName);
										editor.commit();
										jumpView();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									handler.sendEmptyMessage(SELECTCHILDRENFIAL);
								}
							} else {
								handler.sendEmptyMessage(NETISNOTWORKING);
							}

							ActivityUtil.login.close();
						}
					});
					thread.start();
				}
			}
		});

		return item;
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case SELECTCHILDRENSUCCESS:
				missDialog();
				Toast.makeText(getContext(), R.string.children_manage_success,
						Toast.LENGTH_SHORT).show();
				break;
			case SELECTCHILDRENFIAL:
				missDialog();
				Toast.makeText(getContext(), R.string.children_manage_fail,
						Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				missDialog();
				Toast.makeText(getContext(), R.string.out_time,
						Toast.LENGTH_SHORT).show();
				break;
			case MISSDIALOG:
				missDialog();
				break;
			}
			return false;
		}
	});

	private void showDialog() {
		dialog.show();
	}

	private void missDialog() {
		dialog.dismiss();
	}

	private void jumpView() {
		Intent intent = new Intent(getContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getContext().startActivity(intent);
	}
}
