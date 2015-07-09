package com.android.support.customview;


import com.android.support.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
	static final String LOG_TAG = "LinearLayoutForListView";
	private android.widget.BaseAdapter adapter;
	private OnClickListener onClickListener = null;
	private Context mContext;
	private int mDividerHeight;
	private Drawable mDivider;

	public void fillLinearLayout() {
		removeAllViews();
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			View v = adapter.getView(i, null, null);
			v.setOnClickListener(mOnItemClickListener);
			v.setId(i);
			addView(v, i * 2);

			ImageView imageView = new ImageView(mContext);
			// imageView.setImageResource(R.drawable.divider);
			imageView.setImageDrawable(mDivider);
//			imageView.setBackgroundResource(R.color.divider);
			LinearLayout.LayoutParams params = new LayoutParams(
					LayoutParams.FILL_PARENT, mDividerHeight);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.FIT_XY);
			if (0 == mDividerHeight || i == count - 1)
				imageView.setVisibility(View.GONE);
			addView(imageView, i * 2 + 1);

		}
		Log.v("countTAG", "" + count);
	}

	public LinearLayoutForListView(Context context) {
		super(context);
		init(context);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.LinearLayoutForListView);

		Drawable d = a.getDrawable(R.styleable.LinearLayoutForListView_linearForListDivider);
		mDivider = d;

		int dividerHeight = a.getDimensionPixelSize(
				R.styleable.LinearLayoutForListView_dividerHeight, 0);
		mDividerHeight = dividerHeight;

		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		setScrollbarFadingEnabled(true);
		setOrientation(LinearLayout.VERTICAL);
	}

	public android.widget.BaseAdapter getAdpater() {
		return adapter;
	}

	public void setAdapter(BaseAdapter adpater) {
		this.adapter = adpater;
		removeAllViews();
		fillLinearLayout();
	}

	public OnClickListener getOnclickListner() {
		return onClickListener;
	}

	public void setOnClickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	/**
	 * 用来设置列表中的点击事件,否则设置监听必须的在setAdapter之前调用
	 */
	private android.view.View.OnClickListener mOnItemClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (null != onClickListener) {
				onClickListener.onClick(v);
			}
		}
	};
}
