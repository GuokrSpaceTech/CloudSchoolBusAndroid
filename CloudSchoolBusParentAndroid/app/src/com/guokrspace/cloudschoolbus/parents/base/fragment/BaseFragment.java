package com.guokrspace.cloudschoolbus.parents.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.android.support.debug.DebugLog;
import com.android.support.dialog.CustomWaitDialog;
import com.android.support.dialog.CustomWaitDialog.OnKeyCancel;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageBodyEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageBodyEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.entity.Sender;
import com.guokrspace.cloudschoolbus.parents.entity.Tag;
import com.guokrspace.cloudschoolbus.parents.entity.Timeline;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class BaseFragment extends Fragment {
	
	protected Context mParentContext;
	protected Fragment mFragment;
	
	protected CloudSchoolBusParentsApplication mApplication;
	public    NetworkStatusEvent networkStatusEvent;
	private   CustomWaitDialog mCustomWaitDialog;
	private List<MessageEntity> mMesageEntities = new ArrayList<>();

	final private static int MSG_ONREFRESH = 1;
	final private static int MSG_ONLOADMORE = 2;
	final private static int MSG_ONCACHE = 3;
	final private static int MSG_NOCHANGE = 4;
	private static final int MSG_NO_NETOWRK = 5;
	private static final int MSG_SERVER_ERROR = 6;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mParentContext = activity;
		mFragment = this;
		mApplication = (CloudSchoolBusParentsApplication) mParentContext
				.getApplicationContext();
		networkStatusEvent = new NetworkStatusEvent(false,false,false);
		ConnectivityManager manager = (ConnectivityManager) mParentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo        mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo        activeInfo = manager.getActiveNetworkInfo();
		if(activeInfo!=null)
		{
			networkStatusEvent.setIsNetworkConnected(true);
			if(wifiInfo.isConnected())
				networkStatusEvent.setIsWifiConnected(true);
			if(mobileInfo.isConnected())
				networkStatusEvent.setIsMobileNetworkConnected(true);
		}
		DebugLog.setTag(mFragment.getClass().getName());
	}
	

	protected void setViewData(View view) {
		
	}
	
	protected void setListener(View view) {

	}

	protected void setTitleNavBar(View view) {

	}
	
	/**
	 * 显示等待对话框
	 * 
	 * @param messageString
	 * @param
	 */
	protected void showWaitDialog(String messageString,
			final OnKeyCancel onKeyCancel) {
		if (null == mCustomWaitDialog) {
			mCustomWaitDialog = new CustomWaitDialog(mParentContext,
					com.android.support.R.style.CustomWaitDialog);
			mCustomWaitDialog.setCancelable(true);
			mCustomWaitDialog.setCanceledOnTouchOutside(false);
			mCustomWaitDialog.setMessage(messageString);
			mCustomWaitDialog.setOnKeyCancelListener(new OnKeyCancel() {

				@Override
				public void onKeyCancelListener() {
					if (null != onKeyCancel) {
						onKeyCancel.onKeyCancelListener();
					}
					if (null != mCustomWaitDialog) {
						mCustomWaitDialog.cancel();
						mCustomWaitDialog = null;
					}
				}
			});
			mCustomWaitDialog.show();
		}
	}

	protected void hideWaitDialog() {
		if (null != mCustomWaitDialog) {
			mCustomWaitDialog.cancel();
			mCustomWaitDialog = null;
		}
	}
	
	@Override
	public void onResume() {
        BusProvider.getInstance().register(this);
        super.onResume();
	}
	
	@Override
	public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null != savedInstanceState){
			DebugLog.logI("Activity onRestoreInstanceState");
	//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onRestoreInstanceState");
			mApplication.mSchools = (List<SchoolEntity>)savedInstanceState.getSerializable("mSchools");
			mApplication.mClasses = (List<ClassEntity>)savedInstanceState.getSerializable("mClasses");
			mApplication.mTeachers = (List<TeacherEntity>)savedInstanceState.getSerializable("mTeachers");
			mApplication.mStudents = (List<StudentEntity>)savedInstanceState.getSerializable("mStudents");
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		DebugLog.logI("Fragment onSaveInstanceState");
//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onSaveInstanceState");
		outState.putSerializable("mConfig", mApplication.mConfig);
		outState.putSerializable("mSchools", (ArrayList<SchoolEntity>)mApplication.mSchools);
		outState.putSerializable("mClasses", (ArrayList<ClassEntity>)mApplication.mClasses);
		outState.putSerializable("mTeachers",(ArrayList<TeacherEntity>)mApplication.mTeachers);
		outState.putSerializable("mStudents",(ArrayList<StudentEntity>)mApplication.mStudents);
		
		super.onSaveInstanceState(outState);
	}

	//Get all articles from newest in Cache to newest in Server
	public void GetNewMessagesFromServer(String endtime, android.os.Handler handler) {
		GetMessagesFromServer("0", endtime, handler);
	}

	//Get the older 20 articles from server then update the cache
	public void GetOldMessagesFromServer(String starttime, android.os.Handler handler) {
		GetMessagesFromServer(starttime, "0", handler);
	}

	//Get Lastest 20 Articles from server, only used when there is no cache
	public void GetLastestMessagesFromServer(android.os.Handler handler) {
		GetMessagesFromServer("0", "0", handler);
	}

	void GetMessagesFromServer(final String starttime, final String endtime, final android.os.Handler handler) {
		if (!networkStatusEvent.isNetworkConnected()) {
			handler.sendEmptyMessage(MSG_NO_NETOWRK);
			return;
		}
		showWaitDialog("", null);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("starttime", starttime);
		params.put("endtime", endtime);

		CloudSchoolBusRestClient.get(ProtocolDef.METHOD_timeline, params, new JsonHttpResponseHandler() {
			MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
			SenderEntityDao senderEntityDao = mApplication.mDaoSession.getSenderEntityDao();
			MessageBodyEntityDao messageBodyEntityDao = mApplication.mDaoSession.getMessageBodyEntityDao();
			TagEntityDao tagEntityDao = mApplication.mDaoSession.getTagEntityDao();

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				String retCode = "";

				for (int i = 0; i < headers.length; i++) {
					Header header = headers[i];
					if ("code".equalsIgnoreCase(header.getName())) {
						retCode = header.getValue();
						break;
					}
				}
				if (!retCode.equals("1")) {
					handler.sendEmptyMessage(MSG_SERVER_ERROR);
					return;
				}

				List<Timeline> timelines = FastJsonTools.getListObject(response.toString(), Timeline.class);
				for (int i = 0; i < timelines.size(); i++) {
					Timeline message = timelines.get(i);
					Sender sender = message.getSender();
					MessageEntity messageEntity = new MessageEntity(message.getId(), message.getTitle(), message.getDescription(), message.getIsconfirm(), message.getSendtime(), message.getApptype(), message.getStudentid(), message.getIsmass(), message.getIsreaded(), sender.getId());
					messageEntityDao.insertOrReplace(messageEntity);

					String avatar = "http://apps.bdimg.com/developer/static/12261449/assets/v3/case_meitu.png";
//					SenderEntity senderEntity = new SenderEntity(sender.getId(), sender.getRole(), sender.getAvatar(), sender.getClassname(), sender.getName());
					SenderEntity senderEntity = new SenderEntity(sender.getId(), sender.getRole(), avatar, sender.getClassname(), sender.getName());

					senderEntityDao.insertOrReplace(senderEntity);

					List<String> messageBodies = message.getBody();
					for (int j = 0; j < messageBodies.size(); j++) {
						MessageBodyEntity messageBodyEntity = new MessageBodyEntity(null, messageBodies.get(j), message.getMessageid());
						messageBodyEntityDao.insertOrReplace(messageBodyEntity);
					}

//					List<Tag> tags = message.getTag();
//					for (int j = 0; j < tags.size(); tags.size()) {
//						Tag tag = tags.get(j);
//						TagEntity tagEntity = new TagEntity(tag.getTagid(), tag.getTagName(), tag.getTagnamedesc(), tag.getTagname_en(), tag.getTagnamedesc_en(), message.getMessageid());
//						tagEntityDao.insertOrReplace(tagEntity);
//					}
				}
				handler.sendEmptyMessage(MSG_ONREFRESH);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				super.onSuccess(statusCode, headers, responseString);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				handler.sendEmptyMessage(MSG_SERVER_ERROR);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				String retCode = "";
				for (int i = 0; i < headers.length; i++) {
					Header header = headers[i];
					if ("code".equalsIgnoreCase(header.getName())) {
						retCode = header.getValue();
						break;
					}
				}
				if (retCode != "-2") {
					// No New Records are found
					handler.sendEmptyMessage(MSG_NOCHANGE);
				}
			}

		});
	}
	public void animation(View v) {
		v.clearAnimation();
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(300);
		v.setAnimation(animation);
	}

	@Subscribe
	public void onNetworkStatusChange(NetworkStatusEvent event)
	{
		networkStatusEvent = event;
	}

	public void showShare() {
		ShareSDK.initSDK(mParentContext);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//		oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
		oks.show(mParentContext);
	}



}
