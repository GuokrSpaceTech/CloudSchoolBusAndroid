package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;

/**
 * Created by macbook on 15-8-13.
 */
public class StudentReport implements Serializable{
    String reportType;
    String reportUrl;

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
