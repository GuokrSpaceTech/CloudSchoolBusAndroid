package com.guokrspace.cloudschoolbus.teacher.module.photo.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.android.support.utils.InputMethodUtils;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.DaoSession;
import com.guokrspace.cloudschoolbus.teacher.module.photo.SelectStudentActivity;
import com.guokrspace.cloudschoolbus.teacher.module.photo.adapter.TagSelectAdapter;
import com.guokrspace.cloudschoolbus.teacher.module.photo.adapter.ImageThumbRecycleViewAdapter;

import net.soulwolf.image.picturelib.model.Picture;

import java.util.ArrayList;

/**
 * 编辑内容
 * 
 * @author lenovo
 * 
 */
public class EditCommentView extends BaseLinearLayout {

	private EditText mCommentEditText;
	private TextView mNumberTextView;
	private GridView mGridView;
	public TagSelectAdapter mTagSelectAdapter;
	private ImageThumbRecycleViewAdapter mPictureThumbnailsAdapter;
	private ArrayList<Picture> mPictures;
    private RecyclerView.OnItemTouchListener  mThumbNailClickListener;
    public RecyclerView thumbNails;

	public EditCommentView(Context context, ArrayList<Picture> pictures) {
		super(context);
		mPictures = pictures;

		init();
	}

	public EditCommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater.from(mContext).inflate(R.layout.view_edit_content, this, true);

		thumbNails = (RecyclerView)findViewById(R.id.thumbnails_recycler_view);
		mPictureThumbnailsAdapter = new ImageThumbRecycleViewAdapter(mContext, mPictures);
		thumbNails.setHasFixedSize(true);
		// use a linear layout manager
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
		linearLayoutManager.setOrientation(HORIZONTAL);
		thumbNails.setLayoutManager(linearLayoutManager);
		thumbNails.addItemDecoration(new SpacesItemDecoration(2));
		thumbNails.setAdapter(mPictureThumbnailsAdapter);

		mGridView = (GridView) findViewById(R.id.gridView);

		mTagSelectAdapter = new TagSelectAdapter(mContext, mApplication.mTagsT);
		mTagSelectAdapter.clearSelected();
		mGridView.setAdapter(mTagSelectAdapter);

		mCommentEditText = (EditText) findViewById(R.id.contentEditText);
		mNumberTextView = (TextView) findViewById(R.id.numberTextView);

		setListener();
	}

	private void setListener() {

		mCommentEditText.setFilters(new InputFilter[]{new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
									   Spanned dest, int dstart, int dend) {

//				if (source.equals("\n")) {
//					return "";
//				}
				return source;
			}

		}});

		mCommentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                mNumberTextView.setText(arg0.length() + "/280");
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

            }
        });
    }


	public void cancel() {
		InputMethodUtils.hideSoftKeyboard(mContext, mCommentEditText);
	}

	public String getCommentText()
	{
		return mCommentEditText.getText().toString();
	}


    public RecyclerView.OnItemTouchListener getmThumbNailClickListener() {
        return mThumbNailClickListener;
    }

    public void setmThumbNailClickListener(RecyclerView.OnItemTouchListener mThumbNailClickListener) {
        this.mThumbNailClickListener = mThumbNailClickListener;
        this.thumbNails.addOnItemTouchListener(mThumbNailClickListener);
    }

	public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
		private int space;

		public SpacesItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view,
								   RecyclerView parent, RecyclerView.State state) {
			outRect.left = space;
			outRect.right = space;
			outRect.bottom = space;

			// Add top margin only for the first item to avoid double space between items
//			if(parent.getChildPosition(view) == 0)
//				outRect.top = space;
		}
	}
//
//    /**
//     * 返回当前选中的index
//     *
//     * @return
//     */
//    public void updateTagSelectedDb(String pickey) {
//        for (int i = 0; i < mApplication.mTagsT.size(); i++) {
//            if ( mTagSelectAdapter.getmSeletions()[i]) {
//                DaoSession db = ((SelectStudentActivity) mContext).mApplication.mDaoSession;
//                mApplication.mTagsT.get(i).setPickey(pickey);
//                db.getTagsEntityTDao().update(mApplication.mTagsT.get(i));
//            }
//        }
//    }
}