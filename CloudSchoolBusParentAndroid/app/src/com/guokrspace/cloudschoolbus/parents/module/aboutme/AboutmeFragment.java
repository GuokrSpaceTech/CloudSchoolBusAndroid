package com.guokrspace.cloudschoolbus.parents.module.aboutme;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by wangjianfeng on 15/8/13.
 */
public class AboutmeFragment extends BaseFragment {
    static String ARG_IPCPARAM = "ipcparam";
    private Ipcparam mIpcparam;
    private ImageView imageViewAvatar;
    private TextView  textViewChildName;
    private Button  buttonKindergarten;
    private LinearLayout layoutChildSetting;
    private LinearLayout layoutSystemSetting;
    private LinearLayout layoutHelpFeedback;
    private Button logoutButton;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case HandlerConstant.MSG_ONREFRESH:
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_ONLOADMORE:
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_ONCACHE:
                    break;
                case HandlerConstant.MSG_NOCHANGE:
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_NO_NETOWRK:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_SERVER_ERROR:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
            }
            return false;
        }
    });

    public static AboutmeFragment newInstance()
    {
        AboutmeFragment fragment = new AboutmeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AboutmeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mIpcparam = (Ipcparam) getArguments().get(ARG_IPCPARAM);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_aboutme, container, false);
        layoutChildSetting = (LinearLayout)root.findViewById(R.id.linearLayoutChildSetting);
        layoutSystemSetting = (LinearLayout)root.findViewById(R.id.linearLayoutSystemSetting);
        layoutHelpFeedback = (LinearLayout)root.findViewById(R.id.linearLayoutHelp);
        logoutButton = (Button)root.findViewById(R.id.logoutButton);
        imageViewAvatar = (ImageView)root.findViewById(R.id.child_avatar);
        textViewChildName = (TextView)root.findViewById(R.id.child_name);
        buttonKindergarten = (Button)root.findViewById(R.id.kindergarten_name);

        /* Get the current child's avatar */
        StudentEntity studentEntity = null;
        String avatar = "";
        for (int i = 0; i < mApplication.mStudents.size(); i++) {
            studentEntity = mApplication.mStudents.get(i);
            if(!studentEntity.getStudentid().equals(mApplication.mConfig.getUserid()))
            {
                avatar = studentEntity.getAvatar();
                avatar = "http://cloud.yunxiaoche.com/images/teacher.jpg";
                Picasso.with(mParentContext).load(avatar).into(imageViewAvatar);
                break;
            }
        }

        if(studentEntity.getNikename()==null)
            textViewChildName.setText(studentEntity.getCnname());
        else
            textViewChildName.setText(studentEntity.getNikename());

        buttonKindergarten.setText(mApplication.mSchools.get(0).getName());

        setListeners();

        return root;
    }

    private void setListeners()
    {
        layoutChildSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildSettingFragment fragment = new ChildSettingFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
