package com.guokrspace.cloudschoolbus.parents.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.android.support.dialog.CustomWaitDialog;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.Timeline;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.SidExpireEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
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

/**
 * Created by kai on 10/8/15.
 */
public class ServerInteractions {

    private CustomWaitDialog mCustomWaitDialog;

    private static final ServerInteractions SERVER_INTERACT = new ServerInteractions();
    private static CloudSchoolBusParentsApplication mApplication=null;
    private static Context mContext = null;
    private List<MessageEntity> mMesageEntities = null;

    public boolean isDebug = false;

    public static ServerInteractions getInstance() {
        return SERVER_INTERACT;
    }

    public List<MessageEntity> getmMesageEntities() {
        return mMesageEntities;
    }

    public enum OldNewFlag {
        OLD_FLAG, NEW_FLAG
    }

    public String cardType(String type)
    {
        String cardtype = "";

        if(type.equals("Article"))
            cardtype = mContext.getResources().getString(R.string.picturetype);
        if(type.equals("Notice"))
            cardtype = mContext.getResources().getString(R.string.noticetype);
        else if(type.equals("Punch"))
            cardtype = mContext.getResources().getString(R.string.attendancetype);
        else if(type.equals("OpenClass"))
            cardtype = mContext.getResources().getString(R.string.openclass);
        else if(type.equals("Report"))
            cardtype = mContext.getResources().getString(R.string.report);
        else if(type.equals("Food"))
            cardtype = mContext.getResources().getString(R.string.food);
        else if(type.equals("Schedule"))
            cardtype = mContext.getResources().getString(R.string.schedule);
        else if(type.equals("Active"))
            cardtype = mContext.getResources().getString(R.string.activity);
        return cardtype;
    }

    public ServerInteractions() {
    }

    public void init(CloudSchoolBusParentsApplication application, Context context) {
        mApplication = application;
        mContext = context;
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
                    try {
                        Timeline message = timelines.get(i);
                        if (!cardType(message.getApptype()).equals("")) {
                            Timeline.Sender sender = message.getSender();
                            /*
                             * Check if the messages are intended to multiple kids
                             */
                            if (message.getStudentid().contains(",")) {
                                String studentids[] = message.getStudentid().split(",");
                                for (String studentid : studentids) {
                                    MessageEntity messageEntity = new MessageEntity(message.getMessageid(), message.getTitle(),
                                            message.getDescription(), message.getIsconfirm(), message.getSendtime(), message.getApptype(),
                                            studentid, message.getIsmass(), message.getIsreaded(), message.getBody(), message.getTag(), sender.getId());

                                    SenderEntity senderEntity = new SenderEntity(sender.getId(), sender.getRole(), sender.getAvatar(), sender.getClassname(), sender.getName());
                                    if (senderEntity.getId() != null
                                            && !senderEntity.getId().isEmpty()
                                            && messageEntity.getMessageid() != null
                                            && !messageEntity.getMessageid().isEmpty()) {
                                        messageEntityDao.insertOrReplace(messageEntity);
                                        senderEntityDao.insertOrReplace(senderEntity);
                                    }
                                }
                            } else {
                                MessageEntity messageEntity = new MessageEntity(message.getMessageid(), message.getTitle(),
                                        message.getDescription(), message.getIsconfirm(), message.getSendtime(), message.getApptype(),
                                        message.getStudentid(), message.getIsmass(), message.getIsreaded(), message.getBody(), message.getTag(),sender.getId());

                                SenderEntity senderEntity = new SenderEntity(sender.getId(), sender.getRole(), sender.getAvatar(), sender.getClassname(), sender.getName());
                                if (senderEntity.getId() != null
                                        && !senderEntity.getId().isEmpty()
                                        && messageEntity.getMessageid() != null
                                        && !messageEntity.getMessageid().isEmpty()) {
                                    messageEntityDao.insertOrReplace(messageEntity);
                                    senderEntityDao.insertOrReplace(senderEntity);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Timeline Parsor Error",e.toString());
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
                    handler.sendEmptyMessage(HandlerConstant.MSG_ONCACHE); //Try to display the cached message
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = HandlerConstant.MSG_SERVER_ERROR;
                    msg.obj = throwable;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    public List<MessageEntity> GetMessagesFromCache()
    {

//        mMesageEntities = mApplication.mDaoSession.getMessageEntityDao().queryBuilder()
//                    .orderDesc(MessageEntityDao.Properties.Messageid)
//                    .list();

        mMesageEntities = mApplication.mDaoSession.getMessageEntityDao().queryBuilder()
                .orderRaw("MESSAGEID+1 DESC")
                .list();

        return mMesageEntities;
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
                    if (remoteUrl.charAt(remoteUrl.length() - 1) == '.')
                        remoteUrl = remoteUrl.substring(0, remoteUrl.lastIndexOf('.'));
                    bundle.putString("filepath", remoteUrl);
                    bundle.putString("userid", userid);
                    bundle.putString("cache", (String) image);
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

        showWaitDialog("",null);

        HashMap<String, String> params = new HashMap<String, String>();
        if(messageid!=null) params.put("messageid", messageid);

        CloudSchoolBusRestClient.get(ProtocolDef.METHOD_noticeconfirm, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hideWaitDialog();
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                button.setTag(messageid);
                msg.obj = button;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                hideWaitDialog();
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                button.setTag(messageid);
                msg.obj = button;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                hideWaitDialog();
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                button.setTag(messageid);
                msg.obj = button;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideWaitDialog();
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_SERVER_ERROR;
                msg.obj = throwable;
                handler.sendMessage(msg);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                hideWaitDialog();
                handler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideWaitDialog();
                handler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void QRCodeLogin(final String sequenceno, final Handler handler)
    {
        if (!mApplication.networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        if(sequenceno!=null) params.put("sequence", sequenceno);

        CloudSchoolBusRestClient.get(ProtocolDef.METHOD_qrcodelogin, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
                handler.sendMessage(msg);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Message msg = handler.obtainMessage();
                msg.what = HandlerConstant.MSG_CONFIRM_OK;
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

    public void renew_sid(String mobile, final android.os.Handler handler) throws com.alibaba.fastjson.JSONException {

        if(!mApplication.networkStatusEvent.isNetworkConnected()) {
            return;
        }

        RequestParams params = new RequestParams();
        params.put("token", mApplication.mConfig.getToken());
        params.put("mobile",mobile);

        CloudSchoolBusRestClient.post("login", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                String retCode = "";

                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }

                if (!retCode.equals("1")) {
                    return;
                } else {
                    //Debug Feature only returns sid
                    String sid = "",userid ="",imToken="";
                    try {
                        if(response.has("sid")) {
                            sid = response.getString("sid");
                        }
                        if(response.has("userid")) {
                            userid = response.getString("userid");
                        }
                        if(response.has("imToken")) {
                            imToken = response.getString("imToken");
                        }

                        if(isDebug)
                        {
                            //无需保存在数据库里,目前仅仅保存sid
                            mApplication.mConfig.setSid(sid);
                            mApplication.mConfig.setImToken(imToken);
                            mApplication.mConfig.setCurrentuser(0);
                            CloudSchoolBusRestClient.updateSessionid(mApplication.mConfig.getSid());
                            handler.sendEmptyMessage(HandlerConstant.MSG_LOGIN_OK_DEBUG);

                        } else {
                            ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
                            if(configEntityDao.queryBuilder().limit(1).list().size()>0) {
                                ConfigEntity oldConfigEntity = configEntityDao.queryBuilder().limit(1).list().get(0);
                                ConfigEntity configEntity = new ConfigEntity(null, mApplication.mConfig.getToken(), sid, mApplication.mConfig.getMobile(), userid, imToken, oldConfigEntity.getCurrentuser());
                                configEntityDao.update(configEntity);
                            }
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    /**
     * 显示等待对话框
     *
     * @param messageString
     */
    protected void showWaitDialog(String messageString,
                                  final CustomWaitDialog.OnKeyCancel onKeyCancel) {
        if (null == mCustomWaitDialog) {
            mCustomWaitDialog = new CustomWaitDialog(mContext,
                    com.android.support.R.style.CustomWaitDialog);
            mCustomWaitDialog.setCancelable(true);
            mCustomWaitDialog.setCanceledOnTouchOutside(false);
            mCustomWaitDialog.setMessage(messageString);
            mCustomWaitDialog.setOnKeyCancelListener(new CustomWaitDialog.OnKeyCancel() {

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
}
