package com.guokrspace.cloudschoolbus.teacher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class LastLetterCardItemView extends CardItemView<LastLetterCard> {

    private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");

    public LastLetterCardItemView(Context context) {
        super(context);
    }

    public LastLetterCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LastLetterCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void build(LastLetterCard card) {
        super.build(card);

        /*
         * Header
         */
        //Teacher Head
        ImageView teacherHead = (ImageView) findViewById(R.id.teacher_avatar);
        if (teacherHead != null) {
            if (card.getTeacherAvatarUrl() == null || card.getTeacherAvatarUrl().isEmpty()) {
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
        TextView kindergarten = (TextView) findViewById(R.id.classname);
        kindergarten.setText(card.getClassname());
        if (card.getDescriptionColor() != -1) {
            kindergarten.setTextColor(card.getDescriptionColor());
        }

        //Timestamp
        String publishTime = card.getTimestamp();
        TextView sentTime = (TextView) findViewById(R.id.timestamp);
        if (publishTime != null) {
            long foo = Long.parseLong(publishTime) * 1000;
            long tmp = System.currentTimeMillis() - foo;
            if (tmp < 12 * 60 * 60 * 1000) {
                if (tmp < 60 * 60 * 1000) {
                    if (tmp <= 60 * 1000) {
                        sentTime.setText("1" + card.getContext().getResources().getString(R.string.minute_befor));
                    } else {
                        sentTime.setText(tmp / (60 * 1000) + card.getContext().getResources().getString(R.string.minute_befor));
                    }
                } else {
                    sentTime.setText(tmp / (60 * 60 * 1000) + card.getContext().getResources().getString(R.string.hour_befor));
                }
            } else {
                sentTime.setText(toYearSdf.format(new Date(foo)));
            }
        }

        //Chat Message
        String chatMessage = card.getChatMessage();
        TextView chatMessageTextView = (TextView) findViewById(R.id.lastletter);
        chatMessageTextView.setText(chatMessage);
    }
}
