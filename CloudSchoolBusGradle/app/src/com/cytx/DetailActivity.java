package com.cytx;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.cytx.adapter.DetailAdapter;
import com.cytx.constants.Constants;
import com.cytx.constants.HandlerConstants;
import com.cytx.domain.QuestionAskedDomain;
import com.cytx.domain.QuestionDetailContentDomain;
import com.cytx.domain.QuestionDetailDomain;
import com.cytx.dto.ContentImageDto;
import com.cytx.dto.ContentTextDto;
import com.cytx.dto.QuestionAskedDto;
import com.cytx.service.WebService;
import com.cytx.service.impl.WebServiceImpl;
import com.cytx.utility.BitmapTools;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.FileTools;
import com.cytx.utility.JsonHelp;
import com.cytx.utility.MD5;
import com.cytx.utility.UtilUI;
import com.cytx.widget.UploadImageDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 提问详情界面
 * 
 * @author xilehang
 * 
 */
public class DetailActivity extends BaseActivity implements OnClickListener {

	private ListView detailListView;
	private DetailAdapter detailAdapter;

	private Button backButton;
	// private RelativeLayout detailAssessRelativeLayout;// 显示进入评价前的界面
	// private LinearLayout detailAssessLinearLayout;// 点击开始评价
	private RelativeLayout questionClosedRelativeLayout;// 问题已关闭
	private RelativeLayout questionOKAndAssessRelativeLayout;// 问题已解决，请评价
	private RelativeLayout bottomRelativeLayout;// 提交问题界面
	private Button beginAssessButton;// 点击按钮：问题已解决，请评价

	private ImageView chooseImageView;// 选择图片
	private Button commitButton;// 提交
	private EditText contactEditText;// 显示提交的内容：包括文字和图片
	private long problem_id;
	private String user_id;
	private String status;

	private GestureDetector mGestureDetector;// 监听手势
	private InputMethodManager imm;
	private CYTXApplication cytxApp = CYTXApplication.getInstance();
	private MediaPlayer mediaPlayer;
	private String currentFilePath = "";

	@SuppressLint("HandlerLeak")
	private Handler detailHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 拍照
			case HandlerConstants.CAMERA_IMAGE:
				doTakePhoto();
				break;
			// 相册
			case HandlerConstants.PHOTO_IMAGE:
				doSelectImageFromLoacal();
				break;
			// 播放音频
			case HandlerConstants.AUDIO:
				String filePath = (String) msg.obj;
				// 如果音频正在播放，那么暂停；否则开始播放音乐
				if (mediaPlayer.isPlaying()) {
					pauseAudio(filePath);
				} else {
					currentFilePath = filePath;
					playAudio(filePath);
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
			setContentView(R.layout.activity_detail_480);
		} else {
			setContentView(R.layout.activity_detail);
		}

		initViews();
		initDatas();
	}

	private void initViews() {
		detailListView = (ListView) findViewById(R.id.listView_question_detail);
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(this);

		// detailAssessRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_detail_assess);
		// detailAssessLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_detail_assess);
		// detailAssessLinearLayout.setOnClickListener(this);

		questionClosedRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_question_closed);
		bottomRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_bottom_bottom);
		bottomRelativeLayout.setVisibility(View.INVISIBLE);
		questionOKAndAssessRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_question_ok_and_assess);
		beginAssessButton = (Button) findViewById(R.id.button_begin_assess);
		beginAssessButton.setOnClickListener(this);

		chooseImageView = (ImageView) findViewById(R.id.imageView_choose_image);
		chooseImageView.setOnClickListener(this);
		commitButton = (Button) findViewById(R.id.button_commit);
		commitButton.setOnClickListener(this);
		contactEditText = (EditText) findViewById(R.id.editText_contact);

		// 手势
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		detailListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return mGestureDetector.onTouchEvent(arg1);
			}
		});
		// 软键盘Manager
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
				Intent intent = new Intent(DetailActivity.this, ConsultActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
				DetailActivity.this.finish();

				return false;
			}
			return false;
		}
	}

	private QuestionDetailDomain qdd;

	private void initDatas() {
		Intent intent = getIntent();
		if (intent != null) {
			problem_id = intent.getLongExtra("problem_id", 0);
			user_id = intent.getStringExtra("user_id");
			status = intent.getStringExtra("status");
		}

		WebService webService = WebServiceImpl.getInstance();
		webService.questionDetail(user_id, problem_id + "", new AsyncHttpResponseHandler() {

			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(DetailActivity.this, DetailActivity.this
					.getResources().getString(R.string.loading));

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				if (Constants.isDebug) {
					Log.e("DetailActivity", arg1);
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
					Log.d("DetailActivity", arg0);
					FileTools.save2SDCard(FileTools.getSDcardPath() + "/detail", "detail", ".json", arg0);
				}

				qdd = FastJsonTools.getObject(arg0, QuestionDetailDomain.class);
				if (detailAdapter == null) {
					detailAdapter = new DetailAdapter(detailHandler, DetailActivity.this, qdd, cytxApp.getScreenType(
							screenWidth, screenHeight), user_id, problem_id, status);
					detailListView.setAdapter(detailAdapter);
				} else {
					detailAdapter.setQuestionDetailDomain(qdd);
					detailAdapter.notifyDataSetChanged();
				}
				// 新问题（字段n）、待处理（字段a）、已答复（字段s、v））、待评价（字段c）、已评价（字段d）、系统举报（字段p，特殊情况）
				// 当状态为新问题、待处理、已答复时，底部有补充提问框
				if ("n".equals(status) || "a".equals(status) || "s".equals(status) || "v".equals(status)) {
					questionClosedRelativeLayout.setVisibility(View.GONE);
					questionOKAndAssessRelativeLayout.setVisibility(View.GONE);
					bottomRelativeLayout.setVisibility(View.VISIBLE);

				}

				// 当状态为待评价时，点击底部按钮可进入评价页面
				if ("c".equals(status)) {
					questionClosedRelativeLayout.setVisibility(View.GONE);
					questionOKAndAssessRelativeLayout.setVisibility(View.VISIBLE);
					bottomRelativeLayout.setVisibility(View.INVISIBLE);
				}

				// 当状态为已评价时，底部为状态文字，不可点击
				if ("d".equals(status)) {
					questionClosedRelativeLayout.setVisibility(View.VISIBLE);
					questionOKAndAssessRelativeLayout.setVisibility(View.GONE);
					bottomRelativeLayout.setVisibility(View.INVISIBLE);
				}
			}

		});
		// 实例化MediaPlayer对象
		mediaPlayer = new MediaPlayer();
		// 设置音乐播放完成时的监听器
		mediaPlayer.setOnCompletionListener(onCompletionListener);
	}

	/**
	 * 监听MediaPlayer播放结束
	 */
	OnCompletionListener onCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			notifyAdapterData("");
		}

	};

	/**
	 * 刷新Adapter
	 */
	private void notifyAdapterData(String path) {
		if (detailAdapter != null) {
			detailAdapter.setCurrentAudioPath(path);
			detailAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 播放音频
	 */
	private void playAudio(String filePath) {
		try {
			// 媒体播放器重置
			mediaPlayer.reset();
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.start();
			notifyAdapterData(filePath);
		} catch (Exception e) {

		}
	}

	/**
	 * 暂停音频
	 */
	private void pauseAudio(String filePath) {
		try {
			// 如果点击正在播放的音频，那么暂停;
			// 否则播放当前点击的音频
			if (filePath.equals(currentFilePath)) {
				mediaPlayer.pause();
				notifyAdapterData("");
			} else {
				currentFilePath = filePath;
				playAudio(filePath);
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 点击进入评价界面
		// case R.id.linearLayout_detail_assess:
		// Intent intent = new Intent(DetailActivity.this, AssessActivity.class);
		// intent.putExtra("user_id", user_id);
		// intent.putExtra("problem_id", problem_id);
		// startActivity(intent);
		// break;
		// 返回
		case R.id.button_back:
			Intent intent2 = new Intent(DetailActivity.this, ConsultActivity.class);
			intent2.putExtra("user_id", user_id);
			startActivity(intent2);
			DetailActivity.this.finish();
			break;
		// 选择图片
		case R.id.imageView_choose_image:
			UploadImageDialog dialog = new UploadImageDialog(DetailActivity.this, detailHandler, cytxApp.getScreenType(
					screenWidth, screenHeight));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			break;
		// 提交问题
		case R.id.button_commit:
			if ("".equals(contactEditText.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(), "请输入您要咨询的问题", Toast.LENGTH_LONG).show();
			} else {
				askQuestion("");
			}
			break;
		// 底部：问题已解决，请评价
		case R.id.button_begin_assess:
			Intent intent = new Intent(DetailActivity.this, AssessActivity.class);
			intent.putExtra("user_id", user_id);
			intent.putExtra("problem_id", problem_id);
			intent.putExtra("status", status);
			startActivity(intent);
			finish();
			break;
		}
	}

	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 1001;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 1002;

	private Bitmap bitMap; // 用来保存图片

	/**
	 * 拍照获取图片
	 * 
	 */
	private void doTakePhoto() {
		try {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从本地手机中选择图片
	 */
	private void doSelectImageFromLoacal() {
		Intent localIntent = new Intent();
		localIntent.setType("image/*");
		localIntent.setAction("android.intent.action.GET_CONTENT");
		Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
		startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);
	}

	// 上传的图片路径
	private String bitmapFilePath = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: // 从本地选择图片

			Uri selectedImageUri = data.getData();
			if (selectedImageUri != null) {

				// 获取相册图片的路径
				String[] proj = { MediaStore.Images.Media.DATA };
				// 好像是android多媒体数据库的封装接口，具体的看Android文档
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(selectedImageUri, proj, null, null, null);
				// 按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				bitmapFilePath = cursor.getString(column_index);

				// 直接上传图片
				uploadImage();

			}
			break;
		case CAMERA_WITH_DATA: // 拍照
			// Bundle bundle = data.getExtras();
			// bitMap = (Bitmap)bundle.get("data");
			if (bitMap != null && !bitMap.isRecycled())
				bitMap.recycle();
			bitMap = (Bitmap) data.getExtras().get("data");
			bitmapFilePath = BitmapTools.saveImage(bitMap);// 将图片保存到指定的路径

			// 直接上传图片
			uploadImage();

			break;
		}
	}

	/**
	 * 上传图片
	 */
	private void uploadImage() {
		WebService webService = WebServiceImpl.getInstance();
		webService.fileUpload("image", bitmapFilePath, new AsyncHttpResponseHandler() {
			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(DetailActivity.this, DetailActivity.this
					.getResources().getString(R.string.uploading_image));

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
				System.out.println("arg0==" + arg0);
				JSONObject jsonObejct;
				try {
					jsonObejct = new JSONObject(arg0);
					String imageFileFromService = jsonObejct.getString("file");
					askQuestion(imageFileFromService);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		});
	}

	/**
	 * 追问问题
	 */
	protected void askQuestion(final String imageFileFromService) {
		QuestionAskedDto qad = new QuestionAskedDto();
		String atime = (System.currentTimeMillis() / 1000) + "";
		qad.setAtime(atime);
		final List<Object> lists = new ArrayList<Object>();
		// 当没有上传图片时，加载text内容
		if (imageFileFromService == null || "".equals(imageFileFromService)) {
			ContentTextDto ctd = new ContentTextDto();
			ctd.setType("text");
			ctd.setText(contactEditText.getText().toString().trim());
			lists.add(ctd);
			// 当有上传图片时，加载image内容
		} else {
			ContentImageDto cid = new ContentImageDto();
			cid.setType("image");
			cid.setFile(imageFileFromService);
			lists.add(cid);
		}
		qad.setContent(lists);
		qad.setProblem_id(problem_id + "");
		qad.setSign(MD5.md5(MD5.getString(atime, problem_id + "")));
		qad.setUser_id(user_id);
		WebService webService = WebServiceImpl.getInstance();
		webService.questionAsked(qad, new AsyncHttpResponseHandler() {
			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(DetailActivity.this, DetailActivity.this
					.getResources().getString(R.string.submiting_question));

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
				QuestionAskedDomain qcd = FastJsonTools.getObject(arg0, QuestionAskedDomain.class);
				if (qcd.getError() == 0) {

					// 改变问题详情内容
					List<QuestionDetailContentDomain> contents = qdd.getContent();
					QuestionDetailContentDomain qdcd = new QuestionDetailContentDomain();
					qdcd.setCreated_time_ms(System.currentTimeMillis());
					qdcd.setType("p");
					qdcd.setContent(JsonHelp.jsonObjectToString(lists));
					contents.add(qdcd);
					qdd.setContent(contents);

					// 刷新adapter
					if (detailAdapter == null) {
						detailAdapter = new DetailAdapter(detailHandler, DetailActivity.this, qdd, cytxApp
								.getScreenType(screenWidth, screenHeight), user_id, problem_id, status);
						detailListView.setAdapter(detailAdapter);
					} else {
						detailAdapter.setQuestionDetailDomain(qdd);
						detailAdapter.notifyDataSetChanged();
						detailListView.setSelection(contents.size() - 1);
					}

					/**
					 * 判断上传的方式：1.只上传文字，2.上传图片和文字，3.只上传图片
					 */
					// 1. 只上传文字
					if (imageFileFromService == null || "".equals(imageFileFromService)) {
						// 将edittext内容清空
						contactEditText.setText("");
					}

					// 2. 上传图片和文字
					if (imageFileFromService != null && !"".equals(imageFileFromService)) {
						// 有文字内容，那么上传文字
						if (!contactEditText.getText().toString().equals("")) {
							// 开始上传
							askQuestion("");
						}
					}

					// 3. 只上传图片，此时不需要任何操作

					// 强制隐藏键盘
					imm.hideSoftInputFromWindow(contactEditText.getWindowToken(), 0);

				} else {
					UtilUI.showErrorMsgDialog(qcd.getError_msg(), DetailActivity.this);
				}
			}

		});
	}

	@Override
	protected void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
		super.onDestroy();
	}

}
