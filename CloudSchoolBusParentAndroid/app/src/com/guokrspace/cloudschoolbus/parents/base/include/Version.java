package com.guokrspace.cloudschoolbus.parents.base.include;

//	软件版本信息,每次发布新版本时,都要修改此文件.
public class Version {
	/** true表示debug版本，有打印信息和调试功能，false去掉打印信息和调试功能，调试功能长按关于键 */
	public static final boolean DEBUG = false;
    public static final boolean PARENT = true;
	/** 产品最后更新时间*/
	public final static String productLastUpdateDate = "20150916";
	/** 产品版本号(用来更新的产品版本号)*/
	public final static String versionCode = "4";
	/** 版本描述，用来上传参数和显示，显示的时候讲前面的a去掉*/
	public final static String versionName = "a3.5.1";
	
	
	// 产品描述
	public final static String desc = "云中校车";
	// 更新内容
	public final static String updateContent = "正式版本";
	// 产品名称
	public final static String productNameParent = "家长端";
    public final static String productNameTeacher = "教师端";
	// 产品别名
	public final static String minProductName = "CloudSchoolBus";
	// 下载地址
	public static String url = "";
	// 当前版本是否必须更新
	public final static String mustUpdate = "0";
	// 运行平台
	public final static String platform = "android";
	// App名称
	public final static String APP_NAME = "CloudSchoolBusParent.apk";
}
