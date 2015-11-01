package com.guokrspace.cloudschoolbus.teacher.base.include;

/**
 * Created by macbook on 15/9/6.
 */
public class ErrorCodeConstant {
    public int CODE_SUCCESS = 1  ;// 操作成功 |
    public int CODE_SESSION_ERROR = -1 ;  //SESSION 过期
    public int CODE_NO_DATA = -1000 ; //无数据
    public int CODE_APIKEY_ERROR = -1110 ;//	|apikey 不正确|
    public int CODE_PARAM_ERROR = -1111 ;//	|参数错误|
    public int CODE_NO_SID = -1116 ;//|没有传入sid 参数|
    public int CODE_UNAUTHORIZED =-1120 ;  //无对象的操作权限
    public int CODE_SID_TIMEOUT = -1113 ;//	|sid 为空，用户登录被退出|
    public int CODE_SEVICE_OVER = -1115 ;//	|服务到期|
    public int CODE_NO_MOBILE = -1117 ; // 电话号码不存在
    public int CODE_ERR_VERIFY = -1118 ; // 效验码错误
    public int CODE_VERIFYSEND_ERR = -1119 ; // 效验码发送失败
    public int CODE_RONG_GETTOKEN_ERR = -2001 ; // 调用融云即时通讯Token错误
}
