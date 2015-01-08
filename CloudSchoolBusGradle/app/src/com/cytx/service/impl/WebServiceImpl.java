package com.cytx.service.impl;

import java.io.File;
import java.io.FileNotFoundException;

import com.cytx.constants.Constants;
import com.cytx.dto.QuestionAskedDto;
import com.cytx.dto.QuestionAssessDto;
import com.cytx.dto.QuestionCreatedDto;
import com.cytx.dto.QuestionHistoryDto;
import com.cytx.service.WebService;
import com.cytx.utility.CytxClient;
import com.cytx.utility.JsonHelp;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 实现WebService接口
 * @author xilehang
 *
 */
public class WebServiceImpl implements WebService {
	
	private static WebServiceImpl wsi;
	private WebServiceImpl(){}
	public static WebServiceImpl getInstance(){
		if (wsi == null) {
			wsi = new WebServiceImpl();
		}
		return wsi;
	}

	// 问题创建
	@Override
	public void questionCreated(QuestionCreatedDto qcd,
			AsyncHttpResponseHandler asynchttpresponsehandler) {
		RequestParams params = new RequestParams();
		params.put("user_id", qcd.getUser_id());
		params.put("sign", qcd.getSign());
		params.put("atime", qcd.getAtime());
		params.put("clinic_no", qcd.getClinic_no());
		params.put("content", JsonHelp.jsonObjectToString(qcd.getContent()));
		CytxClient.post(Constants.QUESTION_CTREATED, params, asynchttpresponsehandler);
		
	}

	// 问题追问
	@Override
	public void questionAsked(QuestionAskedDto qad,
			AsyncHttpResponseHandler asynchttpresponsehandler) {
		RequestParams params = new RequestParams();
		params.put("problem_id", qad.getProblem_id());
		params.put("user_id", qad.getUser_id());
		params.put("atime", qad.getAtime());
		params.put("sign", qad.getSign());
		params.put("content", JsonHelp.jsonObjectToString(qad.getContent()));
		CytxClient.post(Constants.QUESTION_ASKED, params, asynchttpresponsehandler);
	}

	// 问题评价
	@Override
	public void questionAssess(QuestionAssessDto qad,
			AsyncHttpResponseHandler asynchttpresponsehandler) {
		RequestParams params = new RequestParams();
		params.put("user_id", qad.getUser_id());
		params.put("problem_id", qad.getProblem_id());
		params.put("star", qad.getStar() + "");
		params.put("content", qad.getContent());
		params.put("sign", qad.getSign());
		params.put("atime", qad.getAtime());
		CytxClient.post(Constants.QUESTION_ASSESS, params, asynchttpresponsehandler);
	}

	// 问题详情
	@Override
	public void questionDetail(String user_id, String problem_id,
			AsyncHttpResponseHandler asynchttpresponsehandler) {
		RequestParams params = new RequestParams();
		params.put("user_id", user_id);
		CytxClient.get(Constants.QUESTION_DETAIL + "/" + problem_id + "/detail", params, asynchttpresponsehandler);
		
	}

	// 问题历史
	@Override
	public void questionHistory(QuestionHistoryDto qhd,
			AsyncHttpResponseHandler asynchttpresponsehandler) {
//		String jsonString = JsonHelp.jsonObjectToString(qhd);
		RequestParams params = new RequestParams();
		params.put("user_id", qhd.getUser_id());
		params.put("atime", qhd.getAtime());
		params.put("sign", qhd.getSign());
		params.put("count", qhd.getCount() + "");
		params.put("start_num", qhd.getStart_num() + "");
		CytxClient.post(Constants.QUESTION_HISTORY, params, asynchttpresponsehandler);
	}

	// 上传文件
	@Override
	public void fileUpload(String type, String filePath,
			AsyncHttpResponseHandler asynchttpresponsehandler) {
		
//		System.out.println("filePath ===== " + filePath);
		
		File file = new File(filePath);
		RequestParams params = new RequestParams();
		try {
			params.put("type", type);
			params.put("file", file);
			CytxClient.post(Constants.FILE_UPLOAD, params, asynchttpresponsehandler);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// 医生信息
	@Override
	public void doctorInfo(String doctor_id,
			AsyncHttpResponseHandler asynchttpresponsehandle) {
		RequestParams params = new RequestParams();
		CytxClient.get(Constants.DOCTOR_INFO + "/" + doctor_id + "/detail", params, asynchttpresponsehandle);
	}
	

}
