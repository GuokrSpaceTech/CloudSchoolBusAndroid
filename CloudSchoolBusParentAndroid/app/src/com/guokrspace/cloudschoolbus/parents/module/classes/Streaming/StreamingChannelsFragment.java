package com.guokrspace.cloudschoolbus.parents.module.classes.Streaming;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;

/**
 * Created by Kai on 15/8/13.
 */
public class StreamingChannelsFragment extends BaseFragment {

    static String ARG_IPCPARAM = "ipcparam";
    private Ipcparam mIpcparam;
    private MaterialListView materialListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
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

    public static StreamingChannelsFragment newInstance(Ipcparam ipcparam)
    {
        StreamingChannelsFragment fragment = new StreamingChannelsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_IPCPARAM, ipcparam);
        fragment.setArguments(args);
        return fragment;
    }

    public StreamingChannelsFragment() {
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

        View root = inflater.inflate(R.layout.activity_streaming_channle_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.swipeRefreshLayout);
        materialListView = (MaterialListView)root.findViewById(R.id.material_listview);

        setListeners();

        buildCards();

        setHasOptionsMenu(true);

        return root;
    }

    private void setListeners()
    {
        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView view, int position) {
                Intent intent = new Intent(mParentContext, Preview.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ipcparams", mIpcparam);
                intent.putExtras(bundle);
                intent.putExtra("id", position); //Which camera
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {
            }
        });
    }

    private void buildCards()
    {
        for(int i=0; i<mIpcparam.getChannels().size(); i++)
        {
            Ipcparam.Channel channel = mIpcparam.getChannels().get(i);
            SmallImageCard smallImageCard = new SmallImageCard(mParentContext);
            smallImageCard.setDescription(getResources().getString(R.string.realtime_streaming));
            smallImageCard.setTitle(channel.getChanneldesc());
            smallImageCard.setDrawable(getResources().getDrawable(R.drawable.ic_image_default));
            materialListView.add(smallImageCard);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }
}
