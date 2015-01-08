package com.cytx.service;

import com.cytx.dto.QuestionAskedDto;
import com.cytx.dto.QuestionAssessDto;
import com.cytx.dto.QuestionCreatedDto;
import com.cytx.dto.QuestionHistoryDto;
import com.loopj.android.http.AsyncHttpResponseHandler;


/**  
 * 类说明：  与服务器交互的接口
 */
public interface WebService {
	/**
	 * 问题创建访问服务器
	 * @param qcd 问题创建信息
	 * @param asynchttpresponsehandler
	 */
	void questionCreated(QuestionCreatedDto qcd, AsyncHttpResponseHandler asynchttpresponsehandler);
	
	/**
	 * 问题追问访问服务器
	 * @param qad 问题追问信息
	 * @param asynchttpresponsehandler
	 */
	void questionAsked(QuestionAskedDto qad, AsyncHttpResponseHandler asynchttpresponsehandler);

	/**
	 * 问题评价访问服务器
	 * @param qad 问题评价信息
	 * @param asynchttpresponsehandler
	 */
	void questionAssess(QuestionAssessDto qad, AsyncHttpResponseHandler asynchttpresponsehandler);

	/**
	 * 问题详情访问服务器
	 * @param user_id 用户ID
	 * @param asynchttpresponsehandler
	 */
	void questionDetail(String user_id,String problem_id, AsyncHttpResponseHandler asynchttpresponsehandler);

	/**
	 * 问题历史访问服务器
	 * @param qhd 问题历史信息
	 * @param asynchttpresponsehandler
	 */
	void questionHistory(QuestionHistoryDto qhd, AsyncHttpResponseHandler asynchttpresponsehandler);
	
	/**
	 * 上传文件
	 * @param imageFilePath
	 * @param asynchttpresponsehandler
	 */
	void fileUpload(String type, String filePath, AsyncHttpResponseHandler asynchttpresponsehandler);
	
	/**
	 * 获取医生信息
	 * @param doctor_id
	 * @param asynchttpresponsehandle
	 */
	void doctorInfo(String doctor_id, AsyncHttpResponseHandler asynchttpresponsehandle);

}
