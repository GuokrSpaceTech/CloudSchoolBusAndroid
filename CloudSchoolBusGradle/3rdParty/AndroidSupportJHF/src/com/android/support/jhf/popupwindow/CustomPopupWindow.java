package com.android.support.jhf.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
/**
 * 自定义popuWindow的基类
 * @author hongfeijia
 *
 */
public class CustomPopupWindow implements OnDismissListener{

	private Context mContext;
	private WindowManager mWindowManager;
	private View mContentView;
	private PopupWindow mPopupWindow;
	private OnDismissListener mOnDismissListener;

	public CustomPopupWindow(Context context) {
		mContext = context;
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
	}

	public void setContentView(View view, int width, int height) {
		mContentView = view;
		mPopupWindow = new PopupWindow(mContentView, width, height);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		// 使其聚集
		mPopupWindow.setFocusable(true);
		// 设置允许在外点击消失
		mPopupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		mPopupWindow.setOnDismissListener(this);
	}

	/**
	 * xoff,yoff基于anchor的左下角进行偏移。
	 * 
	 * @param anchor
	 * @param xoff
	 * @param yoff
	 */
	public void show(View anchor, int xoff, int yoff) {
		mPopupWindow.showAsDropDown(anchor, xoff, yoff);
	}
	
	public void dismiss(){
		mPopupWindow.dismiss();
	}
	
	public void setOnDismissListener(OnDismissListener onDismissListener){
		mOnDismissListener = onDismissListener;
	}

	@Override
	public void onDismiss() {
		if(null != mOnDismissListener){
			mOnDismissListener.onDismiss();
		}
	}
	
	public void setAnimation(int animationStyle){
		mPopupWindow.setAnimationStyle(animationStyle);
	}
	

}
