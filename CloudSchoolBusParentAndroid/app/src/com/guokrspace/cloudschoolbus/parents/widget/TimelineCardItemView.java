package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class TimelineCardItemView extends CardItemView<TimelineCard> {

    private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");

    public TimelineCardItemView(Context context) {
        super(context);
    }

    public TimelineCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimelineCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(TimelineCard card) {
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
        TextView teacherName = (TextView) findViewById(R.id.teacehr_name);
        teacherName.setText(card.getTeacherName());
        if (card.getDescriptionColor() != -1) {
            teacherName.setTextColor(card.getDescriptionColor());
        }

        //Kindergarten
        TextView kindergarten = (TextView) findViewById(R.id.kindergarten_name);
        kindergarten.setText(card.getKindergarten());
        if (card.getDescriptionColor() != -1) {
            kindergarten.setTextColor(card.getDescriptionColor());
        }
        //Timestamp
        String publishTime = card.getSentTime();
        TextView sentTime = (TextView) findViewById(R.id.timestamp);
        if (publishTime != null) {
            long foo = Long.parseLong(publishTime) * 1000;
            long tmp = System.currentTimeMillis() - foo;
            if (tmp < 12 * 60 * 60 * 1000) {
                if (tmp < 60 * 60 * 1000) {
                    if (tmp <= 60 * 1000) {
                        sentTime.setText("1" + card.getContext().getString(R.string.minute_befor));
                    } else {
                        sentTime.setText(tmp / (60 * 1000) + card.getContext().getResources().getString(R.string.minute_befor));
                    }
                } else {
                    sentTime.setText(tmp / (60 * 60 * 1000) + card.getContext().getResources().getString(R.string.hour_befor));
                }
            } else {
                sentTime.setText(toYearSdf.format(new Date(foo)));
            }

            if (card.getDescriptionColor() != -1) {
                sentTime.setTextColor(card.getDescriptionColor());
            }
        }

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

        //ImageGrid
        GridView gridView = (GridView) findViewById(R.id.dynamic_grid);
        if(card.getImageAdapter().getCount()== 4){
            gridView.setNumColumns(2);
            ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.timeline_thumb_pic_width) *2 + getResources().getDimensionPixelSize(R.dimen.timeline_thumb_pic_spacing);
            gridView.setLayoutParams(layoutParams);
        }

        gridView.setAdapter(card.getImageAdapter());

        /*
         * Card Bottom
         */
        //Tags
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tags_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(card.getContext());
        linearLayoutManager.setOrientation(HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // specify an adapter (see also next example)
        recyclerView.setAdapter(card.getTagAdapter());

        if (card.getTagAdapter().getItemCount() > 0) {
            recyclerView.addOnItemTouchListener(card.getmOnItemSelectedListener());
        }

        //ShareButton
        ImageView shareButton = (ImageView) findViewById(R.id.share_button);
        shareButton.setOnClickListener(card.getmShareButtonClickListener());
    }
}
