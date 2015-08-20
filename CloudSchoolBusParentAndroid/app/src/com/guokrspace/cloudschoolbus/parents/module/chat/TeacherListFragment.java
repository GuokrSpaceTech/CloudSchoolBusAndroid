package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.widget.TeacherListCard;
import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import io.rong.imkit.fragment.ConversationFragment;

/**
 * Created by macbook on 15-8-9.
 */
public class TeacherListFragment extends BaseFragment {

    private MaterialListView listview;
    private MainActivity mainActivity;
    private String teacherName;

    public static TeacherListFragment newInstance()
    {
        TeacherListFragment f = new TeacherListFragment();
        return f;
    }

    public TeacherListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) mParentContext;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_teacher_list,container,false);
        listview = (MaterialListView)root.findViewById(R.id.material_listview);

        initData();

        setListener();

        return root;
    }

    @Override
    protected void setListener() {
        super.setListener();

        listview.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView view, int position) {

                teacherName = mApplication.mTeachers.get(position).getName();

                String mTargetID = "47582";
                mTargetID = mApplication.mTeachers.get(position).getId();
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

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Log.i("TeacherListFragment", "back stack changed ");
                int backCount = getFragmentManager().getBackStackEntryCount();
                // First Level of Fragment, no Homeasup Arrow, with bottoms Tabs
                if (backCount == 0) {
                    // block where back has been pressed. since backstack is zero.
                    mainActivity.getTabs().setVisibility(View.VISIBLE);
                    mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    View view = mainActivity.getSupportActionBar().getCustomView();
                    TextView textView = (TextView) view.findViewById(R.id.abs_layout_titleTextView);
                    textView.setText(getResources().getString(R.string.module_teacher));
                }
                // Next Level of Fragment(Conversation), has Homeasup Arrow, without bottom Tabs
                if (backCount > 0)
                {
                    mainActivity.getTabs().setVisibility(View.GONE);
                    mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    View view = mainActivity.getSupportActionBar().getCustomView();
                    TextView textView = (TextView) view.findViewById(R.id.abs_layout_titleTextView);
                    textView.setText(teacherName);
                }
            }
        });
    }

    private void initData()
    {
        for(int i=0; i<mApplication.mTeachers.size(); i++)
        {
            final TeacherInbox teacherInbox = new TeacherInbox();
            teacherInbox.setTeacherEntity(mApplication.mTeachers.get(i));
            for(int j=0;j<mMesageEntities.size();j++)
            {
            }

            TeacherListCard card = new TeacherListCard(mParentContext);
            card.setTeacherAvatarUrl(teacherInbox.getTeacherEntity().getAvatar());
            card.setTeacherName(teacherInbox.getTeacherEntity().getName());

            QueryBuilder queryBuilder = mApplication.mDaoSession.getClassEntityDao().queryBuilder();
            List<ClassEntity> results = queryBuilder.where(ClassEntityDao.Properties.Classid.eq(teacherInbox.getTeacherEntity().getClassid())).list();
            ClassEntity classEntity = null;
            if(results.size()!=0) {
                classEntity = results.get(0);
                card.setClassname(classEntity.getName());
            }



            listview.add(card);
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

//    @Produce
//    public TeacherSelectEvent ProduceTeacherSelectChatEvent(TeacherSelectEvent teacherSelectEvent){
//        return teacherSelectEvent;
//    }
}
