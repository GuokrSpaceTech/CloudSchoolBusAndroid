package com.guokrspace.cloudschoolbus.parents.module.classes.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.service.UploadFileHelper;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.view.EditCommentView;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.view.SelectedStuView;

import net.soulwolf.image.picturelib.model.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SelectStudentActivity extends BaseActivity {

    private ArrayList<UploadFile> mUploadFiles = new ArrayList<>();
    private ArrayList<Picture> mPictures;
    private EditCommentView mCommentView;
    private SelectedStuView mStudentView;
    private String mCommentStr;
    private String mTagListStr;
    private String mStudentListStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student);

        Intent intent = getIntent();
        mPictures = (ArrayList<Picture>)intent.getSerializableExtra("pictures");

        for (int i = 0; i < mPictures.size(); i++) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.picPathString = mPictures.get(i).getPicturePath();
            uploadFile.picSizeString = getPicSize(uploadFile.picPathString)
                    + "";
            uploadFile.picFileString = getPicName(uploadFile.picPathString);
            uploadFile.studentIdList = "";
            uploadFile.classuid = "";
            uploadFile.intro = null;
            uploadFile.photoTag = null;
            uploadFile.teacherid = mApplication.mConfig.getUserid();
            mUploadFiles.add(uploadFile);
        }

        mCommentView = new EditCommentView(this, mPictures);
        mStudentView = new SelectedStuView(this, mPictures ,mApplication.mStudents);
        RelativeLayout container = (RelativeLayout)findViewById(R.id.mainRelLayout);
        container.addView(mCommentView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mCommentView.setId(getResources().getInteger(R.integer.content_edit));
        params.addRule(RelativeLayout.BELOW, mCommentView.getId());
        container.addView(mStudentView,params);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_student, menu);
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

            updateUploadList();
            UploadFileHelper.getUploadUtils().setContext(mContext);
            UploadFileHelper.getUploadUtils().setUploadFileDB(mUploadFiles);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainRelLayout, new UploadListFragment(), "uploadList");
            transaction.addToBackStack("uploadList");
            transaction.commit();
            return true;
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

    private void updateUploadList()
    {
        for (int i = 0; i < mPictures.size(); i++) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.picPathString = mPictures.get(i).getPicturePath();
            uploadFile.picSizeString = getPicSize(uploadFile.picPathString)
                    + "";
            uploadFile.picFileString = getPicName(uploadFile.picPathString);
            uploadFile.studentIdList = mStudentListStr;
            uploadFile.classuid = "";
            uploadFile.intro = mCommentStr;
            uploadFile.photoTag = mTagListStr;
            uploadFile.teacherid = mApplication.mConfig.getUserid();
            mUploadFiles.add(uploadFile);
        }
    }
}
