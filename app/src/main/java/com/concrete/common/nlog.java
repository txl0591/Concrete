package com.concrete.common;


import android.util.Log;

public class nlog {
private final static String TAG = "CoreSoft";
		
	private static int mLogLevel = (IntentDef.LOG_LEVEL.LOG_LOW| IntentDef.LOG_LEVEL.LOG_PRINT| IntentDef.LOG_LEVEL.LOG_HIGH| IntentDef.LOG_LEVEL.LOG_MIDDLE);

	public static void Info(String log){
		Exception e = new Exception();
		StackTraceElement[] trace = e.getStackTrace();
		Log.v(TAG,getAutoJumpLogInfos()+log);
	}
	
	public static void IfInfo(int level, String log)
	{
		if (0 != (level&mLogLevel))
		{
			Exception e = new Exception();
			StackTraceElement[] trace = e.getStackTrace();
			Log.v(TAG,getAutoJumpLogInfos()+log);
		}
	}

	/**
	 * 获取打印信息所在方法名，行号等信息
	 * @return
	 */
	private static String getAutoJumpLogInfos() {
		String infos = "";
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (elements.length < 5) {
			return infos;
		} else {
			infos = "["+elements[4].getClassName() + ".java:" + elements[4].getLineNumber()+"]";
			return infos;
		}
	}
}
