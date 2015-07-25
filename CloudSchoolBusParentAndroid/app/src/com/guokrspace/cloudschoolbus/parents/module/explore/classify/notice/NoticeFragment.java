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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.dexafree.materialList.cards.NoticeCard;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.NoticeEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.NoticeEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.NoticeImageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.NoticeImageEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.Notice;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    private ArrayList<NoticeEntity> mNoticeEntities = new ArrayList<NoticeEntity>();
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
                    InsertCardsAtBeginning();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_ONLOADMORE:
                    AppendCards();
                    break;
                case MSG_ONCACHE:
                    AppendCards();
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
                NoticeEntity noticeEntity = mNoticeEntities.get(0);
                String endtime = noticeEntity.getAddtime();
                UpdateNoticesCacheForward(endtime);
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
                    NoticeEntity noticeEntity = mNoticeEntities.get(mNoticeEntities.size() - 1);
                    String starttime = noticeEntity.getAddtime();
                    UpdateNoticesCacheDownward(starttime);

                    // Do something
//                    new LoadTask(MainActivity.this, start).execute();

                    loading = true;
                }
            }
        });


        GetNoticesFromCache();

        if (mNoticeEntities.size() == 0)
            GetLasteNoticesFromServer();
        else {
            NoticeEntity noticeEntity = mNoticeEntities.get(0);
            String endtime = noticeEntity.getAddtime();
            UpdateNoticesCacheForward(endtime);
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


    //Get all articles from cache
    private void GetNoticesFromCache() {
        final NoticeEntityDao noticeEntityDao = mApplication.mDaoSession.getNoticeEntityDao();
        mNoticeEntities = (ArrayList<NoticeEntity>) noticeEntityDao.queryBuilder().list();
        if (mNoticeEntities.size() != 0)
            mHandler.sendEmptyMessage(MSG_ONCACHE);
    }

    //Get all articles from newest in Cache to newest in Server
    private void UpdateNoticesCacheForward(String endtime) {
        GetNoticesFromServer("0", endtime);
    }

    //Get the older 20 articles from server then update the cache
    private void UpdateNoticesCacheDownward(String starttime) {
        GetNoticesFromServer(starttime, "0");
    }

    //Get Lastest 20 Articles from server, only used when there is no cache
    private void GetLasteNoticesFromServer() {
        GetNoticesFromServer("0", "0");
    }

    private void GetNoticesFromServer(final String starttime, final String endtime) {
        final NoticeEntityDao noticeEntityDao = mApplication.mDaoSession.getNoticeEntityDao();
        final NoticeImageEntityDao noticeImageEntityDao = mApplication.mDaoSession.getNoticeImageEntityDao();


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("starttime", starttime);
        params.put("endtime", endtime);

        CloudSchoolBusRestClient.get("notice", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String retCode = "";
                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }
                if (retCode != "1") {
                    // Errro Handling
                }
                List<Notice> noticeList = FastJsonTools.getListObject(response.toString(), Notice.class);
                for (int i = 0; i < noticeList.size(); i++) {
                    Notice notice = noticeList.get(i);
                    NoticeEntity noticeEntity = new NoticeEntity(
                            notice.noticekey,
                            notice.noticeid,
                            notice.noticetitle,
                            notice.noticecontent,
                            notice.addtime,
                            notice.isteacher,
                            notice.isconfirm,
                            notice.haveisconfirm
                    );
                    noticeEntityDao.insertOrReplace(noticeEntity);

                    for (int j = 0; j < notice.plist.size(); j++) {
                        Notice.PList imageFile = notice.plist.get(j);
                        NoticeImageEntity noticeImageEntity = new NoticeImageEntity(
                                imageFile.source,
                                imageFile.filename,
                                imageFile.iscloud,
                                notice.noticekey);
                        noticeImageEntityDao.insertOrReplace(noticeImageEntity);
                    }
                }

                //Update mArticleEntities
                String start = noticeList.get(0).addtime;
                String end = noticeList.get(noticeList.size() - 1).addtime;
                QueryBuilder queryBuilder = noticeEntityDao.queryBuilder();
                queryBuilder.where(NoticeEntityDao.Properties.Addtime.between(end, start));
                mNoticeEntities = (ArrayList<NoticeEntity>) queryBuilder.list();

                if (starttime.equals("0") && endtime.equals("0"))
                    mHandler.sendEmptyMessage(MSG_ONREFRESH);
                else if (endtime.equals("0"))
                    mHandler.sendEmptyMessage(MSG_ONLOADMORE);
                else if (starttime.equals("0"))
                    mHandler.sendEmptyMessage(MSG_ONREFRESH);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                String retCode = "";
                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }
                if (retCode != "-2") {
                    // No New Records are found
                    mHandler.sendEmptyMessage(MSG_NOCHANGE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    private void AppendCards() {
        for (int i = 0; i < mNoticeEntities.size(); i++) {
            NoticeEntity noticeEntity = mNoticeEntities.get(i);
            NoticeCard card = new NoticeCard(mParentContext);
            card.setTeacherAvatarUrl("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
            card.setTeacherName("小花老师");
            card.setKindergarten("星星幼儿园");
            card.setSentTime(noticeEntity.getAddtime());

            card.setTitle(noticeEntity.getNoticetitle() + "Title");
            card.setDescription(noticeEntity.getNoticecontent() + "Test Content: this is a content...");
            if (noticeEntity.getNoticeImages().size() > 0)
                card.setDrawable("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
                //card.setDrawable(noticeEntity.getNoticeImages().get(0).getSource());


            View.OnClickListener confirmButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                    animation(v);
                }
            };
            card.setmConfirmButtonClickListener(confirmButtonClickListener);

            mMaterialListView.add(card);
        }
    }

    private void InsertCardsAtBeginning() {
        for (int i = mNoticeEntities.size() - 1; i >= 0; i--) {
            NoticeEntity noticeEntity = mNoticeEntities.get(i);
            NoticeCard card = new NoticeCard(mParentContext);
            card.setTeacherAvatarUrl("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
            card.setTeacherName("小花老师");
            card.setKindergarten("星星幼儿园");
            card.setSentTime(noticeEntity.getAddtime());

            card.setTitle(noticeEntity.getNoticetitle() + "Title");
            card.setDescription(noticeEntity.getNoticecontent() + "Test Content: this is a content...");
            if (noticeEntity.getNoticeImages().size() > 0)
                card.setDrawable(noticeEntity.getNoticeImages().get(0).getSource());

            View.OnClickListener confirmButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                    animation(v);
                }
            };
            card.setmConfirmButtonClickListener(confirmButtonClickListener);

            mMaterialListView.addAtStart(card);
        }
    }


    public void animation(View v) {
        v.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        v.setAnimation(animation);
    }
}