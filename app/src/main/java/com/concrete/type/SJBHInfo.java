package com.concrete.type;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.Serializable;

/**
 * Created by Tangxl on 2017/11/24.
 */

public class SJBHInfo implements Serializable {
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
    public String TBL_SGDW;
    public String TBL_WTDW;
    public String TBL_JZDW;
    public String TBL_JZR;
    public String TBL_JZBH;
    public String TBL_GCID;
    public String TBL_GCDM;
    public String TBL_YPBH;
    public String TBL_JCJG;
    public float TBL_JCresult;
    public float TBL_JCbfb;
    public byte TBL_STATE;
    public String TBL_SYRQ;
    public String TBL_SYJG;

    public SJBHInfo(String TBL_SJBH,
            String TBL_YPLX,
            String TBL_GCMC,
            String TBL_GJBW,
            String TBL_QDDJ,
            String TBL_YHFS,
            String TBL_ZZRQ,
            String TBL_PHBBH,
            String TBL_SCLSH,
            String TBL_BZDW,
            String TBL_SGDW,
            String TBL_WTDW,
            String TBL_JZDW,
            String TBL_JZR,
            String TBL_JZBH,
            String TBL_GCID,
            String TBL_GCDM,
            String TBL_YPBH,
            String TBL_JCJG,
            float TBL_JCresult,
            float TBL_JCbfb,
            byte TBL_STATE,
            String TBL_SYJG,
            String TBL_SYRQ){
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
        this.TBL_SGDW=TBL_SGDW;
        this.TBL_WTDW=TBL_WTDW;
        this.TBL_JZDW=TBL_JZDW;
        this.TBL_JZR=TBL_JZR;
        this.TBL_JZBH=TBL_JZBH;
        this.TBL_GCID=TBL_GCID;
        this.TBL_GCDM=TBL_GCDM;
        this.TBL_YPBH=TBL_YPBH;
        this.TBL_JCJG=TBL_JCJG;
        this.TBL_JCresult=TBL_JCresult;
        this.TBL_JCbfb=TBL_JCbfb;
        this.TBL_STATE=TBL_STATE;
        this.TBL_SYRQ = TBL_SYRQ;
        this.TBL_SYJG = TBL_SYJG;
    }

    public void PrintSJBHInfo(){
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
