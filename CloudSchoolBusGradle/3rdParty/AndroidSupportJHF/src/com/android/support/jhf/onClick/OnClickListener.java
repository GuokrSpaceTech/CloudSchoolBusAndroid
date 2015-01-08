package com.android.support.jhf.onClick;

import com.android.support.jhf.utils.CommonUtils;

import android.view.View;

public abstract class OnClickListener implements View.OnClickListener {
	
	CommonUtils commonUtils = new CommonUtils();
	
	public OnClickListener() {
		commonUtils.setLastClickTime();
	}

	@Override
	public void onClick(View v) {

		if (commonUtils.isFastDoubleClick()) {
			return;
		} else {
			onFastDoubleClick(v);
		}

	}

	public abstract void onFastDoubleClick(View view);

}
