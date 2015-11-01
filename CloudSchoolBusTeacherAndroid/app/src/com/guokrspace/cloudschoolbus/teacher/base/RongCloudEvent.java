package com.guokrspace.cloudschoolbus.teacher.base;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.guokrspace.cloudschoolbus.teacher.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.LastIMMessageEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.LastIMMessageEntityDao;

import java.util.List;

import io.rong.imkit.PushNotificationManager;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationMessage;

/**
 * Created by macbook on 15/10/10.
 */
public class RongCloudEvent implements RongIMClient.OnReceiveMessageListener, RongIMClient.OnReceivePushMessageListener, Handler.Callback {

    private static final String TAG = RongCloudEvent.class.getSimpleName();

    private static RongCloudEvent mRongCloudInstance;
    private Context mContext;
    private CloudSchoolBusParentsApplication mApplication;
    private OnReceiveMessageListener mListener;
    private Handler mHandler;

    public interface OnReceiveMessageListener {
        void onMessageReceived();
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContext = context;
        mApplication = (CloudSchoolBusParentsApplication) mContext.getApplicationContext();
        initDefaultListener();
        mHandler = new Handler(this);
    }

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    public void setmListener(OnReceiveMessageListener mListener) {
        this.mListener = mListener;
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {
//        RongIM.setUserInfoProvider(this, true);//设置用户信息提供者。
//        RongIM.setGroupInfoProvider(this, true);//设置群组信息提供者。
//        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
//        RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
//        RongIM.setConversationListBehaviorListener(this);
        //消息体内是否有 userinfo 这个属性
//        RongIM.getInstance().setMessageAttachedUserInfo(true);
//        RongIM.getInstance().getRongIMClient().setOnReceivePushMessageListener(this);//自定义 push 通知。
    }

    /*
 * 连接成功注册。
 * <p/>
 * 在RongIM-connect-onSuccess后调用。
 */
    public void setOtherListener() {

        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);//设置消息接收监听器。
//        RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.
//        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);//设置连接状态监听器。
//
////        扩展功能自定义
//        InputProvider.ExtendProvider[] provider = {
//                new PhotoCollectionsProvider(RongContext.getInstance()),//图片
//                new CameraInputProvider(RongContext.getInstance()),//相机
//                new RealTimeLocationInputProvider(RongContext.getInstance()),//地理位置
//                new VoIPInputProvider(RongContext.getInstance()),// 语音通话
//                new ContactsProvider(RongContext.getInstance()),//通讯录
//        };
//
//        InputProvider.ExtendProvider[] provider1 = {
//                new PhotoCollectionsProvider(RongContext.getInstance()),//图片
//                new CameraInputProvider(RongContext.getInstance()),//相机
//                new RealTimeLocationInputProvider(RongContext.getInstance()),//地理位置
//        };

//        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
//        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.DISCUSSION, provider1);
//        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, provider1);
//        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CUSTOMER_SERVICE, provider1);
//        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, provider1);
//        RongIM.getInstance().setPrimaryInputProvider(new InputTestProvider((RongContext) mContext));

    }


    @Override
    public boolean onReceived(Message message, int i) {

        MessageContent messageContent = message.getContent();
        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            textMessage.getExtra();
            Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
//        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
//            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
//            Log.d(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
//            if (DemoContext.getInstance() != null)
//                getFriendByUserIdHttpRequest = DemoContext.getInstance().getDemoApi().getUserInfoByUserId(message.getSenderUserId(), (ApiCallback<User>) this);
//        } else if (messageContent instanceof DeAgreedFriendRequestMessage) {//好友添加成功消息
//            DeAgreedFriendRequestMessage deAgreedFriendRequestMessage = (DeAgreedFriendRequestMessage) messageContent;
//            Log.d(TAG, "onReceived-deAgreedFriendRequestMessage:" + deAgreedFriendRequestMessage.getMessage());
//            receiveAgreeSuccess(deAgreedFriendRequestMessage);
//        } else if (messageContent instanceof ContactNotificationMessage) {//好友添加消息
//            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
//            Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
//            Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());
//            Intent in = new Intent();
//            in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
//            in.putExtra("rongCloud", contactContentMessage);
//            in.putExtra("has_message", true);
//            mContext.sendBroadcast(in);
        } else {
            Log.d(TAG, "onReceived-其他消息，自己来判断处理");
        }

        String hasUnread = "0";
        Long timestamp = message.getSentTime();
        if (!message.getReceivedStatus().isRead()) hasUnread = "1";
        List<LastIMMessageEntity> lastIMs = mApplication.mDaoSession.getLastIMMessageEntityDao().queryBuilder()
                .where(LastIMMessageEntityDao.Properties.Userid.eq(message.getTargetId()))
                .list();
        if (lastIMs.size() > 0) {
            LastIMMessageEntity lastIM = lastIMs.get(0);
            lastIM.setTimestamp(Long.toString(timestamp / 1000));
            lastIM.setHasUnread(hasUnread);
            mApplication.mDaoSession.getLastIMMessageEntityDao().update(lastIM);
        } else {
            LastIMMessageEntity lastIMMessageEntity = new LastIMMessageEntity();
            lastIMMessageEntity.setTimestamp(Long.toString(timestamp / 1000));
            lastIMMessageEntity.setHasUnread(hasUnread);
            lastIMMessageEntity.setUserid(message.getTargetId());
            mApplication.mDaoSession.getLastIMMessageEntityDao().insert(lastIMMessageEntity);
        }

        if(mListener!=null)
            mListener.onMessageReceived();

        return false;
    }


    /**
     * 自定义 push 通知。
     *
     * @param msg
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onReceivePushMessage(PushNotificationMessage msg) {
        Log.d(TAG, "onReceived-onPushMessageArrive:" + msg.getContent());

        PushNotificationManager.getInstance().onReceivePush(msg);

        Intent intent = new Intent();
        Uri uri;

        intent.setAction(Intent.ACTION_VIEW);

        Conversation.ConversationType conversationType = msg.getConversationType();

        uri = Uri.parse("rong://" + RongContext.getInstance().getPackageName()).buildUpon().appendPath("conversationlist").build();
        intent.setData(uri);
        Log.d(TAG, "onPushMessageArrive-url:" + uri.toString());

        Notification notification = null;

        PendingIntent pendingIntent = PendingIntent.getActivity(RongContext.getInstance(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT < 11) {
            notification = new Notification(RongContext.getInstance().getApplicationInfo().icon, "自定义 notification", System.currentTimeMillis());

            notification.setLatestEventInfo(RongContext.getInstance(), "自定义 title", "这是 Content:" + msg.getObjectName(), pendingIntent);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_SOUND;
        } else {

            notification = new Notification.Builder(RongContext.getInstance())
                    .setLargeIcon(getAppIcon())
                    .setSmallIcon(R.drawable.ic_launcher_teacher)
                    .setTicker("自定义 notification")
                    .setContentTitle("自定义 title")
                    .setContentText("这是 Content:" + msg.getObjectName())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL).build();

        }

        NotificationManager nm = (NotificationManager) RongContext.getInstance().getSystemService(RongContext.getInstance().NOTIFICATION_SERVICE);

        nm.notify(0, notification);

        return true;
    }

    @Override
    public boolean handleMessage(android.os.Message message) {
        return false;
    }

    private Bitmap getAppIcon() {
        BitmapDrawable bitmapDrawable;
        Bitmap appIcon;
        bitmapDrawable = (BitmapDrawable) RongContext.getInstance().getApplicationInfo().loadIcon(RongContext.getInstance().getPackageManager());
        appIcon = bitmapDrawable.getBitmap();
        return appIcon;
    }
}
