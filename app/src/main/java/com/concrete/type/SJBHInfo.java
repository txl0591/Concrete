package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class SJBHInfo {
    public String TBL_UUID;
    public String TBL_SJBH;
    public String TBL_YPLX;
    public String TBL_GCMC;
    public String TBL_GJBW;
    public String TBL_QDDJ;
    public String TBL_YHFS;
    public String TBL_ZZRQ;
    public String TBL_PHBBH;
    public String TBL_SCLSH;
    public String TBL_BZDW;
    public String TBL_WTDW;
    public String TBL_SGDW;
    public String TBL_JZDW;
    public String TBL_JZR;
    public String TBL_JZBH;

    public SJBHInfo(String TBL_UUID,
            String TBL_SJBH,
            String TBL_YPLX,
            String TBL_GCMC,
            String TBL_GJBW,
            String TBL_QDDJ,
            String TBL_YHFS,
            String TBL_ZZRQ,
            String TBL_PHBBH,
            String TBL_SCLSH,
            String TBL_BZDW,
            String TBL_WTDW,
            String TBL_SGDW,
            String TBL_JZDW,
            String TBL_JZR,
            String TBL_JZBH){
        this.TBL_UUID=TBL_UUID;
        this.TBL_SJBH=TBL_SJBH;
        this.TBL_YPLX=TBL_YPLX;
        this.TBL_GCMC=TBL_GCMC;
        this.TBL_GJBW=TBL_GJBW;
        this.TBL_QDDJ=TBL_QDDJ;
        this.TBL_YHFS=TBL_YHFS;
        this.TBL_ZZRQ=TBL_ZZRQ;
        this.TBL_PHBBH=TBL_PHBBH;
        this.TBL_SCLSH=TBL_SCLSH;
        this.TBL_BZDW=TBL_BZDW;
        this.TBL_WTDW=TBL_WTDW;
        this.TBL_SGDW=TBL_SGDW;
        this.TBL_JZDW=TBL_JZDW;
        this.TBL_JZR=TBL_JZR;
        this.TBL_JZBH=TBL_JZBH;
    }

    public void PrintSJBHInfo(){
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_UUID ["+this.TBL_UUID+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SJBH ["+this.TBL_SJBH+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_YPLX ["+this.TBL_YPLX+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_GCMC ["+this.TBL_GCMC+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_GJBW ["+this.TBL_GJBW+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_QDDJ ["+this.TBL_QDDJ+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_YHFS ["+this.TBL_YHFS+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_ZZRQ ["+this.TBL_ZZRQ+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_PHBBH ["+this.TBL_PHBBH+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SCLSH ["+this.TBL_SCLSH+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_BZDW ["+this.TBL_BZDW+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_WTDW ["+this.TBL_WTDW+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_SGDW ["+this.TBL_SGDW+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_JZDW ["+this.TBL_JZDW+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_JZR ["+this.TBL_JZR+"]");
        nlog.IfInfo(IntentDef.LOG_LEVEL.LOG_HIGH,"TBL_JZBH ["+this.TBL_JZBH+"]");
    }
}
