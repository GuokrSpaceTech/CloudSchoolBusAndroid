package com.cytx;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.cytx.constants.Constants;
import com.cytx.constants.HandlerConstants;
import com.cytx.domain.QuestionAssessDomain;
import com.cytx.dto.QuestionAssessDto;
import com.cytx.service.WebService;
import com.cytx.service.impl.WebServiceImpl;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.MD5;
import com.cytx.utility.UtilUI;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 问题评价界面
 * @author xilehang
 *
 */
public class AssessActivity extends BaseActivity {
	
	// 返回
	private Button backButton;
	// 勾选
	private Button checkButton;
	private RatingBar ratingBar;
	private EditText assessEditText;
	private String user_id = "";
	private long problem_id;
	private String status;
	
	private CYTXApplication cytxApp = CYTXApplication.getInstance();
	
	private GestureDetector mGestureDetector;// 监听手势
	private String [] stars;
	private TextView starTextView;
	
	@SuppressLint("HandlerLeak")
	private Handler assessHandler  = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == HandlerConstants.CREATED_SUCCESS) {
				Intent intent = new Intent(AssessActivity.this, DetailActivity.class);
				intent.putExtra("problem_id", problem_id);
				intent.putExtra("user_id", user_id);
				intent.putExtra("status", "d");
				startActivity(intent);
				AssessActivity.this.finish();
			}
		};
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
			setContentView(R.layout.activity_assess_480);
			
		} else {
			setContentView(R.layout.activity_assess);
			
		}
		
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AssessActivity.this, DetailActivity.class);
				intent.putExtra("problem_id", problem_id);
				intent.putExtra("user_id", user_id);
				intent.putExtra("status", status);
				startActivity(intent);
				AssessActivity.this.finish();
			}
		});
		
		checkButton = (Button) findViewById(R.id.button_assess);
		checkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int star = ratingBar.getProgress();
				String assessContent = assessEditText.getText().toString().trim();
				if ("".equals(assessContent)) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_input_assess), Toast.LENGTH_LONG).show();
					return ;
				} 

				if(ratingBar.getProgress() == 0){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_input_assess_start), Toast.LENGTH_LONG).show();
					return ;
				}
				
				// 提交评价
				commitAssess(star, assessContent);
			}
		});
		
		
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		ratingBar.setMax(5);// 最大值设置为5
		ratingBar.setProgress(0);// 默认为0
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				int star = arg0.getProgress();
				if (star !=0) {
					starTextView.setText(stars[star-1]);
				}
				
			}
		});
		assessEditText = (EditText) findViewById(R.id.editText_assess);
		
		Intent intent = getIntent();
		if (intent != null) {
			user_id = intent.getStringExtra("user_id");
			problem_id = intent.getLongExtra("problem_id", 0);
			status = intent.getStringExtra("status");
		}
		
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		stars = getResources().getStringArray(R.array.star_assess);
		starTextView = (TextView) findViewById(R.id.textView3);
		
	}

	/**
	 * 提交评价
	 */
	protected void commitAssess(int star, String content) {
		QuestionAssessDto qad = new QuestionAssessDto();
		qad.setContent(content);
		String atime = (System.currentTimeMillis() / 1000 ) + "";
		qad.setAtime(atime);
		qad.setProblem_id(problem_id + "");
		qad.setSign(MD5.md5(MD5.getString(atime, problem_id + "")));
		qad.setStar(star);
		qad.setUser_id(user_id);
		WebService webService = WebServiceImpl.getInstance();
		webService.questionAssess(qad, new AsyncHttpResponseHandler(){
			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(AssessActivity.this, getResources().getString(R.string.toast_submit_assess));

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				dialog.dismiss();
				UtilUI.showToastError(arg1, getApplicationContext());
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

			@Override
			public void onStart() {
				super.onStart();
				dialog.show();
			}

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				dialog.dismiss();
				QuestionAssessDomain qad = FastJsonTools.getObject(arg0, QuestionAssessDomain.class);
				if (qad.getError() == 0) {
					UtilUI.showCreatedSuccessDialog(AssessActivity.this, assessHandler, getResources().getString(R.string.toast_assess_success));
				} else {
					UtilUI.showErrorMsgDialog(qad.getError_msg(), AssessActivity.this);
				}
			}
			
		});
	}
	
	private final int PIXEL = Constants.PIXEL_SCROLL;
	private class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			// 表示向右滑动
			if (e1.getX() - e2.getX() < -PIXEL) {
				Intent intent = new Intent(AssessActivity.this, DetailActivity.class);
				intent.putExtra("problem_id", problem_id);
				intent.putExtra("user_id", user_id);
				intent.putExtra("status", status);
				startActivity(intent);
				AssessActivity.this.finish();
				
				return false;
			}
			return false;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	

}
