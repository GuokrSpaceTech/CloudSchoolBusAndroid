package com.guokrspace.cloudschoolbus.parents.module.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.parents.module.photo.service.UploadFileHelper;
import com.guokrspace.cloudschoolbus.parents.module.photo.view.EditCommentView;
import com.guokrspace.cloudschoolbus.parents.module.photo.view.SelectedStuView;

import net.soulwolf.image.picturelib.model.Picture;
import net.soulwolf.image.picturelib.ui.BigImageGalleryFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SelectStudentActivity extends BaseActivity implements ISimpleDialogListener{


    private ArrayList<Picture> mPictures = new ArrayList<>();
    private EditCommentView mCommentView;
    private SelectedStuView mStudentView;
    private String mCommentStr;
    private String mTagListStr;
    private String mStudentListStr;
    public MenuItem mUploadAction;
    private CommonRecyclerItemClickListener mThumbNailClickListener;
    public AdapterView.OnItemClickListener mStudentClickListener;

    private static final int REQUEST_SIMPLE_DIALOG = 42;

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
                transaction.add(R.id.mainFrameLayout,new BigImageGalleryFragment().newInstance(mPictures,position,false));
                transaction.addToBackStack("big_picture");
                transaction.commit();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mCommentView = new EditCommentView(this, mPictures);
        mCommentView.setmThumbNailClickListener(mThumbNailClickListener);

        mStudentView = new SelectedStuView(this , findStudentsinClass(findCurrentClass().getClassid()));
        RelativeLayout container = (RelativeLayout)findViewById(R.id.mainLayout);
        container.addView(mCommentView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mCommentView.setId(getResources().getInteger(R.integer.content_edit));
        params.addRule(RelativeLayout.BELOW, mCommentView.getId());
        container.addView(mStudentView, params);
        mStudentClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!mStudentView.getSelectionString().equals(""))
                {
                    mUploadAction.setEnabled(false);
                } else
                    mUploadAction.setEnabled(true);
            }
        };
        mStudentView.setmItemClickListener(mStudentClickListener);
        getSupportActionBar().setTitle(getResources().getString(R.string.upload));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            mCommentStr = mCommentView.getCommentText();
            mTagListStr = mCommentView.getTagListString();
            mStudentListStr = mStudentView.getSelectionString();


            UploadFileHelper.getInstance().setContext(mContext);
            UploadFileHelper.getInstance().addUploadQueue(mPictures, mCommentStr, mTagListStr, mStudentListStr);
//            UploadFileHelper.getInstance().setUploadFileDB(mUploadFiles);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainFrameLayout, new UploadListFragment(), "uploadlist");
            transaction.addToBackStack("uploadlist");
            transaction.commit();
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




    public ArrayList<StudentEntityT> findStudentsinClass(String classid)
    {
        ArrayList<StudentEntityT> retStudents = new ArrayList<>();
        for(StudentEntityT student:mApplication.mStudentsT)
        {
            for(StudentClassRelationEntity relation:mApplication.mStudentClasses) {
                if(relation.getClassid().equals(classid)) {
                    if (student.getStudentid().equals(relation.getStudentid()))
                    {
                        //Found the student, then find the parents
                        retStudents.add(student);
                        break;
                    }
                }
            }
        }
        return retStudents;
    }

    public ArrayList<ClassEntityT> findMyClass()
    {
        ArrayList<ClassEntityT> retEntity= new ArrayList<ClassEntityT>();

        for(ClassEntityT theClass: mApplication.mClassesT)
        {
            for(TeacherDutyClassRelationEntity relation:mApplication.mTeacherClassDutys) {
                if (theClass.getClassid().equals(relation.getClassid())) {
                    retEntity.add(theClass);
                }
            }
        }

        return retEntity;
    }

    public ClassEntityT findCurrentClass()
    {
        ClassEntityT retEntity=null;

        int current = mApplication.mConfig.getCurrentChild();
        String classid = mApplication.mTeacherClassDutys.get(current).getClassid();

        for(ClassEntityT theClass: findMyClass())
        {
            if(theClass.getClassid().equals(classid))
            {
                retEntity = theClass; break;
            }
        }
        return retEntity;
    }
}