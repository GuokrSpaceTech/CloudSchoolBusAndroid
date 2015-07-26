package com.guokrspace.cloudschoolbus.parents.views;

import android.content.Context;

import com.dexafree.materialList.cards.SimpleCard;

/**
 * Created by wangjianfeng on 15/7/26.
 */
public class ReportListCard extends SimpleCard {
    private String reporttype;
    private String timestamp;
    private Context mContext;

    public ReportListCard(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayout() {
        return 0;
    }

    public String getReporttype() {
        return reporttype;
    }

    public void setReporttype(String reporttype) {
        this.reporttype = reporttype;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}
