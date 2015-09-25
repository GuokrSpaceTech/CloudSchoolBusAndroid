package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.model.CardItemView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.WrappableGridLayoutManager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class PictureSentCardItemView extends CardItemView<PictureSentCard> {

    public PictureSentCardItemView(Context context) {
        super(context);
    }

    public PictureSentCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSentCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(PictureSentCard card) {
        super.build(card);

        //Timestamp
        String sendTime = card.getSentTime();
        TextView sentTimeTextView = (TextView) findViewById(R.id.timestamp);
        if (sendTime != null && sendTime!="") {
            sentTimeTextView.setText(sendTime);
            if (card.getDescriptionColor() != -1) {
                sentTimeTextView.setTextColor(card.getDescriptionColor());
            }
        }

        // Description
        TextView description = (TextView) findViewById(R.id.text_content);
        description.setText(card.getDescription());
        if (card.getDescriptionColor() != -1) {
            description.setTextColor(card.getDescriptionColor());
        }

        //ImageGrid
        GridView gridView = (GridView) findViewById(R.id.image_grid);
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

        //Tags
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tags_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(card.getContext());
        linearLayoutManager.setOrientation(HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(card.getTagAdapter());
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }
}
