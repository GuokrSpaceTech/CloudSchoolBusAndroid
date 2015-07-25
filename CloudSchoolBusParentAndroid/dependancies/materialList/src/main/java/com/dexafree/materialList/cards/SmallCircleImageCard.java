package com.dexafree.materialList.cards;

import android.content.Context;

import com.dexafree.materialList.R;

public class SmallCircleImageCard extends SimpleCard {

    public SmallCircleImageCard(final Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_small_image_card;
    }
}
