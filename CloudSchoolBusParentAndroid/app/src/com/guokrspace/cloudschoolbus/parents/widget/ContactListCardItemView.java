package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class ContactListCardItemView extends CardItemView<ContactListCard> {

    public ContactListCardItemView(Context context) {
        super(context);
    }

    public ContactListCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactListCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(ContactListCard card) {
        super.build(card);

        /*
         * Header
         */
        //Teacher Head
        ImageView ContactHead = (ImageView) findViewById(R.id.teacher_avatar);
        if (ContactHead != null) {
            if(card.getContactAvatarUrl() == null || card.getContactAvatarUrl().isEmpty()) {
                ContactHead.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getContactAvatarUrl()).into(ContactHead);
            }
        }
        //Teacher Name
        TextView ContactName = (TextView) findViewById(R.id.teacher_name);
        ContactName.setText(card.getContactName());
        if (card.getDescriptionColor() != -1) {
            ContactName.setTextColor(card.getDescriptionColor());
        }

        //Classname
        TextView classNameTextView = (TextView) findViewById(R.id.class_name);
        classNameTextView.setText(card.getClassname());
        if (card.getDescriptionColor() != -1) {
            classNameTextView.setTextColor(card.getDescriptionColor());
        }

        //Timestamp
        TextView timstampTextView = (TextView) findViewById(R.id.timestamp);
        timstampTextView.setText(card.getTimestamp());
        if (card.getDescriptionColor() != -1) {
            timstampTextView.setTextColor(card.getDescriptionColor());
        }

        //Badge
        if(card.getBadgeCount() != 0)
        {
            BadgeView badgeView = new BadgeView(card.getContext());
            badgeView.setBadgeCount(card.getBadgeCount());
            badgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
            badgeView.setTargetView(ContactHead);
        }
    }
}
