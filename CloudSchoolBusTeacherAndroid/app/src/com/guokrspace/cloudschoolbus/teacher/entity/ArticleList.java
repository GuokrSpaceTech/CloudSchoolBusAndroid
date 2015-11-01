package com.guokrspace.cloudschoolbus.teacher.entity;

import java.util.List;

/**
 * Created by kai on 11/26/14.
 */
public class ArticleList {
    private List<Article> articlelist;
    private String can_comment_action;
    private String can_comment;

    public List<Article> getArticlelist() {
        return articlelist;
    }

    public void setArticlelist(List<Article> articlelist) {
        this.articlelist = articlelist;
    }

    public String getCan_comment_action() {
        return can_comment_action;
    }

    public void setCan_comment_action(String can_comment_action) {
        this.can_comment_action = can_comment_action;
    }

    public String getCan_comment() {
        return can_comment;
    }

    public void setCan_comment(String can_comment) {
        this.can_comment = can_comment;
    }
}
