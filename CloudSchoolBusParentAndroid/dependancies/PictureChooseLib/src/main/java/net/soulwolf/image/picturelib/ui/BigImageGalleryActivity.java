package net.soulwolf.image.picturelib.ui;

import android.os.Bundle;
import android.view.MenuItem;

import net.soulwolf.image.picturelib.R;
import net.soulwolf.image.picturelib.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

/**
 * Created by Yang Kai on 15/9/5.
 */
public class BigImageGalleryActivity extends BaseActivity{
    private GalleryViewPager mViewPager;
    int mTitleBarBackground = 0xFF16C2DD;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> items = new ArrayList<String>();
        int position;
        Bundle bundle = getIntent().getExtras();
        items = bundle.getStringArrayList("fileUrls");
        position = bundle.getInt("currentFile");

        setContentView(R.layout.fragment_image_viewer);

        if (getIntent() != null) {
            mTitleBarBackground = getIntent().getIntExtra(Constants.TITLE_BAR_BACKGROUND, mTitleBarBackground);
        }
        setTitleBarBackground(mTitleBarBackground);
        setTitleText(R.string.ps_picture_choose);
        setLeftText(R.string.ps_gallery);
        setRightText(R.string.ps_complete);

        UrlPagerAdapter pagerAdapter = new ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter(this, items);
        final List<String> finalItems = items;
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
        {
            @Override
            public void onItemChange(int currentPosition)
            {
//                getSupportActionBar().setTitle(title(currentPosition, finalItems.size()));
            }
        });

//        mViewPager = (GalleryViewPager)findViewById(R.id.view_pager);
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
