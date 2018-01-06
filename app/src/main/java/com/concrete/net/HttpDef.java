package com.concrete.net;

import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;

/**
 * Created by Tangxl on 2017/12/21.
 */

public class HttpDef {
    public  final static String BASE_URL = "http://47.100.126.101:81/?";

    public static final String HTTP_DISTRIBUTE			="http.intent.action.DISTRIBUTE";
    public static final String INTENT_HANDLE_HTTP_CMD				="http.intent.CMD";
    public static final String INTENT_HANDLE_HTTP_ECHO				="http.intent.ECHO";
    public static final String INTENT_HANDLE_HTTP_PARAM			="http.intent.PARAM";
    public static final int INTENT_HANDLE_HTTP_DEFAULT = -1;

    public static final String UNKNOW = "Unknow";

    public class HTTP_CMD{
        public static final String Http_LoginIn = "LoginIn";
        public static final String Http_LoginOut = "LoginOut";
        public static final String Http_Query = "Query";
        public static final String Http_Insert = "Insert";
        public static final String Http_Update = "Update";
        public static final String Http_Delete = "Delete";
    }

    public class HTTP_OPER_CMD{
        public static final int HANDLE_HTTP_QUREY_GCID = 0xF0001;
        public static final int HANDLE_HTTP_QUERY_RFID = 0xF0002;
        public static final int HANDLE_HTTP_QUERY_SJBH = 0xF0003;
        public static final int HANDLE_HTTP_QUERY_IMAGE = 0xF0004;
        public static final int HANDLE_HTTP_INSERT_SJBH = 0xF0005;
        public static final int HANDLE_HTTP_UPDATE_SJBH = 0xF0006;
        public static final int HANDLE_HTTP_DELETE_SJBH = 0xF0007;
        public static final int HANDLE_HTTP_INSERT_RFID = 0xF0008;
        public static final int HANDLE_HTTP_UPDATE_RFID = 0xF0009;
        public static final int HANDLE_HTTP_DELETE_RFID = 0xF000A;
        public static final int HANDLE_HTTP_INSERT_IMAGE = 0xF000B;
        public static final int HANDLE_HTTP_UPDATE_IMAGE = 0xF000C;
        public static final int HANDLE_HTTP_DELETE_IMAGE = 0xF000D;
        public static final int HANDLE_HTTP_QUERY_RFID_FROM_SJBH = 0xF000E;

        public final static int HANDLE_HTTP_LOGIN = 0xF1001;
        public final static int HANDLE_HTTP_LOGOUT = 0xF1002;
    }

    public interface HttpReqCallBack
    {
        void onReqSuccess(int Cmd, Object result);
    }

    public static String GetHttpCmd(int Cmd){
        String HttpCmd = "";
        switch(Cmd){
            case HANDLE_HTTP_LOGIN:
                HttpCmd = HttpDef.HTTP_CMD.Http_LoginIn;
                break;
            case HANDLE_HTTP_LOGOUT:
                HttpCmd = HttpDef.HTTP_CMD.Http_LoginOut;
                break;

            case HANDLE_HTTP_QUERY_RFID_FROM_SJBH:
            case HANDLE_HTTP_QUREY_GCID:
            case HANDLE_HTTP_QUERY_RFID:
            case HANDLE_HTTP_QUERY_SJBH:
            case HANDLE_HTTP_QUERY_IMAGE:
                HttpCmd = HttpDef.HTTP_CMD.Http_Query;
                break;
            case HANDLE_HTTP_INSERT_RFID:
            case HANDLE_HTTP_INSERT_SJBH:
            case HANDLE_HTTP_INSERT_IMAGE:
                HttpCmd = HttpDef.HTTP_CMD.Http_Insert;
                break;

            case HANDLE_HTTP_UPDATE_SJBH:
            case HANDLE_HTTP_UPDATE_RFID:
            case HANDLE_HTTP_UPDATE_IMAGE:
                HttpCmd = HTTP_CMD.Http_Update;
                break;

            case HANDLE_HTTP_DELETE_SJBH:
            case HANDLE_HTTP_DELETE_RFID:
            case HANDLE_HTTP_DELETE_IMAGE:
                HttpCmd = HTTP_CMD.Http_Delete;
                break;
        }

        return HttpCmd;
    }

    public static int GetRequestType(int Cmd){
        int HttpCmd = HttpUtil.TYPE_GET;
        switch(Cmd){
            case HANDLE_HTTP_LOGIN:
            case HANDLE_HTTP_LOGOUT:
            case HANDLE_HTTP_QUREY_GCID:
            case HANDLE_HTTP_QUERY_RFID:
            case HANDLE_HTTP_QUERY_SJBH:
            case HANDLE_HTTP_QUERY_IMAGE:
            case HANDLE_HTTP_QUERY_RFID_FROM_SJBH:
                HttpCmd = HttpUtil.TYPE_GET;
                break;

            case HANDLE_HTTP_INSERT_RFID:
            case HANDLE_HTTP_INSERT_SJBH:
            case HANDLE_HTTP_INSERT_IMAGE:
                HttpCmd = HttpUtil.TYPE_POST_JSON;
                break;

            case HANDLE_HTTP_UPDATE_SJBH:
            case HANDLE_HTTP_UPDATE_RFID:
            case HANDLE_HTTP_UPDATE_IMAGE:
                HttpCmd = HttpUtil.TYPE_POST_JSON;
                break;

            case HANDLE_HTTP_DELETE_SJBH:
            case HANDLE_HTTP_DELETE_RFID:
            case HANDLE_HTTP_DELETE_IMAGE:
                HttpCmd = HttpUtil.TYPE_POST_JSON;
                break;
        }

        return HttpCmd;
    }
}
