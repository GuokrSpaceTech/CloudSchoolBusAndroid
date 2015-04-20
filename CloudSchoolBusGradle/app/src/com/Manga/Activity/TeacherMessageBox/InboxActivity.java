package com.Manga.Activity.TeacherMessageBox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.Entity.Baseinfo;
import com.Manga.Activity.Entity.Teacher;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.android.support.jhf.utils.DateUtils;
import com.cytx.utility.FastJsonTools;

import java.util.HashMap;
import java.util.List;

import me.maxwin.view.XListView;

/**
 * Created by kai on 12/27/14.
 */
public class InboxActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mlistView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_messagebox_inbox);
        mlistView = (ListView)findViewById(R.id.listView);
        mlistView.setOnItemClickListener(this);
        init();
    }

    void init()
    {
        getTeacherList();
    }

    private void getTeacherList()
    {
        if(ActivityUtil.login != null)
        {
            Baseinfo baseinfo = ActivityUtil.login.getmBaseInfo();
            mTeacherList = baseinfo.getTeacherlist();

            if(mTeacherList!=null)
            {
                TeacherListAdapter teacherListAdapter = new TeacherListAdapter(getApplicationContext(), mTeacherList);
                mlistView.setAdapter(teacherListAdapter);
            }

            GetPrivateLetters();
        }
        else
        {
            //DebugClass.displayCurrentStack("Error: no techer info got!");
        }
    }

    public void GetPrivateLetters() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (HttpUtil.isNetworkConnected(InboxActivity.this)) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    ActivityUtil.main.disPRO();
                    Result result = HttpUtil.httpGet(InboxActivity.this, new Params("latestletter", map));
                    if (result == null) {
                        mHandler.sendEmptyMessage(1);
                    } else if ("1".equals(result.getCode())) {
                        List<LetterDto> resultList = FastJsonTools.getListObject(result.getContent(), LetterDto.class);

                        //Update the isShowDate flag for each record
                        String listAddTime = "-1";
                        for (int i = 0; i < resultList.size(); i++) {
                            long addtime = 0L;
                            try {
                                addtime = Long.parseLong(resultList.get(i).getAddtime()) * 1000;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            String date = DateUtils.dateFormat(addtime, "yyyy-MM-dd");
                            if ("-1".equals(listAddTime) || !date.equals(listAddTime)) {
                                resultList.get(i).isShowDate = true;
                                listAddTime = date;
                            } else {
                                resultList.get(i).isShowDate = false;
                            }
                        }

                        mHandler.sendEmptyMessage(GOTMESSAGE);
                    }
                } else {
                    mHandler.sendEmptyMessage(NONETWORK);
                }
            }
        });
        thread.start();
    }


    //Back button pressed
    public void close(View v)
    {
        ActivityUtil.main.move();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,TeacherMessageBoxActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("teacher",mTeacherList.get(i));
        intent.putExtras(bundle);

        startActivity(intent);
    }
}

class TeacherListAdapter extends BaseAdapter
{
    List<Teacher> mTeacherList;
    Context mContext;

    TeacherListAdapter(Context context, List<Teacher> teachers) {
        mTeacherList = teachers;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mTeacherList.size();
    }

    @Override
    public Object getItem(int i) {
        return mTeacherList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_teacher_messagebox_inbox_list_item,null);
        }

        TextView textViewTeacherName = (TextView)view.findViewById(R.id.textView_teachername);

        textViewTeacherName.setText(mTeacherList.get(i).getTeachername());

        return view;
    }
}
