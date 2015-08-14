package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.support.utils.ImageUtil;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.avast.android.dialogs.iface.ISimpleDialogCancelListener;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.widget.ChatMessageCard;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by kai on 12/25/14.
 */
public class TeacherMessageBoxFragment extends BaseFragment implements
        View.OnClickListener,
        IListDialogListener,
        ISimpleDialogCancelListener
{
    private final static String ARG_TEACHER = "teacher";

    //Styled Dialog defines
    private static final int REQUEST_PROGRESS = 1;
    private static final int REQUEST_LIST_SIMPLE = 9;
    private static final int REQUEST_LIST_MULTIPLE = 10;
    private static final int REQUEST_LIST_SINGLE = 11;
    private static final int REQUEST_DATE_PICKER = 12;
    private static final int REQUEST_TIME_PICKER = 13;
    private static final int REQUEST_SIMPLE_DIALOG = 42;

    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 1001;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 1002;

    //OnLoadMore and pulltorefresh related
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int previousTotal = 0;
    private int visibleThreshold = 3;

    //Views
    ImageView imageViewPictureUpload;
    EditText  sendContentEditText;
    private MaterialListView mListView;

    //Datasets
    private List<MessageEntity> mChatMessages;
    private TeacherEntity mTeacher;

    // 上传图片
    private Bitmap bitMap;
    private String bitmapFilePath = "";

    //Handler
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerConstant.MSG_SERVER_ERROR:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;
                case HandlerConstant.MSG_NO_NETOWRK:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    break;
                case HandlerConstant.MSG_ONREFRESH:
                    mSwipeRefreshLayout.setRefreshing(false);
                    hideWaitDialog();
                    if(filterMessages())
                        insertCards();
                    else
                        GetNewMessagesFromServer(mMesageEntities.get(0).getSendtime(),mHandler);
                    break;
                case HandlerConstant.MSG_ONLOADMORE:
                    mSwipeRefreshLayout.setRefreshing(false);
                    hideWaitDialog();
                    if(filterMessages())
                        appendCards();
                    else
                        GetOldMessagesFromServer(mMesageEntities.get(mMesageEntities.size()-1).getSendtime(), mHandler);
                    break;
                case HandlerConstant.MSG_NOCHANGE:
                    mSwipeRefreshLayout.setRefreshing(false);
                    hideWaitDialog();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TeacherMessageBoxFragment() {
    }

    // TODO: Rename and change types of parameters
    public static TeacherMessageBoxFragment newInstance(TeacherEntity teacher) {
        TeacherMessageBoxFragment fragment = new TeacherMessageBoxFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TEACHER, teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTeacher =  (TeacherEntity)getArguments().get(ARG_TEACHER);
        GetMessagesFromCache();

        if (mChatMessages.size() == 0)
            GetLastestMessagesFromServer(mHandler);
        else
            GetNewMessagesFromServer(mChatMessages.get(0).getSendtime(), mHandler);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_teacher_messagebox, container, false);
        initViews(root);
        setListeners();
        return root;
    }

    private void initViews(View root)
    {
        mListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        imageViewPictureUpload = (ImageView)root.findViewById(R.id.pictureImageView);
        sendContentEditText    = (EditText)root.findViewById(R.id.contentEditText);
        mLayoutManager = (LinearLayoutManager) mListView.getLayoutManager();
    }

    private void setListeners()
    {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                MessageEntity letterEntity = mChatMessages.get(0);
                String endtime = letterEntity.getSendtime();
                GetNewMessagesFromServer(endtime, mHandler);
            }
        });
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
                    MessageEntity messageEntity = mChatMessages.get(mChatMessages.size() - 1);
                    String starttime = messageEntity.getSendtime();
                    GetOldMessagesFromServer(starttime, mHandler);

                    loading = true;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

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

    @Override
    public void GetMessagesFromCache() {
        super.GetMessagesFromCache();

        //Filter out the letters from this teacher and myself to this teacher
        filterMessages();
    }

    /**
     * 发送私信
     *
     * @param picPathString 不为null表示发送照片
     * @param contentString 不为null表示发送字符串
     */

    private void SendLetter(final String picPathString, final String contentString) {
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
        RequestParams params = new RequestParams();
        params.put("receiverid", mTeacher.getId());
        params.put("receiverrole", "teacher");
        params.put("lettertype", lettertype);
        if(!picPathString.equals(""))
            params.put("picture", fbody);
        if(!contentString.equals(""))
            params.put("message", contentString);
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
                    mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mHandler.sendEmptyMessage(HandlerConstant.MSG_SERVER_ERROR);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    private void insertCards() {
        for (int i = mChatMessages.size()-1; i > -1; i--) {
            mListView.add(build_card(mChatMessages.get(i)));
        }
    }

    private void appendCards() {
        for (int i = 0; i < mChatMessages.size(); i++) {
            mListView.add(build_card(mChatMessages.get(i)));
        }
    }

    private boolean filterMessages()
    {
        boolean ret = false;
        for(int i=0; i<mMesageEntities.size(); i++)
        {
            MessageEntity chatMessage = mMesageEntities.get(i);
            if(chatMessage.getApptype().equals("Letter")
                    && (chatMessage.getSenderEntity().getId().equals(mTeacher.getId())
                    || chatMessage.getSenderEntity().getId().equals("Myself")))
            {
                mChatMessages.add(chatMessage);
                ret = true;
            }
        }

        return ret;
    }

    private ChatMessageCard build_card(MessageEntity chatMessage)
    {
        ChatMessageCard chatMessageCard = new ChatMessageCard(mParentContext);

        chatMessageCard.setRoleType(chatMessage.getSenderEntity().getRole());
        String content = chatMessage.getMessageBodyEntityList().get(0).getContent();
        String letterType;
        if(content.contains("http://")) {
            letterType = "picture";
            chatMessageCard.setContentImage(content);
        }
        else {
            letterType = "text";
            chatMessageCard.setContentText(content);
        }
        chatMessageCard.setLetterType(letterType);
        chatMessageCard.setTimestamp(chatMessage.getSendtime());

        return chatMessageCard;
    }

    /**
     * 拍照获取图片
     *
     */
    private void doTakePhoto() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地手机中选择图片
     */
    private void doSelectImageFromLocal() {
        Intent localIntent = new Intent();
        localIntent.setType("image/*");
        localIntent.setAction("android.intent.action.GET_CONTENT");
        Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
        startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: // 从本地选择图片
                if (bitMap != null && !bitMap.isRecycled()) {
                    bitMap.recycle();
                }
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        bitMap = BitmapFactory.decodeStream(mParentContext.getContentResolver().openInputStream(selectedImageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // 下面这两句是对图片按照一定的比例缩放
                    if (bitMap == null) {
                        SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.invalid_picture))
                                .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                        return;
                    }
                    int scale = ImageUtil.reckonThumbnail(bitMap.getWidth(), bitMap.getHeight(), 109, 127);
                    bitMap = ImageUtil.PicZoom(bitMap, (int) (bitMap.getWidth() / scale),
                            (int) (bitMap.getHeight() / scale));

                    ChatMessageCard chatMessageCard = new ChatMessageCard(mParentContext);
                    chatMessageCard.setLetterType("image");
                    chatMessageCard.setRoleType("parents");
                    chatMessageCard.setmDrawableContentImage(Drawable.createFromPath(bitmapFilePath));
                    mListView.add(chatMessageCard);
//                    xImageView.setVisibility(View.VISIBLE);

                    // 获取相册图片的路径
                    String[] proj = { MediaStore.Images.Media.DATA };
                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
                    @SuppressWarnings("deprecation")
                    Cursor cursor = ((MainActivity)(mParentContext)).managedQuery(selectedImageUri, proj, null, null, null);
                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    bitmapFilePath = cursor.getString(column_index);

                }
                break;
            case CAMERA_WITH_DATA: // 拍照
                // Bundle bundle = data.getExtras();
                // bitMap = (Bitmap)bundle.get("data");
                if (bitMap != null && !bitMap.isRecycled())
                    bitMap.recycle();
                bitMap = (Bitmap) data.getExtras().get("data");
                bitmapFilePath = ImageUtil.saveImage(bitMap);// 将图片保存到指定的路径
                int scale = ImageUtil.reckonThumbnail(bitMap.getWidth(), bitMap.getHeight(), 109, 127);
                bitMap = ImageUtil.PicZoom(bitMap, (int) (bitMap.getWidth() / scale), (int) (bitMap.getHeight() / scale));
                ChatMessageCard chatMessageCard = new ChatMessageCard(mParentContext);
                chatMessageCard.setLetterType("image");
                chatMessageCard.setRoleType("parents");
                chatMessageCard.setmDrawableContentImage(Drawable.createFromPath(bitmapFilePath));
                mListView.add(chatMessageCard);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.pictureImageView:
                ListDialogFragment
                        .createBuilder(mParentContext, getFragmentManager())
                        .setTitle(getResources().getString(R.string.picture_ops))
                        .setItems(new String[]{getResources().getString(R.string.picture_ops_album),
                                               getResources().getString(R.string.picture_ops_take_pic),})
                        .setRequestCode(REQUEST_LIST_SIMPLE)
                        .show();
                break;
            case R.id.sendButton:

                String sendContentText = sendContentEditText.getText().toString().trim();
                if(sendContentText.equals(""))
                {
                    Toast.makeText(mParentContext, getResources().getString(R.string.no_content), Toast.LENGTH_SHORT).show();
                } else {
                    SendLetter(bitmapFilePath, sendContentText);
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == REQUEST_LIST_SIMPLE || requestCode == REQUEST_LIST_SINGLE) {
//            Toast.makeText(mParentContext, "Selected: " + value, Toast.LENGTH_SHORT).show();
            if(value.equals(getResources().getString(R.string.picture_ops_album)))
                doSelectImageFromLocal();
            else if(value.equals(getResources().getString(R.string.picture_ops_take_pic)))
                doTakePhoto();
        }
    }

    @Override
    public void onCancelled(int requestCode) {
        switch (requestCode) {
            case REQUEST_LIST_SIMPLE:
            case REQUEST_LIST_SINGLE:
            case REQUEST_LIST_MULTIPLE:
//                Toast.makeText(mParentContext, "Nothing selected", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
