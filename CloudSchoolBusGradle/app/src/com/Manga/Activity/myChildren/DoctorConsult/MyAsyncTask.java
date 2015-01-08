package com.Manga.Activity.myChildren.DoctorConsult;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

public abstract class MyAsyncTask extends AsyncTask<Void, Void, Void> {
	// private static final String TAG = "ProgressAsyncTask";
	protected ProgressDialog mDialog;
	/** 对话框标�? */
	private Context ctx;
	private String mMessage;
	protected boolean isShowProgressDialog;

	public MyAsyncTask(Context ctx, boolean isShowProgressDialog) {
		this.ctx = ctx;
		this.isShowProgressDialog = isShowProgressDialog;
		this.mMessage = "正在加载数据，请稍后…";
	}

	public MyAsyncTask(Context ctx, boolean isShowProgressDialog, String message) {
		this.ctx = ctx;
		this.isShowProgressDialog = isShowProgressDialog;
		this.mMessage = message;
	}

	/**
	 * 显示正在处理
	 */
	@Override
	protected void onPreExecute() {
		mDialog = getDialog();
		if (!mDialog.isShowing() && !isShowProgressDialog) {
			mDialog.show();
		}
	}

	// TODO
	// 我的问题是当前的Activity起一个异步线�?AsyncTask)发起网络连接去验证注册信息是否有效，信息不对的话在当前UI线程（当前Activity）显示对话框，如此用户恰好在那个异步线程完成之前，按了back键，那么当前Activity就会被销毁，异步线程完成了，但是想弹对话框时找不到调用它的Activity了，�?��就崩溃了�?
	@Override
	protected void onPostExecute(Void result) {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		mDialog.dismiss();
		return null;
	}

	private ProgressDialog getDialog() {
		if (mDialog == null) {
			mDialog = new ProgressDialog(ctx);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setIndeterminate(true);// 设置进度条是否不明确
			mDialog.setMessage(mMessage);
			mDialog.setCancelable(false);
		}
		return mDialog;

	}
}
