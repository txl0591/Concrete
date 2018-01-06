package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class SJBHInfoList implements Serializable {

    public boolean result;
    public String error;
    public int echo_code;
    public int index;
    public ArrayList<SJBHInfo> items;

    public SJBHInfoList(boolean result, String error, int echo_code, int index,ArrayList<SJBHInfo> items){
        this.result = result;
        this.error = error;
        this.echo_code = echo_code;
        this.index = index;
        this.items = items;
    }

    public void PrintSJBHInfoList(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"****************************************");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"Index ["+this.index+"]");
        for(int i = 0; i < items.size(); i++){
            SJBHInfo mSJBHInfo = items.get(i);
            mSJBHInfo.PrintSJBHInfo();
        }
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"****************************************");
    }
}
