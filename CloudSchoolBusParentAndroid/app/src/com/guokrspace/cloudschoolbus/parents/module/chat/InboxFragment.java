package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.support.debug.DebugLog;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LastLetterEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LastLetterEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.entity.LatestLetter;
import com.guokrspace.cloudschoolbus.parents.event.TeacherSelectEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.guokrspace.cloudschoolbus.parents.widget.LastLetterCard;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kai on 12/27/14.
 */
public class InboxFragment extends BaseFragment {
    private MaterialListView mMaterialListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<LastLetterEntity> mLastLetters;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerConstant.MSG_ONREFRESH:
                    hideWaitDialog();
                    RefreshCards();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case HandlerConstant.MSG_ONCACHE:
                    RefreshCards();
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


    public static InboxFragment newInstance(String param1, String param2) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putString("", param1);
        args.putString("", param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InboxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        init_data();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_inbox_list, container, false);
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

            }
        });

        return root;
    }

    void init_data()
    {
        getLastestLettersFromCache();

        getLastestLettersFromServer();
    }

    private void getLastestLettersFromCache()
    {
        LastLetterEntityDao lastLetterEntityDao = mApplication.mDaoSession.getLastLetterEntityDao();
        mLastLetters = lastLetterEntityDao.queryBuilder().list();

        mHandler.sendEmptyMessage(HandlerConstant.MSG_ONCACHE);
    }

    private void getLastestLettersFromServer()
    {
        if (!networkStatusEvent.isNetworkConnected()) {
            mHandler.sendEmptyMessage(HandlerConstant.MSG_NO_NETOWRK);
            return;
        }
//        showWaitDialog("", null);

        HashMap<String, String> params = new HashMap<String, String>();
        CloudSchoolBusRestClient.get(ProtocolDef.METHOD_latestchat, params, new JsonHttpResponseHandler() {

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
                if (!retCode.equals("1")) {
                    mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                    return;
                }

                List<LatestLetter> letters = com.android.support.fastjson.FastJsonTools.getListObject(response.toString(), LatestLetter.class);
                for (int i = 0; i < letters.size(); i++) {
                    LatestLetter letter = letters.get(i);
                    LastLetterEntity lastLetterEntity = new LastLetterEntity(letter.getTeacherid(),letter.getLastchat(),letter.getPicture());
                    LastLetterEntityDao lastLetterEntityDao = mApplication.mDaoSession.getLastLetterEntityDao();
                    lastLetterEntityDao.insertOrReplace(lastLetterEntity);
                }
                mHandler.sendEmptyMessage(HandlerConstant.MSG_ONREFRESH);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
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
                    mHandler.sendEmptyMessage(HandlerConstant.MSG_NOCHANGE);
                }
            }

        });
    }

    private void RefreshCards()
    {
        mMaterialListView.clear();
        for(int i=0; i<mLastLetters.size(); i++)
        {
            LastLetterEntity lastLetter = mLastLetters.get(i);

            LastLetterCard card = new LastLetterCard(mParentContext);

            card.setTeacherAvatarUrl("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
            card.setTimestamp(System.currentTimeMillis()/1000 + "");
            String teacherName = "";
            for(int j=0; j<mApplication.mTeachers.size(); j++) {
                if(mApplication.mTeachers.get(j).getId().equals(lastLetter.getTeacherid())) {
                    teacherName = mApplication.mTeachers.get(j).getName();
                    break;
                }
            }
            card.setTeacherName(teacherName);
            card.setChatMessage(lastLetter.getLastchat());
            mMaterialListView.add(card);
            mMaterialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(CardItemView view, int position) {
                    DebugLog.logI(String.format("%d", position));
                    TeacherEntity teacher = mApplication.mTeachers.get(position);
                    TeacherMessageBoxFragment teacherMessageBoxFragment = TeacherMessageBoxFragment.newInstance(teacher);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inbox_container_layout, teacherMessageBoxFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                @Override
                public void onItemLongClick(CardItemView view, int position) {

                }
            });
         }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MainActivity activity = (MainActivity)getActivity();
        activity.invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.inboxfragmentmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_teacher_list:
                TeacherListDialogFragment dialog = new TeacherListDialogFragment();
                dialog.show(getFragmentManager(), "TeacherList");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe public void onTeacherSelect(TeacherSelectEvent event)
    {
        TeacherEntity teacher = new TeacherEntity(event.getId(),event.getDuty(),event.getAvatar(),event.getName(),event.getClassid());
        TeacherMessageBoxFragment teacherMessageBoxFragment = TeacherMessageBoxFragment.newInstance(teacher);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.inbox_container_layout, teacherMessageBoxFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
