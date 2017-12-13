package com.concrete.logic;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.concrete.common.HttpEcho;
import com.concrete.common.HttpManager;
import com.concrete.common.IntentDef;
import com.concrete.common.ReqCallBack;
import com.concrete.common.nlog;
import com.concrete.type.ChipInfo;
import com.concrete.type.ChipInfoList;
import com.concrete.type.ChipInfoOper;
import com.concrete.type.ImageInfoList;
import com.concrete.type.JsonEcho;
import com.concrete.type.SJBHInfo;
import com.concrete.type.SJBHInfoList;
import com.concrete.type.SJBHInfoOper;
import com.concrete.type.UserInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class Logic {

    private Context nContext = null;
    public final static int HANDLE_HTTP_QUERY_RFID = 0xA1A1;
    public final static int HANDLE_HTTP_QUERY_SJBH = 0xA1A2;
    public final static int HANDLE_HTTP_QUERY_IMAGE = 0xA1A3;
    public final static int HANDLE_HTTP_LOGIN = 0xA1A4;
    public final static int HANDLE_HTTP_LOGOUT = 0xA1A5;
    public final static int HANDLE_HTTP_OPER_RFID = 0xA1A6;
    public final static int HANDLE_HTTP_INSERT_RFID = 0xA1A7;
    public final static int HANDLE_HTTP_INSERT_SJBH = 0xA1A8;

    public Logic(Context context){
        nContext = context;
    }

    private void SendHandlerMsg(Handler handler,int what,int arg)
    {
        if(handler != null){
            Message msg  = new Message();
            msg.what = what;
            msg.arg1 = arg;
            handler.sendMessage(msg);
        }
    }

    private String GetHttpCmd(int Cmd){
        String HttpCmd = "";
        switch(Cmd){
            case HANDLE_HTTP_LOGIN:
                HttpCmd = IntentDef.HTTP_CMD.Http_LoginIn;
                break;
            case HANDLE_HTTP_LOGOUT:
                HttpCmd = IntentDef.HTTP_CMD.Http_LoginOut;
                break;
            case HANDLE_HTTP_QUERY_RFID:
            case HANDLE_HTTP_QUERY_SJBH:
            case HANDLE_HTTP_QUERY_IMAGE:
                HttpCmd = IntentDef.HTTP_CMD.Http_Query;
                break;
            case HANDLE_HTTP_INSERT_RFID:
            case HANDLE_HTTP_INSERT_SJBH:
                HttpCmd = IntentDef.HTTP_CMD.Http_Insert;
                break;



        }

        return HttpCmd;
    }

    private int GetRequestType(int Cmd){
        int HttpCmd = HttpManager.TYPE_GET;
        switch(Cmd){
            case HANDLE_HTTP_LOGIN:
            case HANDLE_HTTP_LOGOUT:
            case HANDLE_HTTP_QUERY_RFID:
            case HANDLE_HTTP_QUERY_SJBH:
            case HANDLE_HTTP_QUERY_IMAGE:
                HttpCmd = HttpManager.TYPE_GET;
                break;

            case HANDLE_HTTP_OPER_RFID:
            case HANDLE_HTTP_INSERT_RFID:
            case HANDLE_HTTP_INSERT_SJBH:
                HttpCmd = HttpManager.TYPE_POST_JSON;
                break;
        }

        return HttpCmd;
    }

    class HttpThread extends Thread{
        private Handler mHandler;
        private IntentDef.HttpReqCallBack mReqCallBack ;
        private String mActionUrl;
        private int mRequestType;
        private int mEncrypt;
        private HashMap<String, String> mParamsMap;
        private String mJson;
        private Context mContext;
        private int mCmd;

        public HttpThread(Context context, Handler handler,int Cmd, int encrypt, HashMap<String, String> paramsMap, String Json, IntentDef.HttpReqCallBack callBack){
            mHandler = handler;
            mReqCallBack = callBack;
            mActionUrl = IntentDef.HTTP_SERVICE+GetHttpCmd(Cmd);
            mRequestType = GetRequestType(Cmd);
            mEncrypt = encrypt;
            mParamsMap = paramsMap;
            mJson = Json;
            mContext = context;
            mCmd = Cmd;
        }

        @Override
        public void run() {
            super.run();
            HttpManager mHttpManager = HttpManager.getInstance(mContext);
            mHttpManager.postrequest(mActionUrl,mRequestType,mEncrypt,mParamsMap,mJson,new ReqCallBack(){

                @Override
                public void onReqSuccess(Object result) {
                    if(mReqCallBack != null){
                        nlog.Info("result ==========["+result+"]");
                        mReqCallBack.onReqSuccess(mCmd,mHandler,result);
                    }
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    SendHandlerMsg(mHandler, mCmd, HttpEcho.ERROR_COMMON);
                }
            });
        }
    }

    public void QueryRFID(String Number, Handler handler){
        HashMap<String, String> Param = new HashMap<String, String>();
        Param.put("RFID", Number);
        new HttpThread(nContext,handler,HANDLE_HTTP_QUERY_RFID,0,Param, null, new IntentDef.HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Handler handler, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    ChipInfoList mChipInfoList = gson.fromJson(String.valueOf(result), ChipInfoList.class);
                    if(handler != null){
                        mChipInfoList.PrintChipInfoList();
                        Message msg  = new Message();
                        msg.what = Cmd;
                        msg.arg1 = mJsonEcho.echo_code;
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("HANDLE_HTTP_QUERY_RFID", mChipInfoList);
                        msg.setData(mBundle);
                        handler.sendMessage(msg);
                    }
                }else{
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }
            }
        }).start();
    }

    public void QuerySJBH(String SJBH, Handler handler){
        HashMap<String, String> Param = new HashMap<String, String>();
        Param.put("TBL_SJBH", SJBH);
        new HttpThread(nContext,handler,HANDLE_HTTP_QUERY_SJBH,0,Param, null, new IntentDef.HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Handler handler, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    SJBHInfoList mSJBHInfoList = gson.fromJson(String.valueOf(result), SJBHInfoList.class);
                    if(handler != null){
                        Message msg  = new Message();
                        msg.what = Cmd;
                        msg.arg1 = mJsonEcho.echo_code;
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("HANDLE_HTTP_QUERY_SJBH", mSJBHInfoList);
                        msg.setData(mBundle);
                        handler.sendMessage(msg);
                    }
                }else{
                    SendHandlerMsg(handler,HANDLE_HTTP_QUERY_SJBH,mJsonEcho.echo_code);
                }
            }
        }).start();
    }

    public  void QueryImage(String uuid, Handler handler){
        HashMap<String, String> Param = new HashMap<String, String>();
        Param.put("SKs_UUid", uuid);
        new HttpThread(nContext,handler,HANDLE_HTTP_QUERY_IMAGE,0,Param, null, new IntentDef.HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Handler handler, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    ImageInfoList mImageInfoList = gson.fromJson(String.valueOf(result), ImageInfoList.class);
                    if(handler != null){
                        Message msg  = new Message();
                        msg.what = Cmd;
                        msg.arg1 = mJsonEcho.echo_code;
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("HANDLE_HTTP_QUERY_IMAGE", mImageInfoList);
                        msg.setData(mBundle);
                        handler.sendMessage(msg);
                    }
                }else{
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }
            }
        }).start();
    }

    public void LogOper(int Cmd, HashMap<String, String> Param, Handler handler){
        new HttpThread(nContext,handler,Cmd,1,Param, null, new IntentDef.HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Handler handler, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }else{
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }
            }
        }).start();
    }

    public void OperRFID(int Cmd, ArrayList<ChipInfo> item, HashMap<String, String> Param, Handler handler){
        ChipInfoOper mChipInfoOper = new ChipInfoOper(GetHttpCmd(Cmd),item.size(),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mChipInfoOper);
        new HttpThread(nContext,handler,Cmd,1,Param, Json, new IntentDef.HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Handler handler, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }else{
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }
            }
        }).start();
    }

    public void OperGongChenInfo(int Cmd, ArrayList<SJBHInfo> item, HashMap<String, String> Param, Handler handler){
        SJBHInfoOper mSJBHInfoOper = new SJBHInfoOper(GetHttpCmd(Cmd),item.size(),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mSJBHInfoOper);
        nlog.Info("Json ["+Json+"]");
        new HttpThread(nContext,handler,Cmd,1,Param, Json, new IntentDef.HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Handler handler, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }else{
                    SendHandlerMsg(handler,Cmd,mJsonEcho.echo_code);
                }
            }
        }).start();
    }
}
