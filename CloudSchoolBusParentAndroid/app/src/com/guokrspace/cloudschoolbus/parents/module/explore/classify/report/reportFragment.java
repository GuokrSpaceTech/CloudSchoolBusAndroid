package com.guokrspace.cloudschoolbus.parents.module.explore.classify.report;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.support.debug.DebugLog;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ReportEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ReportEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ReportItemEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ReportItemEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.Report;
import com.guokrspace.cloudschoolbus.parents.entity.ReportItem;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.widget.ReportListCard;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by wangjianfeng on 15/7/26.
 */
public class ReportFragment extends BaseFragment {
    List<ReportEntity> mReportEntities = new ArrayList<>();
    MaterialListView materialListView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager mLayoutManager;

    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    final private static int MSG_ONREFRESH = 1;
    final private static int MSG_ONLOADMORE = 2;
    final private static int MSG_ONCACHE = 3;
    final private static int MSG_NOCHANGE = 4;
    final private static int REFRESH = 3;
    final private static int LOADMORE = 4;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ONREFRESH:
                    addCardTop();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_ONLOADMORE:
                    addCardBottom();
                    break;
                case MSG_ONCACHE:
                    addCardTop();
                    break;
                case MSG_NOCHANGE:
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
            return false;
        }
    });


    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        GetReportsFromCache();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_report_list, container, false);
        materialListView = (MaterialListView)root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.swipeRefreshLayout);
        mLayoutManager = (LinearLayoutManager) materialListView.getLayoutManager();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetReportsFromCache();
            }
        });
        materialListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean loading = true;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = materialListView.getChildCount();
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

                    // Do something
//                    new LoadTask(MainActivity.this, start).execute();

                    loading = true;
                }
            }
        });
        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView view, int position) {
                DebugLog.logI(String.format("%d", position));
                ReportEntity reportEntity = mReportEntities.get(position);
//                TeacherMessageBoxFragment teacherMessageBoxFragment = TeacherMessageBoxFragment.newInstance(reportEntity);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.inbox_container_layout, teacherMessageBoxFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {

            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void setListener(View view) {
        super.setListener(view);
    }

    //Get all articles from cache
    private void GetReportsFromCache() {
        final ReportEntityDao reportEntityDao = mApplication.mDaoSession.getReportEntityDao();
        mReportEntities = reportEntityDao.queryBuilder().list();
        if (mReportEntities.size() != 0) {
            mHandler.sendEmptyMessage(MSG_ONCACHE);
            GetReportsFromServer("0", mReportEntities.get(0).getCreatetime(), REFRESH); //Get the all the new reports
        } else
            GetReportsFromServer("0", "0", REFRESH); //Get the latest reports
    }

    private void GetReportsFromServer(final String starttime, final String endtime, final int userOps) {
        final ReportEntityDao reportEntityDao = mApplication.mDaoSession.getReportEntityDao();
        final ReportItemEntityDao reportItemEntityDao = mApplication.mDaoSession.getReportItemEntityDao();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("starttime", starttime);
        params.put("endtime", endtime);

        CloudSchoolBusRestClient.get("report", params, new JsonHttpResponseHandler() {
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
                mReportEntities.clear();
                List<Report> reports = FastJsonTools.getListObject(response.toString(), Report.class);
                for (int i = 0; i < reports.size(); i++) {
                    Report report = reports.get(i);
                    ReportEntity reportEntity = new ReportEntity(
                            report.getId(),
                            report.getTitle(),
                            report.getCnname(),
                            report.getReportname(),
                            report.getStudentlist(),
                            report.getReporttime(),
                            report.getCreatetime(),
                            report.getType(),
                            report.getAdduserid(),
                            report.getTeachername(),
                            report.getStudentlistid(),
                            report.getStudentname()
                    );
                    reportEntityDao.insertOrReplace(reportEntity);
                    mReportEntities.add(reportEntity);

                    for (int j = 0; j < report.getContent().size(); j++) {
                        ReportItem reportItem = report.getContent().get(j);
                        ReportItemEntity reportItemEntity = new ReportItemEntity(
                                reportItem.getTitle(),
                                reportItem.getAnswer(),
                                report.getId());
                        reportItemEntityDao.insertOrReplace(reportItemEntity);
                    }
                }

                if(mReportEntities.size()!=0 && userOps==REFRESH)
                {
                    mHandler.sendEmptyMessage(MSG_ONREFRESH);
                } else if (mReportEntities.size()!=0 && userOps==LOADMORE) {
                    mHandler.sendEmptyMessage(MSG_ONLOADMORE);
                } else
                    mHandler.sendEmptyMessage(MSG_NOCHANGE);


                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mHandler.sendEmptyMessage(MSG_NOCHANGE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                mHandler.sendEmptyMessage(MSG_NOCHANGE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mHandler.sendEmptyMessage(MSG_NOCHANGE);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void addCardTop()
    {
        for(int i=0; i< mReportEntities.size(); i++)
        {
            ReportListCard card = new ReportListCard(mParentContext);
            card.setReporttype(mReportEntities.get(i).getReportname());
            card.setTimestamp(mReportEntities.get(i).getCreatetime());
            materialListView.add(card);
        }
    }

    private void addCardBottom()
    {
        for(int i=mReportEntities.size()-1; i>-1; i++)
        {
            ReportListCard card = new ReportListCard(mParentContext);
            card.setReporttype(mReportEntities.get(i).getReportname());
            card.setTimestamp(mReportEntities.get(i).getCreatetime());
            materialListView.add(card);
        }
    }
}
