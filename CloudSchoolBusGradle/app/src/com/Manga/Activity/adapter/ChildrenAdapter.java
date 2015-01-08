package com.Manga.Activity.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.MainActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Msg.SelectChildrenActivity;
import com.Manga.Activity.encryption.ooo;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.modifi.NikeNameActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;

public class ChildrenAdapter extends ArrayAdapter<Map<String, String>> {
	private static final int SELECTCHILDRENSUCCESS = 0;
	private static final int SELECTCHILDRENFIAL = 1;
	private static final int NETISNOTWORKING = 2;
	private static final int MISSDIALOG = 3;
	private ProgressDialog dialog;
	private Thread thread1;

	public ChildrenAdapter(Context context, List<Map<String, String>> list) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.childrenitem, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View item = View.inflate(getContext(), R.layout.childrenitem, null);
		Map<String, String> map = getItem(position);
		Button button = (Button) item.findViewById(R.id.btn_select_children);
		final String strName = map.get("uid_student");
		final String strClass = map.get("uid_class");
		final String name = map.get("name");
		String strFinalname = map.get("name");
		if(strFinalname.length()>=10){
			strFinalname = strFinalname.substring(0, 9) + "...";
		}
		final String strClassname = map.get("class");
		final String strNikename = strFinalname;
		button.setText( strNikename + "   " + strClassname);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ProgressDialog(getContext());
				dialog.setMessage(getContext().getResources().getString(
						R.string.current_lodding));
				dialog.setIndeterminate(false);
				dialog.setCancelable(true);
				showDialog();
				final HashMap<String, String> map = new HashMap<String, String>();
				map.put("uid_student", strName);
				map.put("uid_class", strClass);
				thread1 = new Thread(new Runnable() {

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
									SharedPreferences shp = getContext().getSharedPreferences("sid",Context.MODE_PRIVATE);
									Editor editor = shp.edit();
									JSONObject jso = new JSONObject(result.getContent());
									editor.putString("id",strClass+strName);
									editor.putString("sid",jso.getString("sid"));
									editor.commit();
									SharedPreferences shp1 = getContext().getSharedPreferences("nikename",Context.MODE_PRIVATE);
									Editor editor1 = shp1.edit();
									editor1.putString("nikename",map.get("name"));
									editor1.commit();
									
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

						
					}
				});
				if(!thread1.isAlive()){
					thread1.start();
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
				Toast.makeText(getContext(), R.string.select_children_success,
						Toast.LENGTH_SHORT).show();
				break;
			case SELECTCHILDRENFIAL:
				missDialog();
				Toast.makeText(getContext(), R.string.select_children_fail,
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
		ActivityUtil.selchildren.jumpView();
		
	}
}
