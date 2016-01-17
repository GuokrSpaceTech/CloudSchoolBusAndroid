package com.guokrspace.cloudschoolbus.teacher.module.explore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

import com.android.support.fastjson.FastJsonTools;
import com.android.support.utils.DateUtils;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.teacher.MainActivity;
import com.guokrspace.cloudschoolbus.teacher.MenuSpinnerAdapter;
import com.guokrspace.cloudschoolbus.teacher.base.DataWrapper;
import com.guokrspace.cloudschoolbus.teacher.base.ServerInteractions;
import com.guokrspace.cloudschoolbus.teacher.base.activity.GalleryActivityUrl;
import com.guokrspace.cloudschoolbus.teacher.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.teacher.base.fragment.WebviewFragment;
import com.guokrspace.cloudschoolbus.teacher.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.teacher.base.include.Version;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TagsEntityTDao;
import com.guokrspace.cloudschoolbus.teacher.entity.ActivityBody;
import com.guokrspace.cloudschoolbus.teacher.entity.AttendanceRecord;
import com.guokrspace.cloudschoolbus.teacher.entity.Food;
import com.guokrspace.cloudschoolbus.teacher.entity.Ipcparam;
import com.guokrspace.cloudschoolbus.teacher.entity.NoticeBody;
import com.guokrspace.cloudschoolbus.teacher.entity.Schedule;
import com.guokrspace.cloudschoolbus.teacher.entity.StudentReport;
import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.teacher.event.ImReadyEvent;
import com.guokrspace.cloudschoolbus.teacher.module.classes.Streaming.StreamingChannelsFragment;
import com.guokrspace.cloudschoolbus.teacher.module.explore.adapter.ImageAdapter;
import com.guokrspace.cloudschoolbus.teacher.module.explore.adapter.TagRecycleViewAdapter;
import com.guokrspace.cloudschoolbus.teacher.module.photo.SelectStudentActivity;
import com.guokrspace.cloudschoolbus.teacher.widget.ActivityCard;
import com.guokrspace.cloudschoolbus.teacher.widget.AttendanceRecordCard;
import com.guokrspace.cloudschoolbus.teacher.widget.FoodNoticeCard;
import com.guokrspace.cloudschoolbus.teacher.widget.NoticeCard;
import com.guokrspace.cloudschoolbus.teacher.widget.PictureCard;
import com.guokrspace.cloudschoolbus.teacher.widget.ReportListCard;
import com.guokrspace.cloudschoolbus.teacher.widget.ScheduleNoticeCard;
import com.guokrspace.cloudschoolbus.teacher.widget.StreamingNoticeCard;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.squareup.otto.Subscribe;
import com.toaker.common.tlog.TLog;

import net.soulwolf.image.picturelib.PictureFrom;
import net.soulwolf.image.picturelib.PictureProcess;
import net.soulwolf.image.picturelib.listener.OnPicturePickListener;
import net.soulwolf.image.picturelib.model.Picture;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
public class ExploreFragment extends BaseFragment implements OnPicturePickListener {
    private OnFragmentInteractionListener mListener;

    private MaterialListView mMaterialListView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PictureProcess mPictureProcess;

    private int mCurrentChild;
    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private String mCurrentDisplayingCardType = "All";

    private ArrayList<MenuSpinnerAdapter.MessageType> mMessageTypes = new ArrayList<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            BusProvider.getInstance().post(new ImReadyEvent());

            switch (msg.what) {
                case HandlerConstant.MSG_ONREFRESH:
                    clearAppBadgetCount(mParentContext);
                    setActionBarTitle(getResources().getString(R.string.module_explore));
                    filterCards(null);
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case HandlerConstant.MSG_ONLOADMORE:
                    hideWaitDialog();
                    setActionBarTitle(getResources().getString(R.string.module_explore));
                    filterCards(null);
                    break;
                case HandlerConstant.MSG_ONCACHE:
                    setActionBarTitle(getResources().getString(R.string.module_explore));
                    hideWaitDialog();
                    filterCards(null);
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
                    if (Version.DEBUG) {
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

                    break;
                case HandlerConstant.MSG_CONFIRM_OK:

                    Button button = (Button) msg.obj;
                    button.setText(getResources().getString(R.string.confirmed_notice));
                    button.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    button.setEnabled(false);

                    String messageid = (String) button.getTag();

                    //Update the DB
                    List<MessageEntity> messages = mApplication.mDaoSession.getMessageEntityDao()
                            .queryBuilder().where(MessageEntityDao.Properties.Messageid.eq(messageid))
                            .list();
                    if (messages.size() > 0) {
                        messages.get(0).setIsconfirm("2");
                        mApplication.mDaoSession.getMessageEntityDao().update(messages.get(0));
                    }

                    //Update the memory
                    int i = 0;
                    for (MessageEntity message : mMesageEntities) {
                        if (message.getMessageid().equals(messageid)) {
                            mMesageEntities.get(i).setIsconfirm("2");
                        }
                        i++;
                    }

                    filterCards(null);

                    break;
            }
            return false;
        }
    });

    public static ExploreFragment newInstance() {
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

        mPictureProcess = new PictureProcess(this);
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
            mMesageEntities = ServerInteractions.getInstance().GetMessagesFromCache();
            if (mMesageEntities.size() > 0) {
                MessageEntity messageEntity = mMesageEntities.get(0);
                mHandler.sendEmptyMessage(HandlerConstant.MSG_ONCACHE);
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


    public void filterCards(@Nullable String type) {

        if (type == null) //Do not know the type, just use the current type
        {
            type = mCurrentDisplayingCardType;
        } else {
            mCurrentDisplayingCardType = type;
        }

        MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
        QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
        List<MessageEntity> messageEntityList;

        if (type.equals("All")) {
            messageEntityList = queryBuilder.orderRaw("MESSAGEID+1 DESC").list();  //Note: convert string messageid into int
        } else {
            messageEntityList = queryBuilder.where(MessageEntityDao.Properties.Apptype.eq(type)) //Note: convert string messageid into int
                    .orderRaw("MESSAGEID+1 DESC").list();
        }

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
        } else if (messageEntity.getApptype().equals("Active"))
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
                setActionBarTitle(getResources().getString(R.string.noticetype));
                break;
            case R.id.action_attendance:
                filterCards("Punch");
                setActionBarTitle(getResources().getString(R.string.attendancetype));
                break;
            case R.id.action_report:
                setActionBarTitle(getResources().getString(R.string.report));
                filterCards("Report");
                break;
            case R.id.action_streaming:
                filterCards("OpenClass");
                setActionBarTitle(getResources().getString(R.string.openclass));
                break;
            case R.id.action_picture:
                filterCards("Article");
                setActionBarTitle(getResources().getString(R.string.picturetype));
                break;
            case R.id.action_take_photo:
                mPictureProcess.setPictureFrom(PictureFrom.GALLERY);
                mPictureProcess.setClip(false);
                mPictureProcess.setMaxPictureCount(9);
                mPictureProcess.execute(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title) {
        if (Version.PARENT) {
            MainActivity mainActivity = (MainActivity) mParentContext;
            mainActivity.getSupportActionBar().setTitle(title);
//            View view = mainActivity.getSupportActionBar().getCustomView();
//            TextView textView = (TextView) view.findViewById(R.id.abs_layout_titleTextView);
//            textView.setText(title);
//            mainActivity.mCurrentTitle = title;
//            mainActivity.mUpperLeverTitle = preTitle;
        }
    }

    @Subscribe
    public void onClassSwitched(InfoSwitchedEvent event) {
        mCurrentChild = event.getCurrentChild();

        /**
         * Todo:
         */
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null) {
            setOverflowIconVisible(menu);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MainActivity mainActivity = (MainActivity) mParentContext;

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
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setOverflowIconVisible(Menu menu) {
        try {
            Class clazz = Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
            Field field = clazz.getDeclaredField("mOptionalIconsVisible");
            if (field != null) {
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

    private void clearAppBadgetCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("cloudschoolbuspref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("unreadmessages", 0);
        editor.commit();
        ShortcutBadger.with(context).remove();
    }

    private void initMessageTypes() {
        String[] messageTypes = {"All", "Article", "Notice", "Punch", "Report", "OpenClass"};
        Integer[] resIcon = {0, R.drawable.ic_picture, R.drawable.ic_notice, R.drawable.ic_attendance,
                R.drawable.ic_report, R.drawable.ic_streaming};
        Integer[] descriptions = {R.string.all, R.string.picture, R.string.noticetype, R.string.attendancetype,
                R.string.report, R.string.openclass};

        int i = 0;
        mMessageTypes.clear();
        for (String type : messageTypes) {
            MenuSpinnerAdapter.MessageType messageType = new MenuSpinnerAdapter.MessageType();
            messageType.messageType = type;
            messageType.description = getResources().getString(descriptions[i]);
            messageType.iconRes = resIcon[i];
            mMessageTypes.add(messageType);
            i++;
        }
    }

    public String cardType(String type) {
        String cardtype = "";

        if (type.equals("Article"))
            cardtype = getResources().getString(R.string.picturetype);
        if (type.equals("Notice"))
            cardtype = getResources().getString(R.string.noticetype);
        else if (type.equals("Punch"))
            cardtype = getResources().getString(R.string.attendancetype);
        else if (type.equals("OpenClass"))
            cardtype = getResources().getString(R.string.openclass);
        else if (type.equals("Report"))
            cardtype = getResources().getString(R.string.report);

        return cardtype;
    }

    public PictureCard buildArticleCard(MessageEntity message) {
        PictureCard card = new PictureCard(mParentContext);
        String teacherAvatarString = message.getSenderEntity().getAvatar();
        card.setTeacherAvatarUrl(teacherAvatarString);
        card.setTeacherName(message.getSenderEntity().getName());
        if (mApplication.mSchoolsT.size() > 0)
            card.setKindergarten(mApplication.mSchoolsT.get(0).getName());
        else
            card.setKindergarten("");

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
        if (pictureUrls != null)
            card.setImageAdapter(new ImageAdapter(mParentContext, pictureUrls,
                    message.getDescription(), message.getTitle()));

        List<TagsEntityT> tagEntities = new ArrayList<>();
        if (message.getTagids() != null && message.getTagids().contains(",")) {
            String tagids[] = message.getTagids().split(",");
            for (String tagid : tagids) {
                tagEntities.addAll(mApplication.mDaoSession.getTagsEntityTDao().queryBuilder().where(TagsEntityTDao.Properties.Tagid.eq(tagid)).list());
            }
        }

        TagRecycleViewAdapter adapter = new TagRecycleViewAdapter(tagEntities, mParentContext);
        card.setTagAdapter(adapter);

        CommonRecyclerItemClickListener tagClickListener = new CommonRecyclerItemClickListener(mParentContext, new CommonRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                animation(view);
                SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager())
                        .setMessage((String) view.getTag())
                        .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        card.setmOnItemSelectedListener(tagClickListener);

        return card;
    }

    public NoticeCard BuildNoticeCard(final MessageEntity messageEntity, final Handler handler) {
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
        if (noticeBody != null && noticeBody.getPList().size()>0) {
            String pictureUrl = noticeBody.getPList().get(0);
            final ArrayList<String> picArr = new ArrayList<>();
            picArr.add(pictureUrl);
            noticeCard.setDrawable(pictureUrl);
            noticeCard.setmMediaAttachmentClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mParentContext, GalleryActivityUrl.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("fileUrls", picArr);
                    bundle.putInt("currentFile", 0);
                    bundle.putString("description", "");
                    bundle.putString("tilte", messageEntity.getTitle());
                    intent.putExtras(bundle);
                    mParentContext.startActivity(intent);
                }
            });
        }

        noticeCard.setmConfirmButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerInteractions.getInstance().UserConfirm(messageEntity.getMessageid(), (Button) view, handler);
            }
        });
        return noticeCard;
    }

    public ActivityCard BuildActivityCard(final MessageEntity messageEntity, final Handler handler) {
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

    public AttendanceRecordCard BuildAttendanceCard(MessageEntity messageEntity) {
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

    public StreamingNoticeCard BuildStreamingNoticeCard(MessageEntity messageEntity) {
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
                MainActivity mainActivity = (MainActivity) mParentContext;
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

    public ReportListCard BuildReportListCard(final MessageEntity messageEntity) {
        ReportListCard reportListCard = new ReportListCard(mParentContext);
        String teacherAvatarString = "";
        if (messageEntity.getSenderEntity() != null) {
            teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
            reportListCard.setTeacherAvatarUrl(teacherAvatarString);
            reportListCard.setTeacherName(messageEntity.getSenderEntity().getName());
            reportListCard.setClassName(messageEntity.getSenderEntity().getClassname());
        }
        reportListCard.setCardType(cardType(messageEntity.getApptype()));
        reportListCard.setSentTime(messageEntity.getSendtime());
        reportListCard.setReporttype(messageEntity.getTitle());
        String messageBody = messageEntity.getBody();
        final StudentReport studentReport = FastJsonTools.getObject(messageBody, StudentReport.class);
        reportListCard.setReporttype(studentReport.getReportType());
        reportListCard.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebviewFragment theFragment = WebviewFragment.newInstance(studentReport.getReportUrl(), getResources().getString(R.string.report), "");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, theFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return reportListCard;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  Let the fragment to handle unhandled result
        /* http://stackoverflow.com/questions/6147884/onactivityresult-not-being-called-in-fragment?rq=1 */
        mPictureProcess.onProcessResult(requestCode, resultCode, data); //This only handles the picturechooselib activity results
    }

    public FoodNoticeCard BuildFoodNoticeCard(final MessageEntity messageEntity) {
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
                WebviewFragment fragment = WebviewFragment.newInstance(foodUrl, getResources().getString(R.string.food), "");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return card;
    }

    public ScheduleNoticeCard BuildScheduleNoticeCard(final MessageEntity messageEntity) {
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
                WebviewFragment fragment = WebviewFragment.newInstance(scheduleUrl, getResources().getString(R.string.schedule), "");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.article_module_layout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return card;
    }

    //Listener for the image chooser
    @Override
    public void onSuccess(List<Picture> pictures) {
        if (Version.DEBUG) {
            TLog.i("", "OnSuccess:%s", pictures);
        }
        Intent intent = new Intent(mParentContext, SelectStudentActivity.class);
        intent.putExtra("pictures", (ArrayList) pictures);
        startActivity(intent);

    }

    @Override
    public void onError(Exception e) {
        TLog.e("", "onError", e);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onSuccessString(List<String> pictures) {
        if (Version.DEBUG) {
            TLog.i("", "OnSuccess:%s", pictures);
        }
        Intent intent = new Intent(mParentContext, SelectStudentActivity.class);
        intent.putExtra("pictures", (ArrayList) pictures);
        startActivity(intent);
    }
}