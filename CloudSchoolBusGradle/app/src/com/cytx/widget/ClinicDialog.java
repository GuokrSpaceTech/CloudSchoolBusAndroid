package com.cytx.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.Manga.Activity.R;
import com.cytx.CYTXApplication;
import com.cytx.adapter.ClinicAdapter;
import com.cytx.constants.HandlerConstants;

/**
 * 选择图片方式：拍照或者相册
 * @author xilehang
 *
 */
public class ClinicDialog extends Dialog{

	private Button cancelButton;
	private ListView clinicListView;
	private ClinicAdapter adapter;
	
	private Context context;
	private Handler handler;
	private String [] clinicTypes;
	private int screenType;
	
	public ClinicDialog(Context context, Handler handler, int screenType) {
		super(context, R.style.dialog);
		this.context = context;
		this.handler = handler;
		this.screenType = screenType;
		clinicTypes = context.getResources().getStringArray(R.array.clinic_value);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (screenType == CYTXApplication.getInstance().SCREEN_480) {
			setContentView(R.layout.dialog_clinic_480);
		} else {
			setContentView(R.layout.dialog_clinic);
		}
		
		
		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		
		clinicListView = (ListView) findViewById(R.id.listView_clinic);
		clinicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Message msg = handler.obtainMessage(HandlerConstants.CLINIC_TYPE, arg2);
				handler.sendMessage(msg);
				dismiss();
			}
		});
		
		adapter = new ClinicAdapter(context, clinicTypes, screenType);
		clinicListView.setAdapter(adapter);
		
	}

	
}
