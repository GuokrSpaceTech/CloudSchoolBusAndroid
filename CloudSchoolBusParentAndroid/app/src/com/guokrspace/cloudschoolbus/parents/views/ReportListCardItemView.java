package com.guokrspace.cloudschoolbus.parents.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        ImageView teacherHeadImageView = (ImageView)findViewById(R.id.imageView);
        teacherHeadImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_default));;
        TextView  teacherNameTextView  = (TextView)findViewById(R.id.titleTextView);
        teacherNameTextView.setText("MR. KEITH");
        TextView  schoolNameTextView   = (TextView)findViewById(R.id.subtitleTextView);
        schoolNameTextView.setText("SEI");

        ImageView reportIconImageView  = (ImageView)findViewById(R.id.imageViewReportIcon);
        reportIconImageView.setBackgroundColor(getResources().getColor(R.color.accent));
        reportIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_attach));

        TextView  reportTitleTextView = (TextView)findViewById(R.id.textViewReportTitle);
        reportTitleTextView.setText(card.getReporttype());

        //Timestamp
        TextView  reportTimestampTextView = (TextView)findViewById(R.id.textViewTimestamp);
        String timelineTimestamp = DateUtils.timelineTimestamp(card.getTimestamp());
        if (card.getDescriptionColor() != -1) {
            reportTimestampTextView.setTextColor(card.getDescriptionColor());
        }

        ImageView detailIconImageView  = (ImageView)findViewById(R.id.imageViewDetailIcon);
        detailIconImageView.setBackgroundColor(getResources().getColor(R.color.accent));
        detailIconImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
    }
}
