package com.guokrspace.cloudschoolbus.parents.module.aboutme;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.squareup.picasso.Picasso;

/**
 * Created by wangjianfeng on 15/8/13.
 */
public class ChildSettingFragment extends BaseFragment {
    static String ARG_IPCPARAM = "ipcparam";
    private Ipcparam mIpcparam;
    private ImageView imageViewAvatar;
    private LinearLayout layoutName;
    private LinearLayout layoutPhone;
    private LinearLayout layoutRelation;
    private Button switchChildButton;


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

    public static ChildSettingFragment newInstance(Ipcparam ipcparam)
    {
        ChildSettingFragment fragment = new ChildSettingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_IPCPARAM, ipcparam);
        fragment.setArguments(args);
        return fragment;
    }

    public ChildSettingFragment() {
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

        View root = inflater.inflate(R.layout.activity_setting, container, false);
        layoutName = (LinearLayout)root.findViewById(R.id.linearLayoutName);
        layoutPhone = (LinearLayout)root.findViewById(R.id.linearLayoutPhone);
        layoutRelation = (LinearLayout)root.findViewById(R.id.linearLayoutRelation);
        imageViewAvatar = (ImageView)root.findViewById(R.id.imageViewAvatar);

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

        setListeners();

        return root;
    }

    private void setListeners()
    {

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
