package com.guokrspace.cloudschoolbus.parents.base.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by wangjianfeng on 15/7/27.
 */
public class WebviewFragment extends BaseFragment implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    private SwipeRefreshLayout mRefreshView;
    private String mUrl = "";
    private String mTitle = "";
    private String mParams = "";

    public static WebviewFragment newInstance(String url, String title, String params) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("title", title);
        args.putString("params",params);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mUrl        = (String)getArguments().getSerializable("url");
        mTitle      = (String)getArguments().getSerializable("title");
        mParams     = (String)getArguments().getSerializable("params");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_webview, container, false);
        mWebView = (AdvancedWebView) root.findViewById(R.id.webView);
        mRefreshView = (SwipeRefreshLayout)root.findViewById(R.id.swipeRefreshLayout);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View p_v, MotionEvent p_event) {
                // this will disallow the touch request for parent scroll on touch of child view
                p_v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mWebView.loadUrl(mUrl + mParams);
        ((MainActivity) mParentContext).pager.lock();
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(mUrl + mParams);
                mRefreshView.setRefreshing(false);
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) { }

    @Override
    public void onExternalPageRequest(String url) { }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((MainActivity)mParentContext).getSupportActionBar().setTitle(mTitle);
        ((MainActivity)mParentContext).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}
