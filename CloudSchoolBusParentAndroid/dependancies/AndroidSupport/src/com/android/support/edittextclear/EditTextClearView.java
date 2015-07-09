package com.android.support.edittextclear;

import com.android.support.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 带清除按钮的EditText
 * 
 * @author hongfeijia
 * 
 */
public class EditTextClearView extends LinearLayout implements
		View.OnClickListener {

	private Context mContext;
	private View mView;
	private EditText mEditText;
	private ImageView mClearImageView;

	private OnClickListener mClearOnClickListener;

	public EditTextClearView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public EditTextClearView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		mContext = context;
		mView = LayoutInflater.from(mContext).inflate(
				R.layout.android_support_jhf_view_edittext_clear, this, true);

		mClearImageView = (ImageView) mView.findViewById(R.id.clearImageView);
		mClearImageView.setOnClickListener(this);
		mClearImageView.setVisibility(View.GONE);

		mEditText = (EditText) mView.findViewById(R.id.editText);
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Print.println("s : " + s.toString());
				// Print.println("s.toString().length() : " +
				// s.toString().length());
				// Print.println("count : " + count);
				if (0 == s.toString().length()) {
					mClearImageView.setVisibility(View.GONE);
				} else {
					mClearImageView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		if (null != attrs) {

			TypedArray typedArray = context.obtainStyledAttributes(attrs,
					R.styleable.EditTextClearView);

			ColorStateList color = typedArray
					.getColorStateList(R.styleable.EditTextClearView_textColor);
			if (null != color) {
				mEditText.setTextColor(color);
			}
			ColorStateList colorHint = typedArray
					.getColorStateList(R.styleable.EditTextClearView_textColorHint);
			if (null != colorHint) {
				mEditText.setHintTextColor(colorHint);
			}
			float textSize = typedArray.getDimensionPixelSize(
					R.styleable.EditTextClearView_textSize, -1);
			if (-1 != textSize) {
				// mEditText.setTextSize(textSize);
				mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			}
			// 一定要在password前面设置
			boolean singleLine = typedArray.getBoolean(
					R.styleable.EditTextClearView_singleLine, false);
			if (true == singleLine) {
				mEditText.setSingleLine();
			} else {
			}
			// 一定要在singleLine前面设置
			boolean password = typedArray.getBoolean(
					R.styleable.EditTextClearView_password, false);
			if (true == password) {
				mEditText.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
			}
			String hintString = typedArray
					.getString(R.styleable.EditTextClearView_hint);
			if (null != hintString) {
				mEditText.setHint(hintString);
			}
		}

	}

	/**
	 * 返回EditText的对象
	 * 
	 * @return
	 */
	public EditText getEditText() {
		return mEditText;
	}

	/**
	 * 清楚按键的监听
	 * 
	 * @param clearOnClickListener
	 */
	public void setClearOnClickListener(OnClickListener clearOnClickListener) {
		mClearOnClickListener = clearOnClickListener;
	}

	@Override
	public void onClick(View v) {
		if (R.id.clearImageView == v.getId()) {
			if (null != mClearOnClickListener) {
				mClearOnClickListener.onClick(v);
			}
			mEditText.setText("");
		}
	}

}
