package com.guokrspace.cloudschoolbus.teacher.module.explore;

import com.guokrspace.cloudschoolbus.teacher.module.explore.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.teacher.R;

/**
 * An full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenTextReadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_text_read);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {

            String text = (String)bundle.get("text");
            final TextView contentView = (TextView)findViewById(R.id.fullscreen_content);

            contentView.setText(text);
        }
    }
}
