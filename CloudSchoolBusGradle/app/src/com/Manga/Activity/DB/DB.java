package com.Manga.Activity.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
	private static final String name = "guokr";
	private static final int version = 2;

	public DB(Context context) {
		// TODO Auto-generated constructor stub
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 学生信息uid作为唯一标识,其他数据表以u_id区分学生
		// 存储学生信息
		// int viptype vip类型 0普通用户免费 ,1试用vip ,2付费Vip
		// int applynum 0没有适用过vip，1适用过vip 0没有适用过vip，1适用过vip
		db.execSQL("CREATE  TABLE IF NOT EXISTS student_info (uid TEXT(10), studentid TEXT(10) ,skinid TEXT(5) ,studentno TEXT(20),sex TEXT(5),birthday TEXT(10),cnname TEXT(10),enname TEXT(10),nikename TEXT(10),mobile TEXT(10),classname TEXT(10),ischeck_mobile TEXT(5),username TEXT(20),avatar TEXT(200),allow_muti_online TEXT(5),age TEXT(5),viptype TEXT(5),applynum TEXT(5),orderendtime TEXT(5))");
		// 孩子管理
		db.execSQL("CREATE  TABLE IF NOT EXISTS signin (u_id TEXT(10), uid_student TEXT(20), uid_class TEXT(20) ,nikename TEXT(20),classname TEXT(20))");
		// 学校信息
		db.execSQL("CREATE  TABLE IF NOT EXISTS school (u_id TEXT(10),schoolname TEXT(20))");
		// 日志
		db.execSQL("CREATE  TABLE IF NOT EXISTS article (u_id TEXT(10), articleid TEXT(10) ,title TEXT(20),content TEXT(100),publishtime TEXT(10),plist TEXT(50),upnum TEXT(5),commentnum TEXT(5),havezan TEXT(5),fext TEXT(5),size TEXT(5))");
		// 赞列表 avatar为连接地址
		db.execSQL("CREATE  TABLE IF NOT EXISTS like (u_id TEXT(10), articleid TEXT(10) ,actionid TEXT(10) ,avatar TEXT(20),addtime TEXT(10))");
		// 评论列表 avatar为连接地址content
		db.execSQL("CREATE  TABLE IF NOT EXISTS comment (u_id TEXT(10), articleid TEXT(10) ,commentid TEXT(10) ,content TEXT(100),avatar TEXT(20),addtime TEXT(10),nickname TEXT(10),replynickname TEXT(10),isstudent TEXT(5),candelete TEXT(5),adduserid TEXT(10))");
		// 全部通知
		db.execSQL("CREATE  TABLE IF NOT EXISTS notice (u_id TEXT(10),noticeid TEXT(10), addtime TEXT(10) ,noticetitle TEXT(20),noticecontent TEXT(500),isconfirm TEXT(5),plist TEXT(50),haveisconfirm TEXT(5),noticekey TEXT(20))");
		// 重要通知
		db.execSQL("CREATE  TABLE IF NOT EXISTS tnotice (u_id TEXT(10),noticeid TEXT(10), addtime TEXT(10) ,noticetitle TEXT(20),noticecontent TEXT(500),isconfirm TEXT(5),plist TEXT(50),haveisconfirm TEXT(5),noticekey TEXT(20))");
		// 日志和消息通知图片 p_name图片网络地址,p_res BAES64图片格式
		db.execSQL("CREATE  TABLE IF NOT EXISTS article_pic (p_name TEXT(50),p_res TEXT(100))");
		// 食谱
		db.execSQL("CREATE  TABLE IF NOT EXISTS cookbook (u_id TEXT(10),menu_day TEXT(10),menu_name TEXT(20),menu_type_name TEXT(5))");
		// 课程表
		db.execSQL("CREATE  TABLE IF NOT EXISTS syllabus (u_id TEXT(10),day TEXT(5),scheduletime TEXT(5),cnname TEXT(20))");
		// 全部活动列表
		db.execSQL("CREATE  TABLE IF NOT EXISTS all_activity (u_id TEXT(10),events_id TEXT(5),title TEXT(20),addtime TEXT(10),SignupStatus TEXT(5),isSignup TEXT(5),htmlurl TEXT(15))");
		// 我的活动列表
		db.execSQL("CREATE  TABLE IF NOT EXISTS my_activity (u_id TEXT(10),events_id TEXT(5),title TEXT(20),addtime TEXT(10),SignupStatus TEXT(5),isSignup TEXT(5),htmlurl TEXT(15))");
		// 可报名列表
		db.execSQL("CREATE  TABLE IF NOT EXISTS can_activity (u_id TEXT(10),events_id TEXT(5),title TEXT(20),addtime TEXT(10),SignupStatus TEXT(5),isSignup TEXT(5),htmlurl TEXT(15))");
		// 日常接送表
		db.execSQL("CREATE  TABLE IF NOT EXISTS child_receiver (u_id TEXT(10),pid TEXT(5),filepath TEXT(20),relationship TEXT(10))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion == 1 && newVersion == 2) {
			db.execSQL("ALTER TABLE article ADD COLUMN fext TEXT(5)");
			db.execSQL("ALTER TABLE article ADD COLUMN size TEXT(5)");
			db.execSQL("ALTER TABLE student_info ADD COLUMN orderendtime TEXT(5)");
			oldVersion = newVersion;
		}
	}

}
