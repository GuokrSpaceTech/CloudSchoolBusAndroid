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
import com.guokrspace.cloudschoolbus.parents.base.include.Constant;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;

/**
 * Created by wangjianfeng on 15/8/13.
 */
public class SettingFragment extends BaseFragment {
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
                case Constant.MSG_ONREFRESH:
                    hideWaitDialog();
                    break;
                case Constant.MSG_ONLOADMORE:
                    hideWaitDialog();
                    break;
                case Constant.MSG_ONCACHE:
                    break;
                case Constant.MSG_NOCHANGE:
                    hideWaitDialog();
                    break;
                case Constant.MSG_NO_NETOWRK:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case Constant.MSG_SERVER_ERROR:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
            }
            return false;
        }
    });

    public static SettingFragment newInstance(Ipcparam ipcparam)
    {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_IPCPARAM, ipcparam);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingFragment() {
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

        setListeners();

        return super.onCreateView(inflater, container, savedInstanceState);
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
