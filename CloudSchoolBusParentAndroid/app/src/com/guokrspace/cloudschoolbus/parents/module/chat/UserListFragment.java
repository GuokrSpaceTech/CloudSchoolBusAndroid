package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LastIMMessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.widget.TeacherListCard;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by macbook on 15-8-9.
 */
public class UserListFragment extends BaseFragment {

    private MaterialListView listview;
    private MainActivity mainActivity;
    private String teacherName;
    private String mCurrentClassid;
    private boolean mIsParent;

    public static UserListFragment newInstance() {
        UserListFragment f = new UserListFragment();
        return f;
    }

    public UserListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) mParentContext;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_teacher_list, container, false);
        listview = (MaterialListView) root.findViewById(R.id.material_listview);

        initData();

        setListener();

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    protected void setListener() {
        super.setListener();

        listview.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView view, int position) {

                MainActivity activity = (MainActivity) mParentContext;

                String mTargetID = "";
                if (!Version.PARENT) {
                    if (mIsParent) {
                        mTargetID = findParentsinClass(mCurrentClassid).get(position).getParentid();
                    } else {
                        mTargetID = findTeachersinClass(mCurrentClassid).get(position).getTeacherid();
                    }
                } else {
                    activity.setActionBarTitle(teacherName, getResources().getString(R.string.module_teacher));
                    teacherName = mApplication.mTeachers.get(position).getName();
                    mTargetID = mApplication.mTeachers.get(position).getId();
                }
                ConversationFragment fragment = new ConversationFragment();
                Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation").appendPath(io.rong.imlib.model.Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                        .appendQueryParameter("targetId", mTargetID).appendQueryParameter("title", "hello")
                        .build();
                fragment.setUri(uri);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, "Conversation");
                transaction.addToBackStack("Conversation");
                transaction.commit();
            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {

            }
        });

        if (RongIM.getInstance() != null) {
            /**
             * 接收未读消息的监听器。
             *
             * @param listener          接收所有未读消息消息的监听器。
             */
            RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new MyReceiveUnreadCountChangedListener());

            /**
             * 设置接收未读消息的监听器。
             *
             * @param listener          接收未读消息消息的监听器。
             * @param conversationTypes 接收指定会话类型的未读消息数。
             */
            RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new MyReceiveUnreadCountChangedListener(), Conversation.ConversationType.PRIVATE);
        }
    }

    private void initData() {
        List<?> teachers = (Version.PARENT == true) ? (mApplication.mTeachers) : (mApplication.mTeachersT);

        if (Version.PARENT)
            for (int i = 0; i < teachers.size(); i++) {
                //Get the teacher Inbox entity
                final UserInbox userInbox = new UserInbox();
                userInbox.setTeacherEntity(mApplication.mTeachers.get(i));

            /*
             * Init the card
             */
                TeacherListCard card = new TeacherListCard(mParentContext);
                card.setTeacherAvatarUrl(userInbox.getTeacherEntity().getAvatar()); //Avatar
                card.setTeacherName(userInbox.getTeacherEntity().getName()); //Name
                //Classname
                QueryBuilder queryBuilder = mApplication.mDaoSession.getClassEntityDao().queryBuilder();
                List<ClassEntity> results = queryBuilder.where(ClassEntityDao.Properties.Classid.eq(userInbox.getTeacherEntity().getClassid())).list();
                ClassEntity classEntity = null;
                if (results.size() != 0) {
                    classEntity = results.get(0);
                    card.setClassname(classEntity.getName());
                }

                //Lastmessage timestamp
                List<LastIMMessageEntity> lastIMs = mApplication.mDaoSession.getLastIMMessageEntityDao().queryBuilder().list();
                for (int j = 0; j < lastIMs.size(); j++) {
                    if (lastIMs.get(j).getTeacherid().equals(userInbox.getTeacherEntity().getId())) {
                        card.setTimestamp(DateUtils.timelineTimestamp(lastIMs.get(j).getTimestamp(), mParentContext));
                    }
                }

                //Add the card
                listview.add(card);
            }
        else {

            mCurrentClassid = findMyClass().get(0).getClassid();
            mIsParent = true;
            selectContacts(mCurrentClassid, mIsParent);
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

        if (!Version.PARENT) {
            inflater.inflate(R.menu.menu_contacts, menu);

            SpinnerAdapter mSpinnerAdapter = new ClassSpinnerAdapter(mParentContext, findMyClass());

            ActionBar.OnNavigationListener mOnNavgationListener = new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int i, long l) {
                    mCurrentClassid = findMyClass().get(i).getClassid();
                    selectContacts(mCurrentClassid, mIsParent);
                    return false;
                }
            };
            mainActivity.getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavgationListener);
            mainActivity.getSupportActionBar().setTitle("");
        } else {
            mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_teacher));
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_teacher:
                mIsParent = false;
                selectContacts(mCurrentClassid, mIsParent);
                break;
            case R.id.action_parents:
                mIsParent = true;
                selectContacts(mCurrentClassid, mIsParent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void selectContacts(String classid, boolean mIsParent) {
        if (mIsParent) {
            listview.clear();
            List<ParentEntityT> parents = findParentsinClass(classid);

            for (ParentEntityT parent : parents) {
                TeacherListCard card = new TeacherListCard(mParentContext); //This card can be teacher or parents
                card.setTeacherAvatarUrl(parent.getAvatar()); //Avatar
                card.setTeacherName(parent.getNikename()); //Name

                //Find the kids
                listview.add(card);
            }

        } else {
            listview.clear();
            List<TeacherEntityT> teacherList = findTeachersinClass(classid);
            for (TeacherEntityT teacher : teacherList) {
                TeacherListCard card = new TeacherListCard(mParentContext); //This card can be teacher or parents
                card.setTeacherAvatarUrl(teacher.getAvatar()); //Avatar
                card.setTeacherName(teacher.getRealname()); //Name

                //Find the kids
                listview.add(card);
            }
        }

    }

    /**
     * 接收未读消息的监听器。
     */
    private class MyReceiveUnreadCountChangedListener implements RongIM.OnReceiveUnreadCountChangedListener {

        /**
         * @param count 未读消息数。
         */
        @Override
        public void onMessageIncreased(int count) {
            Log.i("", "" + count);
//            MainActivity theActivty =  (MainActivity)mParentContext;
//            theActivty.getBadgeViews().get(1).setText(Integer.toString(count));
        }
    }


}
