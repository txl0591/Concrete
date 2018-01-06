package com.concrete.net;

/**
 * Created by Tangxl on 2017/12/22.
 */

public class HttpOper {
    public String CMD;
    public String Param1;
    public String Param2;
    public String Param3;
    public String Param4;
    public String Param5;
    public String Param6;
    public String Param7;
    public String Param8;

    public HttpOper(){

    }

    public HttpOper(String CMD, String Param1){
        this.CMD = CMD;
        this.Param1 = Param1;
    }

    public HttpOper(String CMD, String Param1, String Param2, String Param3, String Param4, String Param5
            , String Param6, String Param7, String Param8){
        this.CMD = CMD;
        this.Param1 = Param1;
        this.Param2 = Param2;
        this.Param3 = Param3;
        this.Param4 = Param4;
        this.Param5 = Param5;
        this.Param6 = Param6;
        this.Param7 = Param7;
        this.Param8 = Param8;
    }
}
