package com.Manga.Activity.httputils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.R;

public class HttpUtil {
	private static final int SHOW = 0;
	private static Context context;

	/**
	 * 访问网络get方法
	 * 
	 * @param context
	 *            上下文
	 * @param param
	 *            参数 包括URL 与访问参数
	 * @return
	 */
	public static Result httpGet(Context context, Params param) {
		StringBuffer sb = new StringBuffer();
		if (param.getMap() == null || param.getMap().isEmpty()) {
			sb.append(param.getUrl());
		} else {
			sb.append(param.getUrl());
			sb.append('/');
			for (Entry<String, String> entry : param.getMap().entrySet()) {
				try {
					sb.append(entry.getKey()).append('/').append(URLEncoder.encode(entry.getValue(), "utf-8"))
							.append('/');
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			sb.delete(sb.length() - 1, sb.length());
		}
		HttpGet httpGet = new HttpGet(sb.toString());
		if (!"".equals(getSid(context)) && null != getSid(context)) {
			httpGet.setHeader("sid", getSid(context));
		}
		httpGet.setHeader("apikey", "mactoprestphone");
		PackageInfo packInfo;
		PackageManager packageManager = context.getPackageManager();
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			httpGet.setHeader("Version", packInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return getData(context, httpGet);
	}

	/**
	 * Post请求
	 * 
	 * @param context
	 * @param param
	 * @return
	 */
	public static Result httpPost(Context context, Params param) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (param.getMap() != null && !param.getMap().isEmpty()) {
			for (Entry<String, String> entry : param.getMap().entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		Log.v("访问", param.toString());
		HttpPost httpPost = new HttpPost(param.getUrl());
		httpPost.setHeader("apikey", "mactoprestphone");
		httpPost.setHeader("sid", getSid(context));
		PackageInfo packInfo;
		PackageManager packageManager = context.getPackageManager();
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			httpPost.setHeader("Version", packInfo.versionName);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getData(context, httpPost);
	}

	/**
	 * 获取sid
	 * 
	 * @param context
	 * @return
	 */
	public static String getSid(Context context) {
		SharedPreferences shp = context.getSharedPreferences("sid", Context.MODE_PRIVATE);
		return shp.getString("sid", "");
	}

	private static HttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 4096);
		HttpClientParams.setRedirecting(httpParams, true);
		return new DefaultHttpClient(httpParams);
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private static Result getData(final Context context, HttpUriRequest request) {
		HttpUtil.context = context;
		HttpResponse httpResponse = null;
		Result backMap = null;
		HttpClient client = getHttpClient();
		try {
			httpResponse = client.execute(request);
			backMap = new Result();
			Header[] heardarr = httpResponse.getAllHeaders();
			for (int i = 0; i < heardarr.length; i++) {
				if ("Code".equals(heardarr[i].getName())) {
					backMap.setCode(heardarr[i].getValue());
				}
			}
			backMap.setContent(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
			Log.v("返回", backMap + "");
			if ("-11131".equals(backMap.getCode())) {
				LooperThread looperThread = new LooperThread();
				looperThread.start();
				return null;
			}
			if ("-1115".equals(backMap.getCode())) {
				ArrearageThread arrearageThread = new ArrearageThread();
				arrearageThread.start();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return backMap;
	}

	public static class LooperThread extends Thread {
		public Handler mHandler;

		public void run() {
			Looper.prepare();

			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case SHOW:
						View view = View.inflate(context, R.layout.more_device_online, null);
						Button cancel = (Button) view.findViewById(R.id.cancel);
						Button relog = (Button) view.findViewById(R.id.relog);
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						final AlertDialog alert = builder.create();
						alert.setView(view, 0, 0, 0, 0);
						cancel.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								alert.dismiss();
							}
						});
						relog.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context, LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								context.startActivity(intent);
								alert.dismiss();
							}
						});
						try {
							alert.show();
						} catch (Exception e) {
							// TODO: handle exception
						}

						break;
					}
				}
			};
			mHandler.sendEmptyMessage(SHOW);
			Looper.loop();
		}
	}

	public static class ArrearageThread extends Thread {
		public Handler mHandler;

		public void run() {
			Looper.prepare();

			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case SHOW:
						View view = View.inflate(context, R.layout.arrearage_notification, null);
						Button relog = (Button) view.findViewById(R.id.arrearage_confirm);
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						final AlertDialog alert = builder.create();
						alert.setView(view, 0, 0, 0, 0);
						relog.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context, LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								context.startActivity(intent);
								alert.dismiss();
							}
						});
						try {
							alert.show();
						} catch (Exception e) {
							// TODO: handle exception
						}

						break;
					}
				}
			};
			mHandler.sendEmptyMessage(SHOW);
			Looper.loop();
		}
	}

}
