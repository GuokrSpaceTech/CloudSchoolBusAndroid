package com.guokrspace.cloudschoolbus.teacher.event;

import com.squareup.otto.Bus;

/**
 * Created by wangjianfeng on 15/7/30.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    public BusProvider() {
    }
}
