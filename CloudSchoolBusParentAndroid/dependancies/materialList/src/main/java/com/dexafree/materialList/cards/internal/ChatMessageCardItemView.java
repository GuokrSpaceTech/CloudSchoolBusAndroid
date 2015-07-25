package com.dexafree.materialList.cards.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dexafree.materialList.R;
import com.dexafree.materialList.cards.ChatMessageCard;
import com.dexafree.materialList.cards.SimpleCard;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class ChatMessageCardItemView<T extends ChatMessageCard> extends BaseCardItemView<T> {
    public ChatMessageCardItemView(Context context) {
        super(context);
    }

    public ChatMessageCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ChatMessageCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build(T card) {
		super.build(card);

        // Description
        TextView description = (TextView) findViewById(R.id.text_content);
        description.setText(card.getDescription());
        if (card.getDescriptionColor() != -1) {
            description.setTextColor(card.getDescriptionColor());
        }

        ImageView imageView_teacher_head = (ImageView) findViewById(R.id.imageView);

        ImageView imageView_self_head = (ImageView) findViewById(R.id.imageViewRight);

        if(card.getRoleType().equals("teacher"))
        {
        // ImageSelf
            imageView_teacher_head.setVisibility(VISIBLE);
        if (imageView_teacher_head != null) {
            if(card.getUrlImage() == null || card.getUrlImage().isEmpty()) {
                imageView_teacher_head.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getUrlImage()).into(imageView_teacher_head);
            }
        }
            imageView_self_head.setVisibility(INVISIBLE);
        } else {
            imageView_teacher_head.setVisibility(INVISIBLE);
            imageView_self_head.setVisibility(VISIBLE);
            if (imageView_self_head != null) {
                if(card.getSenderImageUrl() == null || card.getSenderImageUrl().isEmpty()) {
                    imageView_self_head.setImageDrawable(card.getmDrawableSender());
                } else {
                    Picasso.with(getContext()).load(card.getSenderImageUrl()).into(imageView_self_head);
                }
            }
        }

        //Timestamp
        long addtime = 0L;
        addtime = Long.parseLong(card.getTimestamp()) * 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Hongkong"));
        Date date = new Date(addtime);
        String dateString = simpleDateFormat.format(date);
        TextView timestamp_text_view = (TextView) findViewById(R.id.text_timestamp);
        timestamp_text_view.setText(dateString);
        if (card.getDescriptionColor() != -1) {
            description.setTextColor(card.getDescriptionColor());
        }


    }

}
