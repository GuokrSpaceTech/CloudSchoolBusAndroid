package com.guokrspace.cloudschoolbus.parents.module.aboutme;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.support.utils.ImageUtil;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.avast.android.dialogs.iface.ISimpleDialogCancelListener;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.ServerInteractions;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.fragment.WebviewFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.event.AvatarChangedEvent;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.event.IsUploadingEvent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import net.soulwolf.image.picturelib.PictureFrom;
import net.soulwolf.image.picturelib.PictureProcess;
import net.soulwolf.image.picturelib.listener.OnPicturePickListener;
import net.soulwolf.image.picturelib.model.Picture;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjianfeng on 15/8/13.
 */
public class AboutmeFragment extends BaseFragment implements
        IListDialogListener,
        ISimpleDialogCancelListener,
        ISimpleDialogListener,OnPicturePickListener
{

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

    private ImageView imageViewAvatar;
    private TextView textViewUserName;
    private Button  buttonKindergarten;
    private LinearLayout layoutSwitch;
    private TextView textViewCache;
    private LinearLayout layoutClearCache;
    private LinearLayout layoutUploadRecord;
    private LinearLayout layoutHelpFeedback;
    private LinearLayout layoutQRCode;
    private ImageView redDotIcon;
    private Button logoutButton;
    private int currentChild;
    private boolean isUploading = false;

    private PictureProcess mPictureProcess;

    // 上传图片
    private Bitmap bitMap;
    private String bitmapFilePath = "";

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case HandlerConstant.MSG_AVATAR_STUDENT_OK:
                    imageViewAvatar.setImageBitmap(bitMap);
                    Bundle bundle = msg.getData();
                    String avatarurl = (String)bundle.get("filepath");
                    String userid    = (String)bundle.get("userid");
                    String localPath = (String)bundle.get("cache");
                    if(userid!=null && avatarurl!=null) {
                        saveUserAvatarInfo(userid, avatarurl);
                        updateUserInfoUI();
                        File file = new File(localPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    break;
                case HandlerConstant.MSG_AVATAR_STUDENT_FAIL:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_ONCACHE:
                    break;
                case HandlerConstant.MSG_NOCHANGE:
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_NO_NETOWRK:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.no_network))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_SERVER_ERROR:
                    SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.server_error))
                            .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
                    hideWaitDialog();
                    break;
            }
            return false;
        }
    });

    public static AboutmeFragment newInstance()
    {
        AboutmeFragment fragment = new AboutmeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AboutmeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {}

        mPictureProcess = new PictureProcess(this, mApplication.getCacheDir());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_aboutme, container, false);
        layoutSwitch = (LinearLayout)root.findViewById(R.id.linearLayoutSwitchuser);
        layoutClearCache = (LinearLayout)root.findViewById(R.id.linearLayoutClearcache);
        layoutUploadRecord = (LinearLayout)root.findViewById(R.id.linearLayoutUploadRecord);
        layoutHelpFeedback = (LinearLayout)root.findViewById(R.id.linearLayoutHelp);
        layoutQRCode  = (LinearLayout)root.findViewById(R.id.linearLayoutQRCode);
        logoutButton = (Button)root.findViewById(R.id.logoutButton);
        imageViewAvatar = (ImageView)root.findViewById(R.id.child_avatar);
        textViewUserName = (TextView)root.findViewById(R.id.child_name);
        textViewCache = (TextView)root.findViewById(R.id.textViewCacheSize);
        buttonKindergarten = (Button)root.findViewById(R.id.kindergarten_name);
        redDotIcon = (ImageView)root.findViewById(R.id.red_dot);
        if(isUploading == false)
            redDotIcon.setVisibility(View.INVISIBLE);
        else
            redDotIcon.setVisibility(View.VISIBLE);

            currentChild = mApplication.mConfig.getCurrentuser();

        /* Get the current user's avatar */
        updateUserInfoUI();

            buttonKindergarten.setText(mApplication.mSchools.get(0).getName());
            layoutUploadRecord.setVisibility(View.GONE);
            View divider = root.findViewById(R.id.divider3);
            divider.setVisibility(View.GONE);
            divider = root.findViewById(R.id.divider2);
            divider.setVisibility(View.GONE);
            TextView switchTextView = (TextView)root.findViewById(R.id.switchTextView);
            switchTextView.setText(getResources().getString(R.string.switch_child));
            layoutQRCode.setVisibility(View.GONE);


        textViewCache.setText(getDBSize());

        setHasOptionsMenu(true);

        setListeners();

        return root;
    }

    private void setListeners()
    {
        layoutSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSwithUserDialog();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signout();
            }
        });
        final AboutmeFragment aboutmeFragment = this;
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPictureProcess.setPictureFrom(PictureFrom.GALLERY);
                mPictureProcess.setClip(true);
                mPictureProcess.setMaxPictureCount(1);
                mPictureProcess.execute(aboutmeFragment);
            }
        });

        layoutHelpFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mParentContext).pager.lock();
                WebviewFragment fragment = WebviewFragment.newInstance(getResources().getString(R.string.aboutmeurl), getResources().getString(R.string.companyweb), "");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_aboutme_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        layoutClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mApplication.clearData();
                textViewCache.setText("0M");
            }
        });
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
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        if (requestCode == REQUEST_LIST_SIMPLE || requestCode == REQUEST_LIST_SINGLE) {
            if(value.equals(getResources().getString(R.string.picture_ops_album)))
                doSelectImageFromLocal();
            else if(value.equals(getResources().getString(R.string.picture_ops_take_pic)))
                doTakePhoto();
        }
    }
    @Override
    public void onCancelled(int requestCode) {

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
        mPictureProcess.onProcessResult(requestCode, resultCode, data); //This only handles the picturechooselib activity results


        switch (requestCode) {
            case 3001:
                if (data != null)
                    if (data.getExtras() != null) {
                        String result = (String) data.getExtras().get("result");
                        if (result != null && !result.isEmpty())
                            ServerInteractions.getInstance().QRCodeLogin(result, mHandler);
                    }
                return;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((MainActivity)mParentContext).getSupportActionBar().setTitle(getResources().getString(R.string.module_aboutme));
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            ((MainActivity)mParentContext).pager.unlock();
                break;
                default:break;
        }
        return false;
    }

    private void showSwithUserDialog()
    {
        SelectUserDialogFragment theDialogFragment = null;
        theDialogFragment  = SelectUserDialogFragment.newInstance((ArrayList) mApplication.mStudents, "student");

        theDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.MyFragmentDialogStyle);

        theDialogFragment.show(getFragmentManager(), "");
    }


    //Signout
    public void signout()
    {
        SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.altersignout))
                .setPositiveButtonText(getResources().getString(R.string.OKAY)).setNegativeButtonText(getResources().getString(R.string.cancel))
                .setTargetFragment(mFragment, REQUEST_SIMPLE_DIALOG).show();
    }

    // ISimpleDialogListener
    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SIMPLE_DIALOG) {
            /**
             * Just clear the data, DB will stay open
             * LoginActivity will re-init the Database
             */
            mApplication.clearConfig();
            mApplication.clearData();
            mApplication.clearBaseinfo();

            ((MainActivity)mParentContext).finish();
            Intent intent = new Intent(mParentContext, MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SIMPLE_DIALOG) {
        }
    }
    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    @Subscribe
    public void onAvatarChanged(AvatarChangedEvent event)
    {
        Bitmap avatarBitmap = event.getBitMap();
        imageViewAvatar.setImageBitmap(avatarBitmap);
    }

    @Subscribe
    public void onChildrenSwitched(InfoSwitchedEvent event)
    {
        currentChild = event.getCurrentChild();
        updateUserInfoUI();
    }

    @Subscribe public void onUserIsUploadingEvent(IsUploadingEvent event)
    {
        //Just set a red dot
        isUploading = true;
    }

    private void updateUserInfoUI()
    {
        /* Get the current child's avatar */
        StudentEntity studentEntity = null;
        String avatar = "";
            studentEntity = mApplication.mStudents.get(currentChild);

            avatar = studentEntity.getAvatar();

            if(avatar!=null && !avatar.isEmpty())
                Picasso.with(mParentContext).load(avatar).fit().centerCrop().into(imageViewAvatar);

            if (studentEntity.getNikename() == null)
                textViewUserName.setText(studentEntity.getCnname());
            else
                textViewUserName.setText(studentEntity.getNikename());

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private String getDBSize()
    {
        String dbName = mApplication.mDBhelper.getDatabaseName();

        double fileSize = mParentContext.getDatabasePath(dbName).length();;
        String fileSizeString = "0";
        try {
            //Baseinfo size
            fileSize = fileSize / 1024 / 1024 - 0.2;
            DecimalFormat df = new DecimalFormat("0.00");
            fileSizeString = df.format(fileSize) + "M";
        } catch (Exception e) {
        }

        return fileSizeString;
    }

    /*
 * converts dip to px
 */
    private int dip2Px(float dip) {
        return (int) (dip * mParentContext.getResources().getDisplayMetrics().density + 0.5f);
    }

    private void saveUserAvatarInfo(String userid, String url) {


            for(StudentEntity student : mApplication.mStudents)
            {
                if(student.getStudentid().equals(userid))
                {
                    student.setAvatar(url);
                    mApplication.mDaoSession.getStudentEntityDao().update(student);
                    break;
                }
            }
            mApplication.mStudents = mApplication.mDaoSession.getStudentEntityDao().queryBuilder().list();

    }

    @Override
    public void onSuccess(List<Picture> pictures) {

        Log.i("", "");
    }

    @Override
    public void onSuccessString(List<String> pictures) {
        Log.i("", "");

        if (bitMap != null && !bitMap.isRecycled()) {
            bitMap.recycle();
        }

        Uri selectedImageUri = Uri.fromFile(new File(pictures.get(0)));
        if (selectedImageUri != null) {
            try {
                bitMap = BitmapFactory.decodeStream(mParentContext.getContentResolver().openInputStream(selectedImageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        int scale = ImageUtil.reckonThumbnail(bitMap.getWidth(), bitMap.getHeight(), 109, 127);
        bitMap = ImageUtil.PicZoom(bitMap, (int) (bitMap.getWidth() / scale), (int) (bitMap.getHeight() / scale));
        bitmapFilePath = ImageUtil.saveImage(bitMap);// 将图片保存到指定的路径

        // 下面这两句是对图片按照一定的比例缩放
        if (bitMap == null) {
            SimpleDialogFragment.createBuilder(mParentContext, getFragmentManager()).setMessage(getResources().getString(R.string.invalid_picture))
                    .setPositiveButtonText(getResources().getString(R.string.OKAY)).show();
            return;
        }


        if(bitmapFilePath!=null)
        {
            int currentchild = mApplication.mConfig.getCurrentuser();
            String studentid = mApplication.mStudents.get(currentchild).getStudentid();
            ServerInteractions.getInstance().changeAvatarUser(studentid, bitmapFilePath, mHandler);
        }

    }

    @Override
    public void onError(Exception e) {
        Log.i("", "");

    }

    @Override
    public void onCancel() {
        Log.i("", "");

    }
}
