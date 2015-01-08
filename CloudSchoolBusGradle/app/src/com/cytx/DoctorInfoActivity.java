package com.cytx;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.cytx.constants.Constants;
import com.cytx.domain.DoctorInfoDomain;
import com.cytx.domain.IndexDomain;
import com.cytx.service.WebService;
import com.cytx.service.impl.WebServiceImpl;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.FileTools;
import com.cytx.utility.UtilUI;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 医生基本信息界面
 * 
 * @author xilehang
 * 
 */
public class DoctorInfoActivity extends BaseActivity {

	private Button backButton;// 返回
	private ImageView doctorPortraitImaegView;// 头像
	private TextView nameTextView;// 姓名
	private TextView dutyTextView;// 职称
	private TextView hospitaltextView;// 医院
	private TextView signTextView;// 签名，如：医者仁心
	private TextView recommendScoreTextView;// 推荐指数：90
	private TextView recommentPercentTextView;// 推荐指数：高于同行2.2%
	private TextView attitudeScoreTextView;// 服务态度：90
	private TextView attitudePercentTextView;// 服务态度：高于同行2.2%
	private TextView abilityScoreTextView;// 医生能力：100
	private TextView abilityPercentTextView;// 医生能力：高于同行2.2%
	private TextView goodClinicTextView;// 科室以及擅长的领域
	private TextView educationTextView;// 教育背景：改为“个人简介”
	// private TextView studyTextView;// 研究成果、所获奖项
	private GestureDetector mGestureDetector;// 监听手势

	private String doctor_id = "";

	private CYTXApplication cytxApp = CYTXApplication.getInstance();
	private ImageView recommendImageView;// 低于、高于、持平
	private ImageView attitudeImageView;// 低于、高于、持平
	private ImageView levelImageView;// 低于、高于、持平

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
			setContentView(R.layout.activity_doctor_480);
		} else {
			setContentView(R.layout.activity_doctor);
		}

		initViews();
		initDatas();
	}

	private void initViews() {
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DoctorInfoActivity.this.finish();
			}
		});

		doctorPortraitImaegView = (ImageView) findViewById(R.id.imageView_portrait);
		nameTextView = (TextView) findViewById(R.id.textView_name);
		dutyTextView = (TextView) findViewById(R.id.textView_duty);
		hospitaltextView = (TextView) findViewById(R.id.textView_hospital);
		signTextView = (TextView) findViewById(R.id.textView_sign);
		recommendScoreTextView = (TextView) findViewById(R.id.textView_recommend_score);
		recommentPercentTextView = (TextView) findViewById(R.id.textView_recommend_percent);
		attitudeScoreTextView = (TextView) findViewById(R.id.textView_attitude_score);
		attitudePercentTextView = (TextView) findViewById(R.id.textView_attitude_percent);
		abilityScoreTextView = (TextView) findViewById(R.id.textView_ability_score);
		abilityPercentTextView = (TextView) findViewById(R.id.textView_ability_percent);
		goodClinicTextView = (TextView) findViewById(R.id.textView_good_clinic);
		educationTextView = (TextView) findViewById(R.id.textView_education);
		// studyTextView = (TextView) findViewById(R.id.textView_study);

		// 手势
		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		recommendImageView = (ImageView) findViewById(R.id.imageView_low_high);
		attitudeImageView = (ImageView) findViewById(R.id.imageView_attitude_low_high);
		levelImageView = (ImageView) findViewById(R.id.imageView_ability_low_high);

	}

	private final int PIXEL = Constants.PIXEL_SCROLL;

	private class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			// 表示向右滑动
			if (e1.getX() - e2.getX() < -PIXEL) {

				DoctorInfoActivity.this.finish();

				return false;
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	private void initDatas() {
		Intent intent = getIntent();
		if (intent != null) {
			doctor_id = intent.getStringExtra("doctor_id");
		}

		WebService webService = WebServiceImpl.getInstance();
		webService.doctorInfo(doctor_id, new AsyncHttpResponseHandler() {

			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(DoctorInfoActivity.this,
					DoctorInfoActivity.this.getResources().getString(R.string.loading));

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				dialog.dismiss();
				if (Constants.isDebug) {
					Log.e("DoctorInfoActivity", arg1);
				}

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

				if (Constants.isDebug) {
					Log.d("DoctorInfoActivity", arg0);
					FileTools.save2SDCard(FileTools.getSDcardPath() + "/doctorInfo", "doctor", ".json", arg0);

				}
				// 得到服务返回的医生信息
				DoctorInfoDomain doctorInfoDomain = FastJsonTools.getObject(arg0, DoctorInfoDomain.class);
				if (doctorInfoDomain != null) {
					setInfo(doctorInfoDomain);
				}

			}

		});

	}

	// 初始化医生基本信息
	protected void setInfo(DoctorInfoDomain doctorInfoDomain) {

		ImageLoader.getInstance().displayImage(doctorInfoDomain.getImage(), doctorPortraitImaegView);
		nameTextView.setText(doctorInfoDomain.getName());
		dutyTextView.setText(doctorInfoDomain.getLevel_title());
		hospitaltextView.setText(doctorInfoDomain.getHospital());
		signTextView.setText(doctorInfoDomain.getWelcome());

		goodClinicTextView.setText(doctorInfoDomain.getDepartment() + "  " + doctorInfoDomain.getGood_at());
		educationTextView.setText(doctorInfoDomain.getDescription());
		// studyTextView.setText(doctorInfoDomain.getAchievement());

		// 初始化各种指数
		List<IndexDomain> indexList = doctorInfoDomain.getIndex();
		if (indexList != null && indexList.size() != 0) {
			for (int i = 0; i < indexList.size(); i++) {
				IndexDomain indexDomain = indexList.get(i);
				if ("推荐指数".equals(indexDomain.getName())) {
					recommendScoreTextView.setText(indexDomain.getRate() + "");
					recommentPercentTextView.setText(indexDomain.getHint());

					if (indexDomain.getHint().contains("低于")) {
						recommendImageView.setImageResource(R.drawable.lower);
						String lower = DoctorInfoActivity.this.getResources().getString(R.string.doctor_lower)
								+ indexDomain.getHint().substring(4, indexDomain.getHint().length());
						recommentPercentTextView.setText(lower);
					} else if (indexDomain.getHint().contains("高于")) {
						recommendImageView.setImageResource(R.drawable.doctor_info_jiantou);
						String higher = DoctorInfoActivity.this.getResources().getString(R.string.doctor_higher)
								+ indexDomain.getHint().substring(4, indexDomain.getHint().length());
						recommentPercentTextView.setText(higher);
					} else if (indexDomain.getHint().contains("持平")) {
						recommendImageView.setImageResource(R.drawable.same);
						recommentPercentTextView.setText(DoctorInfoActivity.this.getResources().getString(
								R.string.doctor_same));
					}

				}
				if ("服务态度".equals(indexDomain.getName())) {
					attitudeScoreTextView.setText(indexDomain.getRate() + "");
					attitudePercentTextView.setText(indexDomain.getHint());

					if (indexDomain.getHint().contains("低于")) {
						attitudeImageView.setImageResource(R.drawable.lower);
						String lower = DoctorInfoActivity.this.getResources().getString(R.string.doctor_lower)
								+ indexDomain.getHint().substring(4, indexDomain.getHint().length());
						attitudePercentTextView.setText(lower);
					} else if (indexDomain.getHint().contains("高于")) {
						attitudeImageView.setImageResource(R.drawable.doctor_info_jiantou);
						String higher = DoctorInfoActivity.this.getResources().getString(R.string.doctor_higher)
								+ indexDomain.getHint().substring(4, indexDomain.getHint().length());
						attitudePercentTextView.setText(higher);
					} else if (indexDomain.getHint().contains("持平")) {
						attitudeImageView.setImageResource(R.drawable.same);
						attitudePercentTextView.setText(DoctorInfoActivity.this.getResources().getString(
								R.string.doctor_same));
					}
				}
				if ("医术水平".equals(indexDomain.getName())) {
					abilityScoreTextView.setText(indexDomain.getRate() + "");
					abilityPercentTextView.setText(indexDomain.getHint());
					if (indexDomain.getHint().contains("低于")) {
						levelImageView.setImageResource(R.drawable.lower);
						String lower = DoctorInfoActivity.this.getResources().getString(R.string.doctor_lower)
								+ indexDomain.getHint().substring(4, indexDomain.getHint().length());
						abilityPercentTextView.setText(lower);
					} else if (indexDomain.getHint().contains("高于")) {
						levelImageView.setImageResource(R.drawable.doctor_info_jiantou);
						String higher = DoctorInfoActivity.this.getResources().getString(R.string.doctor_higher)
								+ indexDomain.getHint().substring(4, indexDomain.getHint().length());
						abilityPercentTextView.setText(higher);
					} else if (indexDomain.getHint().contains("持平")) {
						levelImageView.setImageResource(R.drawable.same);
						abilityPercentTextView.setText(DoctorInfoActivity.this.getResources().getString(
								R.string.doctor_same));
					}
				}
			}
		}

	}

}
