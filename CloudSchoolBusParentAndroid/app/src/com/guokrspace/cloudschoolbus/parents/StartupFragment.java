package com.guokrspace.cloudschoolbus.parents;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.event.ImReadyEvent;
import com.squareup.otto.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartupFragment extends BaseFragment implements Handler.Callback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isAppReady = false;
    private Handler mHandler;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartupFragment newInstance(String param1, String param2) {
        StartupFragment fragment = new StartupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StartupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mHandler = new Handler(this);

        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        if(isAppReady) {
            getFragmentManager().popBackStackImmediate();
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity mainActivity = (MainActivity)mParentContext;
        mainActivity.getSupportActionBar().hide();

        View root = inflater.inflate(R.layout.fragment_startup, container, false);

        final ImageView mImgBackgroud = (ImageView)root.findViewById(R.id.imageView2);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(mParentContext, R.anim.translate_anim);
                mImgBackgroud.startAnimation(animation);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe
    public void onReceiveImReadyEvent(ImReadyEvent event)
    {
        isAppReady = true;
        MainActivity mainActivity = (MainActivity)mParentContext;
        if( mainActivity!=null )
            mainActivity.getSupportActionBar().show();
        try {
            getFragmentManager().popBackStackImmediate();
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }
}
