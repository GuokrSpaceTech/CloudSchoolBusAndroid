package com.guokrspace.cloudschoolbus.parents.module.classes.attendance;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.support.customview.KCalendar;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.AttendanceEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.AttendanceEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.FestivalEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.FestivalEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceDto;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceManagerDto;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceRecordDto;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class AttendanceFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<AttendanceEntity> mAttendanceRecords = new ArrayList<AttendanceEntity>();
    private ArrayList<FestivalEntity>   mFestivals         = new ArrayList<FestivalEntity>();

    private KCalendar calendar;
    private TextView tv_calendar_title;
    private TextView header_day, header_title_date, header_title_festival;
    private TextView footer_title;
    private ImageView imbt_nextmoth, imbt_upmoth;
    private ListView  list_view_attendance_records;
    private AttendanceRecordsAdapter attendanceRecordsAdapter;

    String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式
    String str_month;
    int year, month;

    final private static int MSG_ONREFRESH = 1;
    final private static int MSG_ONLOADMORE = 2;
    final private static int MSG_ONCACHE = 3;
    final private static int MSG_NOCHANGE = 4;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_ONREFRESH:
                    refresh_data();
                    break;
                default:
                    break;

            }
            return false;
        }
    });

    // TODO: Rename and change types of parameters
    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
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
    public AttendanceFragment() {
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
        View root = inflater.inflate(R.layout.activity_attendance, container, false);

        //Get the current date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        date = format.format(d);
        if (null != date) {
            year = Integer.parseInt(date.substring(0, date.indexOf("-")));
            month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
        }
        //Get the data from the server
        str_month = month >= 10? year + "-" + month :  year + "-0" + month;

        //Setup the UI elements
        init_views(root);

        //Always try to get data from the server for the current month
        GetAttendanceFromServer(str_month);

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
    private void GetAttendanceFromCache(String month) {
        final AttendanceEntityDao attendanceEntityDao = mApplication.mDaoSession.getAttendanceEntityDao();
        final FestivalEntityDao festivalEntityDao = mApplication.mDaoSession.getFestivalEntityDao();
        mAttendanceRecords = (ArrayList<AttendanceEntity>) attendanceEntityDao.queryBuilder().where(AttendanceEntityDao.Properties.Month.eq(month)).list();
        mFestivals = (ArrayList<FestivalEntity>) festivalEntityDao.queryBuilder().where(FestivalEntityDao.Properties.Date.like(month+"%")).list();
        if (mAttendanceRecords.size() != 0 || mFestivals.size() !=0 )
            mHandler.sendEmptyMessage(MSG_ONCACHE);
    }

    //Get attendance from server, only used when there is no cache
    private void GetAttendanceFromServer(final String month) {
        final AttendanceEntityDao attendanceEntityDao = mApplication.mDaoSession.getAttendanceEntityDao();
        final FestivalEntityDao   festivalEntityDao   = mApplication.mDaoSession.getFestivalEntityDao();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("month", month);

        CloudSchoolBusRestClient.get("attendancemanager", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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

                //Get the School Calendar Note: FastJsonTools cannot parse the festival map
                JSONArray festivalArray = null;
                try {
                    festivalArray = response.getJSONArray("festival");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HashMap<String, String> festivalList = new HashMap<String, String>();
                for (int festival = 0; festival < festivalArray.length(); festival++) {
                    String[] festivalContent = new String[0];
                    try {
                        festivalContent = festivalArray.getString(festival).split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FestivalEntity festivalEntity = new FestivalEntity(festivalContent[0],festivalContent[1]);
                    festivalEntityDao.insertOrReplace(festivalEntity);
                }

                //Get all the attendance information
                AttendanceManagerDto attmgr = FastJsonTools.getObject(response.toString(), AttendanceManagerDto.class);

                int total_num_records_month = 0;
                for(int i=0; i<attmgr.getAttendance().size(); i++)
                {
                    AttendanceDto att = attmgr.getAttendance().get(i);

                    for( int j=0; j<att.getRecord().size(); j++)
                    {
                        AttendanceRecordDto record = att.getRecord().get(j);
                        AttendanceEntity attendanceEntity = new AttendanceEntity(
                                str_month, att.getAttendaceday1(), record.getCreatetime(), record.getImgpath()
                        );
                        attendanceEntityDao.insertOrReplace(attendanceEntity);
                    }
                    total_num_records_month += att.getRecord().size();
                }

                //attmgr.setFestivalList(festivalList);
                mAttendanceRecords = (ArrayList<AttendanceEntity>)attendanceEntityDao.queryBuilder().where(AttendanceEntityDao.Properties.Month.eq(month)).list();
                mFestivals         = (ArrayList<FestivalEntity>)festivalEntityDao.queryBuilder().where(FestivalEntityDao.Properties.Date.like(month+"%")).list();
                attmgr.setTotal_num_attendance_records(total_num_records_month);
                mHandler.sendEmptyMessage(MSG_ONREFRESH);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode,headers,response);
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

    public void animation(View v) {
        v.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        v.setAnimation(animation);
    }

    private void init_views(View root) {
        calendar = (KCalendar) root.findViewById(R.id.popupwindow_calendar);
        tv_calendar_title = (TextView) root.findViewById(R.id.calendar_title_date);

        imbt_upmoth = (ImageView) root.findViewById(R.id.imbt_upmoth);
        imbt_nextmoth = (ImageView) root.findViewById(R.id.imbt_nextmoth);

        header_day        = (TextView)root.findViewById(R.id.header_day);
        header_title_date = (TextView)root.findViewById(R.id.tv_current_date);
        header_title_festival = (TextView)root.findViewById(R.id.tv_festival_title);
        footer_title          = (TextView)root.findViewById(R.id.tv_cq_countday);

        list_view_attendance_records = (ListView)root.findViewById(R.id.listView_attendance_records);

        tv_calendar_title.setText(year + "." + month);
        tv_calendar_title.setTextSize(24);
        calendar.showCalendar(year, month);
        calendar.setCalendarDayBgColor(date, R.color.accent);

        // 监听所选中的日期
        calendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {

            public void onCalendarClick(int row, int col, String dateFormat) {
                int month = Integer.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1,
                        dateFormat.lastIndexOf("-")));

                if (calendar.getCalendarMonth() - month == 1// 跨年跳转
                        || calendar.getCalendarMonth() - month == -11) {
                    calendar.lastMonth();

                } else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
                        || month - calendar.getCalendarMonth() == -11) {
                    calendar.nextMonth();
                }

                calendar.removeCalendarDayBgColor(date);
                calendar.setCalendarDayBgColor(dateFormat, R.color.accent);
                //calendar.setCalendarDayBgColor(dateFormat, Color.parseColor("#5FAFC7"));
                if (!dateFormat.equals(date)) {
                    date = dateFormat;
                    String new_month_str = dateFormat.substring(0, dateFormat.lastIndexOf("-"));
                    if(!new_month_str.equals(str_month))
                    {
                        str_month = new_month_str;
                        init_data(str_month);
                        mHandler.sendEmptyMessage(MSG_ONREFRESH);
                    }
                }
            }
        });

        // 监听当前月份
        calendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            public void onCalendarDateChanged(int year, int month) {
                tv_calendar_title.setTextSize(24);
                tv_calendar_title.setText(year + "." + month);
                calendar.removeCalendarDayBgColor(date);

                String monthTemp = "";
                if (month >= 10) {
                    monthTemp = year + "-" + month;
                } else {
                    monthTemp = year + "-0" + month;
                }
                date = monthTemp + "-01";
                calendar.setCalendarDayBgColor(date, R.color.accent);
                init_data(monthTemp);
            }
        });

        imbt_upmoth.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                calendar.lastMonth();
                String monthTemp = "";
                if (calendar.getCalendarMonth() >= 10) {
                    monthTemp = calendar.getCalendarYear() + "-" + calendar.getCalendarMonth();
                } else {
                    monthTemp = calendar.getCalendarYear() + "-0" + calendar.getCalendarMonth();
                }
                date = monthTemp + "-01";
                calendar.setCalendarDayBgColor(date, R.color.accent);
                str_month = monthTemp;
                init_data(monthTemp);
            }
        });

        imbt_nextmoth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calendar.nextMonth();
                String monthTemp = "";
                if (calendar.getCalendarMonth() >= 10) {
                    monthTemp = calendar.getCalendarYear() + "-" + calendar.getCalendarMonth();
                } else {
                    monthTemp = calendar.getCalendarYear() + "-0" + calendar.getCalendarMonth();
                }
                date = monthTemp + "-01";
                calendar.removeCalendarDayBgColor(date);
                calendar.setCalendarDayBgColor(date, R.color.accent);
                str_month = monthTemp;
                init_data(monthTemp);
            }
        });

        attendanceRecordsAdapter = new AttendanceRecordsAdapter(mParentContext);
        list_view_attendance_records.setAdapter(attendanceRecordsAdapter);

        list_view_attendance_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
//                Intent intent = new Intent(MoringCheckActivity.this, CheckPictureActivity.class);
//                List<AttendanceRecordDto> records = getCurrentDateAttendanceRecords();
//                String url = records.get(arg2).getImgpath();
//                if(url != "null")
//                {
//                    intent.putExtra("image", "http://"+url);
//                    if(ActivityUtil.share != null) {
//                        ActivityUtil.startActivity(ActivityUtil.share, intent);
//                    }
//                    else
//                    {
//                        startActivity(intent);
//                    }
//                }
            }
        });
    }

    private void init_data(String month)
    {
        GetAttendanceFromCache(month);

        if(mAttendanceRecords.size()==0)
        {
            GetAttendanceFromServer(month);
        }
    }

    private void refresh_data()
    {
            //Festival
        for(int i =0; i< mFestivals.size(); i++)
        {
//            if(date.(mFestivals.get(i).getDate()))
//            {
//                header_title_festival.setText(mFestivals.get(i).getFestivalName());
                calendar.setCalendarDayBgColor(mFestivals.get(i).getDate(), R.color.accent_material_light);
//            }
        }


        //Set the marks for the days with attendance records
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < mAttendanceRecords.size(); i++) {
            list.add(mAttendanceRecords.get(i).getDay());
        }

        calendar.addMarks(list, 0);


        //Set the List View and Notify the data changes
        attendanceRecordsAdapter.attendance_records = mAttendanceRecords;
        attendanceRecordsAdapter.notifyDataSetChanged();

        //Set the header
        String str_day = date.substring(date.lastIndexOf("-") + 1, date.length());
        header_day.setText(str_day);
        header_day.setTextSize(20);
        header_day.setBackgroundColor(getResources().getColor(R.color.accent));
        header_day.setTextColor(Color.WHITE);

        int len;
        if(attendanceRecordsAdapter.attendance_records == null)
            len = 0;
        else
            len = attendanceRecordsAdapter.attendance_records.size();

        String str_title= getResources().getString(R.string.current_day_attendance_records)
                + "("
                + len
                + ")";

        header_title_date.setText(str_title);

        //Set the footer
        int days = mAttendanceRecords==null?0:mAttendanceRecords.size();

        str_title = days
                + "  |  "
                + getResources().getString(R.string.total_attendance_records)
                +" : "
                + getMontlyAttendanceNumber(str_month);

        footer_title.setText(str_title);
    }

    private int getMontlyAttendanceNumber(String month)
    {
        final AttendanceEntityDao attendanceEntityDao = mApplication.mDaoSession.getAttendanceEntityDao();
        QueryBuilder queryBuilder = attendanceEntityDao.queryBuilder();
        queryBuilder.where(AttendanceEntityDao.Properties.Month.eq(month));

        return queryBuilder.list().size();

    }

}