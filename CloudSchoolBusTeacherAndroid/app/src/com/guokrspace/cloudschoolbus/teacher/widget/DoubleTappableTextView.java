package com.guokrspace.cloudschoolbus.teacher.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.teacher.module.explore.FullscreenTextReadActivity;

/**
 * Created by macbook on 15/11/12.
 */
public class DoubleTappableTextView extends TextView
{
    Context mContext;
    GestureDetector mDetector;
    private static final int MSG_DOUBLE_TAP = 1;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== MSG_DOUBLE_TAP)
            {
                Intent intent = new Intent(mContext, FullscreenTextReadActivity.class);
                intent.putExtra("text", getText());
                mContext.startActivity(intent);
            }

            super.handleMessage(msg);
        }
    };



    public DoubleTappableTextView(Context context) {
        super(context);
        mContext = context;
        mDetector = new GestureDetector(context, new MyGestureListener(context));
    }

    public DoubleTappableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDetector = new GestureDetector(context, new MyGestureListener(context));
    }

    public DoubleTappableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDetector = new GestureDetector(context, new MyGestureListener(context));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        Context mContext;

        public MyGestureListener(Context context) {
            mContext = context;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            mHandler.sendEmptyMessage(MSG_DOUBLE_TAP);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    }
}


