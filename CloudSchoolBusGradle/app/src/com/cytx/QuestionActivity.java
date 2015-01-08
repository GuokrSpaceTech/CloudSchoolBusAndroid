package com.cytx;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.cytx.constants.Constants;
import com.cytx.constants.HandlerConstants;
import com.cytx.domain.QuestionCreatedDomain;
import com.cytx.dto.ContentDataDto;
import com.cytx.dto.ContentImageDto;
import com.cytx.dto.ContentTextDto;
import com.cytx.dto.QuestionCreatedDto;
import com.cytx.service.WebService;
import com.cytx.service.impl.WebServiceImpl;
import com.cytx.utility.BitmapTools;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.LanguageHelp;
import com.cytx.utility.MD5;
import com.cytx.utility.UtilUI;
import com.cytx.widget.ClinicDialog;
import com.cytx.widget.GenderDialog;
import com.cytx.widget.UploadImageDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 问题创建界面
 * 
 * @author xilehang
 * 
 */
public class QuestionActivity extends BaseActivity implements OnClickListener {

	private Button backButton;// 返回
	private Button checkButton;// 勾选

	private EditText contentEditText;
	private RelativeLayout genderRelativeLayout;// 选择性别
	private RelativeLayout clinicRelativeLayout;// 选择科室

	private EditText ageEditText;

	private TextView clinicTextView;
	private TextView genderTextView;

	private ImageView uploadImageView;
	private ImageView xImageView;

	private GestureDetector mGestureDetector;// 监听手势

	private String user_id;
	private String[] clinicKeys;
	private String[] clinicValues;
	private int currentClinicIndex;
	private CYTXApplication cytxApp = CYTXApplication.getInstance();

	@SuppressLint("HandlerLeak")
	private Handler questionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			// 性别
			case HandlerConstants.HANDLER_GENDER:
				genderTextView.setText((String) msg.obj);
				break;
			// 拍照
			case HandlerConstants.CAMERA_IMAGE:
				doTakePhoto();
				break;
			// 相册
			case HandlerConstants.PHOTO_IMAGE:
				doSelectImageFromLoacal();
				break;
			// 科室
			case HandlerConstants.CLINIC_TYPE:
				currentClinicIndex = ((Integer) msg.obj).intValue();
				clinicTextView.setText(clinicValues[currentClinicIndex]);
				break;
			// 问题创建成功
			case HandlerConstants.CREATED_SUCCESS:
				Intent intent = new Intent(QuestionActivity.this, ConsultActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
				finish();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
			setContentView(R.layout.activity_question_480);
		} else {
			setContentView(R.layout.activity_question);
		}

		initViews();
		initDatas();

	}

	private void initViews() {
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(this);
		checkButton = (Button) findViewById(R.id.button_check);
		checkButton.setOnClickListener(this);

		genderRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_gender);
		genderRelativeLayout.setOnClickListener(this);
		clinicRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_departments);
		clinicRelativeLayout.setOnClickListener(this);

		clinicTextView = (TextView) findViewById(R.id.textView_clinic);
		genderTextView = (TextView) findViewById(R.id.textView_gender);

		contentEditText = (EditText) findViewById(R.id.editText_question);
		ageEditText = (EditText) findViewById(R.id.editText_age);
		uploadImageView = (ImageView) findViewById(R.id.imageView_upload);
		uploadImageView.setOnClickListener(this);

		xImageView = (ImageView) findViewById(R.id.imageView_x);
		xImageView.setOnClickListener(this);
		xImageView.setVisibility(View.GONE);

		if (!LanguageHelp.isZh(QuestionActivity.this)) {
			if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
				uploadImageView.setImageResource(R.drawable.question_asked_uplaod_image_en_480);
			} else {
				uploadImageView.setImageResource(R.drawable.question_asked_uplaod_image_en);
			}

		}

		mGestureDetector = new GestureDetector(this, new MyGestureListener());
	}

	private void initDatas() {
		Intent intent = getIntent();
		if (intent != null) {
			user_id = intent.getStringExtra("user_id");
		}
		clinicKeys = getResources().getStringArray(R.array.clinic_key);
		clinicValues = getResources().getStringArray(R.array.clinic_value);
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

				Intent intent = new Intent(QuestionActivity.this, ConsultActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
				QuestionActivity.this.finish();

				return false;
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 返回
		case R.id.button_back:
			Intent intent = new Intent(QuestionActivity.this, ConsultActivity.class);
			intent.putExtra("user_id", user_id);
			startActivity(intent);
			QuestionActivity.this.finish();
			break;
		// 勾选
		case R.id.button_check:
			if (checkInfo()) {
				if (hasImage) {
					uploadImage();
				} else {
					createQuestion();
				}

			}
			break;
		// 科室
		case R.id.relativeLayout_departments:
			ClinicDialog clinicDialog = new ClinicDialog(QuestionActivity.this, questionHandler, cytxApp.getScreenType(
					screenWidth, screenHeight));
			clinicDialog.setCancelable(false);
			clinicDialog.setCanceledOnTouchOutside(false);
			clinicDialog.show();
			break;
		// 上传图片
		case R.id.imageView_upload:
			UploadImageDialog uploadImageDialog = new UploadImageDialog(QuestionActivity.this, questionHandler,
					cytxApp.getScreenType(screenWidth, screenHeight));
			uploadImageDialog.setCancelable(false);
			uploadImageDialog.setCanceledOnTouchOutside(false);
			uploadImageDialog.show();
			break;
		// 清除要上传的图片
		case R.id.imageView_x:
			xImageView.setVisibility(View.GONE);
			if (bitMap != null && !bitMap.isRecycled()) {
				bitMap.recycle();
			}
			hasImage = false;
			if (!LanguageHelp.isZh(QuestionActivity.this)) {
				if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
					uploadImageView.setImageResource(R.drawable.question_asked_uplaod_image_en_480);
				} else {
					uploadImageView.setImageResource(R.drawable.question_asked_uplaod_image_en);
				}

			} else {
				if (cytxApp.getScreenType(screenWidth, screenHeight) == cytxApp.SCREEN_480) {
					uploadImageView.setImageResource(R.drawable.question_asked_uplaod_image_480);
				} else {
					uploadImageView.setImageResource(R.drawable.question_asked_uplaod_image);
				}
			}
			break;
		// 性别
		case R.id.relativeLayout_gender:
			GenderDialog genderDialog = new GenderDialog(QuestionActivity.this, questionHandler, cytxApp.getScreenType(
					screenWidth, screenHeight));
			genderDialog.setCancelable(false);
			genderDialog.setCanceledOnTouchOutside(false);
			genderDialog.show();
			break;
		default:
			break;
		}
	}

	/**
	 * check提问信息是否完整
	 * 
	 * @return
	 */
	private boolean checkInfo() {
		if ("".equals(contentEditText.getText().toString().trim())) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_u_question),
					Toast.LENGTH_LONG).show();
			return false;
		}

		if ("".equals(genderTextView.getText().toString().trim())) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_u_gender),
					Toast.LENGTH_LONG).show();
			return false;
		}

		if ("".equals(ageEditText.getText().toString().trim())) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_u_age), Toast.LENGTH_LONG)
					.show();
			return false;
		}

		if ("".equals(clinicTextView.getText().toString().trim())) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_clinic), Toast.LENGTH_LONG)
					.show();
			return false;
		}

		return true;
	}

	/**
	 * 上传图片
	 */
	// 服务器返回的图片路径
	private String imageFileFromService = "";

	private void uploadImage() {
		WebService webService = WebServiceImpl.getInstance();
		webService.fileUpload("image", bitmapFilePath, new AsyncHttpResponseHandler() {
			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(QuestionActivity.this,
					QuestionActivity.this.getResources().getString(R.string.uploading_image));

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
					imageFileFromService = jsonObejct.getString("file");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				createQuestion();
			}

		});
	}

	/**
	 * 创建问题
	 */
	private void createQuestion() {
		QuestionCreatedDto qcd = new QuestionCreatedDto();
		String atime = (System.currentTimeMillis() / 1000) + "";
		qcd.setAtime(atime);
		qcd.setUser_id(user_id);
		String sign = MD5.md5(MD5.getString(atime, user_id));
		qcd.setSign(sign);

		List<Object> contentList = new ArrayList<Object>();
		ContentTextDto contentTextDto = new ContentTextDto();
		contentTextDto.setType("text");
		contentTextDto.setText(contentEditText.getText().toString().trim());
		contentList.add(contentTextDto);

		// 如果有图片，那么上传File；否则不上传File
		if (hasImage) {
			ContentImageDto contentImageDto = new ContentImageDto();
			contentImageDto.setType("image");
			contentImageDto.setFile(imageFileFromService);
			contentList.add(contentImageDto);
		}

		ContentDataDto contentDataDto = new ContentDataDto();
		contentDataDto.setType("patient_meta");
		contentDataDto.setAge(ageEditText.getText().toString().trim());
		contentDataDto.setSex(genderTextView.getText().toString().trim());
		contentList.add(contentDataDto);
		qcd.setContent(contentList);
		qcd.setClinic_no(clinicKeys[currentClinicIndex]);

		WebService webService = WebServiceImpl.getInstance();
		webService.questionCreated(qcd, new AsyncHttpResponseHandler() {
			private ProgressDialog dialog = UtilUI.getProgressMessageDialog(QuestionActivity.this,
					QuestionActivity.this.getResources().getString(R.string.creating_u_question));

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
				System.out.println("question===" + arg0);
				dialog.dismiss();
				QuestionCreatedDomain qcd = FastJsonTools.getObject(arg0, QuestionCreatedDomain.class);
				if (qcd.getError() == 0) {
					UtilUI.showCreatedSuccessDialog(QuestionActivity.this, questionHandler, QuestionActivity.this
							.getResources().getString(R.string.question_success));
				} else {
					UtilUI.showErrorMsgDialog(qcd.getError_msg(), QuestionActivity.this);
				}
			}

		});
	}

	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 1001;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 1002;

	private Bitmap bitMap; // 用来保存图片
	private boolean hasImage; // 是否已经选择了图片

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
			if (bitMap != null && !bitMap.isRecycled()) {
				bitMap.recycle();
			}
			Uri selectedImageUri = data.getData();
			if (selectedImageUri != null) {
				try {
					bitMap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// 下面这两句是对图片按照一定的比例缩放
				if (bitMap == null) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_useful_photo),
							Toast.LENGTH_LONG).show();
					return;
				}
				int scale = BitmapTools.reckonThumbnail(bitMap.getWidth(), bitMap.getHeight(), 109, 127);
				bitMap = BitmapTools.PicZoom(bitMap, (int) (bitMap.getWidth() / scale),
						(int) (bitMap.getHeight() / scale));
				uploadImageView.setImageBitmap(bitMap);
				xImageView.setVisibility(View.VISIBLE);
				hasImage = true;

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

			}
			break;
		case CAMERA_WITH_DATA: // 拍照
			// Bundle bundle = data.getExtras();
			// bitMap = (Bitmap)bundle.get("data");
			if (bitMap != null && !bitMap.isRecycled())
				bitMap.recycle();
			bitMap = (Bitmap) data.getExtras().get("data");
			bitmapFilePath = BitmapTools.saveImage(bitMap);// 将图片保存到指定的路径
			int scale = BitmapTools.reckonThumbnail(bitMap.getWidth(), bitMap.getHeight(), 109, 127);
			bitMap = BitmapTools.PicZoom(bitMap, (int) (bitMap.getWidth() / scale), (int) (bitMap.getHeight() / scale));
			uploadImageView.setImageBitmap(bitMap);
			xImageView.setVisibility(View.VISIBLE);
			hasImage = true;
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitMap != null && !bitMap.isRecycled()) {
			bitMap.recycle();
		}
	}

}
