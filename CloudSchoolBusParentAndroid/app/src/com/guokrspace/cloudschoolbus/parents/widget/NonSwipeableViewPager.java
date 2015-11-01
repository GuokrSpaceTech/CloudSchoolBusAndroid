package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kai on 9/28/15.
 */
public class NonSwipeableViewPager extends ViewPager {
    private boolean lock = false;
    public void lock(){
        lock=true;
    }
    public void unlock(){
        lock=false;
    }
    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(lock)
            return false;
        else{
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(lock)
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean arrowScroll(int direction) {
        if(lock)
            return false;
        else
            return super.arrowScroll(direction);
    }
}
