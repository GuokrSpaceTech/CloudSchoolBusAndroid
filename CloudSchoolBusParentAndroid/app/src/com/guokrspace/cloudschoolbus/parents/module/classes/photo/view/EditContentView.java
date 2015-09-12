package com.guokrspace.cloudschoolbus.parents.module.classes.photo.view;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.support.utils.InputMethodUtils;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.BaseLinearLayout;
import com.guokrspace.cloudschoolbus.parents.entity.Tag;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.EditContentAdapter;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.ImageThumbRecycleViewAdapter;

import net.soulwolf.image.picturelib.model.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑内容
 * 
 * @author lenovo
 * 
 */
public class EditContentView extends BaseLinearLayout {

	private EditText mContentEditText;
	private TextView mNumberTextView;
	private ViewFlipper mViewFlipper;
//	private SelectedStuView mSelectedStuView;
//	private TitleNavBarView mTitleNavBarView;
	private GridView mGridView;
	private TextView mAllButton;
	private EditContentAdapter mEditContentAdapter;
	private ImageThumbRecycleViewAdapter mPictureThumbnailsAdapter;
    private Context mParentContext;
	private ArrayList<Picture> mPictures;

	private UploadFile mUploadFile;
	private List<UploadFile> mUploadFileList = new ArrayList<UploadFile>();

//	private RotationHelper rotationHelper;

	// private boolean isAllFlag = false;

	public EditContentView(Context context, List<UploadFile> uploadFiles, ArrayList<Picture> pictures) {
		super(context);
//		mViewFlipper = viewFlipper;
        mParentContext = context;
		mUploadFileList = uploadFiles;
		mPictures = pictures;

		init();
	}

	public EditContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
        mParentContext = context;
		init();
	}

	private void init() {
		LayoutInflater.from(mContext).inflate(R.layout.view_edit_content, this,
				true);

        //Init the action bar and the menu
//        SendFileToStuActivity parentActivity = (SendFileToStuActivity) mParentContext;
//        ActionBar actionBar = parentActivity.getSupportActionBar();
//        actionBar.setTitle(mContext.getString(R.string.say_something));
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        parentActivity.setCurrentView(SendFileToStuActivity.EDIT_CONTENT);

		RecyclerView thumbNails = (RecyclerView)findViewById(R.id.thumbnails_recycler_view);
		mPictureThumbnailsAdapter = new ImageThumbRecycleViewAdapter(mContext, mPictures);
		thumbNails.setHasFixedSize(true);
		// use a linear layout manager
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
		linearLayoutManager.setOrientation(HORIZONTAL);
		thumbNails.setLayoutManager(linearLayoutManager);
		thumbNails.setAdapter(mPictureThumbnailsAdapter);

		mAllButton = (TextView) findViewById(R.id.allButton);
		mGridView = (GridView) findViewById(R.id.gridView);

//        int tagLanguages = (mApplication.photoTagList).size();
//		if (ToolUtils.isLanguage(mContext, "zh")) {
//			// 系统汉语的时候
//            if(tagLanguages >= 1) {
//                mEditContentAdapter = new EditContentAdapter(mContext,
//                        mApplication.mLoginSetting.photoTagList.get(0));
//            }
//		} else {
//			// 系统英文的时候
//            if(tagLanguages >= 2) {
//                mEditContentAdapter = new EditContentAdapter(mContext,
//                        mApplication.mLoginSetting.photoTagList.get(1));
//            } else if ( tagLanguages >= 1) {
//                mEditContentAdapter = new EditContentAdapter(mContext,
//                        mApplication.mLoginSetting.photoTagList.get(0));
//            }
//		}
		//Test code
		ArrayList<Tag> tags = new ArrayList<>();
		for(int i=0; i<5; i++)
		{
			Tag tag = new Tag();
			tag.setTagid(String.valueOf(i));
			tag.setTagName("TestTag");
			tags.add(tag);
		}
		mEditContentAdapter = new EditContentAdapter(mContext, tags);
		mEditContentAdapter.clearSelected();
		mGridView.setAdapter(mEditContentAdapter);
		mEditContentAdapter.setAllButton(mAllButton);

//		rotationHelper = new RotationHelper(EditContentView.this);

		mContentEditText = (EditText) findViewById(R.id.contentEditText);
		mNumberTextView = (TextView) findViewById(R.id.numberTextView);

		setListener();

	}

	/**
	 * 
	 * @param index
	 * @return 返回表示当前item是否选中
	 */
	public void setSelectedIndex(int index) {
		mEditContentAdapter.setSelectedIndex(index, mAllButton.isSelected());
		mEditContentAdapter.notifyDataSetChanged();
	}

	private void setListener() {

		mContentEditText.setFilters(new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {

				if (source.equals("\n")) {
					return "";
				}
				return source;
			}

		} });

		mContentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				mNumberTextView.setText(arg0.length() + "/280");
//				if (null != mAllButton && !arg0.toString().equals(mUploadFile.intro)) {
//					mAllButton.setEnabled(true);
//					mAllButton.setSelected(false);
//				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TextView textView = (TextView)
				// arg1.findViewById(R.id.textView);
				// textView.setSelected(!textView.isSelected());
			}
		});

		mAllButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// mEditContentAdapter.allSelected();
				if (mAllButton.isSelected()) {
					mAllButton.setSelected(false);
				} else {
					mAllButton.setEnabled(false);
					mAllButton.setSelected(true);
				}
			}
		});
	}


	public void cancel() {
		InputMethodUtils.hideSoftKeyboard(mContext, mContentEditText);
//		getRotationHelper().applyRotation(Constants.viewA, 0, -90);
//        ((SendFileToStuActivity)mParentContext).setCurrentView(SendFileToStuActivity.SELECTED_STU);
//        ((SendFileToStuActivity)mParentContext).getSupportActionBar().setTitle(mContext.getString(R.string.in_the_photo));
//        ((SendFileToStuActivity)mParentContext).invalidateOptionsMenu();
	}

//	public void setSelectedStuView(SelectedStuView selectedStuView) {
//		mSelectedStuView = selectedStuView;
//	}

	public void setUploadFile(UploadFile uploadFile) {
		mUploadFile = uploadFile;
	}

	public void setUploadFileList(List<UploadFile> uploadFiles) {
		mUploadFileList = uploadFiles;
	}

	public EditText getContentEditText() {
		return mContentEditText;
	}

	public EditContentAdapter getEditContentAdapter() {
		return mEditContentAdapter;
	}

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
//		return Constants.viewA;
//	}
//
//	@Override
	public void swapViewA() {
		// DebugLog.logI("swapViewA");
//		mViewFlipper.setDisplayedChild(SendFileToStuActivity.SELECTED_STU);
//		mSelectedStuView.getRotationHelper().applyRotation(Constants.viewA, 90,
//				0);
		// if (mEditContentAdapter.getAllSelected()) {
		if (mAllButton.isSelected()) {
			 mUploadFile.photoTag = mContext.getString(R.string.all);
			 mUploadFile.intro = mContentEditText.getText().toString();
//			 mSelectedStuView.setContent(mContext.getString(R.string.apply_all),
//			 mContentEditText.getText().toString());
			int photoIndex = -1;
			String photoTag = "";
//			photoIndex = mEditContentAdapter.getCurrentItem();
//			if (-1 != photoIndex) {
//				photoTag = ((PhotoTag) mEditContentAdapter.getItem(photoIndex)).tagname;
//			}

            photoTag = mEditContentAdapter.getSelection();

			for (int i = 0; i < mUploadFileList.size(); i++) {
				UploadFile uploadFile = mUploadFileList.get(i);
				uploadFile.photoTag = photoTag;
				uploadFile.intro = mContentEditText.getText().toString();
			}
//			mSelectedStuView.setContent(photoTag, mContentEditText.getText()
//					.toString());
		} else {
			int photoIndex = -1;
			String photoTag = "";
//			if (-1 != photoIndex) {
//				photoTag = ((PhotoTag) mEditContentAdapter.getItem(photoIndex)).tagname;
//			}
            photoTag = mEditContentAdapter.getSelection();

            mUploadFile.photoTag = photoTag;
			mUploadFile.intro = mContentEditText.getText().toString();
//			mSelectedStuView.setContent(photoTag, mContentEditText.getText()
//					.toString());
		}


	}
//
//	@Override
//	public void swapViewB() {
//		// DebugLog.logI("swapViewB");
//		mViewFlipper.setDisplayedChild(SendFileToStuActivity.EDIT_CONTENT);
//	}
}