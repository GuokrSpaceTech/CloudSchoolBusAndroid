package com.guokrspace.cloudschoolbus.parents.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.base.activity.FullScreenTextActivity;

/**
 * Created by wangjianfeng on 16/1/14.
 */
public class DoubleTapTextView extends TextView {

    GestureDetector gestureDetector;
    Context mContext;
    String mContent;

    public DoubleTapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // creating new gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
    }
    // skipping measure calculation and drawing

    // delegate the event to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

                Intent intent = new Intent(mContext, FullScreenTextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("description", mContent);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("","Fullscreen Textview onScrolled");

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
