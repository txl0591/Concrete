package com.concrete.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

public class IntentDef {

	public static final String SERVICE_NAME_MAIN			="com.rfid.service.MainService";

	public static final String MODULE_RESPONSION			="rfid.intent.action.MODULE_RESPONSION";
	public static final String MODULE_DISTRIBUTE			="rfid.intent.action.MODULE_DISTRIBUTE";

	public static final String INTENT_COMM_CMD				="rfid.intent.netcomm.CMD";
	public static final String INTENT_COMM_DATA			="rfid.intent.netcomm.DATA";
	public static final String INTENT_COMM_DATALEN		="rfid.intent.netcomm.DATALEN";
	public static final String INTENT_COMM_PARAM			="rfid.intent.netcomm.PARAM";
	public static final int    INTENT_TYPE_INVALID        = -1;

	public final static String DEFAULT_DIR = "CoreSoft";

	public  final static String HTTP_SERVICE = "http://268212.iask.in:10433?";

	public class HTTP_CMD{
		public static final String Http_LoginIn = "LoginIn";
		public static final String Http_LoginOut = "LoginOut";
		public static final String Http_Query = "Query";
		public static final String Http_Insert = "Insert";
		public static final String Http_Update = "Update";
		public static final String Http_Delete = "Delete";
	}

	public class LOG_LEVEL{
		public static final int LOG_LOW = 0x01;
		public static final int LOG_MIDDLE = 0x02;
		public static final int LOG_HIGH = 0x04;
		public static final int LOG_PRINT = 0x08;
	}

	public interface OnFragmentListener
	{
		public void OnFragmentReport(View view);
		public void OnFragmentReport(String Id);
	}

	public interface OnSqlReportListener
    {
        public void OnSqlDataReport(int Oper, ResultSet Result);
    }

	public class NFC_STATE
	{
		public final static int NFC_STATE_NONE = 0x00;
		public final static int NFC_STATE_IDLE = 0x01;
		public final static int NFC_STATE_WORK = 0x01;
	}


	public interface OnCommDataReportListener
	{
		public void OnResponsionReport(int Cmd, Bundle Data, int DataLen);
		public void OnDistributeReport(int Cmd, Bundle Data, int DataLen);
	}

	public interface  OnLogUserReportListener{
		public void OnLogUserReport(boolean State);
	}

	public class HttpState
	{
		public final static int DOWNLOAD_START = 0;
		public final static int DOWNLOAD_ING = 1;
		public final static int DOWNLOAD_SUCCESS = 2;
		public final static int DOWNLOAD_ERROR = 3;

		public final static int UPLOAD_START = 10;
		public final static int UPLOAD_ING = 11;
		public final static int UPLOAD_SUCCESS = 12;
		public final static int UPLOAD_ERROR = 13;
	};

	public interface OnHttpReportListener
	{
		public void OnHttpDataReport(int Oper, long param1, long param2);
	}

	public interface HttpReqCallBack
	{
		void onReqSuccess(int Cmd, Handler handler, Object result);
	}
}
