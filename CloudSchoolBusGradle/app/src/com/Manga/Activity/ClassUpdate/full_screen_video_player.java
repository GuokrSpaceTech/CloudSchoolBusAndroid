package com.Manga.Activity.ClassUpdate;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.MediaController;


/**
 * Created by wangjianfeng on 14-12-7.
 */
public class full_screen_video_player extends BaseActivity {

    private CacheableVideoView theVideoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_full_screen_video_player);
        getSupportActionBar().setTitle(getString(R.string.video_play));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    protected void init() {
        super.init();

        Intent intent = getIntent();
        String file = intent.getStringExtra("path");

        theVideoView = (CacheableVideoView)findViewById(R.id.cacheable_videoview);

        Resources resources = mContext.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        double screenWidthInPixels = (double)config.screenWidthDp * dm.density;
//                double screenHeightInPixels = screenWidthInPixels * dm.heightPixels / dm.widthPixels;
        int width = (int)(screenWidthInPixels + .5); // 10 is padding]]

        theVideoView.getmVideoView().setmVideoWidth(width);
        theVideoView.getmVideoView().setmVideoHeight(width);

        theVideoView.loading(file);
        theVideoView.start();

        //The video is square
        mediaController = new MediaController(this);
        theVideoView.getmVideoView().setMediaController(mediaController);
    }

    @Override
    protected void setListener() {
        super.setListener();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
