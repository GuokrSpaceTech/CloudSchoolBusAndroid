package com.guokrspace.cloudschoolbus.teacher.widget;

import android.content.Context;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class NoticeCardItemView extends CardItemView<NoticeCard> {

    GestureDetectorCompat mDetector;

    public NoticeCardItemView(Context context) {
        super(context);
    }

    public NoticeCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoticeCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(NoticeCard card) {
        super.build(card);

        //Teacher Head
        ImageView teacherHead = (ImageView) findViewById(R.id.teacher_avatar);
        if (teacherHead != null) {
            if(card.getTeacherAvatarUrl() == null || card.getTeacherAvatarUrl().isEmpty()) {
                teacherHead.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getTeacherAvatarUrl()).into(teacherHead);
            }
        }

        //Teacher Name
        TextView teacherName = (TextView) findViewById(R.id.teacher_name);
        teacherName.setText(card.getTeacherName());
        if (card.getDescriptionColor() != -1) {
            teacherName.setTextColor(card.getDescriptionColor());
        }

        //Class Name
        TextView kindergarten = (TextView) findViewById(R.id.classname);
        kindergarten.setText(card.getClassName());
        if (card.getDescriptionColor() != -1) {
            kindergarten.setTextColor(card.getDescriptionColor());
        }

        //Timestamp
        String   sendTimeStr = card.getSentTime();
        TextView sentTimeTextView    = (TextView) findViewById(R.id.timestamp);
        sentTimeTextView.setText(DateUtils.timelineTimestamp(sendTimeStr,card.getContext()));
        if (card.getDescriptionColor() != -1) {
            sentTimeTextView.setTextColor(card.getDescriptionColor());
        }

        //Card Type
        String cardType = card.getCardType();
        TextView cardTextView = (TextView) findViewById(R.id.card_type);
        cardTextView.setText(cardType);

        // Title
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(card.getTitle());
        if (card.getTitleColor() != -1) {
            title.setTextColor(card.getTitleColor());
        }

        // Description
        TextView description = (TextView) findViewById(R.id.text_content);
        description.setText(card.getDescription());
        if (card.getDescriptionColor() != -1) {
            description.setTextColor(card.getDescriptionColor());
        }

        // ImageView
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        if (imageView != null) {
            if(card.getUrlImage() == null || card.getUrlImage().isEmpty()) {
                imageView.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getUrlImage()).into(imageView);
            }
        }

        /*
         * Card Bottom
         */
        Button confirmButton = (Button) findViewById(R.id.confirm);
        if(card.getIsNeedConfirm()!= null) {
            if (card.getIsNeedConfirm().equals("1")) {
                confirmButton.invalidate();
                confirmButton.setOnClickListener(card.getmConfirmButtonClickListener());
                confirmButton.setText(getResources().getString(R.string.confirm_notice));
                confirmButton.setBackgroundColor(getResources().getColor(R.color.button_enable));
                confirmButton.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(true);
            } else if (card.getIsNeedConfirm().equals("2")) {
                confirmButton.invalidate();
                confirmButton.setText(getResources().getString(R.string.confirmed_notice));
                confirmButton.setBackgroundColor(getResources().getColor(R.color.button_disable));
                confirmButton.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(false);
            } else {
                confirmButton.setVisibility(View.INVISIBLE);
            }
        }

        mDetector = new GestureDetectorCompat(card.getContext(), new MyGestureListener(card.getContext(),(String)description.getText()));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean retVal = mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return retVal || super.onTouchEvent(event);
    }
}

