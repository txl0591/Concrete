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

	public static final int APP_MODE_CAIJI = 0x00;
	public static final int APP_MODE_WRITE = 0x01;

	public static final int APP_MODE = APP_MODE_WRITE;

	public static final String DEFAULT_PATH = Common.getInnerSDCardPath() +"/"+ "CoreSoft";
	public static final String SERVICE_NAME_MAIN			="com.rfid.service.MainService";

	public final static String DEFAULT_DIR = "CoreSoft";

	public class LOG_LEVEL{
		public static final int LOG_LOW = 0x01;
		public static final int LOG_MIDDLE = 0x02;
		public static final int LOG_HIGH = 0x04;
		public static final int LOG_PRINT = 0x08;
	}

	public interface OnFragmentListener
	{
		public void OnFragmentReport(View view);
		public void OnFragmentReport(int Id);
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


}
