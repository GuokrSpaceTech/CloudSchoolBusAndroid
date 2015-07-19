package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 通知
 * @author jiahongfei
 *
 */
public class Notice implements Serializable{
	
	public static class PList implements Serializable{
		public String source;
		public String filename;
		public String iscloud;
	}
	
	/**添加者id*/
	public String adduserid;//	string	添加者id
	/**添加日期*/
	public String addtime;//	string	添加日期
	/**是否回执0否1是*/
	public String isconfirm;//	string	是否回执0否1是
	public String haveisconfirm;//	string	是否回执0否1是
	/**通知内容*/
	public String noticecontent;//	string	通知内容
	/**通知id*/
	public String noticeid;//	string	通知id
	/**通知唯一key*/
	public String noticekey;//	string	通知唯一key
	/**通知标题*/
	public String noticetitle;  //	string	通知标题
	public String isteacher;    //	string	通知id
	/**图片资源*/
	public List<PList> plist;//	array	图片资源
	/**图片路径路径后跟.tiny.jpg为小图*/
	public String source;//	string	图片路径路径后跟.tiny.jpg为小图
	/**已回执名单*/
	public String[] sisconfirm;//	array	已回执名单
	public String[] sisconfirmFlag;//0表示没有回执，1表示已回执，再下载列表的时候进行过滤
	/**回执人id*/
	public String[] slist;//	array	回执人id
	/**接受此通知的人名列表*/
	public String[] slistname	;//array	接受此通知的人名列表
	/**老师名称*/
	public String teachername;//	string	老师名称

}
