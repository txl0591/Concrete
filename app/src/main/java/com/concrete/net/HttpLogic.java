package com.concrete.net;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.concrete.common.Common;
import com.concrete.common.nlog;
import com.concrete.type.ChipInfo;
import com.concrete.type.ChipInfoList;
import com.concrete.type.ChipInfoOper;
import com.concrete.type.ImageInfo;
import com.concrete.type.ImageInfoList;
import com.concrete.type.ImageInfoOper;
import com.concrete.type.JsonEcho;
import com.concrete.type.ProjectInfoList;
import com.concrete.type.SJBHInfo;
import com.concrete.type.SJBHInfoList;
import com.concrete.type.SJBHInfoOper;
import com.concrete.type.UserEcho;
import com.concrete.type.UserType;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static com.concrete.net.HttpDef.*;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;
import static com.concrete.net.HttpEcho.*;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class HttpLogic {

    private Context mContext = null;

    public HttpLogic(Context context){
        mContext = context;
    }

    public void Close(){
    }

    private void SendHttpBroadcast(int cmd,int echo,Bundle mBundle)
    {
        if(mContext != null){
            Intent intent = new Intent(HTTP_DISTRIBUTE);
            intent.putExtra(INTENT_HANDLE_HTTP_CMD,cmd);
            intent.putExtra(INTENT_HANDLE_HTTP_ECHO,echo);
            if(null != mBundle){
                intent.putExtra(INTENT_HANDLE_HTTP_PARAM,mBundle);
            }
            mContext.sendBroadcast(intent);
        }
    }

    class HttpThread extends Thread{
        private HttpReqCallBack mReqCallBack ;
        private String mActionUrl;
        private int mRequestType;
        private int mEncrypt;
        private HashMap<String, String> mParamsMap;
        private String mJson;
        private Context nContext;
        private int mCmd;

        public HttpThread(int Cmd, int encrypt, HashMap<String, String> paramsMap, String Json, HttpReqCallBack callBack){
            mReqCallBack = callBack;
            mActionUrl = BASE_URL+GetHttpCmd(Cmd);
            mRequestType = GetRequestType(Cmd);
            mEncrypt = encrypt;
            mParamsMap = paramsMap;
            mJson = Json;
            mCmd = Cmd;
            nContext = mContext;
        }

        @Override
        public void run() {
            super.run();
            HttpUtil mHttpManager = HttpUtil.getInstance(nContext);
            mHttpManager.postrequest(mActionUrl,mRequestType,mEncrypt,mParamsMap,mJson,new ReqCallBack(){

                @Override
                public void onReqSuccess(Object result) {
                    if(mReqCallBack != null){
                        nlog.Info("result ==========["+result+"]");
                        mReqCallBack.onReqSuccess(mCmd,result);
                    }
                }

                @Override
                public void onReqFailed(String errorMsg) {
                    SendHttpBroadcast(mCmd, HttpEcho.ERROR_COMMON,null);
                }
            });
        }
    }

    public void QueryRFID(String Number){
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUERY_RFID),Number);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);
        new HttpThread(HANDLE_HTTP_QUERY_RFID,0,null, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    ChipInfoList mChipInfoList = gson.fromJson(String.valueOf(result), ChipInfoList.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("HANDLE_HTTP_QUERY_RFID", mChipInfoList);
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code,mBundle);

                }else{
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code,null);
                }
            }
        }).start();
    }

    public void QuerySJBH(String SJBH){
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUERY_SJBH),SJBH);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);
        new HttpThread(HANDLE_HTTP_QUERY_SJBH,0,null, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    SJBHInfoList mSJBHInfoList = gson.fromJson(String.valueOf(result), SJBHInfoList.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("HANDLE_HTTP_QUERY_SJBH", mSJBHInfoList);
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code,mBundle);
                }else{
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code, null);
                }
            }
        }).start();
    }

    public  void QueryImage(String uuid){
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUERY_IMAGE),uuid);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);
        new HttpThread(HANDLE_HTTP_QUERY_IMAGE,0,null, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                if(mJsonEcho.result){
                    ImageInfoList mImageInfoList = gson.fromJson(String.valueOf(result), ImageInfoList.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("HANDLE_HTTP_QUERY_IMAGE", mImageInfoList);
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code,mBundle);
                }else{
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code, null);
                }
            }
        }).start();
    }

    public  void QueryGGInfo(String Username){
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUREY_GCID),Username);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);

        new HttpThread(HANDLE_HTTP_QUREY_GCID,0,null, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);

                if(mJsonEcho.result){
                    mJsonEcho.PrintJsonEcho();
                    ProjectInfoList mProjectInfoList = gson.fromJson(String.valueOf(result), ProjectInfoList.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("HANDLE_HTTP_QUREY_GCID", mProjectInfoList);
                    nlog.Info("mProjectInfoList ============= ["+mProjectInfoList.items.size()+"]HANDLE_HTTP_QUREY_GCID ["+Cmd+"]");
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code,mBundle);
                }else{
                    SendHttpBroadcast(Cmd,mJsonEcho.echo_code, null);
                }
            }
        }).start();
    }

    public void LogOper(int Cmd, HashMap<String, String> Param){
        new HttpThread(Cmd,1,Param, null, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                if(Cmd == HANDLE_HTTP_LOGIN){
                    Gson gson = new Gson();
                    UserEcho mUserEcho = gson.fromJson(String.valueOf(result), UserEcho.class);
                    if(mUserEcho.result){
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("HANDLE_HTTP_LOGIN", mUserEcho.items);
                        SendHttpBroadcast(Cmd,mUserEcho.echo_code,mBundle);
                    }else{
                        SendHttpBroadcast(Cmd,mUserEcho.echo_code, null);
                    }
                }else{
                    SendHttpBroadcast(Cmd,SUCCESS, null);
                }

            }
        }).start();
    }

    public void OperRFID(int Cmd, ArrayList<ChipInfo> item, HashMap<String, String> Param){
        ChipInfoOper mChipInfoOper = new ChipInfoOper(Common.toHexString(Cmd),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mChipInfoOper);
        nlog.Info("Json ["+Json.toString()+"]");
        new HttpThread(Cmd,1,Param, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                SendHttpBroadcast(Cmd,mJsonEcho.echo_code, null);
            }
        }).start();
    }

    public void OperSJBHInfo(int Cmd, ArrayList<SJBHInfo> item, HashMap<String, String> Param){
        SJBHInfoOper mSJBHInfoOper = new SJBHInfoOper(Common.toHexString(Cmd),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mSJBHInfoOper);
        new HttpThread(Cmd,1,Param, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                SendHttpBroadcast(Cmd,mJsonEcho.echo_code, null);
            }
        }).start();
    }

    public void OperImageInfo(int Cmd, ArrayList<ImageInfo> item, HashMap<String, String> Param) {
        ImageInfoOper mImageInfoOper = new ImageInfoOper(Common.toHexString(Cmd),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mImageInfoOper);
        new HttpThread(Cmd,1,Param, Json, new HttpReqCallBack() {
            @Override
            public void onReqSuccess(int Cmd, Object result) {
                Gson gson = new Gson();
                JsonEcho mJsonEcho = gson.fromJson(String.valueOf(result), JsonEcho.class);
                mJsonEcho.PrintJsonEcho();
                SendHttpBroadcast(Cmd,mJsonEcho.echo_code, null);
            }
        }).start();
    }

    /**************************************************
     * 阻塞查询接口
     * @param Number
     * @return
     */
    public ChipInfo QueryRFIDBlock(String Number){
        ChipInfo mChipInfo = null;
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUERY_RFID),Number);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);
        String mActionUrl = BASE_URL+GetHttpCmd(HANDLE_HTTP_QUERY_RFID);
        int mRequestType = GetRequestType(HANDLE_HTTP_QUERY_RFID);
        String result = HttpUtil.getInstance(mContext).SendRequest(mActionUrl, mRequestType, 0, null, Json);
        if(result != null){
            JsonEcho mJsonEcho = gson.fromJson(result, JsonEcho.class);
            mJsonEcho.PrintJsonEcho();
            if(mJsonEcho.result){
                ChipInfoList mChipInfoList = gson.fromJson(result, ChipInfoList.class);
                mChipInfo = mChipInfoList.items.get(0);
            }
        }
        return mChipInfo;
    }

    public ArrayList<ChipInfo> QueryRFIDListlock(ArrayList<String> RFID){
        ArrayList<ChipInfo> mChipInfo = new ArrayList<ChipInfo>();

        for(int i = 0; i < RFID.size(); i++){
            ChipInfo chipInfo = QueryRFIDBlock(RFID.get(i));
            if(null != chipInfo){
                mChipInfo.add(mChipInfo.size(),chipInfo);
            }
        }

        return mChipInfo;
    }

    public ArrayList<ChipInfo> QueryRFIDFromSJBHlock(String SJBH){
        ArrayList<ChipInfo> mChipInfo = new ArrayList<ChipInfo>();
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUERY_RFID_FROM_SJBH),SJBH);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);
        String mActionUrl = BASE_URL+GetHttpCmd(HANDLE_HTTP_QUERY_RFID_FROM_SJBH);
        int mRequestType = GetRequestType(HANDLE_HTTP_QUERY_RFID_FROM_SJBH);

        String result = HttpUtil.getInstance(mContext).SendRequest(mActionUrl, mRequestType, 0, null, Json);
        if(result != null){
            JsonEcho mJsonEcho = gson.fromJson(result, JsonEcho.class);
            mJsonEcho.PrintJsonEcho();
            if(mJsonEcho.result){
                ChipInfoList mChipInfoList = gson.fromJson(result, ChipInfoList.class);
                for(int i = 0; i < mChipInfoList.items.size(); i++){
                    mChipInfo.add(i,mChipInfoList.items.get(i));
                }
            }
        }

        return mChipInfo;
    }

    public boolean OperRFIDBlock(int Cmd, ArrayList<ChipInfo> item, HashMap<String, String> Param){
        boolean ret = false;
        ChipInfoOper mChipInfoOper = new ChipInfoOper(Common.toHexString(Cmd),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mChipInfoOper);
        String mActionUrl = BASE_URL+GetHttpCmd(Cmd);
        int mRequestType = GetRequestType(Cmd);

        nlog.Info("Json==============["+Json+"]");

        String result = HttpUtil.getInstance(mContext).SendRequest(mActionUrl, mRequestType, 1, Param, Json);

        nlog.Info("OperRFIDBlock result =============["+result+"]");

        if(result != null){
            JsonEcho mJsonEcho = gson.fromJson(result, JsonEcho.class);
            mJsonEcho.PrintJsonEcho();
            ret = mJsonEcho.result;
        }

        return ret;
    }

    public SJBHInfo QuerySJBHBlock(String SJBH){
        SJBHInfo mSJBHInfo = null;
        HttpOper mHttpOper = new HttpOper(Common.toHexString(HANDLE_HTTP_QUERY_SJBH),SJBH);
        Gson gson = new Gson();
        String Json =gson.toJson(mHttpOper);

        String mActionUrl = BASE_URL+GetHttpCmd(HANDLE_HTTP_QUERY_SJBH);
        int mRequestType = GetRequestType(HANDLE_HTTP_QUERY_SJBH);
        String result = HttpUtil.getInstance(mContext).SendRequest(mActionUrl, mRequestType, 0, null, Json);
        if(result != null){
            JsonEcho mJsonEcho = gson.fromJson(result, JsonEcho.class);
            mJsonEcho.PrintJsonEcho();
            if(mJsonEcho.result){
                SJBHInfoOper mSJBHInfoOper = gson.fromJson(result, SJBHInfoOper.class);
                mSJBHInfo = mSJBHInfoOper.items.get(0);
            }
        }

        return mSJBHInfo;
    }

    public boolean OperSJBHInfolock(int Cmd, ArrayList<SJBHInfo> item, HashMap<String, String> Param){
        boolean ret = false;
        SJBHInfoOper mSJBHInfoOper = new SJBHInfoOper(Common.toHexString(Cmd),item);
        Gson gson = new Gson();
        String Json =gson.toJson(mSJBHInfoOper);
        nlog.Info("OperSJBHInfolock=========["+Json+"]");
        String mActionUrl = BASE_URL+GetHttpCmd(Cmd);
        int mRequestType = GetRequestType(Cmd);
        String result = HttpUtil.getInstance(mContext).SendRequest(mActionUrl, mRequestType, 1, Param, Json);
        if(result != null){
            JsonEcho mJsonEcho = gson.fromJson(result, JsonEcho.class);
            mJsonEcho.PrintJsonEcho();
            ret = mJsonEcho.result;
        }

        return ret;
    }

}
