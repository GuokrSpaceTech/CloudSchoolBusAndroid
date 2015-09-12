package com.guokrspace.cloudschoolbus.parents.module.classes.photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.view.EditContentView;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.view.SelectedStuView;

import net.soulwolf.image.picturelib.model.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SelectStudentActivity extends BaseActivity {

    private ArrayList<UploadFile> mUploadFiles = new ArrayList<>();
    private ArrayList<Picture> mPictures;

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
            uploadFile.studentIdList = new ArrayList<String>();
            uploadFile.classuid = "";
            uploadFile.intro = null;
            uploadFile.photoTag = null;
            uploadFile.teacherid = mApplication.mConfig.getUserid();
            mUploadFiles.add(uploadFile);
        }

        EditContentView contentView = new EditContentView(this, mUploadFiles, mPictures);
        SelectedStuView studentView = new SelectedStuView(this, mPictures ,mApplication.mStudents);
        RelativeLayout container = (RelativeLayout)findViewById(R.id.mainRelLayout);
        container.addView(contentView);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        contentView.setId(getResources().getInteger(R.integer.content_edit));
        params.addRule(RelativeLayout.BELOW, contentView.getId());
        container.addView(studentView,params);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
}
