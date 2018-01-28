package com.concrete.type;

import java.util.ArrayList;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class JzrInfoList {
    public boolean result;
    public String error;
    public int echo_code;
    public int index;
    public ArrayList<JzrInfo> items;

    public JzrInfoList(boolean result,String error, int echo_code, ArrayList<JzrInfo> items){
        this.result = result;
        this.error = error;
        this.echo_code = echo_code;
        if(null == items){
            this.index = 0;
        }else {
            this.index = items.size();
        }
        this.items = items;
    }
}
