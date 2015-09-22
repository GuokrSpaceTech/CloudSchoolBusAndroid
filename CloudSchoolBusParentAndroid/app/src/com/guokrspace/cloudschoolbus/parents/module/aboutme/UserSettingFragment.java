package com.guokrspace.cloudschoolbus.parents.module.aboutme;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.support.utils.ImageUtil;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.avast.android.dialogs.iface.ISimpleDialogCancelListener;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.event.AvatarChangedEvent;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjianfeng on 15/8/13.
 */
public class UserSettingFragment extends BaseFragment implements IListDialogListener, ISimpleDialogCancelListener {

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

    /* 用来标识请求gallery的activity */
    private ImageView imageViewAvatar;
    private LinearLayout layoutName;
    private LinearLayout layoutPhone;
    private LinearLayout layoutRelation;
    private Button switchInfoButton;

        // 上传图片
        private Bitmap bitMap;
        private String bitmapFilePath = "";

    private Object mUser;

        private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case HandlerConstant.MSG_AVATAR_STUDENT_OK:
                    imageViewAvatar.setImageBitmap(bitMap);
                    BusProvider.getInstance().post(new AvatarChangedEvent(bitMap));
                    break;
                case HandlerConstant.MSG_AVATAR_STUDENT_FAIL:
                    //TODO: handling the failure
                    break;
                case HandlerConstant.MSG_ONREFRESH:
                    hideWaitDialog();
                    break;
                case HandlerConstant.MSG_ONLOADMORE:
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

    public static UserSettingFragment newInstance(Object user)
    {
        UserSettingFragment fragment = new UserSettingFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", (Serializable) user);
        fragment.setArguments(args);
        return fragment;
    }

    public UserSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null){
            mUser = getArguments().get("user");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = null;
        if(Version.PARENT)
            root = inflater.inflate(R.layout.activity_setting, container, false);
        else
            root = inflater.inflate(R.layout.fragment_user_setting, container, false);
        layoutPhone = (LinearLayout)root.findViewById(R.id.linearLayoutPhone);
        layoutRelation = (LinearLayout)root.findViewById(R.id.linearLayoutRelation);
        imageViewAvatar = (ImageView)root.findViewById(R.id.imageViewAvatar);
        switchInfoButton = (Button)root.findViewById(R.id.switchButton);

        String avatar = "";
        if(mUser instanceof StudentEntity) {
            avatar = ((StudentEntity)mUser).getAvatar();
        } else if(mUser instanceof StudentEntityT) {
            avatar = ((StudentEntityT)mUser).getAvatar();
        } else if(mUser instanceof TeacherEntityT) {
            avatar = ((TeacherEntityT)mUser).getAvatar();
        }
        if(!avatar.equals(""))
            Picasso.with(mParentContext).load(avatar).into(imageViewAvatar);

        setListeners();

        return root;
    }

    private void setListeners()
    {
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListDialogFragment
                        .createBuilder(mParentContext, getFragmentManager())
                        .setTitle(getResources().getString(R.string.picture_ops))
                        .setItems(new String[]{getResources().getString(R.string.picture_ops_album),
                                getResources().getString(R.string.picture_ops_take_pic)})
                        .setRequestCode(REQUEST_LIST_SINGLE)
                        .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                        .setTargetFragment(mFragment, REQUEST_LIST_SIMPLE)
                        .show();
            }
        });

        switchInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSwithUserDialog();
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
                break;
        }

        if(bitmapFilePath!=null)
            if(!bitmapFilePath.equals(""))
            {
                if(Version.PARENT)
                    changeAvatarStudent(mApplication.mStudents.get(mApplication.mConfig.getCurrentChild()).getStudentid(),bitmapFilePath, mHandler);
                else
                    changeAvatarStudent(getMyself().getTeacherid(),bitmapFilePath, mHandler);
            }
    }

    private void showSwithUserDialog()
    {
        ArrayList<ClassEntityT> classes = findMyClass();
        SelectUserDialogFragment theDialogFragment = null;
        if (Version.PARENT) {
            theDialogFragment  = SelectUserDialogFragment.newInstance((ArrayList) mApplication.mStudents, "student");
        } else {
            theDialogFragment = SelectUserDialogFragment.newInstance(classes, "class");
        }

//        theDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_light);

        theDialogFragment.show(getFragmentManager(),"");
    }
}
