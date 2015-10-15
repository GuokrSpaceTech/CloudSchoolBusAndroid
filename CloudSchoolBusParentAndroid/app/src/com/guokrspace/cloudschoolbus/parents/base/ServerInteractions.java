package com.guokrspace.cloudschoolbus.parents.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by kai on 10/8/15.
 */
public class ServerInteractions {

    private static final ServerInteractions SERVER_INTERACT = new ServerInteractions();
    private static CloudSchoolBusParentsApplication mApplication=null;
    private static Context mContext = null;
    private List<MessageEntity> mMesageEntities = null;

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
                    Timeline message = timelines.get(i);
                    if (!cardType(message.getApptype()).equals("")) {
                        Timeline.Sender sender = message.getSender();
                        MessageEntity messageEntity = new MessageEntity(message.getMessageid(), message.getTitle(),
                                message.getDescription(), message.getIsconfirm(), message.getSendtime(), message.getApptype(),
                                message.getStudentid(), message.getIsmass(), message.getIsreaded(), message.getBody(), sender.getId());

                        SenderEntity senderEntity = new SenderEntity(sender.getId(), sender.getRole(), sender.getAvatar(), sender.getClassname(), sender.getName());
                        if(senderEntity.getId()!=null
                                && !senderEntity.getId().isEmpty()
                                && messageEntity.getMessageid()!=null
                                && !messageEntity.getMessageid().isEmpty()) {
                            messageEntityDao.insertOrReplace(messageEntity);
                            senderEntityDao.insertOrReplace(senderEntity);
                        }
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

    public List<MessageEntity> GetMessagesFromCache()
    {
//        if(Version.PARENT) {
//            int current = mApplication.mConfig.getCurrentChild();
//            String currentstudentid = null;
//            if(mApplication.mStudents.size()>(current)) {
//                currentstudentid = mApplication.mStudents.get(current).getStudentid();
//            }

//            if(currentstudentid!=null) {
//                mMesageEntities = mApplication.mDaoSession.getMessageEntityDao().queryBuilder()
//                        .orderDesc(MessageEntityDao.Properties.Messageid)
//                        .where(MessageEntityDao.Properties.Studentid.eq(currentstudentid))
//                        .list();
//            }
//        } else {
            mMesageEntities = mApplication.mDaoSession.getMessageEntityDao().queryBuilder()
                    .orderDesc(MessageEntityDao.Properties.Messageid)
                    .list();
//        }

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


}
