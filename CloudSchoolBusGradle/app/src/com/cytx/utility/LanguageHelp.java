package com.cytx.utility;

import java.util.Locale;

import android.content.Context;

public class LanguageHelp {
	
	/**
	 * 判定当前手机使用的语言是否为中文
	 * @param context
	 * @return true：为中文
	 */
	public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

}
