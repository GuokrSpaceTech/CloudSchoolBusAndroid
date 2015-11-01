package com.guokrspace.cloudschoolbus.teacher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class AttendanceReordCardItemView extends CardItemView<AttendanceRecordCard> {

    public AttendanceReordCardItemView(Context context) {
        super(context);
    }

    public AttendanceReordCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AttendanceReordCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(AttendanceRecordCard card) {
        super.build(card);

        /*
         * Header
         */
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

        //Classname
        TextView classnameTextView = (TextView) findViewById(R.id.classname);
        classnameTextView.setText(card.getClassName());
        if (card.getDescriptionColor() != -1) {
            classnameTextView.setTextColor(card.getDescriptionColor());
        }
        //Timestamp
        String   sentTime = card.getSentTime();
        TextView sentTimeTextView    = (TextView) findViewById(R.id.timestamp);
        sentTimeTextView.setText(DateUtils.timelineTimestamp(sentTime,card.getContext()));

        /* Card Type */
        TextView cardTypeTextView = (TextView)findViewById(R.id.card_type);
        cardTypeTextView.setText(card.getCardType());

        // ImageView
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        if (imageView != null) {
            if(card.getUrlImage() == null || card.getUrlImage().isEmpty()){
                imageView.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getUrlImage()).into(imageView);
            }
        }

        // Description
        TextView description = (TextView) findViewById(R.id.text_content);
        if(card.getDescription()!=null) {
            String recordText = card.getContext().getResources().getString(R.string.attendancetime);
            recordText = recordText + DateUtils.dateFormat(card.getDescription(), "MM-dd HH:mm");

            description.setText(recordText);
        }
        if (card.getDescriptionColor() != -1) {
            description.setTextColor(card.getDescriptionColor());
        }
    }
}
