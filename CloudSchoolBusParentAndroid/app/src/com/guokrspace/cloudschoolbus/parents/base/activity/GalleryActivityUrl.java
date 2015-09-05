package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.guokrspace.cloudschoolbus.parents.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

/**
 * Created by Yang Kai on 15/9/5.
 */
public class GalleryActivityUrl extends BaseActivity{
    private GalleryViewPager mViewPager;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> items = new ArrayList<String>();
        int position;
        Bundle bundle = getIntent().getExtras();
        items = bundle.getStringArrayList("fileUrls");
        position = bundle.getInt("currentFile");

        setContentView(R.layout.activity_image_viewer);
//        String[] urls = {
//                "http://cs407831.userapi.com/v407831207/18f6/jBaVZFDhXRA.jpg",
//                "http://cs407831.userapi.com/v4078f31207/18fe/4Tz8av5Hlvo.jpg",
//                "http://cs407831.userapi.com/v407831207/1906/oxoP6URjFtA.jpg",
//                "http://cs407831.userapi.com/v407831207/190e/2Sz9A774hUc.jpg",
//                "http://cs407831.userapi.com/v407831207/1916/Ua52RjnKqjk.jpg",
//                "http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg"
//        };
//        Collections.addAll(items, urls);

        UrlPagerAdapter pagerAdapter = new ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter(this, items);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
        {
            @Override
            public void onItemChange(int currentPosition)
            {
                Toast.makeText(GalleryActivityUrl.this, "Current item is " + currentPosition, Toast.LENGTH_SHORT).show();
            }
        });

        mViewPager = (GalleryViewPager)findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);

        mViewPager.setCurrentItem(position);

    }
}
