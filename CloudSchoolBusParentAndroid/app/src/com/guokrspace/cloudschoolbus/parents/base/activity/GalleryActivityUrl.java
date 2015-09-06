package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
