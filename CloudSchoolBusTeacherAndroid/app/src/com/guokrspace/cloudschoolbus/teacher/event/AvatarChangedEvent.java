package com.guokrspace.cloudschoolbus.teacher.event;

import android.graphics.Bitmap;

/**
 * Created by macbook on 15-8-20.
 */
public class AvatarChangedEvent {
    private Bitmap bitMap;

    public AvatarChangedEvent(Bitmap bitMap) {
        this.bitMap = bitMap;
    }

    public Bitmap getBitMap() {
        return bitMap;
    }

    public void setBitMap(Bitmap bitMap) {
        this.bitMap = bitMap;
    }
}
