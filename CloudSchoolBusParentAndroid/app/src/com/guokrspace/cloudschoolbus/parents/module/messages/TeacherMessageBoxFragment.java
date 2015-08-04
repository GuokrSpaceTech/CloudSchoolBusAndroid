package com.guokrspace.cloudschoolbus.parents.module.messages;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.support.utils.ImageUtil;
import com.dexafree.materialList.cards.ChatMessageCard;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LetterEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.LetterEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.LetterDto;
import com.guokrspace.cloudschoolbus.parents.entity.Teacher;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by kai on 12/25/14.
 */
public class TeacherMessageBoxFragment extends BaseFragment {
    private MaterialListView mListView;
    private List<LetterEntity> mLetterEntities;
    private Teacher mTeacher;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final static String ARG_TEACHER = "teacher";

    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;


    private final int OUTTIME = -1;
    private final int NONETWORK = -2;
    private final int MSG_NEW_DATA = 3;
    private final int MSG_OLD_DATA = 4;
    private final int MSG_NO_DATA = 5;
    private final int REFRESH = 1;
    private final int LOADMORE = 2;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case NONETWORK:
//                    Toast.makeText(TeacherMessageBoxActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NEW_DATA:
                    mSwipeRefreshLayout.setRefreshing(false);
//                    insertCards();
                    break;
                case MSG_OLD_DATA:
//                    appendCards();
                    break;
                case MSG_NO_DATA:
                    mSwipeRefreshLayout.setRefreshing(false);
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onPause() {
        super.onPause();
    }

    // TODO: Rename and change types of parameters
    public static TeacherMessageBoxFragment newInstance(Teacher teacher) {
        TeacherMessageBoxFragment fragment = new TeacherMessageBoxFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TEACHER, teacher);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TeacherMessageBoxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTeacher =  (Teacher)getArguments().get(ARG_TEACHER);
//        GetLettersFromCache(mTeacher.getTeacherid());
//        if (mLetterEntities.size() == 0)
//            GetLettersLatest(mTeacher.getTeacherid());
//        else
//            GetLettersNew(mLetterEntities.get(0).getAddtime(), mTeacher.getTeacherid());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_teacher_messagebox, container, false);
        mListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                LetterEntity letterEntity = mLetterEntities.get(0);
                String endtime = letterEntity.getAddtime();
//                GetLettersNew(endtime, mTeacher.getTeacherid());
            }
        });


        Button sendButton = (Button)root.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendLetter("", "This is a test message.", 0);
            }
        });
        mLayoutManager = (LinearLayoutManager) mListView.getLayoutManager();
        mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean loading = true;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //Log.d("Aing", "dx:" + dx + ", dy:" + dy);

                visibleItemCount = mListView.getChildCount();
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
                    LetterEntity letterEntity = mLetterEntities.get(mLetterEntities.size() - 1);
                    String starttime = letterEntity.getAddtime();
//                    GetLettersOld(starttime, mTeacher.getTeacherid());

                    loading = true;
                }
            }
        });

        return root;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }


    private void copyContentDialog(final String contentString, final View anchor) {
//        View view = LayoutInflater.from(getApplicationContext()).inflate(
//                R.layout.popup_letter_content_copy, null);
//        ViewGroup copyLayout = (ViewGroup) view.findViewById(R.id.copyLayout);
//        copyLayout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                ClipboardUtils.copy(contentString, getApplicationContext());
//                customPopupWindow.dismiss();
//            }
//        });
//        int width = ToolUtils.dipToPx(getApplicationContext(), 50);
//        int height = ToolUtils.dipToPx(getApplicationContext(), 50);
//        customPopupWindow.setContentView(view, width, height);
//        customPopupWindow.show(anchor, anchor.getWidth() / 2 - width / 2, -anchor.getHeight() - height);
    }


    private void GetLettersFromCache(String teacherid) {
        final LetterEntityDao leetterEntityDao = mApplication.mDaoSession.getLetterEntityDao();
        mLetterEntities = (ArrayList<LetterEntity>) leetterEntityDao.queryBuilder().list();
        if (mLetterEntities.size() != 0)
            mHandler.sendEmptyMessage(MSG_NEW_DATA);
    }

    private void GetLettersLatest(String teacherid) {
        GetLetters("0", "0", teacherid, REFRESH);
    }

    private void GetLettersNew(String endtime, String teacherid) {
        GetLetters("0", endtime, teacherid, REFRESH);
    }

    private void GetLettersOld(String starttime, String teacherid) {
        GetLetters(starttime, "0", teacherid, LOADMORE);
        mHandler.sendEmptyMessage(MSG_OLD_DATA);
    }

    public void GetLetters(final String starttime, final String endtime, final String teacherid, final int direction) {

        final LetterEntityDao letterEntityDao = mApplication.mDaoSession.getLetterEntityDao();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        params.put("id", teacherid);

        CloudSchoolBusRestClient.get("messageletter", params, new JsonHttpResponseHandler() {
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
                if (retCode != "1") {
                    // Errro Handling
                }
                List<LetterDto> letters = FastJsonTools.getListObject(response.toString(), LetterDto.class);
                for (int i = 0; i < letters.size(); i++) {
                    LetterEntity letterEntity = new LetterEntity(letters.get(i).getLetterid(), letters.get(i).getLetter_type(), letters.get(i).getFrom_role(), letters.get(i).getFrom_id(), letters.get(i).getTo_role(), letters.get(i).getTo_id(), letters.get(i).getAddtime(), letters.get(i).getContent(), true);
                    letterEntityDao.insertOrReplace(letterEntity);
                    if(direction==REFRESH)
                        mLetterEntities.add(0,letterEntity);
                    else if(direction==LOADMORE)
                        mLetterEntities.add(letterEntity);
                }

                if(letters.size()!=0 && direction == REFRESH)
                    mHandler.sendEmptyMessage(MSG_NEW_DATA);
                else if(letters.size()!=0 && direction == LOADMORE)
                    mHandler.sendEmptyMessage(MSG_OLD_DATA);
                else if(letters.size()==0)
                    mHandler.sendEmptyMessage(MSG_NO_DATA);


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
                mHandler.sendEmptyMessage(MSG_NO_DATA);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    /**
     * 发送私信
     *
     * @param picPathString 不为null表示发送照片
     * @param contentString 不为null表示发送字符串
     * @param requestCode   调用系统照相机返回或者是系统相册返回
     */

    private void SendLetter(final String picPathString, final String contentString, final int requestCode) {
        String lettertype = "txt";
        String fbody = null;
        String fsize = null;
        String fext = null;
        if (!TextUtils.isEmpty(picPathString)) {
            lettertype = "img";
            fbody = ImageUtil.getPicString(picPathString, 512);
            fext = picPathString.substring(picPathString.lastIndexOf(".") + 1);
            fsize = fbody.length() + "";
        } else if (!TextUtils.isEmpty(contentString)) {
            lettertype = "txt";
        }
//        HashMap<String, String> params = new HashMap<String, String>();
        RequestParams params = new RequestParams();
//        params.put("id", mTeacher.getTeacherid());
        params.put("receiverid", "111");
        params.put("receiverrole", "1");
        params.put("message", contentString);
//        params.put("picture",fbody);
        CloudSchoolBusRestClient.post("sendto", params, new JsonHttpResponseHandler() {
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
                if (retCode != "1") {
                    // Errro Handling
                }

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


    private void insertCards() {
        ChatMessageCard chatMessageCard = new ChatMessageCard(mParentContext);
        for (int i = mLetterEntities.size()-1; i > -1; i--) {
            chatMessageCard.setRoleType(mLetterEntities.get(i).getFrom_role());
            chatMessageCard.setLetterType(mLetterEntities.get(i).getLetter_type());
            chatMessageCard.setDescription(mLetterEntities.get(i).getContent());
            chatMessageCard.setTimestamp(mLetterEntities.get(i).getAddtime());
        }
        mListView.addAtStart(chatMessageCard);
    }

    private void appendCards() {
        ChatMessageCard chatMessageCard = new ChatMessageCard(mParentContext);
        for (int i = 0; i < mLetterEntities.size(); i++) {
            chatMessageCard.setRoleType(mLetterEntities.get(i).getFrom_role());
            chatMessageCard.setLetterType(mLetterEntities.get(i).getLetter_type());
            chatMessageCard.setDescription(mLetterEntities.get(i).getContent());
            chatMessageCard.setTimestamp(mLetterEntities.get(i).getAddtime());
        }
        mListView.add(chatMessageCard);
    }
}
