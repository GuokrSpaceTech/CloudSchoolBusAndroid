package com.cytx.constants;

import com.cytx.utility.FileTools;

/**
 * 类说明：常量类
 */
public class Constants {

	// 条件编译：变量控制
	public final static boolean isDebug = false;

	// 横评滑动的像素判定
	public final static int PIXEL_SCROLL = 200;

	// ================接口定义==================//
	/**
	 * ServiceUrl
	 */
	// public final static String SERVICE_URL = "http://apitest.yunxiaoche.com";
	public final static String SERVICE_URL = "http://yzxc.summer2.chunyu.me";

	/**
	 * 问题创建接口
	 */
	public final static String QUESTION_CTREATED = SERVICE_URL + "/partner/yzxc/problem/create";

	/**
	 * 问题追问接口
	 */
	public final static String QUESTION_ASKED = SERVICE_URL + "/partner/yzxc/problem_content/create";

	/**
	 * 问题评价借口
	 */
	public final static String QUESTION_ASSESS = SERVICE_URL + "/partner/yzxc/problem/assess";

	/**
	 * 问题详情接口
	 */
	public final static String QUESTION_DETAIL = SERVICE_URL + "/partner/yzxc/problem";
	// /<problem_id>/detail";

	/**
	 * 我的提问历史
	 */
	public final static String QUESTION_HISTORY = SERVICE_URL + "/partner/yzxc/problem/list/my";
	// public final static String QUESTION_HISTORY = "http://test2.xilehang.cn/test";

	/**
	 * 文件上传
	 */
	public final static String FILE_UPLOAD = SERVICE_URL + "/files/upload/";

	/**
	 * 医生信息
	 */
	public final static String DOCTOR_INFO = SERVICE_URL + "/partner/yzxc/doctor";
	// /<doctor_id>/detail";

	// =======================错误处理========================//
	// 无法解析主机
	public static final String HOST_ERROR = "can't resolve host";

	// 网络超时
	public static final String SOCKET_TIME_OUT = "socket time out";

	// =======================sharepreference的key值============//
	// 是否为第一次进入医生咨询界面
	public static final String IS_FIRST_CONSULT = "is_first_consult";
	// 音频文件存储的地址
	public static final String AUDIO_DIR = FileTools.getSDcardPath() + "/cyxc";
}
