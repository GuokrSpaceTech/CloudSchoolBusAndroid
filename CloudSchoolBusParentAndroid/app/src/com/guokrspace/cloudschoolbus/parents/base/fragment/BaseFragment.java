package com.guokrspace.cloudschoolbus.parents.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.android.support.debug.DebugLog;
import com.android.support.dialog.CustomWaitDialog;
import com.android.support.dialog.CustomWaitDialog.OnKeyCancel;
import com.android.support.utils.DateUtils;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.entity.ActivityBody;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceRecord;
import com.guokrspace.cloudschoolbus.parents.entity.Food;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.guokrspace.cloudschoolbus.parents.entity.NoticeBody;
import com.guokrspace.cloudschoolbus.parents.entity.Schedule;
import com.guokrspace.cloudschoolbus.parents.entity.StudentReport;
import com.guokrspace.cloudschoolbus.parents.entity.Timeline;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.event.SidExpireEvent;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.StreamingChannelsFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.adapter.ImageAdapter;
import com.guokrspace.cloudschoolbus.parents.module.explore.adapter.TagRecycleViewAdapter;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.guokrspace.cloudschoolbus.parents.widget.ActivityCard;
import com.guokrspace.cloudschoolbus.parents.widget.AttendanceRecordCard;
import com.guokrspace.cloudschoolbus.parents.widget.FoodNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.NoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.PictureCard;
import com.guokrspace.cloudschoolbus.parents.widget.ReportListCard;
import com.guokrspace.cloudschoolbus.parents.widget.ScheduleNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.StreamingNoticeCard;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.dao.query.QueryBuilder;

public class BaseFragment extends Fragment {
	
	protected Context mParentContext;
	protected Fragment mFragment;
	
	protected CloudSchoolBusParentsApplication mApplication;
	private   CustomWaitDialog mCustomWaitDialog;
	public    List<MessageEntity> mMesageEntities = new ArrayList<>();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mParentContext = activity;
		mFragment = this;
		mApplication = (CloudSchoolBusParentsApplication) mParentContext
				.getApplicationContext();
		BusProvider.getInstance().register(this);
		DebugLog.setTag(mFragment.getClass().getName());
	}


	@Override
	public void onDetach() {
		super.onDetach();
		BusProvider.getInstance().unregister(this);
	}

	protected void setViewData(View view) {
		
	}
	
	protected void setListener() {

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
        super.onResume();
	}
	
	@Override
	public void onPause() {
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

	public void GetMessagesFromCache()
	{
        if(Version.PARENT) {
            int current = mApplication.mConfig.getCurrentChild();
            String currentstudentid = null;
            if(mApplication.mStudents.size()>(current)) {
                currentstudentid = mApplication.mStudents.get(current).getStudentid();
            }

            if(currentstudentid!=null) {
                mMesageEntities = mApplication.mDaoSession.getMessageEntityDao().queryBuilder()
                        .orderDesc(MessageEntityDao.Properties.Messageid)
                        .where(MessageEntityDao.Properties.Studentid.eq(currentstudentid))
                        .list();
            }
        } else {
            mMesageEntities = mApplication.mDaoSession.getMessageEntityDao().queryBuilder()
                    .orderDesc(MessageEntityDao.Properties.Messageid)
                    .list();
        }
	}

	//Get all articles from newest in Cache to newest in Server
	public void GetNewMessagesFromServer(String messageid, android.os.Handler handler) {
		GetMessagesFromServer(messageid, OldNewFlag.NEW_FLAG, handler);
	}

	//Get the older 20 articles from server then update the cache
	public void GetOldMessagesFromServer(String messageid, android.os.Handler handler) {
		GetMessagesFromServer(messageid, OldNewFlag.OLD_FLAG, handler);
	}

	//Get Oldest 50 Articles from server, only used when there is no cache
	public void GetLastestMessagesFromServer(android.os.Handler handler) {
		GetMessagesFromServer("0", OldNewFlag.NEW_FLAG, handler);
	}

	void GetMessagesFromServer(final String messageid, final OldNewFlag flag, final android.os.Handler handler) {
		if (!mApplication.networkStatusEvent.isNetworkConnected()) {
			handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
			return;
		}
//		showWaitDialog("", null);

		HashMap<String, String> params = new HashMap<String, String>();
		if(messageid!=null && flag.equals(OldNewFlag.NEW_FLAG))
		    params.put("newid", messageid);

		if(messageid!=null && flag.equals(OldNewFlag.OLD_FLAG))
		    params.put("oldid", messageid);

		CloudSchoolBusRestClient.get(ProtocolDef.METHOD_timeline, params, new JsonHttpResponseHandler() {
            MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
            SenderEntityDao senderEntityDao = mApplication.mDaoSession.getSenderEntityDao();
            TagEntityDao tagEntityDao = mApplication.mDaoSession.getTagEntityDao();

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                String retCode = "";
                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }
                if (retCode.equals("-1")) //Session Expire
                {
                    BusProvider.getInstance().post(new SidExpireEvent(mApplication.mConfig.getSid()));
                }
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
                    Message msg = handler.obtainMessage();
                    msg.what = HandlerConstant.MSG_SERVER_ERROR;
                    msg.obj = response;
                    handler.sendMessage(msg);
                    return;
                }

                List<Timeline> timelines = FastJsonTools.getListObject(response.toString(), Timeline.class);
                for (int i = 0; i < timelines.size(); i++) {
                    Timeline message = timelines.get(i);
                    if (!cardType(message.getApptype()).equals("")) {
                        Timeline.Sender sender = message.getSender();
                        MessageEntity messageEntity = new MessageEntity(message.getMessageid(), message.getTitle(),
                                message.getDescription(), message.getIsconfirm(), message.getSendtime(), message.getApptype(),
                                message.getStudentid(), message.getIsmass(), message.getIsreaded(), message.getBody(), sender.getId());

                        messageEntityDao.insertOrReplace(messageEntity);
                        SenderEntity senderEntity = new SenderEntity(sender.getId(), sender.getRole(), sender.getAvatar(), sender.getClassname(), sender.getName());
                        senderEntityDao.insertOrReplace(senderEntity);
                    }
                }

                //Refresh mMessageEntities
                GetMessagesFromCache();

                handler.sendEmptyMessage(HandlerConstant.MSG_ONREFRESH);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_SERVER_ERROR;
                msg.obj = throwable;
                handler.sendMessage(msg);
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
                if (retCode.equals("1")) {
                    // No New Records are found
                    handler.sendEmptyMessage(HandlerConstant.MSG_NOCHANGE);
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = HandlerConstant.MSG_SERVER_ERROR;
                    msg.obj = throwable;
                    handler.sendMessage(msg);
                }
            }
        });
	}

	public void changeAvatarUser(final String userid, final Object image, final android.os.Handler handler){
		if (!mApplication.networkStatusEvent.isNetworkConnected()) {
			handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
			return;
		}

        RequestParams params = new RequestParams();

        //File path
        if (image instanceof String) {
            if (image != null) {
//			params.put("fbody", ImageUtil.getPicString(imageFilePath, 512));
                File file = new File((String)image);
                try {
                    params.put("fbody", file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (image instanceof Bitmap)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ((Bitmap)image).compress(Bitmap.CompressFormat.JPEG, 80, bos);
            params.put("fbody", new ByteArrayInputStream(bos.toByteArray()));
        }

        String requestMethod = "";
        if(Version.PARENT)
        {
            requestMethod = ProtocolDef.METHOD_changeAvartarStudent;
            params.put("studentid",userid);
        } else {
            requestMethod = ProtocolDef.METHOD_changeAvartar;
        }

		CloudSchoolBusRestClient.post(requestMethod, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_AVATAR_STUDENT_OK;
                Bundle bundle = new Bundle();
                try {
                    String remoteUrl = response.get("filepath").toString();
                    //Trim the . in the end
                    remoteUrl = remoteUrl.substring(0,remoteUrl.lastIndexOf('.'));
                    bundle.putString("filepath", remoteUrl);
                    bundle.putString("userid",userid);
                    bundle.putString("cache",(String)image);
                    msg.setData(bundle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_AVATAR_STUDENT_OK;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_AVATAR_STUDENT_OK;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.MSG_AVATAR_STUDENT_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.MSG_AVATAR_STUDENT_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HandlerConstant.MSG_AVATAR_STUDENT_FAIL);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void UserConfirm(final String messageid, final Button button, final android.os.Handler handler) {
		if (!mApplication.networkStatusEvent.isNetworkConnected()) {
			handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
			return;
		}

		showWaitDialog("", null);

        HashMap<String, String> params = new HashMap<String, String>();
		if(messageid!=null) params.put("messageid", messageid);

		CloudSchoolBusRestClient.get(ProtocolDef.METHOD_noticeconfirm, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                button.setTag(messageid);
                msg.obj = button;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                button.setTag(messageid);
                msg.obj = button;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                button.setTag(messageid);
                msg.obj = button;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_SERVER_ERROR;
                msg.obj = throwable;
                handler.sendMessage(msg);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                handler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }


    public void animation(View v) {
		v.clearAnimation();
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(300);
		v.setAnimation(animation);
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

	public String cardType(String type)
	{
		String cardtype = "";

		if(type.equals("Article"))
			cardtype = getResources().getString(R.string.picturetype);
		if(type.equals("Notice"))
			cardtype = getResources().getString(R.string.noticetype);
		else if(type.equals("Punch"))
			cardtype = getResources().getString(R.string.attendancetype);
		else if(type.equals("OpenClass"))
			cardtype = getResources().getString(R.string.openclass);
		else if(type.equals("Report"))
			cardtype = getResources().getString(R.string.report);
		else if(type.equals("Food"))
			cardtype = getResources().getString(R.string.food);
		else if(type.equals("Schedule"))
			cardtype = getResources().getString(R.string.schedule);
        else if(type.equals("Active"))
            cardtype = getResources().getString(R.string.activity);
		return cardtype;
	}

	public PictureCard buildArticleCard(MessageEntity message) {
		PictureCard card = new PictureCard(mParentContext);
		String teacherAvatarString = message.getSenderEntity().getAvatar();
		card.setTeacherAvatarUrl(teacherAvatarString);
		card.setTeacherName(message.getSenderEntity().getName());
		card.setKindergarten(mApplication.mSchools.get(0).getName());
        card.setCardType(cardType(message.getApptype()));
		card.setSentTime(message.getSendtime());
		card.setTitle(message.getTitle());
        card.setDescription(message.getDescription());
		List<String> pictureUrls = new ArrayList<>();
		try {
			JSONObject jsonObject = new JSONObject(message.getBody());
			pictureUrls = FastJsonTools.getListObject(jsonObject.get("PList").toString(), String.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        if(pictureUrls!=null)
		card.setImageAdapter(new ImageAdapter(mParentContext, pictureUrls));
		final List<TagEntity> tagEntities = message.getTagEntityList();
		TagRecycleViewAdapter adapter = new TagRecycleViewAdapter(tagEntities);
		card.setTagAdapter(adapter);

        CommonRecyclerItemClickListener tagClickListener = new CommonRecyclerItemClickListener(mParentContext, new CommonRecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				animation(view);
				SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager())
						.setMessage(tagEntities.get(position).getTagnamedesc())
						.setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});
		card.setmOnItemSelectedListener(tagClickListener);

        View.OnClickListener shareButtonClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showShare();
			}
		};
		card.setmShareButtonClickListener(shareButtonClickListener);

        return card;
	}

	public NoticeCard BuildNoticeCard(final MessageEntity messageEntity, final Handler handler)
	{
		NoticeCard noticeCard = new NoticeCard(mParentContext);
		String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
		noticeCard.setTeacherAvatarUrl(teacherAvatarString);
		noticeCard.setTeacherName(messageEntity.getSenderEntity().getName());
		noticeCard.setClassName(messageEntity.getSenderEntity().getClassname());
		noticeCard.setCardType(cardType(messageEntity.getApptype()));
		noticeCard.setSentTime(messageEntity.getSendtime());
		noticeCard.setIsNeedConfirm(messageEntity.getIsconfirm());
		noticeCard.setTitle(messageEntity.getTitle());
		noticeCard.setDescription(messageEntity.getDescription());
		NoticeBody noticeBody = FastJsonTools.getObject(messageEntity.getBody(), NoticeBody.class);
		if (noticeBody != null) noticeCard.setDrawable(noticeBody.getPList().get(0));
		noticeCard.setmConfirmButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UserConfirm(messageEntity.getMessageid(), (Button) view, handler);
			}
		});
		return noticeCard;
	}

	public ActivityCard BuildActivityCard(final MessageEntity messageEntity, final Handler handler)
	{
		ActivityCard theCard = new ActivityCard(mParentContext);
		String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
		theCard.setTeacherAvatarUrl(teacherAvatarString);
		theCard.setTeacherName(messageEntity.getSenderEntity().getName());
		theCard.setClassName(messageEntity.getSenderEntity().getClassname());
		theCard.setCardType(cardType(messageEntity.getApptype()));
		theCard.setSentTime(messageEntity.getSendtime());
		theCard.setIsNeedConfirm(messageEntity.getIsconfirm());
		theCard.setTitle(messageEntity.getTitle());
		theCard.setDescription(messageEntity.getDescription());
        ActivityBody messageBody = FastJsonTools.getObject(messageEntity.getBody(), ActivityBody.class);
		if (messageBody != null) theCard.setDrawable(messageBody.getPList().get(0));
		theCard.setmConfirmButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserConfirm(messageEntity.getMessageid(), (Button) view, handler);
            }
        });
        return theCard;
	}

	public AttendanceRecordCard BuildAttendanceCard(MessageEntity messageEntity)
	{
		AttendanceRecordCard attendanceRecordCard = new AttendanceRecordCard(mParentContext);
		String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
		attendanceRecordCard.setTeacherAvatarUrl(teacherAvatarString);
        attendanceRecordCard.setTeacherName(messageEntity.getSenderEntity().getName());
        attendanceRecordCard.setClassName(messageEntity.getSenderEntity().getClassname());
        attendanceRecordCard.setCardType(cardType(messageEntity.getApptype()));
        attendanceRecordCard.setSentTime(messageEntity.getSendtime());
        String messageBody = messageEntity.getBody();
		AttendanceRecord attendanceRecord = FastJsonTools.getObject(messageBody, AttendanceRecord.class);
        attendanceRecordCard.setRecordTime(attendanceRecord.getPunchtime().toString());
		attendanceRecordCard.setDrawable(attendanceRecord.getPicture());
		attendanceRecordCard.setDescription(attendanceRecord.getPunchtime());
        return attendanceRecordCard;
	}

	public StreamingNoticeCard BuildStreamingNoticeCard(MessageEntity messageEntity)
	{
		StreamingNoticeCard streamingNoticeCard = new StreamingNoticeCard(mParentContext);
		streamingNoticeCard.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
		streamingNoticeCard.setKindergartenName(messageEntity.getSenderEntity().getName());
		streamingNoticeCard.setClassName(messageEntity.getSenderEntity().getClassname());
        streamingNoticeCard.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(), mParentContext));
        streamingNoticeCard.setCardType(cardType(messageEntity.getApptype()));
        streamingNoticeCard.setContext(mParentContext);
        streamingNoticeCard.setDescription(messageEntity.getDescription());
        String messageBody = messageEntity.getBody();
		final Ipcparam ipcpara = FastJsonTools.getObject(messageBody, Ipcparam.class);
		streamingNoticeCard.setClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity mainActivity = (MainActivity)mParentContext;
				mainActivity.setActionBarTitle(getResources().getString(R.string.openclass), getResources().getString(R.string.module_explore));
				StreamingChannelsFragment fragment = StreamingChannelsFragment.newInstance(ipcpara);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.article_module_layout, fragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});

		return streamingNoticeCard;
	}

	public ReportListCard BuildReportListCard(final MessageEntity messageEntity)
	{
		ReportListCard reportListCard = new ReportListCard(mParentContext);
		String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
		reportListCard.setTeacherAvatarUrl(teacherAvatarString);
        reportListCard.setTeacherName(messageEntity.getSenderEntity().getName());
        reportListCard.setClassName(messageEntity.getSenderEntity().getClassname());
        reportListCard.setCardType(cardType(messageEntity.getApptype()));
        reportListCard.setSentTime(messageEntity.getSendtime());
        reportListCard.setReporttype(messageEntity.getTitle());
        String messageBody = messageEntity.getBody();
		final StudentReport studentReport = FastJsonTools.getObject(messageBody, StudentReport.class);
        reportListCard.setReporttype(studentReport.getReportType());
		reportListCard.setClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				MainActivity mainActivity = (MainActivity)mParentContext;
//				mainActivity.setActionBarTitle(getResources().getString(R.string.report), getResources().getString(R.string.module_explore));
				WebviewFragment theFragment = WebviewFragment.newInstance(studentReport.getReportUrl(), getResources().getString(R.string.report),"");
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.article_module_layout, theFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		return reportListCard;
	}

	public FoodNoticeCard BuildFoodNoticeCard(final MessageEntity messageEntity)
	{
		FoodNoticeCard card = new FoodNoticeCard(mParentContext);
		card.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
		card.setKindergartenName(messageEntity.getSenderEntity().getName());
		card.setClassName(messageEntity.getSenderEntity().getClassname());
		card.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(), mParentContext));
		card.setCardType(cardType(messageEntity.getApptype()));
		card.setContext(mParentContext);
		card.setDescription(messageEntity.getDescription());
		Food food = FastJsonTools.getObject(messageEntity.getBody(), Food.class);
		final String foodUrl = food.getUrl();
		card.setClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				WebviewFragment fragment = WebviewFragment.newInstance(foodUrl, getResources().getString(R.string.food),"");
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.article_module_layout, fragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		return card;
	}

	public ScheduleNoticeCard BuildScheduleNoticeCard(final MessageEntity messageEntity)
	{
		ScheduleNoticeCard card = new ScheduleNoticeCard(mParentContext);
		card.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
		card.setKindergartenName(messageEntity.getSenderEntity().getName());
		card.setClassName(messageEntity.getSenderEntity().getClassname());
		card.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(), mParentContext));
		card.setCardType(cardType(messageEntity.getApptype()));
		card.setContext(mParentContext);
		card.setDescription(messageEntity.getDescription());
		Schedule schedule = FastJsonTools.getObject(messageEntity.getBody(), Schedule.class);
        final String scheduleUrl = schedule.getUrl();
		card.setClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity mainActivity = (MainActivity)mParentContext;
//				mainActivity.setActionBarTitle(getResources().getString(R.string.schedule), getResources().getString(R.string.module_explore));
				WebviewFragment fragment = WebviewFragment.newInstance(scheduleUrl, getResources().getString(R.string.schedule),"");
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.article_module_layout, fragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		return card;
	}

	//Get filtered messages from cache
	public ArrayList<MessageEntity> GetMessageFromCache(String messageType) {
		MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
		QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
		return (ArrayList<MessageEntity>)queryBuilder.where(MessageEntityDao.Properties.Apptype.eq(messageType)).list();
	}

    //Get all messages from cache
	private ArrayList<MessageEntity> GetMessageFromCache() {
		MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
		QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
		return (ArrayList<MessageEntity>)queryBuilder.list();
	}

	public enum OldNewFlag {
		OLD_FLAG, NEW_FLAG
	}

	public void switchChildren(int currentChild)
	{
		ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
		ConfigEntity oldConfigEntity = configEntityDao.queryBuilder().limit(1).list().get(0);
		oldConfigEntity.setCurrentChild(currentChild);
		ConfigEntity newConfigEntity = oldConfigEntity;
		configEntityDao.update(newConfigEntity);
		mApplication.mConfig = newConfigEntity;

		BusProvider.getInstance().post(new InfoSwitchedEvent(currentChild));
 	}

    public TeacherEntityT getMyself()
    {
        TeacherEntityT entity = null;
        for(TeacherEntityT teacher:mApplication.mTeachersT)
        {
            if(teacher.getTeacherid().equals(mApplication.mConfig.getUserid())) {
                entity = teacher;
                break;
            }
        }
        return entity;
    }

    public ClassEntityT findCurrentClass(int current)
    {
        ClassEntityT retEntity=null;

        String classid = mApplication.mTeacherClassDutys.get(current).getClassid();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            if(theClass.getClassid().equals(classid))
            {
                retEntity = theClass; break;
            }
        }

        return retEntity;
    }

    public ClassEntityT findCurrentClass()
    {
        ClassEntityT retEntity=null;

        int current = mApplication.mConfig.getCurrentChild();

        String classid = mApplication.mTeacherClassDutys.get(current).getClassid();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            if(theClass.getClassid().equals(classid))
            {
                retEntity = theClass; break;
            }
        }

        return retEntity;
    }

    public ArrayList<ClassEntityT> findMyClass()
    {
        ArrayList<ClassEntityT> retEntity= new ArrayList<ClassEntityT>();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            for(TeacherDutyClassRelationEntity relation:mApplication.mTeacherClassDutys) {
                if (theClass.getClassid().equals(relation.getClassid()) && mApplication.mConfig.getUserid().equals(relation.getTeacherid())) {
                    retEntity.add(theClass);
                }
            }
        }

        return retEntity;
    }

    public ArrayList<ParentEntityT> findParentsinClass(String classid)
    {
        ArrayList<ParentEntityT> retParents = new ArrayList<>();
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            for(StudentClassRelationEntity relation:mApplication.mStudentClasses) {
                if(relation.getClassid().equals(classid)) {
                    if (student.getStudentid().equals(relation.getStudentid()))
                    {
                        //Found the student, then find the parents
                        retStudents.add(student);
                        break;
                    }
                }
            }
        }

        for(StudentEntityT student:retStudents){
            for(StudentParentRelationEntity relation:mApplication.mStudentParents)
            {
                if(student.getStudentid().equals(relation.getStudentid()))
                {
                    //find the parentid, get the entity
                    for(ParentEntityT parent:mApplication.mParents)
                    {
                        if(parent.getParentid().equals(relation.getParentid()))
                        {
                            retParents.add(parent);
                        }
                    }
                }
            }
        }

        return retParents;
    }


    public ArrayList<StudentEntityT> findStudentsinClass(String classid)
    {
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            for(StudentClassRelationEntity relation:mApplication.mStudentClasses) {
                if(relation.getClassid().equals(classid)) {
                    if (student.getStudentid().equals(relation.getStudentid())) {
                        //Found the student, then find the parents
                        retStudents.add(student);
                        break;
                    }
                }
            }
        }

        return retStudents;
    }


    public ArrayList<TeacherEntityT> findTeachersinClass(String classid)
    {
        ArrayList<TeacherEntityT> retTeachers = new ArrayList<>();
        for(TeacherEntityT teacher:mApplication.mTeachersT) {
            for (TeacherDutyClassRelationEntity relation : mApplication.mTeacherClassDutys) {
                if (relation.getClassid().equals(classid)) {
                    if (teacher.getTeacherid().equals(relation.getTeacherid())) {
                        //Found the student, then find the parents
                        retTeachers.add(teacher);
                        break;
                    }
                }
            }
        }

        return retTeachers;
    }

    public ArrayList<StudentEntityT> findStudentsOfParents(ParentEntityT parent)
    {
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        //Loop over students list
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            //Loop Over the relationship table
            for(StudentParentRelationEntity relation:mApplication.mStudentParents)
            {
                //If found the student in the relation table and its parent is eqaul to the designated parent
                if(student.getStudentid().equals(relation.getStudentid()) && parent.getParentid().equals(relation.getParentid()))
                {
                    retStudents.add(student);
                }
            }
        }
        return retStudents;
    }
}
