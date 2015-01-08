package com.Manga.Activity.utils.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.Manga.Activity.R;
import com.Manga.Activity.utils.dialog.CustomListDialog;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {

	private Context mContext;
	private List<CustomListDialog.CustomListDialogItem> mCustomListDialogItemList;

	public CustomListAdapter(Context context, List<CustomListDialog.CustomListDialogItem> customListDialogItems) {
		mContext = context;
		mCustomListDialogItemList = customListDialogItems;
	}

	@Override
	public int getCount() {
		if (null == mCustomListDialogItemList) {
			return 0;
		} else {
			return mCustomListDialogItemList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (null == mCustomListDialogItemList) {
			return 0;
		} else {
			return mCustomListDialogItemList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_custom_list, null);
		}
		
		CustomListDialog.CustomListDialogItem customListDialogItem = mCustomListDialogItemList.get(position);
		
		TextView itemTextView = (TextView) convertView
				.findViewById(R.id.itemTextView);
		itemTextView.setText(customListDialogItem.text);
		itemTextView.setTextColor(customListDialogItem.textColor);
		itemTextView.setTextSize(customListDialogItem.textSize);

		// ViewGroup simpleLayout = (ViewGroup) convertView
		// .findViewById(R.id.simpleLayout);
		// if (mSelect.equals(mStrings.get(position))) {
		// itemTextView.setSelected(true);
		// } else {
		// itemTextView.setSelected(false);
		// }

		return convertView;
	}

}
