package com.guokrspace.cloudschoolbus.parents.module.explore.classify.report;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by wangjianfeng on 15/7/27.
 */
public class ReportDetailFragment extends BaseFragment implements AdvancedWebView.Listener {

    private WebView webView;
    private AdvancedWebView mWebView;
    private String reportUrl = null;
    private String reportDateString = "";

    public static ReportDetailFragment newInstance(String reportDate, String reportUrl) {
        ReportDetailFragment fragment = new ReportDetailFragment();
        Bundle args = new Bundle();
        args.putString("reportDate", reportDate);
        args.putString("reportUrl", reportUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        reportDateString = (String)getArguments().getSerializable("reportDate");
        reportUrl        = (String)getArguments().getSerializable("reportUrl");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_webview, container, false);
        mWebView = (AdvancedWebView) root.findViewById(R.id.webView);
        mWebView.loadUrl(reportUrl);
//        webView.loadUrl("http://192.168.1.140:81/api/page/index");
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

}
