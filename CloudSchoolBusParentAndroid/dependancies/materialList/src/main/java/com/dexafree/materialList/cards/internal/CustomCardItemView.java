package com.dexafree.materialList.cards.internal;

import android.content.Context;
import android.support.v7.widget.BadgeView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.R;
import com.dexafree.materialList.cards.CustomCard;
import com.dexafree.materialList.model.CardItemView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class CustomCardItemView extends CardItemView<CustomCard> {

    private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
    private long toYear;
    private Context mCntx;

    public CustomCardItemView(Context context) {
        super(context);
    }

    public CustomCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(CustomCard card) {
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
        String   publishTime = card.getSentTime();
        TextView sentTime    = (TextView) findViewById(R.id.timestamp);
        if (publishTime != null) {
            long foo = Long.parseLong(publishTime) * 1000;
            long tmp = System.currentTimeMillis() - foo;
            if (foo > toYear) {
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
            } else {
                sentTime.setText(spl.format(new Date(foo)));
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
        recyclerView.setAdapter(card.getAdapter());

        recyclerView.addOnItemTouchListener(card.getmOnItemSelectedListener());

        //ShareButton
        ImageView shareButton = (ImageView)findViewById(R.id.share_button);
        shareButton.setOnClickListener(card.getmShareButtonClickListener());

        //Likes
        ImageView likesButton = (ImageView)findViewById(R.id.likes_button);
        BadgeView likesNumber = new BadgeView(card.getContext());
        likesNumber.setTargetView(likesButton);
        likesNumber.setBadgeGravity(Gravity.LEFT);
        likesNumber.setBadgeMargin(0, 0, 0, 0);
        likesButton.setOnClickListener(card.getmLikeButtonClickListener());

        //Test Purpose
        if(card.getLikesNum()==null || card.getLikesNum().equals("0"))
            likesNumber.setBadgeCount(3);
        else
            likesNumber.setBadgeCount(Integer.parseInt(card.getLikesNum()));

        // Comments
        ImageView commentsButton = (ImageView)findViewById(R.id.comments_button);
        BadgeView commentsNumber = new BadgeView(card.getContext());
        commentsNumber.setTargetView(commentsButton);
        commentsNumber.setBadgeGravity(Gravity.LEFT);
        commentsNumber.setBadgeMargin(0, 0, 0, 0);

        if(card.getCommentNum()==null|| card.getCommentNum().equals("0"))
            commentsNumber.setBadgeCount(3);
        else
            commentsNumber.setBadgeCount(Integer.parseInt(card.getCommentNum()));

        commentsButton.setOnClickListener(card.getmCommentButtonClickListener());
    }
}
