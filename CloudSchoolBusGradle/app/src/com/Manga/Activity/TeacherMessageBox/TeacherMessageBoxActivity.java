package com.Manga.Activity.TeacherMessageBox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.Entity.Teacher;
import com.Manga.Activity.R;
import com.Manga.Activity.bigPicture.BigPictureActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.UploadPic;
import com.android.support.jhf.androidpulltorefresh.library.ILoadingLayout;
import com.android.support.jhf.androidpulltorefresh.library.PullToRefreshBase;
import com.android.support.jhf.androidpulltorefresh.library.PullToRefreshListView;
import com.android.support.jhf.debug.DebugLog;
import com.android.support.jhf.handlerui.HandlerPostUI;
import com.android.support.jhf.handlerui.HandlerToastUI;
import com.android.support.jhf.popupwindow.CustomPopupWindow;
import com.android.support.jhf.utils.ClipboardUtils;
import com.android.support.jhf.utils.DateUtils;
import com.android.support.jhf.utils.ToolUtils;
import com.cytx.utility.FastJsonTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import me.maxwin.view.XListView;

/**
 * Created by kai on 12/25/14.
 */
public class TeacherMessageBoxActivity extends BaseActivity {
    private PullToRefreshListView mListView;
    private EditText mContentEditText;
    private UploadPic mUploadPic;
    private Teacher mTeacher;
    private List<LetterDto> mLetterList = new ArrayList<LetterDto>();
    private LettersAdapter mAdapter;
    private InputMethodManager mImm;
    private String mLastStartTime = "0";
    private String mLastEndTime = "0";

    private final int OUTTIME = -1;
    private final int NONETWORK = -2;
    private final int GOTMESSAGE = 1;
    private final int MESSAGESENT = 2;
    private final int NOMOREDATA = 3;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case OUTTIME:
                    ActivityUtil.main.setCancelPRO();
                    Toast.makeText(TeacherMessageBoxActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
                    ActivityUtil.main.setCancelPRO();
                    break;
                case NONETWORK:
                    ActivityUtil.main.setCancelPRO();
                    Toast.makeText(TeacherMessageBoxActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
                    break;
                case GOTMESSAGE:
                    ActivityUtil.main.setCancelPRO();
                    mContentEditText.setText("");
                    mImm.hideSoftInputFromWindow(mContentEditText.getWindowToken(), 0);
                    mAdapter.setmLetters(mLetterList);
                    mAdapter.notifyDataSetChanged();
                    mListView.getRefreshableView().setSelection(0);
                    break;
                case MESSAGESENT:
                    GetPrivateLetters(mLastStartTime, mLastEndTime, mTeacher.getTeacherid(), false);
                    break;
                case NOMOREDATA:
                    Toast.makeText(TeacherMessageBoxActivity.this, R.string.not_more_data, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_messagebox);

        mUploadPic = new UploadPic(TeacherMessageBoxActivity.this);
        mListView = (PullToRefreshListView) findViewById(R.id.listView);
        mContentEditText = (EditText) findViewById(R.id.contentEditText);

        mAdapter = new LettersAdapter(getApplicationContext(), mLetterList);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout iLoadingLayout = mListView
                .getLoadingLayoutProxy();
        iLoadingLayout.setPullLabel(getString(R.string.pull_down_more));
        iLoadingLayout
                .setRefreshingLabel(getString(R.string.release_loading_more));
        iLoadingLayout
                .setReleaseLabel(getString(R.string.release_loading_more));
        ListView listView = mListView.getRefreshableView();
        listView.setSelector(getResources().getDrawable(
                android.R.color.transparent));
        listView.setDivider(getResources().getDrawable(
                android.R.color.transparent));
        listView.setDividerHeight(10);
        mListView.setAdapter(mAdapter);

        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTeacher = (Teacher) bundle.getSerializable("teacher");

        GetPrivateLetters(mLastStartTime, mLastEndTime, mTeacher.getTeacherid(), false);

        setListener();
    }

    void setListener() {
        mContentEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (source.equals("\n")) {
                    return "";
                }
                return source;
            }
        }});

        mUploadPic.setOnGetPicSucceed(new UploadPic.OnGetPicSucceed() {

            @Override
            public void onGetPicSucceed(String picPathString, int requestCode) {
                DebugLog.logI("picPathString : " + picPathString);
                sendPrivateLetter(picPathString, null, requestCode);
            }
        });

        ImageView pictureImageView = (ImageView) findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mUploadPic.doPickPhotoAction();
            }
        });
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(mContentEditText.getText().toString())) {
                    // HandlerToastUI.getHandlerToastUI(mParentContext,
                    // "请填写问题");
                } else {
                    sendPrivateLetter(null, mContentEditText.getText()
                            .toString(), -1);
                }
            }
        });

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mLastStartTime = "0";
                mLastEndTime   = "0";
                GetPrivateLetters(mLastStartTime, mLastEndTime, mTeacher.getTeacherid(), false);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mLastStartTime = mLastEndTime;
                mLastEndTime = "0"; // 15 items by default
                GetPrivateLetters(mLastStartTime, mLastEndTime, mTeacher.getTeacherid(), true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 2000);
            }
        });

        mAdapter.setmImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String contentString = (String) arg0.getTag();
                DebugLog.logI("image contentString : " + contentString);
                Intent intent = new Intent(TeacherMessageBoxActivity.this, BigPictureActivity.class);
                intent.putExtra("image", new String[]{contentString});
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });

        mAdapter.setmTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String contentString = (String) arg0.getTag();
                DebugLog.logI("text contentString : " + contentString);
                copyContentDialog(contentString, arg0);
            }
        });
    }

    private void copyContentDialog(final String contentString, final View anchor) {
        final CustomPopupWindow customPopupWindow = new CustomPopupWindow(
                getApplicationContext());
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.popup_letter_content_copy, null);
        ViewGroup copyLayout = (ViewGroup) view.findViewById(R.id.copyLayout);
        copyLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ClipboardUtils.copy(contentString, getApplicationContext());
                customPopupWindow.dismiss();
            }
        });
        int width = ToolUtils.dipToPx(getApplicationContext(), 50);
        int height = ToolUtils.dipToPx(getApplicationContext(), 50);
        customPopupWindow.setContentView(view, width, height);
        customPopupWindow.show(anchor, anchor.getWidth() / 2 - width / 2, -anchor.getHeight() - height);
    }

    public void GetPrivateLetters(final String starttime, final String endtime, final String teacherid, final boolean loadMoreFlag) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (HttpUtil.isNetworkConnected(TeacherMessageBoxActivity.this)) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("starttime", starttime);
                    map.put("endtime", endtime);
                    map.put("id", teacherid);
                    ActivityUtil.main.disPRO();

                    Result result = HttpUtil.httpGet(TeacherMessageBoxActivity.this, new Params("messageletter", map));
                    if (result == null) {
                        mHandler.sendEmptyMessage(OUTTIME);
                    } else if ("1".equals(result.getCode())) {
                        List<LetterDto> resultList = FastJsonTools.getListObject(result.getContent(), LetterDto.class);

                        //Update the isShowDate flag for each record
                        String listAddTime = "-1";
                        for (int i = 0; i < resultList.size(); i++) {
                            long addtime = 0L;
                            addtime = Long.parseLong(resultList.get(i).getAddtime()) * 1000;
                            String date = DateUtils.dateFormat(addtime, "yyyy-MM-dd");
                            if ("-1".equals(listAddTime) || !date.equals(listAddTime)) {
                                resultList.get(i).isShowDate = true;
                                listAddTime = date;
                            } else {
                                resultList.get(i).isShowDate = false;
                            }
                        }

                        //Update the dataset
                        if (loadMoreFlag)
                            //Append the list
                            mLetterList.addAll(resultList);
                        else
                            mLetterList = resultList;

                        //Update the mLastStartTime for the next load more
                        if (0 != resultList.size()) {
                            mLastEndTime = resultList.get(resultList.size()-1).getAddtime();
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

    /**
     * 发送私信
     *
     * @param picPathString 不为null表示发送照片
     * @param contentString 不为null表示发送字符串
     * @param requestCode   调用系统照相机返回或者是系统相册返回
     */
    private void sendPrivateLetter(final String picPathString,
                                   final String contentString, final int requestCode) {


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (HttpUtil.isNetworkConnected(TeacherMessageBoxActivity.this)) {

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
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id", mTeacher.getTeacherid());
                    map.put("lettertype", lettertype);
                    map.put("fbody", fbody);
                    map.put("ftime", (System.currentTimeMillis() / 1000) + "");
                    map.put("fsize", fsize);
                    map.put("fext", fext);
                    map.put("content", contentString);
                    ActivityUtil.main.disPRO();

                    Result result = HttpUtil.httpPost(TeacherMessageBoxActivity.this, new Params("messageletter", map));
                    if ("1".equals(result.getCode())) {
                        mHandler.sendEmptyMessage(MESSAGESENT);
                    } else {
                        mHandler.sendEmptyMessage(OUTTIME);
                    }
                } else {
                    mHandler.sendEmptyMessage(NONETWORK);
                }
            }
        });
        thread.start();

    }

    //Back button pressed
    public void close(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mUploadPic) {
            mUploadPic.onActivityResult(requestCode, resultCode, data);
        }
    }
}

class LettersAdapter extends BaseAdapter {
    private Context mContext;
    private List<LetterDto> mLetters;
    private String mLastAddTime = "-1";
    private boolean isShowDate = false;

    private View.OnClickListener mImageOnClickListener;
    private View.OnClickListener mTextOnClickListener;

    LettersAdapter(Context context, List<LetterDto> list) {
        mContext = context;
        mLetters = list;
    }

    @Override
    public int getCount() {
        return mLetters.size();
    }

    @Override
    public Object getItem(int i) {
        return mLetters.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_teacher_messagebox_list_item, null);
        }

        final LetterDto letter = mLetters.get(i);
        long addtime = 0L;
        addtime = Long.parseLong(letter.getAddtime()) * 1000;

        LinearLayout dateLayout = (LinearLayout) view.findViewById(R.id.dateLayout);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Hongkong"));
        Date date = new Date(addtime);
        String dateString = simpleDateFormat.format(date);

        if (letter.isShowDate) {
            dateTextView.setVisibility(View.VISIBLE);
            dateLayout.setVisibility(View.VISIBLE);
            dateTextView.setText(dateString);
            mLastAddTime = dateString;
        }
        else
        {
            dateLayout.setVisibility(View.GONE);
            dateTextView.setVisibility(View.GONE);
        }


        ViewGroup leftLayout = (ViewGroup) view.findViewById(R.id.leftLayout);
        TextView leftTextView = (TextView) view.findViewById(R.id.leftTextView);
        ImageView leftImageView = (ImageView) view
                .findViewById(R.id.leftImageView);

        ViewGroup rightLayout = (ViewGroup) view.findViewById(R.id.rightLayout);
        TextView rightTextView = (TextView) view
                .findViewById(R.id.rightTextView);
        ImageView rightImageView = (ImageView) view
                .findViewById(R.id.rightImageView);

        DisplayImageOptions cacheDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_bg)
                .showImageForEmptyUri(R.drawable.default_bg) // empty
                        // URI时显示的图片
                .showImageOnFail(R.drawable.default_bg) // 不是图片文件 显示图片
                .resetViewBeforeLoading(true) // default
                .delayBeforeLoading(1000).cacheInMemory(true) // 缓存至内存
                .cacheOnDisc(true) // 缓存至手机SDCard
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                        // .displayer(new SimpleBitmapDisplayer()) // default
                        // 可以设置动画，比如圆角或者渐变
                        // .handler(new Handler()) // default
                .build();

        if (!"parent".equals(letter.getFrom_role())) {
            leftLayout.setVisibility(View.VISIBLE);
            rightLayout.setVisibility(View.GONE);

            if ("txt".equals(letter.getLetter_type())) {
                leftImageView.setVisibility(View.GONE);
                leftTextView.setVisibility(View.VISIBLE);
                leftTextView.setText(letter.getContent());
                leftLayout.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View arg0) {
                        arg0.setTag(letter.getContent());
                        if (null != mTextOnClickListener) {
                            mTextOnClickListener.onClick(arg0);
                        }
                        return false;
                    }
                });
            } else if ("img".equals(letter.getLetter_type())) {
                leftImageView.setVisibility(View.VISIBLE);
                leftTextView.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(letter.getContent(),
                        leftImageView, cacheDisplayImageOptions,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                ImageView imageView = (ImageView) view;
//								imageView.setImageBitmap(loadedImage);
                                if (imageUri.startsWith("http://")) {
                                    imageView.setImageBitmap(loadedImage);
                                } else if (imageUri.startsWith("file:///")) {
                                    ImageUtil.setRotaingImageBitmap(imageUri.replace("file:///", "/"), loadedImage, imageView);
                                } else {
                                    ImageUtil.setRotaingImageBitmap(imageUri, loadedImage, imageView);
                                }
                            }
                        });
                leftImageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        arg0.setTag(letter.getContent());
                        if (null != mImageOnClickListener) {
                            mImageOnClickListener.onClick(arg0);
                        }
                    }
                });
            }

        } else {
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.VISIBLE);

            if ("txt".equals(letter.getLetter_type())) {
                rightImageView.setVisibility(View.GONE);
                rightTextView.setVisibility(View.VISIBLE);
                rightTextView.setText(letter.getContent());
                rightLayout.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View arg0) {
                        arg0.setTag(letter.getContent());
                        if (null != mTextOnClickListener) {
                            mTextOnClickListener.onClick(arg0);
                        }
                        return false;
                    }
                });
            } else if ("img".equals(letter.getLetter_type())) {
                rightImageView.setVisibility(View.VISIBLE);
                rightTextView.setVisibility(View.GONE);


                ImageLoader.getInstance().displayImage(letter.getContent(),
                        rightImageView, cacheDisplayImageOptions,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                ImageView imageView = (ImageView) view;
//								imageView.setImageBitmap(loadedImage);
                                if (imageUri.startsWith("http://")) {
                                    imageView.setImageBitmap(loadedImage);
                                } else if (imageUri.startsWith("file:///")) {
                                    ImageUtil.setRotaingImageBitmap(imageUri.replace("file:///", "/"), loadedImage, imageView);
                                } else {
                                    ImageUtil.setRotaingImageBitmap(imageUri, loadedImage, imageView);
                                }
                            }
                        });
                rightImageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        arg0.setTag(letter.getContent());
                        if (null != mImageOnClickListener) {
                            mImageOnClickListener.onClick(arg0);
                        }
                    }
                });
            }
        }
        return view;
    }

    public View.OnClickListener getmImageOnClickListener() {
        return mImageOnClickListener;
    }

    public void setmImageOnClickListener(View.OnClickListener mImageOnClickListener) {
        this.mImageOnClickListener = mImageOnClickListener;
    }

    public View.OnClickListener getmTextOnClickListener() {
        return mTextOnClickListener;
    }

    public void setmTextOnClickListener(View.OnClickListener mTextOnClickListener) {
        this.mTextOnClickListener = mTextOnClickListener;
    }

    public List<LetterDto> getmLetters() {
        return mLetters;
    }

    public void setmLetters(List<LetterDto> mLetters) {
        this.mLetters = mLetters;
    }

    public void clearLastAddTime(){
        mLastAddTime = "-1";
    }
}
