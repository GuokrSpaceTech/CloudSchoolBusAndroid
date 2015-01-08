package com.cytx;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;

import com.Manga.Activity.R;

/**
 * activity基类
 * 作用：全屏、横竖屏切换去从新启动activity
 * @author xilehang
 *
 */
public class BaseActivity extends Activity {
	
	protected int screenWidth;
	protected int screenHeight;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 设置无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Window window = getWindow(); // 得到对话框
		window.setWindowAnimations(R.style.windowAnim); // 设置窗口弹出动画
		
		Display currDisplay = getWindowManager().getDefaultDisplay();
		screenWidth = currDisplay.getWidth();
		screenHeight = currDisplay.getHeight();
		
		Log.d("screen", "screenWidth="+screenWidth+",screenHeight="+screenHeight);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
