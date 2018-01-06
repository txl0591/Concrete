package com.concrete.type;

import java.util.ArrayList;

public class UserEcho {

    public boolean result;
    public String error;
    public int echo_code;
    public int index;
    public UserType items;

    public UserEcho(boolean result, String error, int echoCode, int index, UserType items){
        this.result = result;
        this.echo_code = echoCode;
        this.error = error;
        this.index = index;
        this.items = items;
    }
}