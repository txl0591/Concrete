package com.concrete.type;

import com.concrete.common.nlog;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class JzrInfo {
    public String JZDW_ID;
    public String JZDW;
    public String JZR;
    public String JZH;

    public JzrInfo(String JZDW_ID,String JZDW,String JZR,String JZH){
        this.JZDW_ID = JZDW_ID;
        this.JZDW = JZDW;
        this.JZR = JZR;
        this.JZH = JZH;
    }

    public void PrintJzrInfo(){
        nlog.Info("****************************************************");
        nlog.Info("JZDW_ID ["+this.JZDW_ID+"]");
        nlog.Info("JZDW    ["+this.JZDW+"]");
        nlog.Info("JZR     ["+this.JZR+"]");
        nlog.Info("JZH     ["+this.JZH+"]");
        nlog.Info("****************************************************");
    }

}
