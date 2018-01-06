package com.concrete.net;

import com.concrete.app.R;

/**
 * Created by Tangxl on 2017/11/23.
 */

public class HttpEcho {
    public final static int  SUCCESS = 0;

    public final static int  ERROR_COMMON = 10000;
    public final static int  ERROR_PARAM = 10001;
    public final static int  ERROR_FORMAT = 10002;
    public final static int  ERROR_TIMEOUT = 10003;
    public final static int  ERROR_NOFOUND = 10004;
    public final static int  ERROR_LICENSE = 10005;
    public final static int  ERROR_ACCESS = 10006;
    public final static int  ERROR_UPLOAD = 10007;
    public final static int  ERROR_USERNAME = 10008;
    public final static int  ERROR_PASSWORD = 10009;
    public final static int  ERROR_QUERY = 10010;

    public final static int ERROR_INSERT_DB = 20001;

    public final static int  ERROR_SERVER = 0xF0000;

    public static int GetHttpEcho(int Echo) {
        int mEchoStr = 0;
        switch (Echo) {
            case SUCCESS:
                mEchoStr = R.string.http_SUCCESS;
                break;

            case ERROR_COMMON:
                mEchoStr = R.string.http_ERROR_COMMON;
                break;

            case ERROR_PARAM:
                mEchoStr = R.string.http_ERROR_PARAM;
                break;
            case ERROR_FORMAT:
                mEchoStr = R.string.http_ERROR_FORMAT;
                break;
            case ERROR_TIMEOUT:
                mEchoStr = R.string.http_ERROR_TIMEOUT;
                break;
            case ERROR_NOFOUND:
                mEchoStr = R.string.http_ERROR_NOFOUND;
                break;
            case ERROR_LICENSE:
                mEchoStr = R.string.http_ERROR_LICENSE;
                break;
            case ERROR_ACCESS:
                mEchoStr = R.string.http_ERROR_ACCESS;
                break;
            case ERROR_UPLOAD:
                mEchoStr = R.string.http_ERROR_UPLOAD;
                break;
            case ERROR_USERNAME:
                mEchoStr = R.string.http_ERROR_USERNAME;
                break;
            case ERROR_PASSWORD:
                mEchoStr = R.string.http_ERROR_PASSWORD;
                break;
            case ERROR_QUERY:
                mEchoStr = R.string.http_ERROR_QUERY;
                break;
            case  ERROR_SERVER:
                mEchoStr = R.string.http_ERROR_SERVER;
                break;

            case ERROR_INSERT_DB:
                mEchoStr = R.string.http_ERROR_INSERT_DB;
                break;

            default:
                mEchoStr = R.string.http_ERROR_DEFAULT;
                break;
        }

        return mEchoStr;
    }

}
