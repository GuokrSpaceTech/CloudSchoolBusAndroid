package com.Manga.Activity.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.Manga.Activity.R;

public class CustomAlertDialog extends Dialog {

	private Context mContext;
//	private TitleNavBarView mTitleNavBarView;

	public CustomAlertDialog(Context context) {
		super(context);
		init(context);
	}

	public CustomAlertDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public CustomAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		getWindow().setGravity(Gravity.CENTER);
		setContentView(R.layout.dialog_custom_alert);

		setCanceledOnTouchOutside(true);
		
		setTitleNavBar();
	}
	
	public CustomAlertDialog setTitleMessage(String tileString, int color) {
//		mTitleNavBarView.setMessage(tileString, color);
//		mTitleNavBarView.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomAlertDialog setTitleMessage(String tileString) {
//		mTitleNavBarView.setMessage(tileString);
//		mTitleNavBarView.setVisibility(View.VISIBLE);
		return this;
	}

	public CustomAlertDialog setMessage(String messageString) {
		TextView messageTextView = (TextView) findViewById(R.id.messageDialogTextView);
		messageTextView.setText(messageString);
		return this;
	}

	public CustomAlertDialog setLeftButton(String textString,
			final View.OnClickListener onClickListener) {
		Button leftButton = (Button) findViewById(R.id.leftDialogButton);
		leftButton.setText(textString);
		leftButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != onClickListener) {
					onClickListener.onClick(arg0);
				}
				cancel();
			}
		});
		return this;
	}

	public CustomAlertDialog setRightButton(String textString,
			final View.OnClickListener onClickListener) {
		Button rightButton = (Button) findViewById(R.id.rightDialogButton);
		rightButton.setText(textString);
		rightButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != onClickListener) {
					onClickListener.onClick(arg0);
				}
				cancel();
			}
		});
		return this;
	}
	
	public CustomAlertDialog setRightButtonGone(){
		Button rightButton = (Button) findViewById(R.id.rightDialogButton);
		rightButton.setVisibility(View.GONE);
		TextView buttonDivider = (TextView)findViewById(R.id.buttonDivider);
		buttonDivider.setVisibility(View.GONE);
		ViewGroup rightDialogLayout = (ViewGroup)findViewById(R.id.rightDialogLayout);
		rightDialogLayout.setVisibility(View.GONE);
		return this;
	}

	protected void setTitleNavBar() {
//		mTitleNavBarView = (TitleNavBarView) findViewById(R.id.titleNavBarView);
//		mTitleNavBarView.setMessage("");
//		mTitleNavBarView.setTitleBackground(R.color.bg_title_dialog);
//		mTitleNavBarView.setLeftButtonVisibility(View.INVISIBLE);
//		mTitleNavBarView.setRightButtonVisibility(View.INVISIBLE);

	}

}
