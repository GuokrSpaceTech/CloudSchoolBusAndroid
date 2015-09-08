package com.guokrspace.cloudschoolbus.parents.event;

import android.graphics.Bitmap;

/**
 * Created by macbook on 15-8-20.
 */
public class ChildSwitchedEvent {
    private int currentChild;

    public ChildSwitchedEvent(int currentChild) {
        this.currentChild = currentChild;
    }

    public int getCurrentChild() {
        return currentChild;
    }

    public void setCurrentChild(int currentChild) {
        this.currentChild = currentChild;
    }
}
