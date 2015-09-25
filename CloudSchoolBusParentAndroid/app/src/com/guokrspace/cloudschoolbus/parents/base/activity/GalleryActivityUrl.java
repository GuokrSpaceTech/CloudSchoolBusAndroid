package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.guokrspace.cloudschoolbus.parents.R;

import java.util.ArrayList;
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

        setContentView(R.layout.fragment_image_viewer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title(position,items.size()));

        UrlPagerAdapter pagerAdapter = new ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter(this, items);
        final List<String> finalItems = items;
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
        {
            @Override
            public void onItemChange(int currentPosition)
            {
                getSupportActionBar().setTitle(title(currentPosition + 1, finalItems.size()));
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

    private String title(int current, int total)
    {
        return getResources().getString(R.string.picturetype)+"("+ current + "/" + total +")";
    }

}
