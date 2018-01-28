package com.concrete.type;

import java.util.List;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class JzrInfoOper {
    public String Cmd;
    public int index;
    public List<JzrInfo> items;

    public JzrInfoOper(String Cmd, List<JzrInfo> items){
        this.Cmd = Cmd;
        this.index = items.size();
        this.items = items;
    }
}
