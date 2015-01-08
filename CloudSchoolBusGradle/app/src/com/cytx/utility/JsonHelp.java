package com.cytx.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonHelp {
	
	// 将对象反序列化，转化为json字符串
	public static String jsonObjectToString( Object object ) throws JSONException{
		
		return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
	}
	
}
