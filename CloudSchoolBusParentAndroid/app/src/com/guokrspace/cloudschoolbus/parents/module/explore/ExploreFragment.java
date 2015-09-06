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
import android.widget.Button;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;

import org.json.JSONObject;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ExploreFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MaterialListView mMaterialListView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case HandlerConstant.MSG_ONREFRESH:
                    AddCards();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case HandlerConstant.MSG_ONLOADMORE:
                    hideWaitDialog();
                    AddCards();
                    break;
                case HandlerConstant.MSG_ONCACHE:
                    hideWaitDialog();
                    AddCards();
                    break;
                case HandlerConstant.MSG_NOCHANGE:
                    hideWaitDialog();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case HandlerConstant.MSG_NO_NETOWRK:
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_SERVER_ERROR:
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    String errorMsg;
                    if(Version.DEBUG) {
                        if (msg.obj instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) msg.obj;
                            errorMsg = jsonObject.toString();
                        } else if (msg.obj instanceof Throwable) {
                            Throwable throwable = (Throwable) msg.obj;
                            errorMsg = throwable.getMessage();
                        } else {
                            errorMsg = getResources().getString(R.string.server_error);
                        }
                    } else {
                        errorMsg = getResources().getString(R.string.server_error);
                    }

                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(errorMsg)
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_CONFIRM_OK:
                    hideWaitDialog();
                    Button button = (Button) msg.obj;
                    button.setText(getResources().getString(R.string.confirmed_notice));
                    button.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    button.setEnabled(false);
                    break;
            }
            return false;
        }
    });

    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (Version.DEBUG) {
            ClearCache();
            GetLastestMessagesFromServer(mHandler);
        } else {
            GetMessagesFromCache(mHandler);

            if (mMesageEntities.size() == 0)
                GetLastestMessagesFromServer(mHandler);
            else {
                MessageEntity messageEntity = mMesageEntities.get(0);
                GetNewMessagesFromServer(messageEntity.getSendtime(), mHandler);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_timeline, container, false);

        mMaterialListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        mLayoutManager = (LinearLayoutManager) mMaterialListView.getLayoutManager();

        MainActivity mainActivity = (MainActivity) mParentContext;
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setListeners();

        return root;
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Version.DEBUG) {
                    ClearCache();
                    GetLastestMessagesFromServer(mHandler);
                } else {
                    if (mMesageEntities.size() > 0) {
                        MessageEntity messageEntity = mMesageEntities.get(0);
                        GetNewMessagesFromServer(messageEntity.getSendtime(), mHandler);
                    } else {
                        GetLastestMessagesFromServer(mHandler);
                    }
                }
            }
        });

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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
        public void onFragmentInteraction(String id);
    }

//    private void AppendCards() {
//        for (int i = 0; i < mMesageEntities.size(); i++) {
//            mMaterialListView.add((Card) buildcard(mMesageEntities.get(i)));
//        }
//    }

    private void AddCards() {
        for (int i = 0; i < mMesageEntities.size(); i++) {
            Card theCard = (Card) buildcard(mMesageEntities.get(i), i);
            if (theCard != null)
                mMaterialListView.add(theCard);
        }
    }

//    private void InsertCardsAtBeginning() {
//        for (int i = mMesageEntities.size() - 1; i >= 0; i--) {
//            mMaterialListView.addAtStart((Card) buildcard(mMesageEntities.get(i)));
//        }
//    }

    //Get all Messages from cache
    private void GetMessagesFromCache(android.os.Handler handler) {
        final MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        mMesageEntities = messageEntityDao.queryBuilder().list();
        if (mMesageEntities.size() != 0)
            handler.sendEmptyMessage(HandlerConstant.MSG_ONCACHE);
    }

    private void ClearCache() {
        final MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        messageEntityDao.deleteAll();
    }

    public void animation(View v) {
        v.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        v.setAnimation(animation);
    }

    private Object buildcard(final MessageEntity messageEntity, final int position_in_list) {
        if (messageType(messageEntity).equals("Article")) {
            return buildArticleCard(messageEntity);
        } else if (messageEntity.getApptype().equals("Notice")) {
            return BuildNoticeCard(messageEntity, mHandler);
        } else if (messageEntity.getApptype().equals("Punch")) {
            return BuildAttendanceCard(messageEntity);
        } else if (messageEntity.getApptype().equals("OpenClass")) {
            return BuildStreamingNoticeCard(messageEntity);
        } else if (messageEntity.getApptype().equals("Report")) {
            return BuildReportListCard(messageEntity);
        } else if (messageEntity.getApptype().equals("Food")) {
            return BuildFoodNoticeCard(messageEntity);
        } else if (messageEntity.getApptype().equals("Schedule")) {
            return BuildScheduleNoticeCard(messageEntity);
        } else {
            SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.unknow_message))
                    .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
        }

        return null;
    }

    private String messageType(MessageEntity msg) {
        return msg.getApptype();
    }
}