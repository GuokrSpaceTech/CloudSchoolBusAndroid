package com.Manga.Activity.utils;

public class Push_Info {
	private String strPush = "";
	private String strPushOpen = "";
	private boolean blnIsLogin = true;
	
	 private static Push_Info push_Info = null;

	 private Push_Info()
	 {
	 }
	 
	 public static Push_Info getInstance()
	 {
	     if (push_Info == null)
	     {
	         synchronized (Push_Info.class)
	         {
	              if (push_Info == null)
	              {
		              push_Info = new Push_Info();
	              }
	          }
	      }
	      
	     return push_Info;
	 }

	public String getStrPush() {
		return strPush;
	}

	public void setStrPush(String strPush) {
		this.strPush = strPush;
	}

	public String getStrPushOpen() {
		return strPushOpen;
	}

	public void setStrPushOpen(String strPushOpen) {
		this.strPushOpen = strPushOpen;
	}

	public boolean isBlnIsLogin() {
		return blnIsLogin;
	}

	public void setBlnIsLogin(boolean blnIsLogin) {
		this.blnIsLogin = blnIsLogin;
	}
}
