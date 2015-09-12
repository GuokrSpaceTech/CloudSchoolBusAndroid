package com.guokrspace.cloudschoolbus.parents.module.classes.photo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.android.support.debug.DebugLog;
import com.android.support.handlerui.HandlerToastUI;
import com.android.support.utils.ScreenUtils;
import com.android.support.utils.ToolUtils;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.BaseLinearLayout;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.ImageThumbRecycleViewAdapter;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.StuSelectAdapter;

import net.soulwolf.image.picturelib.model.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import com.cloud.school.bus.teacherhelper.modules.uploadlist.uploadutils.UploadUtils;


/**
 * 选择同学发送文件
 * 
 * @author lenovo
 * 
 */
public class SelectedStuView extends BaseLinearLayout {

	private ImageThumbRecycleViewAdapter mPictureThumbnailsAdapter;
	private GridView mGridView;
	private StuSelectAdapter mStuSelectAdapter;
	private TextView mContentTextView;
	private TextView mPictureNumberTextView;
	private ViewFlipper mViewFlipper;
	private EditContentView mEditContentView;

	private List<StudentEntity> mStudents;
	private List<Picture> mPictures;

	private List<UploadFile> mUploadFiles = new ArrayList<UploadFile>();
	/** 0表示所有照片，1表示相机拍摄 */
	private int mFlag = 0;

//    public SelectedStuView(Context context, List<Picture> pictures,
//						   ViewFlipper viewFlipper, int flag) {
//		super(context);
//		mPictures = pictures;
//		mViewFlipper = viewFlipper;
//		mFlag = flag;
//        mParentContext = context;
//		init();
//	}

	public SelectedStuView(Context context,  AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		init();
	}

	public SelectedStuView(Context context, List<Picture> pictures, List<StudentEntity> students) {
		super(context);
        mContext = context;
		mPictures = pictures;
		mStudents = students;

		init();
	}

	private void init() {
		LayoutInflater.from(mContext).inflate(R.layout.view_selected_stu, this, true);


		ViewGroup pagerLayout = (ViewGroup) findViewById(R.id.pagerLayout);
		int height = (ScreenUtils.getScreenBounds(mContext)[1] - ToolUtils.dipToPx(mContext, 45)) / 2;
//		DebugLog.logI("viewpager height : " + height);
//		pagerLayout.setLayoutParams(new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT, height));

		mContentTextView = (TextView) findViewById(R.id.contentTextView);
		mPictureNumberTextView = (TextView) findViewById(R.id.pictureNumberTextView);
		mPictureNumberTextView.setText("1/" + mPictures.size());

		mGridView = (GridView) findViewById(R.id.gridView);
		mStuSelectAdapter = new StuSelectAdapter(mContext, mApplication.mStudents);
		mGridView.setAdapter(mStuSelectAdapter);

		setListener();
	}

	private String getPicName(String picPathString) {
		String picNameString = "";
		picNameString = picPathString
				.substring(picPathString.lastIndexOf("/") + 1);
		return picNameString;
	}

	private int getPicSize(String picPathString) {
		int size = 0;
		try {
			picPathString = picPathString.replace("file:///", "/");
			FileInputStream inputStream = new FileInputStream(new File(
					picPathString));
			try {
				size = inputStream.available();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return size;
	}


	public void setContent(String photoTag, String contentString) {
		if (TextUtils.isEmpty(photoTag) && TextUtils.isEmpty(contentString)) {
			mContentTextView.setVisibility(View.GONE);
			mContentTextView.setText("");
		} else {
			mContentTextView.setVisibility(View.VISIBLE);
			mContentTextView.setText(photoTag + " " + contentString);
		}
	}


    public void uploadPicture()
    {
        int i = 0;
        for (i = 0; i < mUploadFiles.size(); i++) {
            UploadFile uploadFile = mUploadFiles.get(i);
            if (0 == uploadFile.studentIdList.size()) {
                break;
            }
        }
        if (mUploadFiles.size() == i)
        {
            // 都关联了学生
            // 返回上一级
            for (UploadFile uploadFile : mUploadFiles) {
                uploadFile.setFileType(0);
            }
//            mApplication.imageLoaderInit(80, 80);
            ((Activity) mContext).setResult(Activity.RESULT_OK);
            ((Activity) mContext).finish();
            saveUploadFile();
        } else {
            HandlerToastUI.getHandlerToastUI(mContext, "还有照片没有关联学生");
        }
    }

	private void saveUploadFile() {
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// 保存数据
//		UploadFileUtils.getUploadUtils().setContext(mContext);
//		UploadFileUtils.getUploadUtils().setUploadFileDB(mUploadFiles);
//		UploadFileUtils.getUploadUtils().uploadFileService();
		// }
		// }).start();
	}

//	public void setEditContentView(EditContentView editContentView) {
//		mEditContentView = editContentView;
//		mEditContentView.setUploadFile(mUploadFiles.get(mViewPager
//				.getCurrentItem()));
//		mEditContentView.setUploadFileList(mUploadFiles);
//	}

	protected void setListener() {
//		Button contentButton = (Button) findViewById(R.id.contentButton);
//		contentButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				rotationHelper.applyRotation(Constants.viewB, 0, 90);
//                ((SendFileToStuActivity)mParentContext).setCurrentView(SendFileToStuActivity.EDIT_CONTENT);
//                ((SendFileToStuActivity)mParentContext).getSupportActionBar().setTitle(mContext.getString(R.string.say_something));
//                ((SendFileToStuActivity)mParentContext).invalidateOptionsMenu();
//			}
//		});

//        final Button signinButton = (Button) findViewById(R.id.signinButton);
//        if(((SendFileToStuActivity)mParentContext).mApplication.isTrain == 1)
//        {
//            signinButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    picUploadType = picUploadType==Constant.UPLOAD_TYPE_SUBMIT?Constant.UPLOAD_TYPE_SHARE:Constant.UPLOAD_TYPE_SUBMIT;
//                    signinButton.setBackgroundResource(picUploadType==Constant.UPLOAD_TYPE_SUBMIT?R.drawable.btn_sign_in_checked:R.drawable.btn_sign_in_unchecked);
//                }
//            });
//        }
//        else
//        {
//            signinButton.setVisibility(View.GONE);
//        }

//        mPictureThumbnailsAdapter.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				int position = mViewPager.getCurrentItem();
//				List<Picture> pictures = new ArrayList<Picture>();
//				pictures.add(mPictures.get(position));
//				Intent intent = new Intent(mContext, BigPictureActivity.class);
//				intent.putExtra("pictureList", (ArrayList<Picture>) pictures);
//				intent.putExtra("position", position);
//				intent.putExtra("flag", 1);
//				((Activity) mContext).startActivityForResult(intent, 0);
//			}
//
//		});
//
//		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				mPictureNumberTextView.setText((arg0 + 1) + "/"
//						+ mPictures.size());
//
//				mEditContentView.setUploadFile(mUploadFiles.get(mViewPager
//						.getCurrentItem()));
//				// if (mUploadFiles.size() > 0)
//				// mStuSelectAdapter.setUploadFile(mUploadFiles.get(mViewPager
//				// .getCurrentItem()));
//				refreshUiData(mViewPager.getCurrentItem());
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//
//			}
//		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0: {
			// 查看大图返回
			switch (resultCode) {
			case Activity.RESULT_OK: {
				// 删除图片成功返回
				if (null != data) {
					int delPicIndex = data.getIntExtra("DelPicIndex", -1);
					if (-1 != delPicIndex) {
						mPictures.remove(delPicIndex);
						mUploadFiles.remove(delPicIndex);

						if (0 == mPictures.size()) {
//							mApplication.imageLoaderInit(80, 80);
							((Activity) mContext).finish();
							return;
						} else {
						}
						mPictureThumbnailsAdapter.notifyDataSetChanged();

//						mPictureNumberTextView
//								.setText((mViewPager.getCurrentItem() + 1)
//										+ "/" + mPictures.size());
//						refreshUiData(mViewPager.getCurrentItem());
					}
				}
				break;
			}
			case Activity.RESULT_CANCELED: {
				break;
			}
			default:
				break;
			}
			break;
		}
		default:
			break;
		}
	}

//	public void cancel() {
//		CustomAlertDialog customAlertDialog = new CustomAlertDialog(mContext,
//				R.style.CustomAlertDialog);
//		customAlertDialog.setTitleMessage("提示", Color.BLACK);
//		customAlertDialog.setMessage("取消发送照片");
//		customAlertDialog.setLeftButton("是", new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				mApplication.imageLoaderInit(80, 80);
//				((Activity) mContext).finish();
//			}
//		});
//		customAlertDialog.setRightButton("否", new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//			}
//		});
//		customAlertDialog.show();
//	}

	// private void uploadFile(final UploadFile uploadFile) {
	//
	// String picPathString = uploadFile.picPathString
	// .replace("file:///", "/");
	// StringBuilder studentIdStringBuilder = new StringBuilder();
	// if (uploadFile.studentIdList.size() > 0) {
	// for (int i = 0; i < uploadFile.studentIdList.size(); i++) {
	// studentIdStringBuilder.append(uploadFile.studentIdList.get(i)).append(",");
	// }
	// studentIdStringBuilder.delete(studentIdStringBuilder.length() - 1,
	// studentIdStringBuilder.length());
	// }
	//
	// String fname = null, fsize = null, fbody = null, fext = null;
	// if (!TextUtils.isEmpty(picPathString)) {
	// fbody = PictureUtil.getPicString(picPathString, 512);
	// fext = picPathString.substring(picPathString.lastIndexOf(".") + 1);
	// fname = picPathString.substring(picPathString.lastIndexOf("/") + 1);
	// fsize = fbody.length() + "";
	// }
	//
	// NetworkClient.getNetworkClient().PostRequest(
	// new UploadFileRequest(mContext.getApplicationContext(), fbody,
	// fname, studentIdStringBuilder.toString(),
	// mApplication.mClassInfo.uid, uploadFile.intro,
	// uploadFile.photoTag, mApplication.mTeacher.teacherid),
	// new UploadFileResponse() {
	// @Override
	// public void onResponse(
	// ResponseHandlerInterface responseHandlerInterface) {
	// DebugLog.logI("code : " + code);
	// if ("1".equals(code)) {
	// // 上传成功，更新上传列表
	// UploadFileDB uploadFileDB = UploadFileDB
	// .getUploadFileDB(mContext);
	// uploadFileDB.remove(uploadFile);
	// uploadFileDB.close();
	//
	// //刷新列表
	// Intent intent = new Intent();
	// intent.setAction(UploadListFragment.ACTION_UPDATE_UPLOAD_LIST);
	// mContext.sendBroadcast(intent);
	//
	// }
	// }
	// }, new BaseStateListener() {
	// }, null);
	// }

//	@Override
//	public void initSwapView() {
//		// DebugLog.logI("initSwapView");
//		init();
//	}
//
//	@Override
//	public RotationHelper getRotationHelper() {
//		// DebugLog.logI("getRotationHelper");
//		return rotationHelper;
//	}
//
//	@Override
//	public ViewGroup getViewGroup() {
//		// DebugLog.logI("getViewGroup");
//		return mViewFlipper;
//	}
//
//	@Override
//	public int getSwapView() {
//		// DebugLog.logI("getSwapView");
//		return Constants.viewB;
//	}
//
//	@Override
//	public void swapViewA() {
//		// DebugLog.logI("swapViewA");
//		mViewFlipper.setDisplayedChild(SendFileToStuActivity.SELECTED_STU);
//	}
//
//	@Override
//	public void swapViewB() {
//		// DebugLog.logI("swapViewB");
//		mViewFlipper.setDisplayedChild(SendFileToStuActivity.EDIT_CONTENT);
//		mEditContentView.getRotationHelper().applyRotation(Constants.viewB,
//				-90, 0);
//	}
}
