package com.android.support.handlerui.widget;

import com.android.support.utils.ToolUtils;
import com.android.support.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast extends Toast {
	private View mView;
	private static Context mContext;

	public CustomToast(Context context) {
		super(context);

		init(context);
	}

	private void init(Context context) {
		mContext = context;
	}

	public static Toast makeText(Context context, CharSequence text,
			int duration) {
		CustomToast result = new CustomToast(context);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.android_support_jhf_toast_custom, null);
		TextView tv = (TextView) view.findViewById(R.id.textView);
		tv.setText(text);
		result.setView(view);
		result.setDuration(duration);
		result.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, ToolUtils.dipToPx(mContext, 65));
		return result;
//		return Toast.makeText(context, text, duration);
	}
	
	public static Toast makeTextColor(Context context, CharSequence text,
			int duration, int textColor) {
		CustomToast result = new CustomToast(context);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.android_support_jhf_toast_custom, null);
		TextView tv = (TextView) view.findViewById(R.id.textView);
		tv.setText(text);
		tv.setTextColor(textColor);
		result.setView(view);
		result.setDuration(duration);
		result.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, ToolUtils.dipToPx(mContext, 65));
		return result;
//		return Toast.makeText(context, text, duration);
	}

}
