package com.concrete.type;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class UserProjectList implements Serializable {
    public boolean result;
    public String error;
    public int echo_code;
    public int index;
    public ArrayList<UserProject> items;

    public UserProjectList(boolean result, String error, int echo_code, int index,ArrayList<UserProject> items){
        this.result = result;
        this.error = error;
        this.echo_code = echo_code;
        this.index = index;
        this.items = items;
    }
}
