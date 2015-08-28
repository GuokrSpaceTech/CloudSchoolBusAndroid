package com.guokrspace.cloudschoolbus.parents.module.explore.classify.notice;

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
import android.widget.Button;

import com.android.support.fastjson.FastJsonTools;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.entity.NoticeBody;
import com.guokrspace.cloudschoolbus.parents.widget.NoticeCard;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NoticeFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<MessageEntity> mNoticeEntities = new ArrayList<MessageEntity>();
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ONREFRESH:
                    addCards();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_ONLOADMORE:
                case MSG_ONCACHE:
                    addCards();
                    break;
                case MSG_NOCHANGE:
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
            }
            return false;
        }
    });

    // TODO: Rename and change types of parameters
    public static NoticeFragment newInstance(String param1, String param2) {
        NoticeFragment fragment = new NoticeFragment();
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
    public NoticeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

//        ((MainActivity)mParentContext).getmOptionMenuItem().setVisible(false);
        ((MainActivity)mParentContext).setTitle(getResources().getString(R.string.noticetype));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_notice_list, container, false);
        mMaterialListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                MessageEntity noticeEntity = mNoticeEntities.get(0);
                String endtime = noticeEntity.getSendtime();
                GetNewMessagesFromServer(endtime, mHandler);
            }
        });

        mLayoutManager = (LinearLayoutManager) mMaterialListView.getLayoutManager();
        mMaterialListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean loading = true;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                    MessageEntity noticeEntity = mNoticeEntities.get(mNoticeEntities.size() - 1);
                    String starttime = noticeEntity.getSendtime();
                    GetOldMessagesFromServer(starttime, mHandler);

                    // Do something
//                    new LoadTask(MainActivity.this, start).execute();

                    loading = true;
                }
            }
        });



        GetNoticesFromCache();

//        if (mNoticeEntities.size() == 0)
//            GetLastestMessagesFromServer(mHandler);
//        else {
//            SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager())
//                    .setMessage(getResources().getString(R.string.no_message))
//                    .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
//        }
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


    //Get all articles from cache
    private void GetNoticesFromCache() {
        MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
        mNoticeEntities = (ArrayList<MessageEntity>)queryBuilder.where(MessageEntityDao.Properties.Apptype.eq("Notice")).list();

        if (mNoticeEntities.size() != 0)
            mHandler.sendEmptyMessage(MSG_ONCACHE);
    }

    private void addCards() {
        for (int i = 0; i < mNoticeEntities.size(); i++) {
            MessageEntity noticeEntity = mNoticeEntities.get(i);
            if(noticeEntity.getApptype().equals("Notice")) {
                NoticeCard card = BuildCard(noticeEntity);
                mMaterialListView.add(card);
            }
        }
    }

    private NoticeCard BuildCard(final MessageEntity noticeEntity) {
        NoticeCard noticeCard = new NoticeCard(mParentContext);
        String teacherAvatarString = noticeEntity.getSenderEntity().getAvatar();
        noticeCard.setTeacherAvatarUrl(teacherAvatarString);
        noticeCard.setTeacherName(noticeEntity.getSenderEntity().getName());
        noticeCard.setClassName(noticeEntity.getSenderEntity().getClassname());
        noticeCard.setCardType(cardType(noticeEntity.getApptype()));
        noticeCard.setSentTime(noticeEntity.getSendtime());
        noticeCard.setIsNeedConfirm(noticeEntity.getIsconfirm());
        noticeCard.setIsNeedConfirm("1");
        noticeCard.setTitle(noticeEntity.getTitle());
        noticeCard.setDescription(noticeEntity.getDescription());
        NoticeBody noticeBody = FastJsonTools.getObject(noticeEntity.getBody(), NoticeBody.class);
        if (noticeBody != null) noticeCard.setDrawable(noticeBody.getPList().get(0));
        noticeCard.setmConfirmButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoticeConfirm(noticeEntity.getMessageid(), (Button) view, mHandler);
            }
        });
        return noticeCard;
    }
}