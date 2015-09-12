package com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.entity.Tag;

import java.util.List;

public class EditContentAdapter extends BaseAdapter {

	private Context mContext;
	private List<Tag> mPhotoTagList;
	/** 是否是全部选择 */
	// private boolean mAllSelected = false;
	private int mLastIndex = -1;
	private TextView mAllButton;

	public EditContentAdapter(Context context, List<Tag> editContentAreas) {
		mContext = context;
		mPhotoTagList = editContentAreas;
	}

	@Override
	public int getCount() {
		return mPhotoTagList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mPhotoTagList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {

		if (null == arg1) {
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_edit_content, null);
		}

		final Tag photoTag = mPhotoTagList.get(position);

		final TextView textView = (TextView) arg1.findViewById(R.id.textView);
		textView.setText(photoTag.getTagName());
		textView.setSelected(photoTag.isSelected());
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// photoTag.isSelected = !photoTag.isSelected;
				// textView.setSelected(photoTag.isSelected);
				setSelectedIndex(position,false);
				if(null != mAllButton){
					mAllButton.setEnabled(true);
					mAllButton.setSelected(false);
				}
			}
		});
		return arg1;
	}

	public void setAllButton(TextView allButton){
		mAllButton = allButton;
	}

	public void clearAllSelected() {
		clearSelected();
		// mAllSelected = false;
		notifyDataSetChanged();
	}

	public void clearSelected() {
		for (int i = 0; i < mPhotoTagList.size(); i++)
			mPhotoTagList.get(i).setIsSelected(false);
	}

	// public boolean getAllSelected() {
	// return mAllSelected;
	// }

	/**
	 * 返回当前选中的index
	 * 
	 * @return
	 */
	public int getCurrentItem() {
		int index = -1;
		for (int i = 0; i < mPhotoTagList.size(); i++) {
			if (mPhotoTagList.get(i).isSelected()) {
				index = i;
			}
		}
		return index;
	}

    /**
     * 返回当前选中的index
     *
     * @return
     */
    public String getSelection() {
        int index = -1;
        String tagList = "";
        for (int i = 0; i < mPhotoTagList.size(); i++) {
            if (mPhotoTagList.get(i).isSelected()) {
                index = i;
                tagList += mPhotoTagList.get(i).getTagid() + ",";
            }
        }

        if(!tagList.equals(""))
        {
            int lastPos = tagList.lastIndexOf(",");
            tagList = tagList.substring(0,lastPos);
        }

        return tagList;
    }

	/**
	 * 
	 * @param index
	 * @param ignore
	 *            true标示忽略此次设置
	 * @return 返回表示当前item是否选中
	 */
	public boolean setSelectedIndex(int index, boolean ignore) {
		boolean defaultResult = mPhotoTagList.get(index).isSelected();
		if (!ignore) {
			boolean result = !mPhotoTagList.get(index).isSelected();
			mPhotoTagList.get(index).setIsSelected(result);

            //Ensure only 1 button is selected
//			if (-1 != mLastIndex && index != mLastIndex)
//				mPhotoTagList.get(mLastIndex).isSelected = false;

            mLastIndex = index;

			// if (mAllSelected) {
			// clearSelected();
			// mPhotoTagList.get(index).isSelected = true;
			// }
			// mAllSelected = false;
			notifyDataSetChanged();
			return result;
		} else {
			return defaultResult;
		}

	}

}
