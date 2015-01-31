package com.Manga.Activity.ClassUpdate.Model;

import com.Manga.Activity.ClassUpdate.Model.ArticleDto;

import java.util.List;

public class ArticleListDto {
	private List<ArticleDto> articlelist;
	private String can_comment_action;
	private String can_comment;

	public List<ArticleDto> getArticlelist() {
		return articlelist;
	}

	public void setArticlelist(List<ArticleDto> articlelist) {
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
