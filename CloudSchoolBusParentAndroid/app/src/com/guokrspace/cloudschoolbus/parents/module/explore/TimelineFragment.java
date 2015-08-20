package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.app.Activity;
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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.android.support.utils.DateUtils;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceRecord;
import com.guokrspace.cloudschoolbus.parents.entity.Food;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.guokrspace.cloudschoolbus.parents.entity.NoticeBody;
import com.guokrspace.cloudschoolbus.parents.entity.Schedule;
import com.guokrspace.cloudschoolbus.parents.entity.StudentReport;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.StreamingChannelsFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.food.FoodDetailFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.report.ReportDetailFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.classify.schedule.ScheduleDetailFragment;
import com.guokrspace.cloudschoolbus.parents.widget.AttendanceRecordCard;
import com.guokrspace.cloudschoolbus.parents.widget.FoodNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.NoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.ReportListCard;
import com.guokrspace.cloudschoolbus.parents.widget.ScheduleNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.StreamingNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.TimelinePicturesCard;

import org.json.JSONObject;
import org.json.JSONStringer;

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
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_CONFIRM_OK:
                    hideWaitDialog();
                    Button button = (Button) msg.obj;
                    button.setText(getResources().getString(R.string.confirmed_notice));
                    button.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    button.setEnabled(false);
//                    button.setEnabled(false);
                    break;
            }
            return false;
        }
    });

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

        ClearCache();

        GetLastestMessagesFromServer(mHandler);

//        GetMessagesFromCache(mHandler);
//
//        if (mMesageEntities.size() == 0)
//            GetLastestMessagesFromServer(mHandler);
//        else {
//            MessageEntity messageEntity = mMesageEntities.get(0);
//            GetNewMessagesFromServer(messageEntity.getSendtime(), mHandler);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_timeline, container, false);

        mMaterialListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        mLayoutManager = (LinearLayoutManager) mMaterialListView.getLayoutManager();

        MainActivity mainActivity = (MainActivity)mParentContext;
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setListeners();

        return root;
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if(mMesageEntities.size()>0) {
                    MessageEntity messageEntity = mMesageEntities.get(0);
                    GetNewMessagesFromServer(messageEntity.getSendtime(), mHandler);
                } else
                {
                    GetLastestMessagesFromServer(mHandler);
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
            mMaterialListView.add((Card) buildcard(mMesageEntities.get(i),i));
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

    private void ClearCache()
    {
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
        if (messageEntity.getApptype().equals("picture")) {
            List<String> pictureUrls = new ArrayList<>();
            {
                TimelinePicturesCard card = new TimelinePicturesCard(mParentContext);
                String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
                card.setTeacherAvatarUrl(teacherAvatarString);
                card.setTeacherName(messageEntity.getSenderEntity().getName());
                card.setKindergarten(mApplication.mSchools.get(0).getName());
                card.setSentTime(messageEntity.getSendtime());
                card.setTitle(messageEntity.getTitle());
                card.setDescription(messageEntity.getDescription());
//                pictureUrls.add(messageEntity.getMessageBodyEntity().getContent());
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

            }
        } else if (messageEntity.getApptype().equals("Notice")) {
            NoticeCard noticeCard = new NoticeCard(mParentContext);
            String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
            noticeCard.setTeacherAvatarUrl(teacherAvatarString);
            noticeCard.setTeacherName(messageEntity.getSenderEntity().getName());
            noticeCard.setClassName(messageEntity.getSenderEntity().getClassname());
            noticeCard.setCardType(cardType(messageEntity.getApptype()));
            noticeCard.setSentTime(messageEntity.getSendtime());
            noticeCard.setIsNeedConfirm(messageEntity.getIsconfirm());
            noticeCard.setIsNeedConfirm("1");
            noticeCard.setTitle(messageEntity.getTitle());
            noticeCard.setDescription(messageEntity.getDescription());
            NoticeBody noticeBody = FastJsonTools.getObject(messageEntity.getBody(), NoticeBody.class);
            if (noticeBody != null) noticeCard.setDrawable(noticeBody.getPList().get(0));
            noticeCard.setmConfirmButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NoticeConfirm(messageEntity.getMessageid(),(Button)view,mHandler);
                }
            });
            return noticeCard;
        } else if (messageEntity.getApptype().equals("Punch")) {
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
        } else if (messageEntity.getApptype().equals("OpenClass")) {
            StreamingNoticeCard streamingNoticeCard = new StreamingNoticeCard(mParentContext);
            streamingNoticeCard.setKindergartenAvatar(messageEntity.getSenderEntity().getAvatar());
            streamingNoticeCard.setKindergartenName(messageEntity.getSenderEntity().getName());
            streamingNoticeCard.setClassName(messageEntity.getSenderEntity().getClassname());
            streamingNoticeCard.setSentTime(DateUtils.timelineTimestamp(messageEntity.getSendtime(),mParentContext));
            streamingNoticeCard.setCardType(cardType(messageEntity.getApptype()));
            streamingNoticeCard.setContext(mParentContext);
            streamingNoticeCard.setDescription(messageEntity.getDescription());
            String messageBody = messageEntity.getBody();
            final Ipcparam ipcpara = FastJsonTools.getObject(messageBody, Ipcparam.class);
            streamingNoticeCard.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StreamingChannelsFragment fragment = StreamingChannelsFragment.newInstance(ipcpara);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.article_module_layout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            return streamingNoticeCard;
        } else if (messageEntity.getApptype().equals("Report")) {
            ReportListCard reportListCard = new ReportListCard(mParentContext);
            String teacherAvatarString = messageEntity.getSenderEntity().getAvatar();
            reportListCard.setTeacherAvatarUrl(teacherAvatarString);
            reportListCard.setTeacherName(messageEntity.getSenderEntity().getName());
            reportListCard.setClassName(messageEntity.getSenderEntity().getClassname());
            reportListCard.setCardType(cardType(messageEntity.getApptype()));
            reportListCard.setSentTime(messageEntity.getSendtime());
            String messageBody = messageEntity.getBody();
            final StudentReport studentReport = FastJsonTools.getObject(messageBody, StudentReport.class);
            reportListCard.setReporttype(studentReport.getReportType());
            reportListCard.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ReportDetailFragment reportDetailFragment = ReportDetailFragment.newInstance(messageEntity.getSendtime(), studentReport.getReportUrl());
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.article_module_layout, reportDetailFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            return reportListCard;
        }else if (messageEntity.getApptype().equals("Food")) {
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
                    FoodDetailFragment fragment = FoodDetailFragment.newInstance(foodUrl);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.article_module_layout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            return card;
        }else if (messageEntity.getApptype().equals("Schedule")) {
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
                    ScheduleDetailFragment fragment = ScheduleDetailFragment.newInstance(scheduleUrl);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.article_module_layout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            return card;
        }

        return null;
    }

    private String cardType(String type)
    {
        String cardtype = "";

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
        return cardtype;
    }
}