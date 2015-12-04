package com.guokrspace.cloudschoolbus.teacher.module.chat;

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
import com.guokrspace.cloudschoolbus.teacher.MainActivity;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.base.DataWrapper;
import com.guokrspace.cloudschoolbus.teacher.base.RongCloudEvent;
import com.guokrspace.cloudschoolbus.teacher.base.activity.GalleryActivityUrl;
import com.guokrspace.cloudschoolbus.teacher.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.teacher.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.teacher.base.include.Version;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.LastIMMessageEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.LastIMMessageEntityDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.teacher.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.teacher.widget.ContactListCard;
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
    private String mCurrentClassid;
    private boolean mIsParent;
    private View rootView;
    private Menu mMenu;
    private boolean isInMiddleOfUpdateView;
    private boolean isConverstaionFragmentCreated;

    private Handler mHandler;

    {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HandlerConstant.MSG_IM_RECEIVED:

                        if (mainActivity != null)
                            mainActivity.setBadge(2, 0);

                        if (isInMiddleOfUpdateView) break;

                        if (!mFragment.isVisible()) break;

                        selectContacts(mCurrentClassid, mIsParent);
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
                if (mIsParent) {
                    mTargetID = DataWrapper.getInstance().findParentsinClass(mCurrentClassid).get(position).getParentid();
                    userName = DataWrapper.getInstance().findParentsinClass(mCurrentClassid).get(position).getNikename();
                } else {
                    mTargetID = DataWrapper.getInstance().findTeachersinClass(mCurrentClassid).get(position).getTeacherid();
                    userName = DataWrapper.getInstance().findTeachersinClass(mCurrentClassid).get(position).getNickname();
                }

                //setupTeacherActionBar
                setupTeacherActionBar(userName);

                //Lock the swipe for the second level page
                ((MainActivity) mParentContext).pager.lock();
                fragment = new ConversationFragment();
                isConverstaionFragmentCreated = true;

                //Hide the option menu
                mMenu.setGroupVisible(0,false);

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

    @Override
    public void onDetach() {
        isConverstaionFragmentCreated = false;
        super.onDetach();
    }


    private void initView(View v) {
        mCurrentClassid = DataWrapper.getInstance().findCurrentClass(mApplication.mConfig.getCurrentuser()).getClassid();
        mIsParent = true;
        selectContacts(mCurrentClassid, mIsParent);
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

                            setupTeacherActionBar("");

                        } catch (IllegalStateException ignored) {
                            // There's no way to avoid getting this if saveInstanceState has already been called.
                        }
                    }
                    return false;
                }
            });
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
        mMenu = menu;
        inflater.inflate(R.menu.menu_contacts, menu);
        mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//            SpinnerAdapter mSpinnerAdapter = new ClassSpinnerAdapter(mParentContext, DataWrapper.getInstance().findMyClass());
//            ActionBar.OnNavigationListener mOnNavgationListener = new ActionBar.OnNavigationListener() {
//                @Override
//                public boolean onNavigationItemSelected(int i, long l) {
//                    mCurrentClassid = DataWrapper.getInstance().findMyClass().get(i).getClassid();
//                    selectContacts(mCurrentClassid, mIsParent);
//                    return false;
//                }
//            };
////            mainActivity.getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavgationListener);
        setupTeacherActionBar("");
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

                    setupTeacherActionBar("");
                } catch (IllegalStateException ignored) {
                    // There's no way to avoid getting this if saveInstanceState has already been called.
                }

                //Unhide the option menu
                mMenu.setGroupVisible(0,true);

                break;
            case R.id.action_teacher:
                mIsParent = false;
                selectContacts(mCurrentClassid, mIsParent);
                setupTeacherActionBar("");
                break;
            case R.id.action_parents:
                mIsParent = true;
                selectContacts(mCurrentClassid, mIsParent);
                setupTeacherActionBar("");
                break;
        }

        return false;
    }


    //Select contact group either parents or teachers
    private void selectContacts(String classid, boolean mIsParent) {

        isInMiddleOfUpdateView = true;
        if (mIsParent) {
            listview.clear();
            List<ParentEntityT> parents = DataWrapper.getInstance().findParentsinClass(classid);

            int i = 0;
            for (ParentEntityT parent : parents) {
                ContactListCard card = new ContactListCard(mParentContext); //This card can be teacher or parents
                List<StudentEntityT> students = DataWrapper.getInstance().findStudentInCurrentClassForParent(parent, classid);
                //Find the student in this class
                if (students.size() > 0) {
                    card.setContactAvatarUrl(students.get(0).getAvatar()); //Avatar
                }
                card.setContactName(parent.getNikename()); //Name
                card.setSubtitle(generateStudentsNameSting(DataWrapper.getInstance().findStudentsOfParents(parent)));
                card.setPhonenumber(parent.getMobile());
                List<LastIMMessageEntity> lastIMs = mApplication.mDaoSession.getLastIMMessageEntityDao()
                        .queryBuilder()
                        .where(LastIMMessageEntityDao.Properties.Userid.eq(parent.getParentid()))
                        .list();

                if (lastIMs.size() > 0) {
                    card.setTimestamp(DateUtils.timelineTimestamp(lastIMs.get(0).getTimestamp(), mParentContext));
                    if (lastIMs.get(0).getHasUnread().equals("1")) {
                        card.setHasUnread(true);
                    } else {
                        card.setHasUnread(false);
                    }
                }
                card.setPosition(i);
                card.setOnClickListener(mOnClickListener);
                listview.add(card);
                i++;
            }
        } else {
            listview.clear();
            List<TeacherEntityT> teacherList = DataWrapper.getInstance().findTeachersinClass(classid);
            int i = 0;
            for (TeacherEntityT teacher : teacherList) {
                ContactListCard card = new ContactListCard(mParentContext); //This card can be teacher or parents
                card.setContactAvatarUrl(teacher.getAvatar()); //Avatar
                card.setContactName(teacher.getRealname()); //Name
                card.setSubtitle(DataWrapper.getInstance().findCurrentClass().getClassname());
                card.setPhonenumber(teacher.getMobile());
                card.setPosition(i);
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
                card.setOnClickListener(mOnClickListener);
                listview.add(card);
                i++;
            }
        }
        isInMiddleOfUpdateView = false;
    }

    private void setupTeacherActionBar(String username) {
        //Keycode Back cause 2 times of KeyCode Events
//        if(!mMenu.hasVisibleItems()) {
//            mainActivity.getMenuInflater().inflate(R.menu.menu_contacts, mMenu);
//            if (DataWrapper.getInstance().findMyClass().size() > 1) {
//                mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//                SpinnerAdapter mSpinnerAdapter = new ClassSpinnerAdapter(mParentContext, DataWrapper.getInstance().findMyClass());
//
//                ActionBar.OnNavigationListener mOnNavgationListener = new ActionBar.OnNavigationListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(int i, long l) {
//                        mCurrentClassid = DataWrapper.getInstance().findMyClass().get(i).getClassid();
//                        selectContacts(mCurrentClassid, mIsParent);
//                        return false;
//                    }
//                };
//                mainActivity.getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavgationListener);
//                mainActivity.getSupportActionBar().setTitle("");
//            } else {
//                mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if (username != null && !username.isEmpty())
            mainActivity.getSupportActionBar().setTitle(username);
        else if (mIsParent)
            mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_parents));
        else
            mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_teacher));
//            }
//        }
    }

    @Override
    public void onMessageReceived() {
        Log.i("", "");
        mHandler.sendEmptyMessage(HandlerConstant.MSG_IM_RECEIVED);
    }

    @Subscribe
    public void onUserSwitchEvent(InfoSwitchedEvent event) {
        mCurrentClassid = DataWrapper.getInstance().findCurrentClass(event.getCurrentChild()).getClassid();
        mIsParent = true;
        selectContacts(mCurrentClassid, mIsParent);
        setupTeacherActionBar("");
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


    private String generateStudentsNameSting(ArrayList<StudentEntityT> students) {
        String retString = "";
        for (StudentEntityT student : students) {
            retString += student.getCnname() + ",";
        }

        retString = retString.substring(0, retString.lastIndexOf(',')) + getResources().getString(R.string.parents);

        return retString;
    }
}
