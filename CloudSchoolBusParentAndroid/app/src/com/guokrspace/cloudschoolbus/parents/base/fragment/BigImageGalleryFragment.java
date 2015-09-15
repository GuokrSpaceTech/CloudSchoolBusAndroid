package com.guokrspace.cloudschoolbus.parents.base.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.soulwolf.image.picturelib.R;
import net.soulwolf.image.picturelib.ui.PictureChooseActivity;
import net.soulwolf.image.picturelib.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

/**
 * Created by Yang Kai on 15/9/5.
 */
public class BigImageGalleryFragment extends Fragment{
    private GalleryViewPager mViewPager;
    int mTitleBarBackground = 0xFF16C2DD;
    private List<?> mPictureList;
    private int mCurrent;
    protected RelativeLayout mTitleBar;
    protected TextView mTitleText;

    protected Button mActionLeft;

    protected Button   mActionRight;

    public static BigImageGalleryFragment newInstance(List<?> pictureList, int current)
    {
        BigImageGalleryFragment fragment = new BigImageGalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable("pictures", (ArrayList)pictureList);
        args.putInt("current", current );
        fragment.setArguments(args);
        return fragment;
    }

    public BigImageGalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mPictureList = (ArrayList) getArguments().get("pictures");
            mCurrent = (int)getArguments().get("current");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        mTitleBar = (RelativeLayout) root.findViewById(R.id.pi_title_bar);
        mTitleBar.setBackgroundColor(0xFF16C2DD);
        mTitleText = (TextView)root.findViewById(R.id.pi_title_bar_title);
        mActionLeft = (Button)root.findViewById(R.id.pi_title_bar_left);
        mActionRight = (Button)root.findViewById(R.id.pi_title_bar_right);
        mTitleText.setText(R.string.ps_picture_choose);
        mActionLeft.setText(R.string.ps_gallery);
        mActionRight.setText(R.string.ps_complete);
        mActionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(Constants.PICTURE_CHOOSE_LIST, (ArrayList) mPictureList);
                PictureChooseActivity theActivity = (PictureChooseActivity)getActivity();
                theActivity.setResult(theActivity.RESULT_OK, data);
                theActivity.finish();
            }
        });

        List<String> mUrlPaths = new ArrayList<>();
        for(String picturePath: (List<String>)mPictureList)
        {
            String urlPath = "file://" + picturePath;
            mUrlPaths.add(urlPath);
        }

        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(getActivity(), mUrlPaths);
        final List<String> finalItems = (List<String>)mUrlPaths;
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
        {
            @Override
            public void onItemChange(int currentPosition)
            {
//                getSupportActionBar().setTitle(title(currentPosition, finalItems.size()));
            }
        });

        mViewPager = (GalleryViewPager)root.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mCurrent);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
