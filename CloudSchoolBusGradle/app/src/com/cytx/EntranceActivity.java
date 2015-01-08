package com.cytx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.Manga.Activity.R;

public class EntranceActivity extends BaseActivity {

//	private final String user_id = "768292";// 测试用的user_id
	private final String user_id = "383419";// 测试用的user_id
	private Button entranceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_entrance);
		entranceButton = (Button) findViewById(R.id.button_entrance);
		entranceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EntranceActivity.this,
						ConsultActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
			}
		});
		
		
	}

	
	
}
