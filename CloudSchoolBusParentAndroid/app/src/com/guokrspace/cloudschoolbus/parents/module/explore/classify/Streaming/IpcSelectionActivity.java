package com.guokrspace.cloudschoolbus.parents.module.explore.classify.Streaming;

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


import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by wangjianfeng on 14-12-28.
 */
public class IpcSelectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mlistView;
    private DvrListAdapter mTheAdapter;
    public String m_sStreamIP   = "";//221.122.97.78
    public int    m_iStreamPort = 0;
    public String m_sDVRName    = "";
    public List<Ipcparam.Dvr> mDvrList = new ArrayList<Ipcparam.Dvr>();
    private Ipcparam mIpcparam =  new Ipcparam();
    private final static int GOTSERVERSETTINGS = 1;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GOTSERVERSETTINGS:
                    mTheAdapter = new DvrListAdapter(getApplicationContext(),mDvrList);
                    mlistView.setAdapter(mTheAdapter);
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc_selection_list);
        mlistView = (ListView)findViewById(R.id.listView);
        mlistView.setDivider(null);
        mlistView.setOnItemClickListener(this);
        init();
    }


    @Override
    protected void init()
    {
        getIPCSetting();

    }

    //Back button pressed
    public void close(View v)
    {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,Preview.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ipcparams", mIpcparam);
        intent.putExtras(bundle);
        intent.putExtra("id",i); //Which camera

        startActivity(intent);
    }

    private void getIPCSetting()
    {
        RequestParams params = new RequestParams();

        CloudSchoolBusRestClient.get("camera", params, new JsonHttpResponseHandler() {
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

                mIpcparam = FastJsonTools.getObject(response.toString(), Ipcparam.class);

                //Check the parameters
                if (mIpcparam != null) {
                    m_sStreamIP = mIpcparam.getDdns();
                    try {
                        m_iStreamPort = Integer.parseInt(mIpcparam.getPort());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        m_iStreamPort = 600;
                    }
                    mDvrList = mIpcparam.getDvr();

                    handler.sendEmptyMessage(GOTSERVERSETTINGS);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
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
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }
}

class DvrListAdapter extends BaseAdapter
{
    List<Ipcparam.Dvr> mDvrList;
    Context mContext;

    DvrListAdapter(Context context, List<Ipcparam.Dvr> dvrs) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_ipc_selection_list_item,null);
        }

        TextView textViewIPCName = (TextView)view.findViewById(R.id.textView_ipc_name);

        textViewIPCName.setText(mDvrList.get(i).getChanneldesc());

        return view;
    }
}
