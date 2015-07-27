package com.guokrspace.cloudschoolbus.parents.views;

import android.content.Context;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;

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
        return R.layout.material_report_card;
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
