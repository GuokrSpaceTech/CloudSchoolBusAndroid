package com.Manga.Activity.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.Manga.Activity.utils.dialog.adapter.CustomListAdapter;

import java.util.List;


/**
 * 列表对话框
 * 
 * @author hongfeijia
 * 
 */
public class CustomListDialog extends Dialog {

	public static class CustomListDialogItem{
		public String text;
		public int textSize;
		public int textColor;
	}
	
	private Context mContext;
	private ListView mListView;
	private CustomListAdapter mCustomListAdapter;

	private OnItemClickListener mOnItemClickListener;

	protected CustomListDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	protected CustomListDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	protected CustomListDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mContext = context;

		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);

		setContentView(R.layout.dialog_custom_list);

		mListView = (ListView) findViewById(R.id.listView);
		
		setListener();

		setCanceledOnTouchOutside(true);
	}

	/**
	 * 重构show方法用来解决，当activity调用finish之后Dialog才调用show引起的崩溃
	 */
	@Override
	public void show() {
		try {
			super.show();
		} catch (WindowManager.BadTokenException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个对象
	 * 
	 * @param context
	 * @param theme
	 * @return
	 */
	public static CustomListDialog getCustomListDialog(Context context,
			int theme) {
		return new CustomListDialog(context, theme);
	}

	/**
	 * 设置正文
	 * 
	 * @param customListDialogItems
	 *            传入Adapter的列表
	 */
	public CustomListDialog setMessage(List<CustomListDialogItem> customListDialogItems) {
		if (null == customListDialogItems) {
			throw new NullPointerException();
		}
//		if (customListDialogItems.size() > 4) {
//			LayoutParams layoutParams = mListView.getLayoutParams();
//			layoutParams.height = ToolUtils.dipToPx(mContext, 40)*customListDialogItems.size();
//			mListView.setLayoutParams(layoutParams);
//		}
		mCustomListAdapter = new CustomListAdapter(mContext, customListDialogItems);
		mListView.setAdapter(mCustomListAdapter);
		return this;
	}

	protected void setListener() {
		
		ViewGroup customListLayout = (ViewGroup)findViewById(R.id.customListLayout);
		customListLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				cancel();
			}
		});
		
		TextView cancelTextView = (TextView)findViewById(R.id.cancelTextView);
		cancelTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				cancel();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(null != mOnItemClickListener){
					mOnItemClickListener.onItemClick(parent, view, position, id);
					cancel();
				}
			}
		});
	}
	
	/**
	 * 点击列表每一项的监听， 用v.getId()来获取当前的位置
	 * 
	 * @param onItemClickListener
	 * @return
	 */
	public CustomListDialog setOnItemClickListener(
			OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
		return this;
	}

	/**
	 * 设置标题
	 * 
	 * @param titleString
	 *            null使用默认
	 * @return
	 */
	public CustomListDialog setTitle(String titleString) {
		TextView titleTextView = (TextView)findViewById(R.id.titleTextView);
		titleTextView.setText(titleString);
		return this;
	}
	
}
