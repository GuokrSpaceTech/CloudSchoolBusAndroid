package com.Manga.Activity.utils;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.MainActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;


/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
	@Override
	public void onBind(final Context context,  int errorCode, String appid,
			String userId, String channelId, String requestId) {
		
		//String responseString = "onBind errorCode=" + errorCode + " appid="
		//		+ appid + " userId=" + userId + " channelId=" + channelId
		//		+ " requestId=" + requestId;

		//Log.d(TAG, ">>> responseString: \r\n" + responseString);
		
		if(errorCode == 0)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			
			map.put("type","android");
			map.put("userid",userId);
			map.put("channelid", channelId);
			
			final Params param=new Params("push", map);
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
				if(HttpUtil.isNetworkConnected(LoginActivity.logincontext))
				{
					try {
						HttpUtil.httpPost(LoginActivity.logincontext, param);
					} catch (Exception e) {
					}
				}				
			}
		});
			thread.start();
		}
	}

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
       // String notifyString = "通知点击 title=\"" + title + "\" description=\""
       //         + description + "\" customContent=" + customContentString;
        Intent aIntent = null;
        //Log.d(TAG, notifyString);

        // 自定义内容获取方式，key和value对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String value = null;     
				
                aIntent = new Intent();
                aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
                if (!customJson.isNull("key")) {
                    value = customJson.getString("key");
                    
                    //Jump to different activity based on the key
                    if(Push_Info.getInstance().isBlnIsLogin() == true)
                    {
                    	//Goto the default activity, let it decide which activity to go
                		Push_Info.getInstance().setStrPush(value);
                		Push_Info.getInstance().setStrPushOpen("push");
						aIntent.setClass(context, MainActivity.class);
                    }                
                    else //Ask user to login
                    {
        				aIntent.setClass(context, LoginActivity.class);
                    }
                }
            } catch (JSONException e) {
            }
            
            if(aIntent != null)
			    context.startActivity(aIntent);
			
            return;
        }
    }

    /**
     * setTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
    }

    /**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
    }

	@Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
    }
    /**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                //
                e.printStackTrace();
            }
        }
		
	}

    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            setBind(context, false);
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }
}

