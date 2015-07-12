package com.guokrspace.cloudschoolbus.parents.protocols;

import android.text.TextUtils;

import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjianfeng on 15/7/4.
 */
public class CloudSchoolBusRestClient {
    private static final String BASE_URL = "http://api35.yunxiaoche.com:81/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept","application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept","application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void updateSessionid(String sid)
    {
        client.addHeader("sid",sid);
    }
}
