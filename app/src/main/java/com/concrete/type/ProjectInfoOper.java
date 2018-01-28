package com.concrete.type;

import java.util.ArrayList;

/**
 * Created by Tangxl on 2018/1/10.
 */

public class ProjectInfoOper {
    public String Cmd;
    public int index;
    public ArrayList<PrjectInfo> items;

    public ProjectInfoOper(String Cmd, ArrayList<PrjectInfo> items){
        this.Cmd = Cmd;
        this.index = items.size();
        this.items = items;
    }
}
