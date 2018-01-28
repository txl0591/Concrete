package com.concrete.net;

import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;

/**
 * Created by Tangxl on 2017/12/21.
 */

public class HttpDef {
    public  final static String BASE_URL = "http://47.100.126.101:81/?";
    public static final String INTENT_DOWNLOAD_ADDR 			= "http://47.100.126.101:80/download.ashx?filename=";
    public static final String INTENT_UPLOAD_ADDR 			= "http://47.100.126.101:80/Upload.ashx";

    public static final String HTTP_DISTRIBUTE			="http.intent.action.DISTRIBUTE";
    public static final String INTENT_HANDLE_HTTP_CMD				="http.intent.CMD";
    public static final String INTENT_HANDLE_HTTP_ECHO				="http.intent.ECHO";
    public static final String INTENT_HANDLE_HTTP_PARAM			="http.intent.PARAM";
    public static final int INTENT_HANDLE_HTTP_DEFAULT = -1;

    public static final String UNKNOW = "Unknow";

    public class HTTP_IMAGE{
        public static final String HTTP_IMAGE_ADD = "A";
    }


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
        public static final int HANDLE_HTTP_INSERT_YFPROJECT = 0xF000F;
        public static final int HANDLE_HTTP_UPDATE_YFPROJECT = 0xF0010;
        public static final int HANDLE_HTTP_DELETE_YFPROJECT = 0xF0011;
        public static final int HANDLE_HTTP_QUREY_USERPRJ = 0xF0012;
        public static final int HANDLE_HTTP_INSERT_USERPRJ= 0xF0013;
        public static final int HANDLE_HTTP_UPDATE_USERPRJ = 0xF0014;
        public static final int HANDLE_HTTP_DELETE_USERPRJ = 0xF0015;
        public static final int HANDLE_HTTP_QUREY_JZRPRJ = 0xF0016;
        public static final int HANDLE_HTTP_INSERT_JZRPRJ= 0xF0017;
        public static final int HANDLE_HTTP_UPDATE_JZRPRJ = 0xF0018;
        public static final int HANDLE_HTTP_DELETE_JZRPRJ = 0xF0019;
        public static final int HANDLE_HTTP_QUREY_GCIDMH = 0xF001A;
        public static final int HANDLE_HTTP_INSERT_USER = 0xF001B;


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
            case HANDLE_HTTP_QUREY_USERPRJ:
            case HANDLE_HTTP_QUREY_JZRPRJ:
            case HANDLE_HTTP_QUREY_GCIDMH:
                HttpCmd = HttpDef.HTTP_CMD.Http_Query;
                break;
            case HANDLE_HTTP_INSERT_RFID:
            case HANDLE_HTTP_INSERT_SJBH:
            case HANDLE_HTTP_INSERT_IMAGE:
            case HANDLE_HTTP_INSERT_YFPROJECT:
            case HANDLE_HTTP_INSERT_USERPRJ:
            case HANDLE_HTTP_INSERT_JZRPRJ:
            case HANDLE_HTTP_INSERT_USER:
                HttpCmd = HttpDef.HTTP_CMD.Http_Insert;
                break;

            case HANDLE_HTTP_UPDATE_SJBH:
            case HANDLE_HTTP_UPDATE_RFID:
            case HANDLE_HTTP_UPDATE_IMAGE:
            case HANDLE_HTTP_UPDATE_YFPROJECT:
            case HANDLE_HTTP_UPDATE_USERPRJ:
            case HANDLE_HTTP_UPDATE_JZRPRJ:
                HttpCmd = HTTP_CMD.Http_Update;
                break;

            case HANDLE_HTTP_DELETE_SJBH:
            case HANDLE_HTTP_DELETE_RFID:
            case HANDLE_HTTP_DELETE_IMAGE:
            case HANDLE_HTTP_DELETE_YFPROJECT:
            case HANDLE_HTTP_DELETE_USERPRJ:
            case HANDLE_HTTP_DELETE_JZRPRJ:
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
            case HANDLE_HTTP_QUREY_USERPRJ:
            case HANDLE_HTTP_QUREY_JZRPRJ:
            case HANDLE_HTTP_QUREY_GCIDMH:
                HttpCmd = HttpUtil.TYPE_GET;
                break;

            case HANDLE_HTTP_INSERT_RFID:
            case HANDLE_HTTP_INSERT_SJBH:
            case HANDLE_HTTP_INSERT_IMAGE:
            case HANDLE_HTTP_INSERT_YFPROJECT:
            case HANDLE_HTTP_INSERT_USERPRJ:
            case HANDLE_HTTP_INSERT_JZRPRJ:
            case HANDLE_HTTP_INSERT_USER:
                HttpCmd = HttpUtil.TYPE_POST_JSON;
                break;

            case HANDLE_HTTP_UPDATE_SJBH:
            case HANDLE_HTTP_UPDATE_RFID:
            case HANDLE_HTTP_UPDATE_IMAGE:
            case HANDLE_HTTP_UPDATE_YFPROJECT:
            case HANDLE_HTTP_UPDATE_USERPRJ:
            case HANDLE_HTTP_UPDATE_JZRPRJ:
                HttpCmd = HttpUtil.TYPE_POST_JSON;
                break;

            case HANDLE_HTTP_DELETE_SJBH:
            case HANDLE_HTTP_DELETE_RFID:
            case HANDLE_HTTP_DELETE_IMAGE:
            case HANDLE_HTTP_DELETE_YFPROJECT:
            case HANDLE_HTTP_DELETE_USERPRJ:
            case HANDLE_HTTP_DELETE_JZRPRJ:
                HttpCmd = HttpUtil.TYPE_POST_JSON;
                break;
        }

        return HttpCmd;
    }
}
