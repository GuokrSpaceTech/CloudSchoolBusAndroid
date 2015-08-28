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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
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
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.astuetz.PagerSlidingTabStrip;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.base.baidupush.BaiduPushUtils;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LastIMMessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.LoginResultEvent;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.event.SidExpireEvent;
import com.guokrspace.cloudschoolbus.parents.module.aboutme.AboutmeFragment;
import com.guokrspace.cloudschoolbus.parents.module.chat.TeacherListFragment;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.StreamingFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.ClassifyDialogFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.attendance.AttendanceFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.food.FoodFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.notice.NoticeFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.report.ReportFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.schedule.ScheduleFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.TimelineFragment;
import com.guokrspace.cloudschoolbus.parents.module.hobby.HobbyFragment;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.widget.BadgeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.guokrspace.cloudschoolbus.parents.R.string;

//http://stackoverflow.com/questions/24838668/icon-selector-not-working-with-pagerslidingtabstrips
/*
pstsIndicatorColor Color of the sliding indicator
pstsUnderlineColor Color of the full-width line on the bottom of the view
pstsDividerColor Color of the dividers between tabs
pstsIndicatorHeightHeight of the sliding indicator
pstsUnderlineHeight Height of the full-width line on the bottom of the view
pstsDividerPadding Top and bottom padding of the dividers
pstsTabPaddingLeftRight Left and right padding of each tab
pstsScrollOffset Scroll offset of the selected tab
pstsTabBackground Background drawable of each tab, should be a StateListDrawable
pstsShouldExpand If set to true, each tab is given the same weight, default false
pstsTextAllCaps If true, all tab titles will be upper case, default true
*/

public class MainActivity extends BaseActivity implements
        TimelineFragment.OnFragmentInteractionListener,
        NoticeFragment.OnFragmentInteractionListener,
        AttendanceFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        ReportFragment.OnFragmentInteractionListener,
        ClassifyDialogFragment.OnCompleteListener

{

    private PagerSlidingTabStrip tabs;
    private List<BadgeView> badgeViews = new ArrayList();
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private Fragment[] mFragments = {null, null, null, null};
    private List<String> mFragementTags = new ArrayList();
//    private MenuItem mOptionMenuItem= null;
    private TextView actionBarTitle;

    private Drawable oldBackground = null;
    private int currentColor = 0xF1A141;

    private static final int MSG_LOGIN_FAIL = -1;
    private static final int MSG_NO_NETWORK = 2;
    private static final int MSG_SID_RENEW_FAIL = 4;
    private static final int REQUEST_CODE = 1;

    MainActivity c = this;

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
                    SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager())
                            .setMessage(getResources().getString(R.string.failure_renewsid))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Customise the Action Bar
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.abs_layout);
//        View view = getSupportActionBar().getCustomView();
//        actionBarTitle = (TextView) view.findViewById(R.id.abs_layout_titleTextView);
//        actionBarTitle.setText(getResources().getString(string.module_explore));
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        CheckLoginCredential();
    }

    private void initFragments() {
        mFragments[0] = TimelineFragment.newInstance(null, null);
        mFragments[1] = TeacherListFragment.newInstance();
        mFragments[2] = HobbyFragment.newInstance();
        mFragments[3] = AboutmeFragment.newInstance();
    }

    void setupViews() {
        pager = (ViewPager) findViewById(R.id.pager);
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
            badgeViews.add(badgeView);
        }
    }

    private void setListeners()
    {
        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
        tabs.delegatePageListener = new MyPageChangeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        mOptionMenuItem = menu.findItem(R.id.action_all);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_all:
                TimelineFragment fragment = (TimelineFragment)mFragments[0];
                if(fragment.mMesageEntities.size() != 0) {
                    ClassifyDialogFragment dialog = new ClassifyDialogFragment();
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0); //Let the fragment dialog take control all view elements
                    dialog.show(getSupportFragmentManager(), "");
                }
                break;
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                //fragment = (TimelineFragment)getSupportFragmentManager().findFragmentByTag(mFragementTags.get(0));
                Fragment theFragment = getCurrentFragment();
//                if(theFragment instanceof TimelineFragment)
//                    mOptionMenuItem.setVisible(true);
                /*
                 * Check if this return from the Filter dialog, if yes,
                 * showing the optionMenu, otherwise, hide it.
                */
                if(theFragment instanceof ReportFragment
                        || theFragment instanceof AttendanceFragment
                        || theFragment instanceof NoticeFragment
                        || theFragment instanceof StreamingFragment
                        || theFragment instanceof ScheduleFragment
                        || theFragment instanceof FoodFragment )
                {
//                    mOptionMenuItem.setVisible(true);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(getSupportFragmentManager().getBackStackEntryCount()==0)
                    finish();
                else
                   getSupportFragmentManager().popBackStack();
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

    public TextView getActionBarTitle() {
        return actionBarTitle;
    }

    public void setActionBarTitle(TextView actionBarTitle) {
        this.actionBarTitle = actionBarTitle;
    }

//    public MenuItem getmOptionMenuItem() {
//        return mOptionMenuItem;
//    }
//
//    public void setmOptionMenuItem(MenuItem mOptionMenuItem) {
//        this.mOptionMenuItem = mOptionMenuItem;
//    }

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

    /*
     *  Handles all the retures from the fragment dialog
     */
    public void onComplete(String module) {
        // After the dialog fragment completes, it calls this callback.
        // use the string here
        FragmentTransaction transaction;
        switch (module) {
            case "notice":
                NoticeFragment noticeFragment = NoticeFragment.newInstance(null, null);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, noticeFragment, "notice");
                transaction.addToBackStack("notice");
                transaction.commit();
                break;
            case "attendance":
                AttendanceFragment attendanceFragment = AttendanceFragment.newInstance(null, null);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, attendanceFragment, "attendance");
                transaction.addToBackStack("attendance");
                transaction.commit();
                break;
            case "schedule":
                ScheduleFragment scheduleFragment = ScheduleFragment.newInstance(null, null);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, scheduleFragment, "schedule");
                transaction.addToBackStack("schedule");
                transaction.commit();
                break;
            case "report":
                ReportFragment reportFragment = ReportFragment.newInstance(null, null);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, reportFragment, "report");
                transaction.addToBackStack("report");
                transaction.commit();
                break;
            case "food":
                FoodFragment fragment = FoodFragment.newInstance(null,null);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, fragment, "food");
                transaction.addToBackStack("food");
                transaction.commit();
                break;

            case "streaming":
                StreamingFragment streamingFragment = StreamingFragment.newInstance(null,null);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, streamingFragment, "streaming");
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
                break;
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter
            implements PagerSlidingTabStrip.IconTabProvider {

        private final String[] TITLES = {"Discover", "Class", "Hobby", "Me"};
        private final int[] ICONS = {R.drawable.selector_ic_tab_explore, R.drawable.selector_ic_tab_teacher,
                R.drawable.selector_ic_tab_hobby, R.drawable.selector_ic_tab_aboutme};
        private Fragment[] mFragments = {};
        private Context mContext;

        public MyPagerAdapter(FragmentManager fm, Fragment[] fragments, Context context) {
            super(fm);
            mFragments = fragments;
            mContext = context;
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

            return mFragments[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment)super.instantiateItem(container, position);
            mFragementTags.add(position,fragment.getTag());
            return fragment;
        }

        @Override
        public int getPageIconResId(int position) {
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

    @Subscribe public void OnLoginResultEvent(LoginResultEvent event)
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
        ConfigEntity configEntity = new ConfigEntity(null,sid,token,mApplication.mConfig.getMobile(),mApplication.mConfig.getUserid(),imToken);
        mApplication.mDaoSession.getConfigEntityDao().insertOrReplace(configEntity);
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
            for( int j=0; j < mApplication.mTeachers.size(); j++)
            {
                TeacherEntity teacherEntity = mApplication.mTeachers.get(j);
                if(teacherEntity.getId().equals(message.getSenderUserId()))
                {
                    String hasUnread = "0";
                    if(left>0)  hasUnread= "1";
                    Long timestamp = message.getSentTime();
                    LastIMMessageEntity lastIMMessageEntity = new LastIMMessageEntity(teacherEntity.getId(), Long.toString(timestamp/1000), hasUnread);
                    mApplication.mDaoSession.getLastIMMessageEntityDao().insertOrReplace(lastIMMessageEntity);
                }
            }

             //This is the chat tab
            badgeViews.get(1).setVisibility(View.VISIBLE);
            badgeViews.get(1).setText(Integer.toString(left));

            return false;
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //Clear the badge icon
            badgeViews.get(position).setVisibility(View.INVISIBLE);

            // Check if this is the page you want.
            if (mFragments[position] instanceof TimelineFragment) {
                actionBarTitle.setText(getResources().getString(string.module_explore));
//                mOptionMenuItem.setVisible(true);
            } else if (mFragments[position] instanceof TeacherListFragment) {
                actionBarTitle.setText(getResources().getString(string.module_teacher));
//                mOptionMenuItem.setVisible(false);
            } else if (mFragments[position] instanceof HobbyFragment) {
                actionBarTitle.setText(getResources().getString(string.module_hobby));
//                mOptionMenuItem.setVisible(false);
            } else if (mFragments[position] instanceof AboutmeFragment) {
                actionBarTitle.setText(getResources().getString(string.module_aboutme));
//                mOptionMenuItem.setVisible(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}

