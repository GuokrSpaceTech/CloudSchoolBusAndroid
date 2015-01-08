package com.cytx.utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.cytx.constants.Constants;
import com.cytx.constants.HandlerConstants;

public class UtilUI {
	/**
	 * 自定义的错误提示对话框
	 */
	public static void showErrorMsgDialog(String message, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	public static void showErrorMsgDialog(int messageId, Context context) {
		String message = context.getResources().getString(messageId);
		showErrorMsgDialog(message, context);
	}
	
	/**
	 * Toast错误提示
	 */
	public static void showToastError(String message, Context context){
		if(Constants.HOST_ERROR.equals(message)){
			Toast.makeText(context, context.getResources().getString(R.string.connect_failed), Toast.LENGTH_LONG).show();;
		
		}else if(Constants.SOCKET_TIME_OUT.equals(message)){
			Toast.makeText(context, context.getResources().getString(R.string.connect_timeout), Toast.LENGTH_LONG).show();;
			
		}else{
			Toast.makeText(context, context.getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();;
			
		}
	}
	

	/**
	 * 默认的加载框
	 * 
	 * @param context
	 * @param asyncTask
	 * @return
	 */
	public static ProgressDialog getProgressDialog(Context context) {
		ProgressDialog pg = new ProgressDialog(context);
		pg.setTitle(context.getResources().getString(R.string.prompt));
		pg.setMessage(context.getResources().getString(R.string.loading));
		// 点击对话框以外的区域，Dialog不消失
		pg.setCanceledOnTouchOutside(false);
		return pg;
	}

	/**
	 * 传入提示信息的ProgressDialog
	 * 
	 * @param context
	 * @param asyncTask
	 * @param message
	 * @return
	 */
	public static ProgressDialog getProgressMessageDialog(Context context,String message) {
		ProgressDialog pg = new ProgressDialog(context);
		pg.setTitle(context.getResources().getString(R.string.prompt));
		pg.setMessage(message);
		// 点击对话框以外的区域，Dialog不消失
		pg.setCanceledOnTouchOutside(false);
		return pg;
	}

	/**
	 * 问题提交成功
	 * @param mActivity
	 */
	public static void showCreatedSuccessDialog(final Context context, final Handler handler, String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setMessage(message);
		dialog.setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				handler.sendEmptyMessage(HandlerConstants.CREATED_SUCCESS);
				dialog.cancel();
			}
		});

		AlertDialog ad = dialog.create();
		ad.setCancelable(false);
		ad.setCanceledOnTouchOutside(false);
		ad.show();
	}

	
}
