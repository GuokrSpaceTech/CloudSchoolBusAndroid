package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.R;

/**
 * Created by wangjianfeng on 16/1/14.
 */
public class FullScreenTextActivity extends BaseActivity {

    String contentText;
    GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_text);
        final TextView textView = (TextView)findViewById(R.id.textView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setLineSpacing(2,1);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            contentText = bundle.getString("description");
            textView.setText(contentText);
        }

        //Current User Exit the View by Single Tap
        mDetector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                finish();
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }
}
