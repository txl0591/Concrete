package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

/**
 * Created by Tangxl on 2017/11/23.
 */

public class ChipInfo {
    public String SKs_UUid;
    public String TBL_SJBH;
    public String RFID;
    public int SerialNo;

    public ChipInfo(String SKs_UUid, String TBL_SJBH, String RFID, int SerialNo){
        this.SKs_UUid  = SKs_UUid;
        this.TBL_SJBH  = TBL_SJBH;
        this.RFID  = RFID;
        this.SerialNo  = SerialNo;
    }

    public void PrintChipInfo(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"SKs_UUid ["+this.SKs_UUid+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SJBH ["+this.TBL_SJBH+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"RFID     ["+this.RFID+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"SerialNo ["+this.SerialNo+"]");
    }
}
