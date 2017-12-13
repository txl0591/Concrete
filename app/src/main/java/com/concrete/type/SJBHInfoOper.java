package com.concrete.type;

import java.util.ArrayList;

/**
 * Created by Tangxl on 2017/12/10.
 */

public class SJBHInfoOper {
    public String Table;
    public String Cmd;
    public int index;
    public ArrayList<SJBHInfo> items;

    public SJBHInfoOper(String Cmd, int Index,ArrayList<SJBHInfo> items){
        this.Cmd = Cmd;
        this.index = Index;
        this.items = items;
        this.Table = SJBHInfoList.TABLE;
    }
}
