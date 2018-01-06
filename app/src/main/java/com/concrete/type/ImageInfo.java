package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.Serializable;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class ImageInfo implements Serializable {
    public String cIMG_UUID;
    public String SKs_UUid;
    public String cfilehouzui;
    public String imgFile;
    public String TBL_STATE;
    
    public ImageInfo(String cIMG_UUID,String SKs_UUid,String cfilehouzui,String imgFile,String TBL_STATE){
        this.cIMG_UUID = cIMG_UUID;
        this.SKs_UUid = SKs_UUid;
        this.cfilehouzui = cfilehouzui;
        this.imgFile = imgFile;
        this.TBL_STATE = TBL_STATE;
    }

    public void PrintImageInfo(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"cIMG_UUID   ["+this.cIMG_UUID+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"SKs_UUid    ["+this.SKs_UUid+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"cfilehouzui ["+this.cfilehouzui+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"imgFile     ["+this.imgFile+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_STATE   ["+this.TBL_STATE+"]");
    }
}
