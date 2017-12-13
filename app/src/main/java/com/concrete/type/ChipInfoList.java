package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tangxl on 2017/11/23.
 */

public class ChipInfoList implements Serializable {

    public final static String TABLE = "Y_tempSKs";

    public boolean result;
    public String error;
    public int echo_code;
    public int index;
    public ArrayList<ChipInfo> items;

    public ChipInfoList(boolean result, String error, int echo_code, int index,ArrayList<ChipInfo> items){
        this.result = result;
        this.error = error;
        this.echo_code = echo_code;
        this.index = index;
        this.items = items;
    }

    public void PrintChipInfoList(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"****************************************");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"Index ["+this.index+"]");
        for(int i = 0; i < items.size(); i++){
            ChipInfo mChipInfo = items.get(i);
            mChipInfo.PrintChipInfo();
        }
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"****************************************");
    }
}
