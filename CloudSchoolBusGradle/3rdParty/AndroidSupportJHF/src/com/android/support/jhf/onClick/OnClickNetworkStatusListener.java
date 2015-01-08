package com.android.support.jhf.onClick;

import com.android.support.jhf.handlerui.HandlerToastUI;
import com.android.support.jhf.network.ErrorExceptionHandler;
import com.android.support.jhf.utils.NetworkStatusUtils;

import android.content.Context;
import android.view.View;

abstract public class OnClickNetworkStatusListener extends com.android.support.jhf.onClick.OnClickListener {

	private Context mContext;
	
	public OnClickNetworkStatusListener(Context context) {
		mContext = context;
	}
	
	@Override
	public void onFastDoubleClick(View view) {
		
		if (!NetworkStatusUtils.networkStatusOK(mContext)) {
			HandlerToastUI.getHandlerToastUI(mContext,
					ErrorExceptionHandler.ERR_NET_CONN);
			onClickNetworkFail(view);
		} else {
			onClickNetworkSucceed(view);
		}
	}

	abstract public void onClickNetworkSucceed(View view);
	
	abstract public void onClickNetworkFail(View view);
}
