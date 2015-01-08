package com.Manga.Activity.myChildren.DoctorConsult;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.alipay.Keys;
import com.Manga.Activity.alipay.Result;
import com.Manga.Activity.alipay.Rsa;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.myChildren.SwitchChildren.ManageChildrenActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.alipay.android.app.sdk.AliPay;
import com.cytx.ConsultActivity;
import com.umeng.analytics.MobclickAgent;

public class DoctorActivity extends BaseActivity {
	private ProgressDialog mProgress = null;
	private static int count = 1;
	private TextView tv_num, tv_account;
	public static final String TAG = "alipay-sdk";
	private static final int RQF_PAY = 1;

	private static final int RQF_LOGIN = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_server);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_account = (TextView) findViewById(R.id.tv_account);
		ActivityUtil.doctorActivity = this;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);

			switch (msg.what) {
			case RQF_PAY:
				if (((String) msg.obj).contains("resultStatus={9000}")) {
					Toast.makeText(DoctorActivity.this, R.string.pay_success, Toast.LENGTH_SHORT).show();
					ActivityUtil.share.getStudentInfo();
					// Student_Info.chunyuisopen = "1";
					// Intent intent = new Intent(ActivityUtil.doctorActivity, ConsultActivity.class);
					// intent.putExtra("user_id", Student_Info.username);
					// startActivity(intent);
					ActivityUtil.close(ActivityUtil.doctorActivity);
				} else {
					Toast.makeText(DoctorActivity.this, result.getResult(), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		};
	};

	public void childrenmanage(View view) {
		Intent intent = new Intent(this, ManageChildrenActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void numAdd(View v) {
		count++;
		tv_account.setText(count * 8 + "元");
		tv_num.setText(count + "");
	}

	public void numMinus(View v) {
		if (count > 1) {
			count--;
		}
		tv_account.setText(count * 8 + "元");

		tv_num.setText(count + "");
	}

	public void pay_app(View v) {
		boolean isExist = true;
//		PackageManager manager = DoctorActivity.this.getPackageManager();
//		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
//		for (int i = 0; i < pkgList.size(); i++) {
//			PackageInfo pI = pkgList.get(i);
//			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app")) {
//				isExist = true;
//				break;
//			}
//		}
		if (isExist) {
			MyAsyncTask postSubmitReportTask = new MyAsyncTask(DoctorActivity.this, false) {
				com.Manga.Activity.httputils.Result result;

				@Override
				protected void onPostExecute(Void vod) {

					if (result == null) {
						Toast.makeText(DoctorActivity.this, "请求超时", Toast.LENGTH_SHORT).show();
					} else if ("1".equals(result.getCode())) {
						OrderBean orderBean = new OrderBean();
						try {
							JSONObject allMessage = new JSONObject(result.getContent());
							orderBean.setTitle(allMessage.getString("title"));
							orderBean.setPrice(allMessage.getString("price"));
							orderBean.setDescription(allMessage.getString("description"));
							orderBean.setNotifyURL(allMessage.getString("notifyURL"));
							orderBean.setOrderID(allMessage.getString("oriderid"));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// 获取是否存在支付宝客户端 如果不存在 跳转webview页面
						// 检测安全支付服务是否安装
						// MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(DoctorActivity.this);
						// boolean isMobile_spExist = mspHelper.detectMobile_sp();
						// if (!isMobile_spExist)
						// return;
						// 根据订单信息开始进行支付
						try {
							// 组装好参数
							String orderInfo = getNewOrderInfo(orderBean);
							// 这里根据签名方式对订单信息进行签名
							String sign = Rsa.sign(orderInfo, Keys.PRIVATE);
							// 对签名进行编码
							sign = URLEncoder.encode(sign);
							// 组装好参数
							orderInfo += "&sign=\"" + sign + "\"&" + getSignType();
							// start the pay.
							// 调用pay方法进行支付
							// MobileSecurePayer msp = new MobileSecurePayer();
							// boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, DoctorActivity.this);

							final String info = orderInfo;
							new Thread() {
								public void run() {
									AliPay alipay = new AliPay(DoctorActivity.this, mHandler);

									// 设置为沙箱模式，不设置默认为线上环境
									// alipay.setSandBox(true);

									String result = alipay.pay(info);

									Log.i(TAG, "result = " + result);
									Message msg = new Message();
									msg.what = RQF_PAY;
									msg.obj = result;
									mHandler.sendMessage(msg);
								}
							}.start();
						} catch (Exception ex) {
							Toast.makeText(DoctorActivity.this, "Failure calling remote service", Toast.LENGTH_SHORT)
									.show();
						}

					}

					super.onPostExecute(vod);
				}

				@Override
				protected Void doInBackground(Void... params) {
					if (HttpUtil.isNetworkConnected(DoctorActivity.this)) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("count", String.valueOf(count));
						result = HttpUtil.httpGet(DoctorActivity.this, new Params("order", map));
					}
					return super.doInBackground(params);
				}
			};
			postSubmitReportTask.execute();
		} else {
			Toast.makeText(ActivityUtil.doctorActivity, "您好，您未安装支付宝客户端，请选择其他方式支付！", 3000).show();
		}
	}

	public void init() {
	}

	public void close(View v) {
		ActivityUtil.close(this);
	}

	// public void baseinfo(View view) {
	// Intent intent = new Intent(this, BaseInfoNewActivity.class);
	// ActivityUtil.main.comeIn(intent);
	// }

	/**
	 * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
	 */
	public static class AlixOnCancelListener implements DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	//
	// close the progress bar
	// 关闭进度框
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ActivityUtil.main != null) {
				ActivityUtil.main.move();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private String getNewOrderInfo(OrderBean orderBean) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(orderBean.getOrderID());
		sb.append("\"&subject=\"");
		sb.append(orderBean.getTitle());
		sb.append("\"&body=\"");
		sb.append(orderBean.getDescription());
		sb.append("\"&total_fee=\"");
		// sb.append("0.01");
		sb.append(orderBean.getPrice());
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(orderBean.getNotifyURL());
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"30m");
		sb.append("\"");

		return new String(sb);
	}

	String getSignType() {
		return "sign_type=\"RSA\"";
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		try {
			mProgress.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
