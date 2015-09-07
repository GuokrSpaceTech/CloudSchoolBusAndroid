package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

/**
 * Created by wangjianfeng on 15/7/26.
 */
public class ReportListCardItemView extends CardItemView<ReportListCard> {

    public ReportListCardItemView(Context context) {
        super(context);
    }

    public ReportListCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportListCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(ReportListCard card) {
        super.build(card);

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
        String   publishTime = card.getSentTime();
        TextView sentTime    = (TextView) findViewById(R.id.timestamp);
        sentTime.setText(DateUtils.timelineTimestamp(publishTime, card.getContext()));
        if (card.getDescriptionColor() != -1) {
            sentTime.setTextColor(card.getDescriptionColor());
        }

        /* Card Type */
        TextView cardTypeTextView = (TextView)findViewById(R.id.card_type);
        cardTypeTextView.setText(card.getCardType());

        /* Report Title */
        TextView reportTypeTextView = (TextView)findViewById(R.id.description);
        reportTypeTextView.setText(card.getReporttype());

        LinearLayout layout = (LinearLayout)findViewById(R.id.taptoviewLayout);
        layout.setOnClickListener(card.getClickListener());
    }
}
