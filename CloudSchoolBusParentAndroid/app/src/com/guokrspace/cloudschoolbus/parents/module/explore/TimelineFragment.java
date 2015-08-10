package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.widget.TimelineCard;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TimelineFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<MessageEntity> mMesageEntities = new ArrayList<>();
    private MaterialListView mMaterialListView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    final private static int MSG_ONREFRESH = 1;
    final private static int MSG_ONLOADMORE = 2;
    final private static int MSG_ONCACHE = 3;
    final private static int MSG_NOCHANGE = 4;
    private static final int MSG_NO_NETOWRK = 5;
    private static final int MSG_SERVER_ERROR = 6;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ONREFRESH:
                    hideWaitDialog();
                    InsertCardsAtBeginning();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_ONLOADMORE:
                    hideWaitDialog();
                    AppendCards();
                    break;
                case MSG_ONCACHE:
                    AppendCards();
                    break;
                case MSG_NOCHANGE:
                    hideWaitDialog();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_NO_NETOWRK:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case MSG_SERVER_ERROR:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
            }
            return false;
        }
    });

    // TODO: Rename and change types of parameters
    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimelineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_article_list, container, false);
        mMaterialListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                MessageEntity messageEntity = mMesageEntities.get(0);
                GetNewMessagesFromServer(messageEntity.getSendtime(), mHandler);
            }
        });

        mLayoutManager = (LinearLayoutManager) mMaterialListView.getLayoutManager();
        mMaterialListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean loading = true;

            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                //Log.d("Aing", "dx:" + dx + ", dy:" + dy);

                visibleItemCount = mMaterialListView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("...", "end called");
                    MessageEntity messageEntity = mMesageEntities.get(mMesageEntities.size() - 1);
                    GetOldMessagesFromServer(messageEntity.getSendtime(), mHandler);

                    loading = true;
                }
            }
        });

        GetMessagesFromCache(mHandler);

        if (mMesageEntities.size() == 0)
            GetLastestMessagesFromServer(mHandler);
        else {
            MessageEntity messageEntity = mMesageEntities.get(0);
            GetNewMessagesFromServer(messageEntity.getSendtime(),mHandler);
        }

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private void AppendCards() {
        for (int i = 0; i < mMesageEntities.size(); i++) {
            mMaterialListView.add(buildcard(mMesageEntities.get(i)));
        }
    }

    private void InsertCardsAtBeginning() {
        for (int i = mMesageEntities.size() - 1; i >= 0; i--) {
            mMaterialListView.addAtStart(buildcard(mMesageEntities.get(i)));
        }
    }

    //Get all Messages from cache
    private void GetMessagesFromCache(android.os.Handler handler) {
        final MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        mMesageEntities = messageEntityDao.queryBuilder().list();
        if (mMesageEntities.size() != 0)
            handler.sendEmptyMessage(MSG_ONCACHE);
    }

    public void animation(View v) {
        v.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        v.setAnimation(animation);
    }

    private TimelineCard buildcard(MessageEntity messageEntity) {
        TimelineCard card = new TimelineCard(mParentContext);
        String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
        card.setTeacherAvatarUrl(teacherAvatarString);
        card.setTeacherName(messageEntity.getSenderEntity().getName());
        card.setKindergarten(mApplication.mSchools.get(0).getName());
        card.setSentTime(messageEntity.getSendtime());

        card.setTitle(messageEntity.getTitle());
        card.setDescription(messageEntity.getDescription());

        List<String> pictureUrls = new ArrayList<>();
        for (int j = 0; j < messageEntity.getMessageBodyEntityList().size(); j++) {
//            pictureUrls.add(messageEntity.getMessageBodyEntityList().get(j).getContent());
              pictureUrls.add("http://apps.bdimg.com/developer/static/12261449/assets/v3/case_meitu.png");

        }
        card.setImageAdapter(new ImageAdapter(mParentContext, pictureUrls));

        final List<TagEntity> tagEntities = messageEntity.getTagEntityList();
        TagRecycleViewAdapter adapter = new TagRecycleViewAdapter(tagEntities);
        card.setTagAdapter(adapter);

        CommonRecyclerItemClickListener tagClickListener = new CommonRecyclerItemClickListener(mParentContext, new CommonRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                animation(view);
                SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager())
                        .setMessage(tagEntities.get(position).getTagnamedesc())
                        .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        card.setmOnItemSelectedListener(tagClickListener);

        View.OnClickListener shareButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        };
        card.setmShareButtonClickListener(shareButtonClickListener);

        return card;
    }
}