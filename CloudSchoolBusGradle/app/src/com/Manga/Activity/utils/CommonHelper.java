package com.Manga.Activity.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;


public class CommonHelper {
	private static ProgressDialog mProgress;
	public  static boolean isjustLoction = true;
	
	// show the progress bar.
	public static void showProgress(Context context, CharSequence message)
	{
		mProgress = new ProgressDialog(context);
		mProgress.setMessage(message);
		mProgress.setIndeterminate(false);
		mProgress.setCancelable(false);
		mProgress.show();
	}
	
	public static int doubleStringToInt(String str){
		double latlng = Double.parseDouble(str);
		return  getIntLatLng(latlng);
	}
	
	public static int getIntLatLng(double data){
		int re = (int) (data * 1 * 1000 * 1000);
		return re;
	}

	public static String getUserAgent() {
		return Build.MODEL;
	}
	public static void closeProgress()
    {
    	try
    	{
	    	if( mProgress != null )
	    	{
	    		mProgress.dismiss();
	    		mProgress = null;
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }   
}
