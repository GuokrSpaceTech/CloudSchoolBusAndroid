package com.guokrspace.cloudschoolbus.teacher.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.guokrspace.cloudschoolbus.teacher.base.include.Version;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2015/11/3.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Version.APP_ID);
        api.handleIntent(getIntent(),this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i("","");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("","");
    }
}
