package com.guokrspace.cloudschoolbus.teacher.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.teacher.R;
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
    public void build(final ContactListCard card) {
        super.build(card);
        /*
         * Header
         */
        //Teacher Head
        ImageView ContactHead = (ImageView) findViewById(R.id.teacher_avatar);
        if (ContactHead != null) {
            if (card.getChildrenname() == null || card.getChildrenname().isEmpty()) {
                ContactHead.setImageDrawable(card.getDrawable());
            } else {
                Picasso.with(getContext()).load(card.getChildrenname()).fit().centerCrop().into(ContactHead);
            }
        }
        //Teacher Name
        TextView ContactName = (TextView) findViewById(R.id.teacher_name);
        ContactName.setText(card.getContactName());
        if (card.getDescriptionColor() != -1) {
            ContactName.setTextColor(card.getDescriptionColor());
        }

        //ChildrenName for parents, duty for teacher in teacher app, classname for parent app
        TextView classNameTextView = (TextView) findViewById(R.id.textViewSubtitle);
        classNameTextView.setText(card.getSubtitle());
        if (card.getDescriptionColor() != -1) {
            classNameTextView.setTextColor(card.getDescriptionColor());
        }

        //Badege
        ImageView badgeImageView = (ImageView) findViewById(R.id.badgeImageView);
        if(card.isHasUnread()) badgeImageView.setVisibility(VISIBLE);
        else badgeImageView.setVisibility(INVISIBLE);

//        TextView phoneNumberTextView = (TextView) findViewById(R.id.phonenumber);
//        phoneNumberTextView.setText(card.getPhonenumber());
//        phoneNumberTextView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + card.getPhonenumber()));
//                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for Activity#requestPermissions for more details.
//                    return;
//                }
//                card.getContext().startActivity(intent);
//            }
//        });

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

        CardView cardView = (CardView) findViewById(R.id.cardView);
        cardView.setTag(card.getPosition());
        cardView.setOnClickListener(card.getOnClickListener());
    }
}
