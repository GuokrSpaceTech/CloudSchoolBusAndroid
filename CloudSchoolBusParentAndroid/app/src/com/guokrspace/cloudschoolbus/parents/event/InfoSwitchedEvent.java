package com.guokrspace.cloudschoolbus.parents.event;

/**
 * Created by macbook on 15-8-20.
 */
public class InfoSwitchedEvent {
    private int current;

    public InfoSwitchedEvent(int currentChild) {
        this.current = currentChild;
    }

    public int getCurrentChild() {
        return current;
    }

    public void setCurrentChild(int currentChild) {
        this.current = currentChild;
    }
}
