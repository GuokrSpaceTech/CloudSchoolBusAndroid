package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.DataWrapper;
import com.guokrspace.cloudschoolbus.parents.base.RongCloudEvent;
import com.guokrspace.cloudschoolbus.parents.base.activity.GalleryActivityUrl;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LastIMMessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LastIMMessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.widget.ContactListCard;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

/**
 * Created by macbook on 15-8-9.
 */
public class UserListFragment extends BaseFragment implements RongCloudEvent.OnReceiveMessageListener {

    private MaterialListView listview;
    private MainActivity mainActivity;
    private String userName;
    private View rootView;
    private boolean isConverstaionFragmentCreated;

    private Handler mHandler;

    {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HandlerConstant.MSG_IM_RECEIVED:

                        if (!Version.PARENT) {
                            if (mainActivity != null)
                                mainActivity.setBadge(2, 0);
                        } else {
                            if (mainActivity != null)
                                mainActivity.setBadge(1, 0);
                        }

                        if (!mFragment.isVisible()) break;

                            updateContacts();

                        break;
                }
                return false;
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Start the conversation fragment
            //For some reason, the fragment cannot be found, hence re-entry issue when quickly touch the list
            ConversationFragment fragment = (ConversationFragment) getFragmentManager().findFragmentByTag("Conversation");
            if (isConverstaionFragmentCreated == false) {
                int position = (int) view.getTag();
                String mTargetID = "";

                    userName = mApplication.mTeachers.get(position).getName();
                    mTargetID = mApplication.mTeachers.get(position).getTeacherid();

                //
                setUpChattingPageActionbar();

                //Lock the swipe for the second level page
                ((MainActivity) mParentContext).pager.lock();
                fragment = new ConversationFragment();
                isConverstaionFragmentCreated = true;

                Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation").appendPath(io.rong.imlib.model.Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                        .appendQueryParameter("targetId", mTargetID).appendQueryParameter("title", "hello")
                        .build();
                fragment.setUri(uri);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, "Conversation");
                transaction.addToBackStack("Conversation");
                transaction.commit();

                //Update LastIMMessageEntity
                List<LastIMMessageEntity> lastIMs = mApplication.mDaoSession.getLastIMMessageEntityDao()
                        .queryBuilder()
                        .where(LastIMMessageEntityDao.Properties.Userid.eq(mTargetID))
                        .list();

                if (lastIMs.size() > 0) {
                    lastIMs.get(0).setHasUnread("0");
                    mApplication.mDaoSession.getLastIMMessageEntityDao().update(lastIMs.get(0));
                }

                //Update the View
                ImageView badgeView = (ImageView) view.findViewById(R.id.badgeImageView);
                badgeView.setVisibility(View.INVISIBLE);

                //clear the badge in the bottom
                if (mainActivity != null) {
                    if (Version.PARENT)
                        mainActivity.clearBadge(1);
                    else
                        mainActivity.clearBadge(2);
                }
            }
        }
    };

    public static UserListFragment newInstance() {
        UserListFragment f = new UserListFragment();
        return f;
    }

    public UserListFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) mParentContext;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_teacher_list, container, false);
        listview = (MaterialListView) rootView.findViewById(R.id.material_listview);

        initView(rootView);

        setListener();

        setHasOptionsMenu(true);

        return rootView;
    }

    public void onDestroyView() {
        super.onDestroyView();

        //内嵌的Fragment
        ConversationFragment conversationFragment = (ConversationFragment) getFragmentManager().findFragmentByTag("Conversation");
        if (conversationFragment != null) {
            getFragmentManager().beginTransaction().remove(conversationFragment).commit();
            isConverstaionFragmentCreated = false;
        }
    }


    private void initView(View v) {
            updateContacts();

    }

    @Override
    protected void setListener() {
        super.setListener();
        if (RongIM.getInstance() != null) {
            /**
             * 设置会话界面操作的监听器。
             */
            RongIM.getInstance().setConversationBehaviorListener(new MyConversationBehaviorListener());
        }
        //
        if (RongCloudEvent.getInstance() != null)
            RongCloudEvent.getInstance().setmListener(this);

        if (rootView != null) {
            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();
            rootView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        ((MainActivity) mParentContext).pager.unlock();

                        InputMethodManager imm = (InputMethodManager) mParentContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(((MainActivity) mParentContext).getWindow().getDecorView().getWindowToken(), 0);

                        try {
                            isConverstaionFragmentCreated = false;
                            Fragment fragment = getFragmentManager().findFragmentByTag("Conversation");
                            if (fragment != null) {
                                getFragmentManager().popBackStack();
                            }

                                mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_teacher));
                        } catch (IllegalStateException ignored) {
                            // There's no way to avoid getting this if saveInstanceState has already been called.
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void onBackPressed() {
        ((MainActivity) mParentContext).pager.unlock();

        InputMethodManager imm = (InputMethodManager) mParentContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((MainActivity) mParentContext).getWindow().getDecorView().getWindowToken(), 0);

        try {
            isConverstaionFragmentCreated = false;
            Fragment fragment = getFragmentManager().findFragmentByTag("Conversation");
            if (fragment != null) {
                getFragmentManager().popBackStack();
            }


                mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_teacher));
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_teacher));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                ((MainActivity) mParentContext).pager.unlock();

                InputMethodManager imm = (InputMethodManager) mParentContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((MainActivity) mParentContext).getWindow().getDecorView().getWindowToken(), 0);

                try {
                    isConverstaionFragmentCreated = false;
                    Fragment fragment = getFragmentManager().findFragmentByTag("Conversation");
                    if (fragment != null) {
                        getFragmentManager().popBackStack();
                    }
                    mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_teacher));

                } catch (IllegalStateException ignored) {
                    // There's no way to avoid getting this if saveInstanceState has already been called.
                }
                break;
        }
        return false;
    }

    private void updateContacts() {
        listview.clear();
        int i = 0;
        for (TeacherEntity teacher : mApplication.mTeachers) {
            //Get the teacher Inbox entity
            final UserInbox userInbox = new UserInbox();
            userInbox.setTeacherEntity(teacher);

            // Init the card
            ContactListCard card = new ContactListCard(mParentContext);
            //Trim the . in the end
            String avatar = userInbox.getTeacherEntity().getAvatar();
            if (avatar.contains("jpg."))
                avatar = avatar.substring(0, avatar.lastIndexOf('.'));
            card.setContactAvatarUrl(avatar);
            card.setContactName(userInbox.getTeacherEntity().getName()); //Name

            //Classname
            QueryBuilder queryBuilder = mApplication.mDaoSession.getClassEntityDao().queryBuilder();
            List<ClassEntity> results = queryBuilder.where(ClassEntityDao.Properties.Classid.eq(userInbox.getTeacherEntity().getClassid())).list();
            ClassEntity classEntity = null;
            if (results.size() != 0) {
                classEntity = results.get(0);
                card.setSubtitle(classEntity.getName());
            }
            List<LastIMMessageEntity> lastIMs = mApplication.mDaoSession.getLastIMMessageEntityDao()
                    .queryBuilder()
                    .where(LastIMMessageEntityDao.Properties.Userid.eq(teacher.getTeacherid()))
                    .list();
            if (lastIMs.size() > 0) {
                card.setTimestamp(DateUtils.timelineTimestamp(lastIMs.get(0).getTimestamp(), mParentContext));
                if (lastIMs.get(0).getHasUnread().equals("1")) {
                    card.setHasUnread(true);
                } else {
                    card.setHasUnread(false);
                }
            }
            //Add the card
            card.setPosition(i);
            card.setOnClickListener(mOnClickListener);
            listview.add(card);
            i++;
        }
    }

    private void setUpChattingPageActionbar() {
            mainActivity.setActionBarTitle(userName);
    }

    @Override
    public void onMessageReceived() {
        Log.i("", "");
        mHandler.sendEmptyMessage(HandlerConstant.MSG_IM_RECEIVED);
    }

    @Subscribe
    public void onUserSwitchEvent(InfoSwitchedEvent event) {
            updateContacts();
    }

    private class MyConversationBehaviorListener implements RongIM.ConversationBehaviorListener {

        /**
         * 当点击用户头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param userInfo         被点击的用户的信息。
         * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            return false;
        }

        /**
         * 当长按用户头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param userInfo         被点击的用户的信息。
         * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            return false;
        }

        /**
         * 当点击消息时执行。
         *
         * @param context 上下文。
         * @param view    触发点击的 View。
         * @param message 被点击的消息的实体信息。
         * @return 如果用户自己处理了点击后的逻辑，则返回 true， 否则返回 false, false 走融云默认处理方式。
         */
        @Override
        public boolean onMessageClick(Context context, View view, Message message) {
            if (message.getContent() instanceof ImageMessage) {
                ArrayList<String> urls = new ArrayList<>();
                urls.add(((ImageMessage) message.getContent()).getRemoteUri().toString());
                Intent intent = new Intent(context, GalleryActivityUrl.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("fileUrls", urls);
                bundle.putInt("currentFile", 0);
                bundle.putBoolean("hasTitle", false);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
            return false;
        }

        /**
         * 当长按消息时执行。
         *
         * @param context 上下文。
         * @param view    触发点击的 View。
         * @param message 被长按的消息的实体信息。
         * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onMessageLongClick(Context context, View view, Message message) {
            return false;
        }

        /**
         * 当点击链接消息时执行。
         *
         * @param link 被点击的链接。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
         */
        @Override
        public boolean onMessageLinkClick(String link) {
            return false;
        }
    }

}
