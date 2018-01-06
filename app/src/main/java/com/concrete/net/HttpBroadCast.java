package com.concrete.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.concrete.common.Common;
import com.concrete.common.nlog;

import static com.concrete.net.HttpDef.*;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.HANDLE_HTTP_QUREY_GCID;

/**
 * Created by Tangxl on 2017/12/22.
 */

public class HttpBroadCast {

    public Context mContext = null;
    public Handler mhandler = null;
    public NetBroadCast mNetBroadCast = null;

    public HttpBroadCast(Context context,Handler handler){
        mContext = context;
        mhandler = handler;
    }

    public void Start(){
        mNetBroadCast = new NetBroadCast();
        IntentFilter intentFilter = new IntentFilter(HTTP_DISTRIBUTE);
        mContext.registerReceiver(mNetBroadCast,intentFilter);
    }

    public void Close(){
        if(null != mNetBroadCast){
            mContext.unregisterReceiver(mNetBroadCast);
            mNetBroadCast = null;
        }
    }


    class NetBroadCast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mhandler != null){
                Message msg = new Message();
                msg.what = intent.getIntExtra(INTENT_HANDLE_HTTP_CMD,INTENT_HANDLE_HTTP_DEFAULT);
                msg.arg1 = intent.getIntExtra(INTENT_HANDLE_HTTP_ECHO,INTENT_HANDLE_HTTP_DEFAULT);
                Bundle mBundle = intent.getBundleExtra(INTENT_HANDLE_HTTP_PARAM);
                if(null != mBundle){
                    msg.setData(mBundle);
                }
                mhandler.sendMessage(msg);
            }
        }
    }


}
