package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.TeacherSelectEvent;
import com.guokrspace.cloudschoolbus.parents.widget.TeacherListCard;
import com.squareup.otto.Produce;

import java.util.List;

/**
 * Created by macbook on 15-8-9.
 */
public class TeacherListDialogFragment extends DialogFragment {

    private MaterialListView listview;
    private OnCompleteListener mListener;
    CloudSchoolBusParentsApplication application;

    public TeacherListDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_teacher_list,container,false);
        listview = (MaterialListView)root.findViewById(R.id.material_listview);

        application = (CloudSchoolBusParentsApplication) getActivity().getApplication();
        for(int i=0;i<application.mTeachers.size();i++)
        {
            TeacherEntity teacherEntity = application.mTeachers.get(i);
            TeacherListCard card = new TeacherListCard(getActivity().getBaseContext());
            List<ClassEntity> classes = application.mDaoSession.getClassEntityDao().queryBuilder()
                    .where(ClassEntityDao.Properties.Classid.eq(teacherEntity.getClassid())).list();
            String className = classes.get(0).getName();
            card.setClassname(className);
            card.setContext(getActivity().getBaseContext());
            card.setTeacherName(teacherEntity.getName());
            card.setTeacherAvatarUrl("http://apps.bdimg.com/developer/static/12261449/assets/v3/case_meitu.png");
        }

        listview.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView view, int position) {
                TeacherSelectEvent event = new TeacherSelectEvent();
                TeacherEntity teacher = application.mTeachers.get(position);
                event.setId(teacher.getId());
                event.setDuty(teacher.getDuty());
                event.setAvatar(teacher.getAvatar());
                event.setDuty(teacher.getDuty());
                BusProvider.getInstance().post(ProduceTeacherSelectChatEvent(event));
            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {

            }
        });
        return root;
    }

    public static TeacherListDialogFragment newInstance()
    {
        TeacherListDialogFragment f = new TeacherListDialogFragment();
        return f;
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    @Produce
    public TeacherSelectEvent ProduceTeacherSelectChatEvent(TeacherSelectEvent teacherSelectEvent){
        return teacherSelectEvent;
    }
}
