package com.concrete.type;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class UserProjectOper implements Serializable {
    public String Cmd;
    public int index;
    public ArrayList<UserProject> items;

    public UserProjectOper(String Cmd, ArrayList<UserProject> items){
        this.Cmd = Cmd;
        this.index = items.size();
        this.items = items;
    }
}
