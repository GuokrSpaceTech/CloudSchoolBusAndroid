package com.Manga.Activity.utils;

public class DebugClass {

	public static boolean DEBUG_ON=true;	

	public static String getCurrentMethodName()
	{
		//always the 4th stack is the caller of public method getCurrentMethodName()
		//notes: stack[0]: getThreadStackTrace()  
		//       stack[1]: getStackTrace()  
		//       stack[2]: getCurrentMethodName()  
		//       stack[3]: caller()  
		return Thread.currentThread().getStackTrace()[3].toString();
	}

	public static void displayCurrentStack(String outPut)
	{
		//always the 4th stack is the caller of public method getCurrentMethodName()
		//notes: stack[0]: getThreadStackTrace()  
		//       stack[1]: getStackTrace()  
		//       stack[2]: displayCurrentStack()  
		//       stack[3]: caller()  

		if(DEBUG_ON)
		{
			System.out.println(Thread.currentThread().getStackTrace()[3].toString()+(": "+outPut));
//			System.out.println((": "+outPut));
		}
	}

	public static void displayCurrentStack()
	{
		//always the 4th stack is the caller of public method getCurrentMethodName()
		//notes: stack[0]: getThreadStackTrace()  
		//       stack[1]: getStackTrace()  
		//       stack[2]: displayCurrentStack()  
		//       stack[3]: caller()  
		if(DEBUG_ON)
		{
			System.out.println(Thread.currentThread().getStackTrace()[3].toString());
//			System.out.println(" ok");
		}
	}
}
