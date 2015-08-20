package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

/**
 * Created by wangjianfeng on 15/7/26.
 */
public class ScheduleNoticeCardItemView extends CardItemView<ScheduleNoticeCard> {

    public ScheduleNoticeCardItemView(Context context) {
        super(context);
    }

    public ScheduleNoticeCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScheduleNoticeCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(ScheduleNoticeCard card) {
        super.build(card);

        //Kindergarten Avatar
        ImageView kindergartenAvatar = (ImageView) findViewById(R.id.kindergarten_avatar);
        if (kindergartenAvatar != null) {
            if(card.getKindergartenAvatar() == null || card.getKindergartenAvatar().isEmpty()) {
                kindergartenAvatar.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getKindergartenAvatar()).into(kindergartenAvatar);
            }
        }

        //Kindergarten Name
        TextView kindergartenTextView = (TextView) findViewById(R.id.kindergarten_name);
        kindergartenTextView.setText(card.getKindergartenName());
        if (card.getDescriptionColor() != -1) {
            kindergartenTextView.setTextColor(card.getDescriptionColor());
        }

        //Classname
        TextView classnameTextView = (TextView) findViewById(R.id.classname);
        classnameTextView.setText(card.getKindergartenName());
        if (card.getDescriptionColor() != -1) {
            classnameTextView.setTextColor(card.getDescriptionColor());
        }
        //Timestamp
        String   sendTimeString = card.getSentTime();
        TextView sentTimeTextView    = (TextView) findViewById(R.id.timestamp);
        sentTimeTextView.setText(sendTimeString);
        if (card.getDescriptionColor() != -1) {
            sentTimeTextView.setTextColor(card.getDescriptionColor());
        }

        /* Card Type */
        TextView cardTypeTextView = (TextView)findViewById(R.id.card_type);
        cardTypeTextView.setText(card.getCardType());


        /* Card Description */
        TextView cardDescriptonTextView = (TextView)findViewById(R.id.description);
        cardDescriptonTextView.setText(card.getDescription());

        /*  */
        LinearLayout layout = (LinearLayout)findViewById(R.id.taptoviewLayout);
        layout.setOnClickListener(card.getClickListener());
    }
}
