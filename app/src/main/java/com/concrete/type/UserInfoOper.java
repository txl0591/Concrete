package com.concrete.type;

import java.util.ArrayList;

/**
 * Created by Tangxl on 2018/1/13.
 */

public class UserInfoOper {

    public String Cmd;
    public int index;
    public ArrayList<UserClass> items;

    public UserInfoOper(String Cmd, ArrayList<UserClass> items){
        this.Cmd = Cmd;
        this.index = items.size();
        this.items = items;
    }

}
