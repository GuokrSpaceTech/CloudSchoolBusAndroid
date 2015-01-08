package com.cytx.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.Manga.Activity.R;
import com.cytx.CYTXApplication;
import com.cytx.constants.HandlerConstants;

/**
 * 选择性别
 * @author xilehang
 *
 */
public class GenderDialog extends Dialog implements OnClickListener{

	private Button maleButton;
	private Button femaleButton;
	private Button cancelButton;
	private int screenType;
	private Handler handler;
	private Context context;
	
	public GenderDialog(Context context, Handler handler, int screenType) {
		super(context, R.style.dialog);
		this.handler = handler;
		this.screenType = screenType;
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (screenType == CYTXApplication.getInstance().SCREEN_480) {
			setContentView(R.layout.dialog_gender_480);
		} else {
			setContentView(R.layout.dialog_gender);
		}
		
		
		maleButton = (Button) findViewById(R.id.button_male);
		maleButton.setOnClickListener(this);
		femaleButton = (Button) findViewById(R.id.button_female);
		femaleButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		case R.id.button_male:
			Message msg = handler.obtainMessage(HandlerConstants.HANDLER_GENDER, context.getResources().getString(R.string.male));
			handler.sendMessage(msg);
			dismiss();
			break;

		case R.id.button_female:
			Message msg2 = handler.obtainMessage(HandlerConstants.HANDLER_GENDER, context.getResources().getString(R.string.female));
			handler.sendMessage(msg2);
			dismiss();
			break;
			
		case R.id.button_cancel:
			dismiss();
			break;
		}
		
	}
	
}
