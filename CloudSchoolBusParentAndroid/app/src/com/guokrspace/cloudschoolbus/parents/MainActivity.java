/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guokrspace.cloudschoolbus.parents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.alibaba.fastjson.JSONException;
import com.astuetz.PagerSlidingTabStrip;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.guokrspace.cloudschoolbus.parents.base.RongCloudEvent;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.base.baidupush.BaiduPushUtils;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.IsUploadingEvent;
import com.guokrspace.cloudschoolbus.parents.event.LoginResultEvent;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.event.NewMessageEvent;
import com.guokrspace.cloudschoolbus.parents.event.SidExpireEvent;
import com.guokrspace.cloudschoolbus.parents.module.aboutme.AboutmeFragment;
import com.guokrspace.cloudschoolbus.parents.module.chat.UserListFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.ExploreFragment;
import com.guokrspace.cloudschoolbus.parents.module.hobby.HobbyFragment;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.widget.BadgeView;
import com.guokrspace.cloudschoolbus.parents.widget.NonSwipeableViewPager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import me.leolin.shortcutbadger.ShortcutBadger;

//http://stackoverflow.com/questions/24838668/icon-selector-not-working-with-pagerslidingtabstrips

public class MainActivity extends BaseActivity implements
        ExploreFragment.OnFragmentInteractionListener
{
    private Thread  thread;
    private boolean threadStopFlag = false;

    private PagerSlidingTabStrip tabs;
    private List<BadgeView> badgeViews = new ArrayList();
    public  NonSwipeableViewPager pager;
    private MyPagerAdapter adapter;
    private Fragment[] mFragments = {null, null, null, null};
    private int mPosition = 0;

    public String mUpperLeverTitle="";
    public String mCurrentTitle="";

    private Drawable oldBackground = null;
    private int currentColor = 0xF1A141;

    private static final int MSG_LOGIN_FAIL = -1;
    private static final int MSG_NO_NETWORK = 2;
    private static final int MSG_SID_RENEW_FAIL = 4;
    private static final int REQUEST_CODE = 1;

    MainActivity c = this;
    private IWXAPI api;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOGIN_FAIL:
                    break;
                case MSG_NO_NETWORK:
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                            .setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;
                case MSG_SID_RENEW_FAIL:
//                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
//                            .setMessage(getResources().getString(R.string.failure_renewsid))
//                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;
                case HandlerConstant.MSG_TIMER_TICK:
                    break;
                case HandlerConstant.MSG_TIMER_TIMEOUT:
                    //When startup page is off, show the main page
                    threadStopFlag = false;
                    CheckLoginCredential();

                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStartupPage();
//        initMessageTypes();
        //Hack for force the overflow button in the actionbar
        getOverflowMenu();
        setContentView(R.layout.activity_main);
        BusProvider.getInstance().register(this);

        api = WXAPIFactory.createWXAPI(this, Version.APP_ID, true);

//        api.registerApp(Version.APP_ID);
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        if(RongIM.getInstance()!=null)
            RongIM.getInstance().logout();
        PushManager.stopWork(mContext);
        super.onDestroy();
    }

    private void initFragments() {
            mFragments[0] = ExploreFragment.newInstance(null, null);
            mFragments[1] = UserListFragment.newInstance();
            mFragments[2] = HobbyFragment.newInstance(null,null,null);
            mFragments[3] = AboutmeFragment.newInstance();
    }

    void setupViews() {
        pager = (NonSwipeableViewPager) findViewById(R.id.pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        adapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments, mContext);
        pager.setAdapter(adapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        //Attach the BadgeView
        for (int i = 0; i < mFragments.length; i++) {
            BadgeView badgeView = new BadgeView(c);
            badgeView.setTargetView(tabs.getTabsContainer().getChildAt(i));
            badgeView.setBadgeMargin(6);
            badgeViews.add(badgeView);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getResources().getString(R.string.module_explore));
    }

    private void setListeners()
    {
//        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
//        tabs.delegatePageListener = new MyPageChangeListener();
        tabs.delegateOnTabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPosition = (int)view.getTag();
                if(mPosition==0)
                {
                    ExploreFragment theFragment =  (ExploreFragment)mFragments[0];
                    theFragment.filterCards("All");
                    setActionBarTitle(getResources().getString(R.string.module_explore));
                }
                clearBadge(mPosition);
            }
        };

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Log.i("", "back stack changed ");
                int backCount = getSupportFragmentManager().getBackStackEntryCount();
                // First Level of Fragment, no Homeasup Arrow, with bottoms Tabs
                if (backCount == 0) {
                    //
                    getTabs().setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().show();
                }
                // Next Level of Fragment(Conversation), has Homeasup Arrow, without bottom Tabs
                if (backCount > 0) {
                    getTabs().setVisibility(View.GONE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null)
        {
            setOverflowIconVisible(menu);
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item); //Let the fragment handle the it's own option
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(getSupportFragmentManager().getBackStackEntryCount()==0) {
                    //兴趣爱好直接是WebviewFragment, 返回需要特殊处理
                    if(mPosition==2 && mFragments[mPosition].isVisible())
                    {
                        boolean ret = ((HobbyFragment)mFragments[mPosition]).onKeyDown(keyCode, event);
                        if(ret==true) //到了顶级页面
                            exit_diaglog();
                    } else {
                        exit_diaglog();
                    }
                }
                else {
                    if(mPosition==1 && mFragments[mPosition].isVisible()) //ChatList
                    {
                        ((UserListFragment)mFragments[mPosition]).onBackPressed();
                    } else {
                        getSupportFragmentManager().popBackStack();
                    }
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(colorDrawable);
                }
            } else {
                TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);
            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);


        }
        currentColor = newColor;
    }

    private void exit_diaglog()
    {
        final AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setMessage("确定退出应用？");
        alterDialog.setCancelable(true);

        alterDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().logout();

                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        alterDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alterDialog.show();
    }

    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
//		changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    /**
     *  Getters and Setters
     * @return
     */

    public PagerSlidingTabStrip getTabs() {
        return tabs;
    }

    public void setTabs(PagerSlidingTabStrip tabs) {
        this.tabs = tabs;
    }

    public List<BadgeView> getBadgeViews() {
        return badgeViews;
    }

    public void setBadgeViews(List<BadgeView> badgeViews) {
        this.badgeViews = badgeViews;
    }

    @Override
    public void onFragmentInteraction(String id) {
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  Let the fragment to handle unhandled result
        /* http://stackoverflow.com/questions/6147884/onactivityresult-not-being-called-in-fragment?rq=1 */
        super.onActivityResult(requestCode, resultCode, data); //Rest of the activity results goes to respective fragment
    }

    public class MyPagerAdapter extends FragmentPagerAdapter
            implements PagerSlidingTabStrip.IconTabProvider {

        private final String[] TITLES = {"Discover", "Class", "Hobby", "Me"};
        private int[] ICONS = {};
        private Fragment[] mFragments = {};

        public MyPagerAdapter(FragmentManager fm, Fragment[] fragments, Context context) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            if (Version.PARENT) {
                ICONS = new int[]{R.drawable.selector_ic_tab_explore, R.drawable.selector_ic_tab_teacher,
                        R.drawable.selector_ic_tab_hobby, R.drawable.selector_ic_tab_aboutme};
            } else {
                ICONS = new int[]{R.drawable.selector_ic_tab_explore, R.drawable.selector_ic_tab_hobby,
                        R.drawable.selector_ic_tab_teacher, R.drawable.selector_ic_tab_aboutme};
            }

            return mFragments[position];
        }

        @Override
        public int getPageIconResId(int position) {
            if (ICONS.length == 0) {
                if (Version.PARENT) {
                    ICONS = new int[]{R.drawable.selector_ic_tab_explore, R.drawable.selector_ic_tab_teacher,
                            R.drawable.selector_ic_tab_hobby, R.drawable.selector_ic_tab_aboutme};
                } else {
                    ICONS = new int[]{R.drawable.selector_ic_tab_explore, R.drawable.selector_ic_tab_hobby,
                            R.drawable.selector_ic_tab_teacher, R.drawable.selector_ic_tab_aboutme};
                }
            }
            return ICONS[position];
        }
    }
    /**
     * Handling of Events from other modules
     * @param event
     */
    @Subscribe
    public void onSidExpired(SidExpireEvent event) {
        renew_sid();
    }

    @Subscribe
    public void onLoginResultEvent(LoginResultEvent event)
    {
        if(event.getIsLoginSuccess())
            LoginSuccessHandles();
        else
            handler.sendEmptyMessage(MSG_LOGIN_FAIL);
    }

    @Subscribe public void OnNetworkStateChange(NetworkStatusEvent event)
    {
        mApplication.networkStatusEvent = event;
    }

    @Subscribe public void OnNewMessageReceivedEvent(NewMessageEvent event)
    {
        getBadgeViews().get(0).setBadgeCount(event.getMessage_count());
        ShortcutBadger.with(mContext).remove();
    }

    @Subscribe public void onUserIsUploadingEvent(IsUploadingEvent event)
    {
        this.overridePendingTransition(R.anim.scalefromcorner, R.anim.scaletocorner);
        //Just set a red dot
        setBadge(3, 0);
    }


    /**
     *
     * Check if use has logined before and saved all the credentials
     * @throws JSONException
     *
     */
    public void CheckLoginCredential() throws JSONException {

            if (mApplication.mConfig == null || mApplication.mSchools == null || mApplication.mClasses == null) {
                //Ask for login
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            } else
                LoginSuccessHandles();

    }

    /**
     *  Renew the SessionID,
     */
    public void renew_sid() {

        if (!mApplication.networkStatusEvent.isNetworkConnected()) {
            handler.sendEmptyMessage(MSG_NO_NETWORK);
            return;
        }

//        showWaitDialog("", null);
        RequestParams params = new RequestParams();
        params.put("token", mApplication.mConfig.getToken());
        params.put("mobile", mApplication.mConfig.getMobile());

        CloudSchoolBusRestClient.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String retCode = "";

                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }

                if (retCode.equals("1")) {
                    try {
                        SidRenewSuccessHandles(response);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(MSG_SID_RENEW_FAIL);
                    }
                } else {
                    handler.sendEmptyMessage(MSG_SID_RENEW_FAIL);
                }

                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.sendEmptyMessage(MSG_SID_RENEW_FAIL);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.sendEmptyMessage(MSG_SID_RENEW_FAIL);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                handler.sendEmptyMessage(MSG_SID_RENEW_FAIL);
            }
        });
    }

    private void LoginSuccessHandles()
    {
        //Start Baidu Push
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                BaiduPushUtils.getMetaValue(MainActivity.this, "api_key"));
        initFragments();
        setupViews();
        setListeners();
        httpGetTokenSuccess(mApplication.mConfig.getImToken());
    }

    /**
     *
     * @param response the Jsonrespone from the server
     */
    private void SidRenewSuccessHandles(JSONObject response) throws org.json.JSONException {
        String sid = response.getString("sid");
        String token = response.getString("token");
        String imToken = response.getString("rongtoken");
        ConfigEntity oldConfigEntity =  mApplication.mDaoSession.getConfigEntityDao().queryBuilder().limit(1).list().get(0);
        ConfigEntity newConfigEntity = new ConfigEntity(null,sid,token,mApplication.mConfig.getMobile(),mApplication.mConfig.getUserid(),imToken,oldConfigEntity.getCurrentuser());
        mApplication.mDaoSession.getConfigEntityDao().insertOrReplace(newConfigEntity);
        mApplication.mConfig.setSid(response.getString("sid"));
        mApplication.mConfig.setToken(token);
        mApplication.mConfig.setImToken(imToken);
        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
        httpGetTokenSuccess(mApplication.mConfig.getImToken());
    }

    private void httpGetTokenSuccess(String token) {

    /* IMKit SDK调用第二步 建立与服务器的连接 */

    /* 集成和测试阶段，您可以直接使用从您开发者后台获取到的 token，比如 String token = “d6bCQsXiupB......”; */
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                //扩展功能自定义
                InputProvider.ExtendProvider[] provider = {
                        new ImageInputProvider(RongContext.getInstance()),//图片
                        new CameraInputProvider(RongContext.getInstance()),//相机
//                        new LocationInputProvider(RongContext.getInstance()),//地理位置
//                        new VoIPInputProvider(RongContext.getInstance()),// 语音通话
                };
                RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
                RongCloudEvent.getInstance().setOtherListener();
                Log.i("IM Connect", "Success");
            /* 连接成功 */
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.i("IM Connect", "Error");
            /* 连接失败，注意并不需要您做重连 */
            }

            @Override
            public void onTokenIncorrect() {
                Log.i("IM Connect", "Expired");
                /* Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token */
                BusProvider.getInstance().post(new SidExpireEvent(""));
            }
        });
    }


    /**
     * Listeners
     */
    private class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener
    {
        /**
         * 收到消息的处理。
         *
         * @param message 收到的消息实体。
         * @param left    剩余未拉取消息数目。
         * @return 收到消息是否处理完成，true 表示走自已的处理方式，false 走融云默认处理方式。
         */

        @Override
        public boolean onReceived(io.rong.imlib.model.Message message, int left) {
//            for( int j=0; j < mApplication.mTeachers.size(); j++)
//            {
//                String teacherid;
//                if(Version.PARENT) {
//                    TeacherEntity teacher = mApplication.mTeachers.get(j);
//                    teacherid = teacher.getId();
//                } else {
//                    TeacherEntityT teacherT = mApplication.mTeachersT.get(j);
//                    teacherid = teacherT.getTeacherid();
//                }
//
//                if(teacherid.equals(message.getSenderUserId()))
//                {
//                    String hasUnread = "0";
//                    if(left>0)  hasUnread= "1";
//                    Long timestamp = message.getSentTime();
//                    LastIMMessageEntity lastIMMessageEntity = new LastIMMessageEntity(teacherid, Long.toString(timestamp/1000), hasUnread);
//                    mApplication.mDaoSession.getLastIMMessageEntityDao().insertOrReplace(lastIMMessageEntity);
//                }
//            }
//
            //Just set a red dot
            if(Version.PARENT) setBadge(1,0); else setBadge(2,0);

            return false;
        }
    }

//    private class MyPageChangeListener implements ViewPager.OnPageChangeListener
//    {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            //Clear the badge icon
////            badgeViews.get(position).setVisibility(View.INVISIBLE);
//
//            // Check if this is the page you want.
//            if (mFragments[position] instanceof ExploreFragment) {
//                    setActionBarTitle(getResources().getString(R.string.module_explore),"");
//            } else if (mFragments[position] instanceof UserListFragment) {
//                setActionBarTitle(getResources().getString(R.string.module_teacher), "");
//                if(!Version.PARENT)
//                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//            } else if (mFragments[position] instanceof HobbyFragment) {
//                setActionBarTitle(getResources().getString(R.string.module_hobby),"");
//                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//            } else if (mFragments[position] instanceof AboutmeFragment) {
//                setActionBarTitle(getResources().getString(R.string.module_aboutme),"");
//                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//            }
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//
//        }
//    }

    //This is hack for overflow menu not showing
    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOverflowIconVisible(Menu menu)
    {
        try
        {
            Class clazz=Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
            Field field=clazz.getDeclaredField("mOptionalIconsVisible");
            if(field!=null)
            {
                field.setAccessible(true);
                field.set(menu, true);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setActionBarTitle(String title)
    {
            getSupportActionBar().setTitle(title);
    }

    private void showStartupPage()
    {
        TimerTick(3);
        StartupFragment theFragment = StartupFragment.newInstance(null, null);
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_layout, theFragment, "startup");
        transaction.addToBackStack("startup");
        transaction.commit();
    }

    private void hideStartupPage()
    {
        getSupportFragmentManager().popBackStack("startup", 1);
        getSupportActionBar().show();
    }

    public void setBadge(int position, int badge)
    {
        if(badge==0)
        {
            badgeViews.get(position).setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
            badgeViews.get(position).setPadding(dip2Px(3), dip2Px(1), dip2Px(3), dip2Px(1));
            badgeViews.get(position).setTextColor(Color.parseColor("#d3321b"));
            badgeViews.get(position).setBadgeMargin(12);
            badgeViews.get(position).invalidate();
        } else {
            badgeViews.get(position).setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            badgeViews.get(position).setPadding(dip2Px(5), dip2Px(1), dip2Px(5), dip2Px(1));
            badgeViews.get(position).setTextColor(Color.WHITE);
            badgeViews.get(position).setBadgeMargin(6);
            badgeViews.get(position).setBadgeCount(badge);
            badgeViews.get(position).invalidate();
        }
        badgeViews.get(position).setVisibility(View.VISIBLE);
    }

    public void clearBadge(int position)
    {
        badgeViews.get(position).setVisibility(View.INVISIBLE);
    }

    private void TimerTick(final int max_seconds) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int seconds_left = max_seconds;
                while (seconds_left > 0 && !threadStopFlag) {
                    seconds_left--;
                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(HandlerConstant.MSG_TIMER_TIMEOUT);
            }
        });
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    /*
     * converts dip to px
     */
    private int dip2Px(float dip){
        return (int) (dip * mContext.getResources().getDisplayMetrics().density + 0.5f);
    }

}
