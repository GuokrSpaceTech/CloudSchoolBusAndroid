package net.soulwolf.image.picturelib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.toaker.common.tlog.TLog;

import net.soulwolf.image.picturelib.PictureFrom;
import net.soulwolf.image.picturelib.PictureProcess;
import net.soulwolf.image.picturelib.R;
import net.soulwolf.image.picturelib.adapter.PictureChooseAdapter;
import net.soulwolf.image.picturelib.adapter.PictureChooseRecycerViewAdapter;
import net.soulwolf.image.picturelib.listener.OnPicturePickListener;
import net.soulwolf.image.picturelib.listener.RecyclerItemClickListener;
import net.soulwolf.image.picturelib.model.Picture;
import net.soulwolf.image.picturelib.rx.ResponseHandler;
import net.soulwolf.image.picturelib.task.PictureTask;
import net.soulwolf.image.picturelib.utils.Constants;
import net.soulwolf.image.picturelib.utils.ItemDecorationAlbumColumns;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;

public class PictureChooseActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnPicturePickListener {

    public static final int RESULT_OK            = 200;

    public static final int RESULT_CANCEL        = 1022;

    public static final int GALLERY_REQUEST_CODE = 1023;

    RecyclerView mPictureGridRV;

    ArrayList<Picture> mPictureList;

    PictureChooseAdapter mPictureChooseAdapter;
    PictureChooseRecycerViewAdapter mPictureChooseRVAdapter;

    PictureProcess mPictureProcess;

    int mMaxPictureCount;

    int mTitleBarBackground = 0xFF16C2DD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_choose);
        if (getIntent() != null) {
            mMaxPictureCount = getIntent().getIntExtra(Constants.MAX_PICTURE_COUNT, 1);
            mTitleBarBackground = getIntent().getIntExtra(Constants.TITLE_BAR_BACKGROUND, mTitleBarBackground);
        }
        mPictureGridRV = (RecyclerView) findViewById(R.id.pi_picture_choose_grid);
        mPictureGridRV.setHorizontalScrollBarEnabled(true);
        setTitleBarBackground(mTitleBarBackground);
        setTitleText(R.string.ps_picture_choose);
        setLeftText(R.string.ps_cancel);
        setRightText(R.string.ps_complete);
        mActionRight.setEnabled(false); //Will enable only when users selected picture
        setBottomLeftText(R.string.ps_gallery);

        mPictureList = new ArrayList<>();

        mPictureChooseRVAdapter = new PictureChooseRecycerViewAdapter(this,mPictureList);

        LinearLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mPictureGridRV.setLayoutManager(gridLayoutManager);
        mPictureGridRV.addItemDecoration(new ItemDecorationAlbumColumns(2,3));
        mPictureGridRV.setHasFixedSize(true);
        mPictureGridRV.setAdapter(mPictureChooseRVAdapter);
        RecyclerItemClickListener mRvListener = new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==0)
                {
                    onCamera();
                } else {
                    if (mPictureChooseRVAdapter.contains(position)) {
                        mPictureChooseRVAdapter.removePictureChoose(view, position);
                    } else {
                        if (mPictureChooseRVAdapter.pictureChooseSize() >= mMaxPictureCount) {
                            Toast.makeText(getApplicationContext(), getString(R.string.ps_select_up_count, mMaxPictureCount), Toast.LENGTH_LONG).show();
                            return;
                        }
                        mPictureChooseRVAdapter.addPictureChoose(view, position);
                    }
                    if (mPictureChooseRVAdapter.pictureChooseSize() == 0) {
                        setTitleText(getString(R.string.ps_picture_choose));
                    } else {
                        setTitleText(getString(R.string.ps_picture_choose_count, mPictureChooseRVAdapter.pictureChooseSize()));
                    }
                }

                if(mPictureChooseRVAdapter.pictureChooseSize()>0)
                    mActionRight.setEnabled(true);
                else
                    mActionRight.setEnabled(false);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRvListener.setRecyclerView(mPictureGridRV);
        mPictureGridRV.addOnItemTouchListener(mRvListener);

        mPictureProcess = new PictureProcess(this);

        getAllPictures();
    }

    private void updatePictureList(List<Picture> paths, String folder) {
        mPictureList.clear();
        if(paths != null){

            //Add first Camera Icon
            Picture cameraIcon = new Picture();
            cameraIcon.isDrawable = true;
            cameraIcon.drawable = getResources().getDrawable(R.drawable.ic_camera_icon);
            mPictureList.add(cameraIcon);
            mPictureList.addAll(paths);
            mPictureChooseRVAdapter.changeFolder(folder);
            mPictureChooseRVAdapter.notifyDataSetChanged();
        }
    }

    private void getAllPictures() {
        PictureTask.getAllPictures(getContentResolver())
                .subscribe(new ResponseHandler<List<Picture>>() {
                    @Override
                    public void onSuccess(List<Picture> strings) throws Exception {
                        updatePictureList(strings,"default");
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        super.onFailure(error);
                        Toast.makeText(getApplicationContext(), R.string.ps_load_image_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPictureForGallery(final String folderPath) {
        PictureTask.getPictureForGallery(folderPath)
                .subscribe(new ResponseHandler<List<String>>() {
                    @Override
                    public void onSuccess(List<String> strings) throws Exception {
                        List<Picture> pictures = new ArrayList<>();
                        for (int i = 0; i < strings.size(); i++) {
                            Picture picture = new Picture();
                            picture.setPicturePath(strings.get(i));
                            pictures.add(picture);
                        }

                        updatePictureList(pictures, folderPath);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        super.onFailure(error);
                        Toast.makeText(getApplicationContext(), R.string.ps_load_image_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPictureProcess.onProcessResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE
                && resultCode == GalleryChooseActivity.RESULT_OK
                && data != null){
            String path = data.getStringExtra(Constants.GALLERY_CHOOSE_PATH);
            if(!TextUtils.isEmpty(path)){
                getPictureForGallery(path);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    protected void onLeftClick(View view) {
        super.onRightClick(view);
        Intent data = new Intent();
        setResult(RESULT_CANCEL, data);
        finish();
    }

    @Override
    protected void onRightClick(View view) {
        super.onRightClick(view);
        ArrayList<Picture> list = mPictureChooseRVAdapter.getPictureChoosePath();
        Intent data = new Intent();
        data.putExtra(Constants.PICTURE_CHOOSE_LIST, list);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onBottomRightClick(View view) {
        super.onRightClick(view);
        ArrayList<Picture> list = mPictureChooseRVAdapter.getPictureChoosePath();
        Intent data = new Intent();
        data.putExtra(Constants.PICTURE_CHOOSE_LIST, list);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onBottomLeftClick(View view) {
        super.onLeftClick(view);
        Intent intent = new Intent(this, GalleryChooseActivity.class);
        intent.putExtra(Constants.TITLE_BAR_BACKGROUND, mTitleBarBackground);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setResult(RESULT_CANCEL);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position==0)
        {
            onCamera();
        } else {

            if (mPictureChooseAdapter.contains(position)) {
                mPictureChooseAdapter.removePictureChoose(view, position);
            } else {
                if (mPictureChooseAdapter.pictureChooseSize() >= mMaxPictureCount) {
                    Toast.makeText(this,
                            getString(R.string.ps_select_up_count, mMaxPictureCount), Toast.LENGTH_LONG).show();
                    return;
                }
                mPictureChooseAdapter.addPictureChoose(view, position);
            }
            //mPictureChooseAdapter.notifyDataSetChanged();
            if (mPictureChooseAdapter.pictureChooseSize() == 0) {
                setTitleText(getString(R.string.ps_picture_choose));
            } else {
                setTitleText(getString(R.string.ps_picture_choose_count
                        , mPictureChooseAdapter.pictureChooseSize()));
            }
        }
    }

    public void onCamera(){
        mPictureProcess.setPictureFrom(PictureFrom.CAMERA);
        mPictureProcess.setClip(false);
        mPictureProcess.setMaxPictureCount(1);
        mPictureProcess.execute(this);
    }

    @Override
    public void onSuccess(List<Picture> pictures) {
            TLog.i("", "OnSuccess:%s", pictures);
//        updatePictureList(pictures);
    }

    @Override
    public void onSuccessString(List<String> pictures) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_layout,new BigImageGalleryFragment().newInstance(pictures,0,true,mTitleBarBackground));
        transaction.addToBackStack("bigpicture");
        transaction.commit();
    }

    @Override
    public void onError(Exception e) {

        TLog.e("", "onError", e);
    }

    @Override
    public void onCancel() {

    }
}
