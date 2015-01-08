package com.Manga.Activity.ClassUpdate;

import java.util.List;

public class ArticleDto {
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
	private List<ImageFileDto> plist;
	private List<TagDto> taglist;
	public String getArticlekey() {
		return articlekey;
	}
	public void setArticlekey(String articlekey) {
		this.articlekey = articlekey;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getArticleid() {
		return articleid;
	}
	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPublishtime() {
		return publishtime;
	}
	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getUpnum() {
		return upnum;
	}
	public void setUpnum(String upnum) {
		this.upnum = upnum;
	}
	public String getCommentnum() {
		return commentnum;
	}
	public void setCommentnum(String commentnum) {
		this.commentnum = commentnum;
	}
	public String getHavezan() {
		return havezan;
	}
	public void setHavezan(String havezan) {
		this.havezan = havezan;
	}
	public List<ImageFileDto> getPlist() {
		return plist;
	}
	public void setPlist(List<ImageFileDto> plist) {
		this.plist = plist;
	}
	public List<TagDto> getTaglist() {
		return taglist;
	}
	public void setTaglist(List<TagDto> taglist) {
		this.taglist = taglist;
	}
}
