package net.soulwolf.image.picturelib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.soulwolf.image.picturelib.R;
import net.soulwolf.image.picturelib.model.Picture;
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
    private boolean mHasTitleBar;
    protected RelativeLayout mTitleBar;
    protected TextView mTitleText;

    protected Button mActionLeft;

    protected Button   mActionRight;

    public static BigImageGalleryFragment newInstance(List<?> pictureList, int current, boolean hasTitleBar, int titleBarBackground)
    {
        BigImageGalleryFragment fragment = new BigImageGalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable("pictures", (ArrayList)pictureList);
        args.putInt("current", current);
        args.putBoolean("hasTitleBar", hasTitleBar);
        args.putInt("titleBarBackground", titleBarBackground);
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
            mHasTitleBar = (boolean)getArguments().get("hasTitleBar");
            mTitleBarBackground = (int)getArguments().get("titleBarBackground");
        }

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        mTitleBar = (RelativeLayout) root.findViewById(R.id.pi_title_bar);
        if(!mHasTitleBar) mTitleBar.setVisibility(View.GONE);
        mTitleBar.setBackgroundColor(mTitleBarBackground);
        mTitleText = (TextView)root.findViewById(R.id.pi_title_bar_title);
        mActionLeft = (Button)root.findViewById(R.id.pi_title_bar_left);
        mActionRight = (Button)root.findViewById(R.id.pi_title_bar_right);
        mTitleText.setText(R.string.ps_picture_choose);
        mActionLeft.setText(R.string.ps_cancel);
        mActionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        mActionRight.setText(R.string.ps_complete);
        mActionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(Constants.PICTURE_CHOOSE_LIST, (ArrayList) mPictureList);
                PictureChooseActivity theActivity = (PictureChooseActivity) getActivity();
                theActivity.setResult(theActivity.RESULT_OK, data);
                theActivity.finish();
            }
        });

        List<String> mUrlPaths = new ArrayList<>();

        for( Object picture: (List<?>)mPictureList)
        {
            String urlPath = "";
            if(picture instanceof Picture)
                urlPath = "file://" + ((Picture) picture).getPicturePath();
            else if (picture instanceof String)
                urlPath = "file://" + ((String) picture);
            mUrlPaths.add(urlPath);
        }

        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(getActivity(), mUrlPaths);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(menu != null)
        {
            menu.clear();
        }
    }
}
