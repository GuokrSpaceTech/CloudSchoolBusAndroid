package com.guokrspace.cloudschoolbus.teacher.module.photo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.guokrspace.cloudschoolbus.teacher.MainActivity;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.base.DataWrapper;
import com.guokrspace.cloudschoolbus.teacher.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.IsUploadingEvent;
import com.guokrspace.cloudschoolbus.teacher.module.photo.service.UploadFileHelper;
import com.guokrspace.cloudschoolbus.teacher.module.photo.view.EditCommentView;
import com.guokrspace.cloudschoolbus.teacher.module.photo.view.SelectedStuView;

import net.soulwolf.image.picturelib.model.Picture;
import net.soulwolf.image.picturelib.ui.BigImageGalleryFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectStudentActivity extends BaseActivity implements ISimpleDialogListener{


    private ArrayList<Picture> mPictures = new ArrayList<>();
    private EditCommentView mCommentView;
    private SelectedStuView mStudentView;
    private String mCommentStr;
    public MenuItem mUploadAction;
    private CommonRecyclerItemClickListener mThumbNailClickListener;
    public AdapterView.OnItemClickListener mStudentClickListener;

    private static final int REQUEST_SIMPLE_DIALOG = 42;

    Boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student);

        Intent intent = getIntent();

        ArrayList<?> data = (ArrayList<?>)intent.getSerializableExtra("pictures");

        for (int i = 0; i < data.size(); i++) {
            Picture picture = new Picture();
            if(data.get(i) instanceof String) {
                picture.setPicturePath((String) data.get(i));
            }
            else if (data.get(i) instanceof Picture) {
                picture = (Picture)data.get(i);
            }
            mPictures.add(i, picture);
        }
        mThumbNailClickListener = new CommonRecyclerItemClickListener(mContext, new CommonRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.mainFrameLayout,new BigImageGalleryFragment().newInstance(mPictures,position,false,R.color.accent));
                transaction.addToBackStack("big_picture");
                transaction.commit();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mCommentView = new EditCommentView(this, mPictures);
        mCommentView.setmThumbNailClickListener(mThumbNailClickListener);

        String classid = DataWrapper.getInstance().findCurrentClass().getClassid();
        List<StudentEntityT> students = DataWrapper.getInstance().findStudentsinClass(classid);
        mStudentView = new SelectedStuView(this , students);
        RelativeLayout container = (RelativeLayout)findViewById(R.id.mainLayout);
        container.addView(mCommentView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mCommentView.setId(getResources().getInteger(R.integer.content_edit));
        params.addRule(RelativeLayout.BELOW, mCommentView.getId());
        container.addView(mStudentView, params);
        mStudentClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!mStudentView.hasSelection())
                {
                    mUploadAction.setEnabled(false);
                } else
                    mUploadAction.setEnabled(true);
            }
        };
        mStudentView.setmItemClickListener(mStudentClickListener);
        getSupportActionBar().setTitle(getResources().getString(R.string.upload));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_student, menu);
        mUploadAction = menu.findItem(R.id.action_upload);
        mUploadAction.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_upload) {

            item.setEnabled(false);

            String pickey = System.currentTimeMillis() + mApplication.mConfig.getUserid();

            mCommentStr = mCommentView.getCommentText();

            mCommentView.updateTagSelectedDb(pickey);
            mStudentView.updateStudentSelectedDb(pickey);

            addUploadQueue(mPictures, mCommentStr, pickey);

            BusProvider.getInstance().post(new IsUploadingEvent());

            if(mIsBound)
                mBoundService.uploadFileService();

//            this.overridePendingTransition(R.anim.scalefromcorner, R.anim.scaletocorner);

            finish();

            return true;
        }
        else if (id == android.R.id.home) {
            //If we are at a fragment, just pop the fragment, if this is upload fragment, finish the activity as well
            if(getSupportFragmentManager().getBackStackEntryCount()>0) {
                UploadListFragment theFragment = (UploadListFragment) getSupportFragmentManager().findFragmentByTag("uploadlist");
                if (theFragment != null && theFragment.isVisible()) {
                    finish();
                }
                getSupportFragmentManager().popBackStack();
            }
            //If we are just in the activity page, check if user really want to exit
            else {
                SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager()).setMessage(getResources().getString(R.string.confirm_cancel_upload))
                        .setPositiveButtonText(getResources().getString(R.string.OKAY))
                        .setNegativeButtonText(getResources().getString(R.string.cancel))
                        .setRequestCode(REQUEST_SIMPLE_DIALOG)
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private String getPicName(String picPathString) {
        String picNameString = "";
        picNameString = picPathString
                .substring(picPathString.lastIndexOf("/") + 1);
        return picNameString;
    }

    private int getPicSize(String picPathString) {
        int size = 0;
        try {
            picPathString = picPathString.replace("file:///", "/");
            FileInputStream inputStream = new FileInputStream(new File(
                    picPathString));
            try {
                size = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return size;
    }

    // ISimpleDialogListener

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SIMPLE_DIALOG) {
            finish();
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SIMPLE_DIALOG) {
            return; //do nothing
        }
    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SIMPLE_DIALOG) {
            Toast.makeText(this, "Neutral button clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //If we are at a fragment, just pop the fragment, if this is upload fragment, finish the activity as well
            if(getSupportFragmentManager().getBackStackEntryCount()>0) {
                UploadListFragment theFragment = (UploadListFragment) getSupportFragmentManager().findFragmentByTag("uploadlist");
                if (theFragment != null && theFragment.isVisible()) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            }
            //If we are just in the activity page, check if user really want to exit
            else {
                SimpleDialogFragment.createBuilder(mContext, getSupportFragmentManager()).setMessage(getResources().getString(R.string.confirm_cancel_upload))
                        .setPositiveButtonText(getResources().getString(R.string.OKAY))
                        .setNegativeButtonText(getResources().getString(R.string.cancel))
                        .setRequestCode(REQUEST_SIMPLE_DIALOG)
                        .show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addUploadQueue(final ArrayList<Picture> pictureList, final String content, final String pickey)
    {
        int currentclass = mApplication.mConfig.getCurrentChild();
        String classid = mApplication.mTeacherClassDutys.get(currentclass).getClassid();

        UploadArticleEntity uploadArticle = new UploadArticleEntity();
        uploadArticle.setPickey(pickey);
        uploadArticle.setTeacherid(mApplication.mConfig.getUserid());
        uploadArticle.setPictype("article");
        uploadArticle.setClassid(classid);
        uploadArticle.setContent(content);
        uploadArticle.setSendtime(System.currentTimeMillis() / 1000 + "");

        mApplication.mDaoSession.getUploadArticleEntityDao().insert(uploadArticle);

        for (Picture picture : pictureList) {
            UploadArticleFileEntity uploadFile = new UploadArticleFileEntity();

            String picPathString = picture.getPicturePath().replace("file:///", "/");
            String fname = "";
            String ftime = "";
            if (!TextUtils.isEmpty(picPathString)) {
                File file = new File(picPathString);
                ftime = (file.lastModified() / 1000) + "";
                fname = picPathString.substring(picPathString.lastIndexOf("/") + 1);
            }

            uploadFile.setFname(fname);
            uploadFile.setFtime(ftime);
            uploadFile.setFbody(picPathString);
            uploadFile.setPictype("article");
            uploadFile.setPickey(pickey);

            mApplication.mDaoSession.getUploadArticleFileEntityDao().insertOrReplace(uploadFile);
        }
    }

    private UploadFileHelper mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((UploadFileHelper.LocalBinder)service).getService();

            // Tell the user about this for our demo.
//            Toast.makeText(Binding.this, R.string.local_service_connected,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this, UploadFileHelper.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}