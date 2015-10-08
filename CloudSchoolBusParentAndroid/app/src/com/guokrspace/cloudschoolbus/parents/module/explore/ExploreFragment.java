package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
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

import com.android.support.fastjson.FastJsonTools;
import com.android.support.utils.DateUtils;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.MenuSpinnerAdapter;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.ServerInteractions;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.fragment.WebviewFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.entity.ActivityBody;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceRecord;
import com.guokrspace.cloudschoolbus.parents.entity.Food;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.guokrspace.cloudschoolbus.parents.entity.NoticeBody;
import com.guokrspace.cloudschoolbus.parents.entity.Schedule;
import com.guokrspace.cloudschoolbus.parents.entity.StudentReport;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.event.ImReadyEvent;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.StreamingChannelsFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.adapter.ImageAdapter;
import com.guokrspace.cloudschoolbus.parents.module.explore.adapter.TagRecycleViewAdapter;
import com.guokrspace.cloudschoolbus.parents.widget.ActivityCard;
import com.guokrspace.cloudschoolbus.parents.widget.AttendanceRecordCard;
import com.guokrspace.cloudschoolbus.parents.widget.FoodNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.NoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.PictureCard;
import com.guokrspace.cloudschoolbus.parents.widget.ReportListCard;
import com.guokrspace.cloudschoolbus.parents.widget.ScheduleNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.StreamingNoticeCard;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
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

    private ArrayList<MenuSpinnerAdapter.MessageType> mMessageTypes = new ArrayList<>();


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            BusProvider.getInstance().post(new ImReadyEvent());

            switch (msg.what) {
                case HandlerConstant.MSG_ONREFRESH:
                    clearAppBadgetCount(mParentContext);
                    setActionBarTitle(getResources().getString(R.string.module_explore), getResources().getString(R.string.module_explore));
                    AddCards();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case HandlerConstant.MSG_ONLOADMORE:
                    hideWaitDialog();
                    setActionBarTitle(getResources().getString(R.string.module_explore), getResources().getString(R.string.module_explore));
                    AddCards();
                    break;
                case HandlerConstant.MSG_ONCACHE:
                    setActionBarTitle(getResources().getString(R.string.module_explore), getResources().getString(R.string.module_explore));
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
                    String messageid = (String)button.getTag();
                    List<MessageEntity> messages = mApplication.mDaoSession.getMessageEntityDao()
                            .queryBuilder().where(MessageEntityDao.Properties.Messageid.eq(messageid))
                            .list();
                    if(messages.size()>0) {
                        messages.get(0).setIsconfirm("2");
                        mApplication.mDaoSession.getMessageEntityDao().update(messages.get(0));
                    }
                    //Update the memory
                    int i = mMesageEntities.indexOf(messages.get(0));
                    mMesageEntities.get(i).setIsconfirm("2");
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

        if (Version.DEBUG) {
            ClearCache();
            ServerInteractions.getInstance().GetLastestMessagesFromServer(mHandler);
            mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
        } else {
            ServerInteractions.getInstance().GetMessagesFromCache();
            if(mMesageEntities.size()>0) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_ONCACHE);
                MessageEntity messageEntity = mMesageEntities.get(0);
                ServerInteractions.getInstance().GetNewMessagesFromServer(messageEntity.getMessageid(), mHandler);
                mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
            } else if (mMesageEntities.size() == 0) {
                ServerInteractions.getInstance().GetLastestMessagesFromServer(mHandler);
                mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
            }
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
                    ServerInteractions.getInstance().GetLastestMessagesFromServer(mHandler);
                    mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
                } else {
                    if (mMesageEntities.size() > 0) {
                        MessageEntity messageEntity = mMesageEntities.get(0);
                        ServerInteractions.getInstance().GetNewMessagesFromServer(messageEntity.getMessageid(), mHandler);
                        mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
                    } else {
                        ServerInteractions.getInstance().GetLastestMessagesFromServer(mHandler);
                        mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
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
                    if (mMesageEntities.size() > 0) {
                        MessageEntity messageEntity = mMesageEntities.get(mMesageEntities.size() - 1);
                        ServerInteractions.getInstance().GetOldMessagesFromServer(messageEntity.getMessageid(), mHandler);
                        mMesageEntities = ServerInteractions.getInstance().getmMesageEntities();
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
        if(Version.PARENT) {
            String studentId = mApplication.mStudents.get(mCurrentChild).getStudentid();
            filterCardsChild(studentId);
        }
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
        MainActivity mainActivity = (MainActivity)mParentContext;

        if(Version.PARENT) {
            mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.module_explore));
            inflater.inflate(R.menu.main, menu);
        }else {

            inflater.inflate(R.menu.main_teacher, menu);

            //Setup the spinner menu
            initMessageTypes();

            MenuSpinnerAdapter mSpinnerAdapter = new MenuSpinnerAdapter(mParentContext, mMessageTypes);
            mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            mainActivity.getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int i, long l) {
                    filterCards(mMessageTypes.get(i).messageType);
                    return false;
                }
            });
            mainActivity.getSupportActionBar().setTitle("");
        }
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

    private void initMessageTypes()
    {
        String[]  messageTypes = {"All","Article", "Notice", "Event", "Punch", "Report", "OpenClass", "Food", "Schedule"};
        Integer[] resIcon = {0, R.drawable.ic_picture, R.drawable.ic_notice, R.drawable.ic_event, R.drawable.ic_attendance,
                R.drawable.ic_report, R.drawable.ic_streaming, R.drawable.ic_food, R.drawable.ic_schedule};
        Integer[]  descriptions = {R.string.all, R.string.picture, R.string.noticetype, R.string.activity, R.string.attendancetype,
                R.string.report, R.string.openclass, R.string.food, R.string.schedule};

        int i=0;
        mMessageTypes.clear();
        for(String type:messageTypes) {
            MenuSpinnerAdapter.MessageType messageType = new MenuSpinnerAdapter.MessageType();
            messageType.messageType = type;
            messageType.description = getResources().getString(descriptions[i]);
            messageType.iconRes = resIcon[i];
            mMessageTypes.add(messageType);
            i++;
        }
    }

    public void showShare() {
        ShareSDK.initSDK(mParentContext);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//		oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(mParentContext);
    }

    public String cardType(String type)
    {
        String cardtype = "";

        if(type.equals("Article"))
            cardtype = getResources().getString(R.string.picturetype);
        if(type.equals("Notice"))
            cardtype = getResources().getString(R.string.noticetype);
        else if(type.equals("Punch"))
            cardtype = getResources().getString(R.string.attendancetype);
        else if(type.equals("OpenClass"))
            cardtype = getResources().getString(R.string.openclass);
        else if(type.equals("Report"))
            cardtype = getResources().getString(R.string.report);
        else if(type.equals("Food"))
            cardtype = getResources().getString(R.string.food);
        else if(type.equals("Schedule"))
            cardtype = getResources().getString(R.string.schedule);
        else if(type.equals("Active"))
            cardtype = getResources().getString(R.string.activity);
        return cardtype;
    }

    public PictureCard buildArticleCard(MessageEntity message) {
        PictureCard card = new PictureCard(mParentContext);
        String teacherAvatarString = message.getSenderEntity().getAvatar();
        card.setTeacherAvatarUrl(teacherAvatarString);
        card.setTeacherName(message.getSenderEntity().getName());
        card.setKindergarten(mApplication.mSchools.get(0).getName());
        card.setCardType(cardType(message.getApptype()));
        card.setSentTime(message.getSendtime());
        card.setTitle(message.getTitle());
        card.setDescription(message.getDescription());
        List<String> pictureUrls = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(message.getBody());
            pictureUrls = FastJsonTools.getListObject(jsonObject.get("PList").toString(), String.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(pictureUrls!=null)
            card.setImageAdapter(new ImageAdapter(mParentContext, pictureUrls));
        final List<TagEntity> tagEntities = message.getTagEntityList();
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

    public NoticeCard BuildNoticeCard(final MessageEntity messageEntity, final Handler handler)
    {
        NoticeCard noticeCard = new NoticeCard(mParentContext);
        String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
        noticeCard.setTeacherAvatarUrl(teacherAvatarString);
        noticeCard.setTeacherName(messageEntity.getSenderEntity().getName());
        noticeCard.setClassName(messageEntity.getSenderEntity().getClassname());
        noticeCard.setCardType(cardType(messageEntity.getApptype()));
        noticeCard.setSentTime(messageEntity.getSendtime());
        noticeCard.setIsNeedConfirm(messageEntity.getIsconfirm());
        noticeCard.setTitle(messageEntity.getTitle());
        noticeCard.setDescription(messageEntity.getDescription());
        NoticeBody noticeBody = FastJsonTools.getObject(messageEntity.getBody(), NoticeBody.class);
        if (noticeBody != null) noticeCard.setDrawable(noticeBody.getPList().get(0));
        noticeCard.setmConfirmButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerInteractions.getInstance().UserConfirm(messageEntity.getMessageid(), (Button) view, handler);
            }
        });
        return noticeCard;
    }

    public ActivityCard BuildActivityCard(final MessageEntity messageEntity, final Handler handler)
    {
        ActivityCard theCard = new ActivityCard(mParentContext);
        String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
        theCard.setTeacherAvatarUrl(teacherAvatarString);
        theCard.setTeacherName(messageEntity.getSenderEntity().getName());
        theCard.setClassName(messageEntity.getSenderEntity().getClassname());
        theCard.setCardType(cardType(messageEntity.getApptype()));
        theCard.setSentTime(messageEntity.getSendtime());
        theCard.setIsNeedConfirm(messageEntity.getIsconfirm());
        theCard.setTitle(messageEntity.getTitle());
        theCard.setDescription(messageEntity.getDescription());
        ActivityBody messageBody = FastJsonTools.getObject(messageEntity.getBody(), ActivityBody.class);
        if (messageBody != null) theCard.setDrawable(messageBody.getPList().get(0));
        theCard.setmConfirmButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerInteractions.getInstance().UserConfirm(messageEntity.getMessageid(), (Button) view, handler);
            }
        });
        return theCard;
    }

    public AttendanceRecordCard BuildAttendanceCard(MessageEntity messageEntity)
    {
        AttendanceRecordCard attendanceRecordCard = new AttendanceRecordCard(mParentContext);
        String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
        attendanceRecordCard.setTeacherAvatarUrl(teacherAvatarString);
        attendanceRecordCard.setTeacherName(messageEntity.getSenderEntity().getName());
        attendanceRecordCard.setClassName(messageEntity.getSenderEntity().getClassname());
        attendanceRecordCard.setCardType(cardType(messageEntity.getApptype()));
        attendanceRecordCard.setSentTime(messageEntity.getSendtime());
        String messageBody = messageEntity.getBody();
        AttendanceRecord attendanceRecord = FastJsonTools.getObject(messageBody, AttendanceRecord.class);
        attendanceRecordCard.setRecordTime(attendanceRecord.getPunchtime().toString());
        attendanceRecordCard.setDrawable(attendanceRecord.getPicture());
        attendanceRecordCard.setDescription(attendanceRecord.getPunchtime());
        return attendanceRecordCard;
    }

    public StreamingNoticeCard BuildStreamingNoticeCard(MessageEntity messageEntity)
    {
        StreamingNoticeCard streamingNoticeCard = new StreamingNoticeCard(mParentContext);
        streamingNoticeCard.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
        streamingNoticeCard.setKindergartenName(messageEntity.getSenderEntity().getName());
        streamingNoticeCard.setClassName(messageEntity.getSenderEntity().getClassname());
        streamingNoticeCard.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(), mParentContext));
        streamingNoticeCard.setCardType(cardType(messageEntity.getApptype()));
        streamingNoticeCard.setContext(mParentContext);
        streamingNoticeCard.setDescription(messageEntity.getDescription());
        String messageBody = messageEntity.getBody();
        final Ipcparam ipcpara = FastJsonTools.getObject(messageBody, Ipcparam.class);
        streamingNoticeCard.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)mParentContext;
                mainActivity.setActionBarTitle(getResources().getString(R.string.openclass), getResources().getString(R.string.module_explore));
                StreamingChannelsFragment fragment = StreamingChannelsFragment.newInstance(ipcpara);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return streamingNoticeCard;
    }

    public ReportListCard BuildReportListCard(final MessageEntity messageEntity)
    {
        ReportListCard reportListCard = new ReportListCard(mParentContext);
        String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
        reportListCard.setTeacherAvatarUrl(teacherAvatarString);
        reportListCard.setTeacherName(messageEntity.getSenderEntity().getName());
        reportListCard.setClassName(messageEntity.getSenderEntity().getClassname());
        reportListCard.setCardType(cardType(messageEntity.getApptype()));
        reportListCard.setSentTime(messageEntity.getSendtime());
        reportListCard.setReporttype(messageEntity.getTitle());
        String messageBody = messageEntity.getBody();
        final StudentReport studentReport = FastJsonTools.getObject(messageBody, StudentReport.class);
        reportListCard.setReporttype(studentReport.getReportType());
        reportListCard.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebviewFragment theFragment = WebviewFragment.newInstance(studentReport.getReportUrl(), getResources().getString(R.string.report),"");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, theFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return reportListCard;
    }

    public FoodNoticeCard BuildFoodNoticeCard(final MessageEntity messageEntity)
    {
        FoodNoticeCard card = new FoodNoticeCard(mParentContext);
        card.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
        card.setKindergartenName(messageEntity.getSenderEntity().getName());
        card.setClassName(messageEntity.getSenderEntity().getClassname());
        card.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(), mParentContext));
        card.setCardType(cardType(messageEntity.getApptype()));
        card.setContext(mParentContext);
        card.setDescription(messageEntity.getDescription());
        Food food = FastJsonTools.getObject(messageEntity.getBody(), Food.class);
        final String foodUrl = food.getUrl();
        card.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebviewFragment fragment = WebviewFragment.newInstance(foodUrl, getResources().getString(R.string.food),"");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return card;
    }

    public ScheduleNoticeCard BuildScheduleNoticeCard(final MessageEntity messageEntity)
    {
        ScheduleNoticeCard card = new ScheduleNoticeCard(mParentContext);
        card.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
        card.setKindergartenName(messageEntity.getSenderEntity().getName());
        card.setClassName(messageEntity.getSenderEntity().getClassname());
        card.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(), mParentContext));
        card.setCardType(cardType(messageEntity.getApptype()));
        card.setContext(mParentContext);
        card.setDescription(messageEntity.getDescription());
        Schedule schedule = FastJsonTools.getObject(messageEntity.getBody(), Schedule.class);
        final String scheduleUrl = schedule.getUrl();
        card.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebviewFragment fragment = WebviewFragment.newInstance(scheduleUrl, getResources().getString(R.string.schedule),"");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return card;
    }
}