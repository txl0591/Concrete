package com.concrete.type;

import java.util.ArrayList;

/**
 * Created by Tangxl on 2017/12/10.
 */

public class SJBHInfoOper {
    public String Cmd;
    public int index;
    public ArrayList<SJBHInfo> items;

    public SJBHInfoOper(String Cmd, ArrayList<SJBHInfo> items){
        this.Cmd = Cmd;
        this.index = items.size();
        this.items = items;
    }
}
