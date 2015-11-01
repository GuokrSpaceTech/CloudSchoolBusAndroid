package com.guokrspace.cloudschoolbus.teacher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.NetworkStatusEvent;
import com.squareup.otto.Produce;

/**
 * Created by wangjianfeng on 15/7/30.
 */
public class ConnectivityReceiver extends BroadcastReceiver{
    NetworkStatusEvent networkStatusEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        networkStatusEvent = new NetworkStatusEvent(false,false,false);

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo        wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo        mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo        activeInfo = manager.getActiveNetworkInfo();

        if(activeInfo!=null)
        {
            networkStatusEvent.setIsNetworkConnected(true);

            if(wifiInfo.isConnected())
                networkStatusEvent.setIsWifiConnected(true);
            if(mobileInfo.isConnected())
                networkStatusEvent.setIsMobileNetworkConnected(true);
        }

        BusProvider.getInstance().post(productNetworkChangedEvent());
    }

    @Produce public NetworkStatusEvent productNetworkChangedEvent(){
        return networkStatusEvent;
    }
}
