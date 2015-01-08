package com.cytx.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.Manga.Activity.R;
import com.cytx.CYTXApplication;
import com.cytx.constants.HandlerConstants;

/**
 * 选择图片方式：拍照或者相册
 * @author xilehang
 *
 */
public class UploadImageDialog extends Dialog implements OnClickListener{

	private Button photoButton;
	private Button cameraButton;
	private Button cancelButton;
	private int screenType;
	private Handler handler;
	
	public UploadImageDialog(Context context, Handler handler, int screenType) {
		super(context, R.style.dialog);
		this.handler = handler;
		this.screenType = screenType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (screenType == CYTXApplication.getInstance().SCREEN_480) {
			setContentView(R.layout.dialog_upload_image_480);
		} else {
			setContentView(R.layout.dialog_upload_image);
		}
		
		
		photoButton = (Button) findViewById(R.id.button_photo);
		photoButton.setOnClickListener(this);
		cameraButton = (Button) findViewById(R.id.button_camera);
		cameraButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		case R.id.button_photo:
			handler.sendEmptyMessage(HandlerConstants.PHOTO_IMAGE);
			dismiss();
			break;

		case R.id.button_camera:
			handler.sendEmptyMessage(HandlerConstants.CAMERA_IMAGE);
			dismiss();
			break;
			
		case R.id.button_cancel:
			dismiss();
			break;
		}
		
	}
	
}
