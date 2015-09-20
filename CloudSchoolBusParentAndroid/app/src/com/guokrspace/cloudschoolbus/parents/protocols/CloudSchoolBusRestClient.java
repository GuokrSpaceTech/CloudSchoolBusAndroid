package com.guokrspace.cloudschoolbus.parents.protocols;

import android.text.TextUtils;

import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yang Kai on 15/7/4.
 */
public class CloudSchoolBusRestClient {
//    private static final String BASE_URL = "http://api35.yunxiaoche.com:81/";
//      private static final String BASE_URL = "http://192.168.1.140:81/api/parent/";
    private static final String BASE_URL = (Version.PARENT?"http://222.247.189.132:81/api/parent/":"http://222.247.189.132:81/api/teacher/");
//    private static final String UPLOAD_URL = "http://client35.yunxiaoche.com:81/";
    private static final String UPLOAD_URL = "http://v4-receive.yunxiaoche.com:81/";
//    private static final String BASE_URL = "http://test.yunxiaoche.com/api/parent/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static CloudSchoolBusRestClient sNetworkClient = null;

    private CloudSchoolBusRestClient() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
                100000), new ThreadPoolExecutor.CallerRunsPolicy());
        client.setThreadPool(threadPoolExecutor);
    }

    synchronized private static void newInstance() {
        if (null == sNetworkClient) {
            sNetworkClient = new CloudSchoolBusRestClient();
        }
    }

    public static CloudSchoolBusRestClient getNetworkClient() {
        if (null == sNetworkClient) {
            newInstance();
        }
        return sNetworkClient;
    }

    private String mMethod = "";
    private Map<String, String> mRequestMap = new HashMap<String, String>();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept", "application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.addHeader("User-Agent", (Version.PARENT?Version.productNameParent:Version.productNameTeacher) + ":" + Version.platform);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept","application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.addHeader("User-Agent", (Version.PARENT?Version.productNameParent:Version.productNameTeacher)  + ":" + Version.platform);
        client.get(getAbsoluteUrl(url,params), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept","application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.addHeader("User-Agent", (Version.PARENT?Version.productNameParent:Version.productNameTeacher)  + ":" + Version.platform);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept","application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.addHeader("User-Agent", (Version.PARENT?Version.productNameParent:Version.productNameTeacher)  + ":" + Version.platform);
        client.post(getAbsoluteUrl(url,params), responseHandler);
    }

    public static void upload(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("apikey", "mactoprestphone");
        client.addHeader("Accept","application/json");
        client.addHeader("Version", Version.versionName.substring(1));
        client.addHeader("User-Agent", (Version.PARENT?Version.productNameParent:Version.productNameTeacher) + ":" + Version.platform);
        client.post(getFiluploadUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append(relativeUrl);
        sb.append('/');

        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                sb.append(entry.getKey()).append('/')
                        .append( URLEncoder.encode(entry.getValue(), "utf-8") )
                        .append('/');
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String getFiluploadUrl(String relativeUrl) {
        return UPLOAD_URL + relativeUrl;
    }


    public static void updateSessionid(String sid)
    {
        client.addHeader("sid",sid);
    }

}
