package com.guokrspace.cloudschoolbus.parents.module.photo.model;

import java.util.List;

/**
 * Created by kai on 11/26/14.
 */
public class UploadArticle {
    public String studentIdList;
    /**班级uid*/
    public String classuid;
    /**内容,不是必须*/
    public String intro;
    /**照片标签，不是必须*/
    public String photoTag;
    public String teacherid;

    private String articlekey;
    private String tag;
    private String articleid;
    private String title;
    private String content;
    private String publishtime;
    private String addtime;
    private String upnum;
    private String commentnum;
    private String havezan;
    private List<UploadArticleFile> plist;

}
