package com.guokrspace.cloudschoolbus.parents.module.photo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.support.utils.ImageFormatUtils;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleFileEntity;

import java.text.DecimalFormat;
import java.util.List;

public class UploadQueueAdapter extends BaseAdapter {

    public static final String ITEM_FIRST = "item_first";
    public static final String ITEM = "item";

    private Context mContext;
    private View.OnClickListener mRetryClickListener;
    private List<UploadArticleFileEntity> mUploadFiles;
    /** true标示删除上传文件 */
    /**
     * 列表第一条的view用来更新进度
     */
    private View mFirstView;

    public UploadQueueAdapter(Context context, List<UploadArticleFileEntity> uploadFiles) {
        mContext = context;
        mUploadFiles = uploadFiles;
    }

    @Override
    public int getCount() {
        return mUploadFiles.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUploadFiles.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        if (null == arg1) {
            if (0 == arg0) {
                arg1 = LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_upload_file, null);
                arg1.setTag(ITEM_FIRST);
                mFirstView = arg1;
            } else if (arg0 > 0) {
                arg1 = LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_upload_file, null);
                arg1.setTag(ITEM);
            }
        } else {

            if (0 == arg0 && !ITEM_FIRST.equals(arg1.getTag())) {
                arg1 = mFirstView;
            } else if (arg0 > 0 && !ITEM.equals(arg1.getTag())) {
                arg1 = LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_upload_file, null);
                arg1.setTag(ITEM);
            }
        }

        UploadArticleFileEntity uploadFile = mUploadFiles.get(arg0);

        ImageView leftImageView = (ImageView) arg1.findViewById(R.id.leftImageView);

        Drawable drawable = ImageFormatUtils.getInstance().Bytes2Drawable(uploadFile.getFbody());

        leftImageView.setImageDrawable(drawable);

        TextView fileNameTextView = (TextView) arg1.findViewById(R.id.fileNameTextView);

        fileNameTextView.setText(uploadFile.getFname());

        TextView fileSizeTextView = (TextView) arg1.findViewById(R.id.fileSizeTextView);
        double fileSize = 0D;
        String fileSizeString = "0";
        try {
            fileSize = uploadFile.getFbody().length;
            fileSize = fileSize / 1024 / 1024;
            DecimalFormat df = new DecimalFormat("0.000");
            fileSizeString = df.format(fileSize);
        } catch (Exception e) {
        }
        fileSizeTextView.setText(fileSizeString + "M");
        ImageView retryImageView = (ImageView) arg1.findViewById(R.id.retryImageView);
        TextView progressTextView = (TextView) arg1.findViewById(R.id.progressTextView);
        ProgressBar progressBar = (ProgressBar) arg1.findViewById(R.id.progressBar);
        TextView deleteTextView = (TextView) arg1.findViewById(R.id.deleteTextView);
        progressTextView.setText("0" + "%");

        deleteTextView.setVisibility(View.GONE);
        progressTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if(uploadFile.getIsSuccess()!=null && uploadFile.getIsSuccess()==false)
        {
            retryImageView.setVisibility(View.VISIBLE);
            retryImageView.setTag(uploadFile);
            retryImageView.setOnClickListener(mRetryClickListener);

            fileNameTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            progressTextView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

        } else {
            retryImageView.setVisibility(View.INVISIBLE);
            fileNameTextView.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
            progressTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        };

        return arg1;
    }

    public View getFirstView() {
        return mFirstView;
    }


    public void setmUploadFiles(List<UploadArticleFileEntity> mUploadFiles) {
        this.mUploadFiles = mUploadFiles;
    }

    public View.OnClickListener getmRetryClickListener() {
        return mRetryClickListener;
    }

    public void setmRetryClickListener(View.OnClickListener mRetryClickListener) {
        this.mRetryClickListener = mRetryClickListener;
    }
}
