package com.Manga.Activity.myChildren.Streaming;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.Manga.Activity.Entity.Baseinfo;
import com.Manga.Activity.Entity.Teacher;
import com.Manga.Activity.R;
import com.Manga.Activity.TeacherMessageBox.TeacherMessageBoxActivity;
import com.Manga.Activity.myChildren.Streaming.entity.Dvr;
import com.Manga.Activity.myChildren.Streaming.entity.Ipcparam;
import com.Manga.Activity.utils.ActivityUtil;
import com.cytx.BaseActivity;

import java.util.List;

/**
 * Created by wangjianfeng on 14-12-28.
 */
public class IpcSelectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mlistView;
    Ipcparam mParams;
    private List<Dvr> mDvrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc_selection);
        mlistView = (ListView)findViewById(R.id.listView);
        mlistView.setDivider(null);
        mlistView.setOnItemClickListener(this);
        init();
    }

    void init()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mParams = (Ipcparam)bundle.getSerializable("ipcparam");
        mDvrList = mParams.getDvr();
        DvrListAdapter theAdapter = new DvrListAdapter(getApplicationContext(),mDvrList);
        mlistView.setAdapter(theAdapter);
    }

    //Back button pressed
    public void close(View v)
    {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,Preview.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ipcparams", mParams);
        intent.putExtras(bundle);
        intent.putExtra("id",i); //Which camera

        startActivity(intent);
    }
}

class DvrListAdapter extends BaseAdapter
{
    List<Dvr> mDvrList;
    Context mContext;

    DvrListAdapter(Context context, List<Dvr> dvrs) {
        mDvrList = dvrs;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDvrList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDvrList.get(i);
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

        textViewTeacherName.setText(mDvrList.get(i).getChanneldesc());

        return view;
    }
}
