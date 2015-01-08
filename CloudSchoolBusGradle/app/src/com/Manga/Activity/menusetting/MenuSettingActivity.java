package com.Manga.Activity.menusetting;

import android.os.Bundle;
import android.view.View;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;

public class MenuSettingActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.left_setting);
	}
	public void close(View v){
		ActivityUtil.main.move();
	}
}
