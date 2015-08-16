package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
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
public class StreamingNoticeCardItemView extends CardItemView<StreamingNoticeCard> {

    public StreamingNoticeCardItemView(Context context) {
        super(context);
    }

    public StreamingNoticeCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StreamingNoticeCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(StreamingNoticeCard card) {
        super.build(card);

        //Classname
        TextView kindergarten = (TextView) findViewById(R.id.kindergarten_name);
        kindergarten.setText(card.getKindergartenName());
        if (card.getDescriptionColor() != -1) {
            kindergarten.setTextColor(card.getDescriptionColor());
        }
        //Timestamp
        String   sendTimeString = card.getSentTime();
        TextView sentTimeTextView    = (TextView) findViewById(R.id.timestamp);
        sentTimeTextView.setText(DateUtils.timelineTimestamp(sendTimeString, card.getContext()));
        if (card.getDescriptionColor() != -1) {
            sentTimeTextView.setTextColor(card.getDescriptionColor());
        }

        /* Card Type */
        TextView cardTypeTextView = (TextView)findViewById(R.id.card_type);
        cardTypeTextView.setText(card.getCardType());

        /*  */
        FrameLayout layout = (FrameLayout)findViewById(R.id.cardView);
        layout.setOnClickListener(card.getClickListener());
    }
}
