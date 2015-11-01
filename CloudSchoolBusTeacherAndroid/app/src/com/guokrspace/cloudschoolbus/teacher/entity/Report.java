package com.guokrspace.cloudschoolbus.teacher.entity;

import java.util.List;

/**
 * Created by wangjianfeng on 15/7/26.
 */
//{"id":"428","title":"Ryan","cnname":"Ryan","reportname":"学生日报",
//        "content":[{"title":"课程内容","answer":"hsjjshjahxbhxjbshhshahhbxhxxhbdhhxhbxbxhsjsbxbxhxhdhbxnjdhsjxnjxkdnnx"},
//        {"title":"home work","answer":"bxhhshsgsshxhbxhdgajabvagsychhhxhhdjsjbx"},
//        {"title":"教师评价","answer":"hxhhshsgxbhxjjsbxnkxhsfagywudifofnsbcxyfuhdnsj"}],
//        "studentlist":"53834",
//        "reporttime":"1422633600",
//        "createtime":"1422692085",
//        "type":"1",
//        "adduserid":"1380",
//        "teachername":"掌声有请A班老师",
//        "studentlistid":"53834",
//        "studentname":"Ryan"}
public class Report {
    private String id;
    private String title;
    private String cnname;
    private String reportname;
    private List<ReportItem> content;
    private String studentlist;
    private String reporttime;
    private String createtime;
    private String type;
    private String adduserid;
    private String teachername;
    private String studentlistid;
    private String studentname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public String getReportname() {
        return reportname;
    }

    public void setReportname(String reportname) {
        this.reportname = reportname;
    }

    public String getStudentlist() {
        return studentlist;
    }

    public void setStudentlist(String studentlist) {
        this.studentlist = studentlist;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdduserid() {
        return adduserid;
    }

    public void setAdduserid(String adduserid) {
        this.adduserid = adduserid;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getStudentlistid() {
        return studentlistid;
    }

    public void setStudentlistid(String studentlistid) {
        this.studentlistid = studentlistid;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public List<ReportItem> getContent() {
        return content;
    }

    public void setContent(List<ReportItem> content) {
        this.content = content;
    }
}
