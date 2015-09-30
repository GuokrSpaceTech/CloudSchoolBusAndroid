package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class PictureCardItemView extends CardItemView<PictureCard> {

    private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");

    public PictureCardItemView(Context context) {
        super(context);
    }

    public PictureCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(PictureCard card) {
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
                String url = card.getTeacherAvatarUrl();
                if(url.contains("jpg.")) {
                    url.replaceAll("jpg.","jpg");
                }
                Picasso.with(getContext()).load(card.getTeacherAvatarUrl()).fit().centerCrop().into(teacherHead);
            }
        }
        //Teacher Name
        TextView teacherName = (TextView) findViewById(R.id.teacher_name);
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
        String sendTime = card.getSentTime();
        TextView sentTimeTextView = (TextView) findViewById(R.id.timestamp);
        if (sendTime != null) {
            sentTimeTextView.setText(DateUtils.timelineTimestamp(sendTime, card.getContext()));
            if (card.getDescriptionColor() != -1) {
                sentTimeTextView.setTextColor(card.getDescriptionColor());
            }
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

        //ImageGrid
        GridView gridView = (GridView) findViewById(R.id.dynamic_grid);
        if(card.getImageAdapter()!=null) {
            if (card.getImageAdapter().getCount() == 4) {
                gridView.setNumColumns(2);
                ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
                layoutParams.width = getResources().getDimensionPixelSize(R.dimen.timeline_thumb_pic_width) * 2 + getResources().getDimensionPixelSize(R.dimen.timeline_thumb_pic_spacing);
                gridView.setLayoutParams(layoutParams);
            } else if(card.getImageAdapter().getCount() > 1){
                gridView.setNumColumns(3);
                ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
                layoutParams.width = getResources().getDimensionPixelSize(R.dimen.timeline_thumb_pic_width) * 3 + getResources().getDimensionPixelSize(R.dimen.timeline_thumb_pic_spacing);
                gridView.setLayoutParams(layoutParams);
            }

            gridView.setAdapter(card.getImageAdapter());
        }
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
