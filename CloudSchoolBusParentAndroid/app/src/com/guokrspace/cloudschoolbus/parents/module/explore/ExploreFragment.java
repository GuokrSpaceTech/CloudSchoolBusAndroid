package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

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
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.event.ImReadyEvent;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ExploreFragment extends BaseFragment {
    private OnFragmentInteractionListener mListener;

    private MaterialListView mMaterialListView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mCurrentChild;
    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            BusProvider.getInstance().post(new ImReadyEvent());

            switch (msg.what) {
                case HandlerConstant.MSG_ONREFRESH:
                    clearAppBadgetCount(mParentContext);
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
                    clearAppBadgetCount(mParentContext);
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
//                        errorMsg = getResources().getString(R.string.server_error);
                    }

//                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(errorMsg)
//                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
//                    hideWaitDialog();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_timeline, container, false);

        mMaterialListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        mLayoutManager = (LinearLayoutManager) mMaterialListView.getLayoutManager();

        ((MainActivity) mParentContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        ((MainActivity) mParentContext).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        setActionBarTitle(getResources().getString(R.string.module_explore),"");
//        ((MainActivity) mParentContext).getSupportActionBar().setTitle("");

        if (Version.DEBUG) {
            ClearCache();
            GetLastestMessagesFromServer(mHandler);
        } else {
            GetMessagesFromCache();
            if(mMesageEntities.size()>0) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_ONCACHE);
                MessageEntity messageEntity = mMesageEntities.get(0);
                GetNewMessagesFromServer(messageEntity.getMessageid(), mHandler);
            } else if (mMesageEntities.size() == 0)
                GetLastestMessagesFromServer(mHandler);
        }

        setHasOptionsMenu(true);

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
                        GetNewMessagesFromServer(messageEntity.getMessageid(), mHandler);
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
                    if(mMesageEntities.size()>0) {
                        MessageEntity messageEntity = mMesageEntities.get(mMesageEntities.size() - 1);
                        GetOldMessagesFromServer(messageEntity.getMessageid(), mHandler);
                        loading = true;
                    }
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

    private void AddCards() {
        mMaterialListView.clear();
        for (int i = 0; i < mMesageEntities.size(); i++) {
            Card theCard = (Card) buildcard(mMesageEntities.get(i), i);
            if (theCard != null)
                mMaterialListView.add(theCard);
        }
    }

    public void filterCards(String type) {
        MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
        List<MessageEntity> messageEntityList;
        if (type.equals("All")) {
            messageEntityList = queryBuilder.orderDesc(MessageEntityDao.Properties.Messageid).list();
        } else {
            messageEntityList = queryBuilder.where(MessageEntityDao.Properties.Apptype.eq(type))
                    .orderDesc(MessageEntityDao.Properties.Messageid).list();
        }
        mMaterialListView.clear();
        for (int i = 0; i < messageEntityList.size(); i++) {
            Card theCard = (Card) buildcard(messageEntityList.get(i), i);
            if (theCard != null)
                mMaterialListView.add(theCard);
        }
    }

    public void filterCardsChild(String studentid) {
        MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
        List<MessageEntity> messageEntityList;
        messageEntityList = queryBuilder.where(MessageEntityDao.Properties.Studentid.eq(studentid))
                    .orderDesc(MessageEntityDao.Properties.Messageid).list();
        mMaterialListView.clear();
        for (int i = 0; i < messageEntityList.size(); i++) {
            Card theCard = (Card) buildcard(messageEntityList.get(i), i);
            if (theCard != null)
                mMaterialListView.add(theCard);
        }
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
        } else if (messageEntity.getApptype().equals("Active") | messageEntity.getApptype().equals("Event"))
            return BuildActivityCard(messageEntity, mHandler);
        else {
            SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.unknow_message))
                    .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
            return null;
        }
    }

    private String messageType(MessageEntity msg) {
        return msg.getApptype();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notice:
                filterCards("Notice");
                setActionBarTitle(getResources().getString(R.string.noticetype),getResources().getString(R.string.module_explore));
                break;
            case R.id.action_attendance:
                filterCards("Punch");
                setActionBarTitle(getResources().getString(R.string.attendancetype), getResources().getString(R.string.module_explore));
                break;
            case R.id.action_schedule:
                filterCards("Schedule");
                setActionBarTitle(getResources().getString(R.string.schedule), getResources().getString(R.string.module_explore));
                break;
            case R.id.action_report:
                setActionBarTitle(getResources().getString(R.string.report), getResources().getString(R.string.module_explore));
                filterCards("Report");
                break;
            case R.id.action_food:
                filterCards("Food");
                setActionBarTitle(getResources().getString(R.string.food), getResources().getString(R.string.module_explore));
                break;
            case R.id.action_streaming:
                filterCards("OpenClass");
                setActionBarTitle(getResources().getString(R.string.openclass), getResources().getString(R.string.module_explore));
                break;
            case R.id.action_picture:
                filterCards("Article");
                setActionBarTitle(getResources().getString(R.string.picturetype), getResources().getString(R.string.module_explore));
                break;
            case R.id.action_activity:
                filterCards("Active");
                setActionBarTitle(getResources().getString(R.string.activity), getResources().getString(R.string.module_explore));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title, String preTitle)
    {
        if(Version.PARENT) {
            MainActivity mainActivity = (MainActivity) mParentContext;
            View view = mainActivity.getSupportActionBar().getCustomView();
            TextView textView = (TextView) view.findViewById(R.id.abs_layout_titleTextView);
            textView.setText(title);
            mainActivity.mCurrentTitle = title;
            mainActivity.mUpperLeverTitle = preTitle;
        }
    }

    @Subscribe
    public void onChildrenSwitched(InfoSwitchedEvent event)
    {
        mCurrentChild = event.getCurrentChild();
        String studentId = mApplication.mStudents.get(mCurrentChild).getStudentid();
        filterCardsChild(studentId);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(menu != null)
        {
            setOverflowIconVisible(menu);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(Version.PARENT)
            inflater.inflate(R.menu.main, menu);
        else
            inflater.inflate(R.menu.main_teacher, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setOverflowIconVisible(Menu menu)
    {
        try
        {
            Class clazz=Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
            Field field=clazz.getDeclaredField("mOptionalIconsVisible");
            if(field!=null)
            {
                field.setAccessible(true);
                field.set(menu, true);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void clearAppBadgetCount(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("cloudschoolbuspref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("unreadmessages", 0);
        editor.commit();
        ShortcutBadger.with(context).remove();
    }
}