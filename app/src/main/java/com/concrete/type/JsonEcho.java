package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

/**
 * Created by Tangxl on 2017/11/23.
 */

public class JsonEcho {

    public boolean result;
    public String error;
    public int echo_code;
    public int index;

    public JsonEcho(boolean result, String error, int echo_code, int index){
        this.result = result;
        this.error = error;
        this.echo_code = echo_code;
        this.index = index;
    }

    public void PrintJsonEcho(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"result ["+result+"] error ["+error+"]");
    }

}
