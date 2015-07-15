package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ArticleEntity;
import com.guokrspace.cloudschoolbus.parents.entity.Article;

import java.util.ArrayList;

/**
 * Created by wangjianfeng on 15/7/14.
 */
public class ArticlesAdapter extends BaseAdapter{

    ArrayList<ArticleEntity> mArticles = new ArrayList<ArticleEntity>();
    Context mCntx;


    public ArticlesAdapter(ArrayList<ArticleEntity> articleEntities, Context context) {
        mArticles = articleEntities;
        mCntx = context;
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public Object getItem(int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mCntx).inflate(R.layout.material_customer_card_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArticleEntity articleEntity = mArticles.get(position);
        String imageUrl = articleEntity.getImages().get(0).getSource();
        Uri uri = Uri.parse(imageUrl);
        holder.build(uri);

        return convertView;
    }

    private class ViewHolder
    {
        ImageView bigImageView;

        public ViewHolder(View view) {
            bigImageView = (ImageView)view.findViewById(R.id.imageView);
        }

        void build(Uri url)
        {
            bigImageView.setImageURI(url);
        }
    }
}
