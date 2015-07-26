package com.guokrspace.cloudschoolbus.parents.module.messages;

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
import android.widget.AdapterView;

import com.android.support.debug.DebugLog;
import com.dexafree.materialList.cards.SmallCircleImageCard;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.entity.Teacher;

import java.util.List;

/**
 * Created by kai on 12/27/14.
 */
public class InboxFragment extends BaseFragment {
    private MaterialListView mMaterialListView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private List<Teacher> mTeacherList;
    private final int NONETWORK = -2;
    private final int GOTMESSAGE = 1;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                default:
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

                    // Do something
//                    new LoadTask(MainActivity.this, start).execute();

                    loading = true;
                }
            }
        });

        if(mTeacherList!=null)
        {
            for(int i=0; i<mTeacherList.size(); i++)
            {
                SmallCircleImageCard card = new SmallCircleImageCard(mParentContext);
                card.setTitle(mTeacherList.get(i).getTeachername());
                card.setDescription(mTeacherList.get(i).getTeachername());
                card.setDrawable("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
                mMaterialListView.add(card);
                mMaterialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(CardItemView view, int position) {
                        DebugLog.logI(String.format("%d", position));
                        Teacher teacher = mTeacherList.get(position);
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

        return root;
    }

    void init_data()
    {
        getTeacherList();
    }

    private void getTeacherList()
    {
            mTeacherList = mApplication.mBaseInfo.getTeacherlist();
    }
}
