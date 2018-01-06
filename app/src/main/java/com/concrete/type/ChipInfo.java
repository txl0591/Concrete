package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.Serializable;

/**
 * Created by Tangxl on 2017/11/23.
 */

public class ChipInfo implements Serializable {
    public String SKs_UUid;
    public String TBL_SJBH;
    public String RFID;
    public int SerialNo;
    public String TBL_SYJG;
    public String TBL_SYRQ;
    public String TBL_LDR;
    public String TBL_LRSJ;
    public String TBL_JCRY;
    public String TBL_JCRQ;

    public ChipInfo(String SKs_UUid, String TBL_SJBH, String RFID, int SerialNo,
                    String TBL_SYJG,
                    String TBL_SYRQ,
                    String TBL_LDR,
                    String TBL_LRSJ,
                    String TBL_JCRY,
                    String TBL_JCRQ){
        this.SKs_UUid  = SKs_UUid;
        this.TBL_SJBH  = TBL_SJBH;
        this.RFID  = RFID;
        this.SerialNo = SerialNo;
        this.TBL_SYJG = TBL_SYJG;
        this.TBL_SYRQ = TBL_SYRQ;
        this.TBL_LDR = TBL_LDR;
        this.TBL_LRSJ = TBL_LRSJ;
        this.TBL_JCRY = TBL_JCRY;
        this.TBL_JCRQ = TBL_JCRQ;
    }

    public void PrintChipInfo(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"SKs_UUid ["+this.SKs_UUid+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SJBH ["+this.TBL_SJBH+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"RFID     ["+this.RFID+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"SerialNo ["+this.SerialNo+"]");

        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SYJG ["+this.TBL_SYJG+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SYRQ ["+this.TBL_SYRQ+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_LDR ["+this.TBL_LDR+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_LRSJ ["+this.TBL_LRSJ+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_JCRY ["+this.TBL_JCRY+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_JCRQ ["+this.TBL_JCRQ+"]");
    }
}
