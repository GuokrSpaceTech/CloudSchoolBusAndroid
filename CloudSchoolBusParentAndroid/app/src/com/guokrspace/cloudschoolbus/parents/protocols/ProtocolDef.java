package com.guokrspace.cloudschoolbus.parents.protocols;

/**
 * Created by wangjianfeng on 15/7/4.
 */
public class ProtocolDef {

    public static final String METHOD_register               =    "register";
    public static final String METHOD_verify                 =    "verify";
    public static final String METHOD_baseinfo               =    "baseinfo";
    public static final String METHOD_timeline               =    "getmessage";
    public static final String METHOD_pushchannel            =    "setchannelid";
    public static final String METHOD_latestchat             =    "getLastLetter";
    public static final String METHOD_noticeconfirm          =    "confirm";
    public static final String METHOD_changeAvartarStudent   =    "setStudentAvatar";
    public static final String METHOD_changeAvartarParent    =    "setParentAvatar";
    public static final String METHOD_Source                 =   "file-receiver";
    public static final String METHOD_upload                 =   "upload";
    public static final String METHOD_over                   =   "over";

    /**获取sid,在登录的时候使用,GET*/
    public static final String METHOD_signin                 =    "signin";
    /**获得班级学生列表,教师列表和班级信息*/
    public static final String METHOD_Classinfo              =   "classinfo";
}
