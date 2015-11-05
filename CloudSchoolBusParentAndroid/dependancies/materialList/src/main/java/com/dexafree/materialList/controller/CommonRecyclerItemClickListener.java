package com.dexafree.materialList.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


// From http://stackoverflow.com/a/26196831/1610001
public class CommonRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private RecyclerView mRecyclerView;
    private static long timestamp;
    long delta;

    public static abstract interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public CommonRecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;


        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
//                View childView = (View) mRecyclerView.findChildViewUnder(e.getX(), e.getY());
//
//                if (childView != null && mListener != null) {
//                    mListener.onItemLongClick(childView, mRecyclerView.getChildPosition(childView));
//                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = (View) view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            delta = System.currentTimeMillis() - timestamp;
            timestamp = System.currentTimeMillis();
            if (delta > 500)
                mListener.onItemClick(childView, view.getChildPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }
}