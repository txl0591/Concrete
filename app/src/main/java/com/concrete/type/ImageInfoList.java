package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class ImageInfoList implements Serializable {

    public final static String TABLE = "SK_image";

    public boolean result;
    public String error;
    public int echo_code;
    public int index;
    public ArrayList<ImageInfo> items;

    public ImageInfoList(boolean result,String error,int echo_code, int index, ArrayList<ImageInfo> items){
        this.result = result;
        this.error = error;
        this.echo_code = echo_code;
        this.index = index;
        this.items = items;
    }

    public void PrintImageInfoList(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"****************************************");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"Index ["+this.index+"]");
        for(int i = 0; i < items.size(); i++){
            ImageInfo mImageInfo = items.get(i);
            mImageInfo.PrintImageInfo();
        }
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"****************************************");
    }
}
