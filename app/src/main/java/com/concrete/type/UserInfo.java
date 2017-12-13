package com.concrete.type;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Tangxl on 2017/11/25.
 */

public class UserInfo {

    private static volatile UserInfo mInstance;//单利引用

    private String UserName = "XXXX";
    private String Passwd = "XXXX";
    private String UserType = "XXXX";
    private int SerialNo = 1;
    private boolean mLoginState = false;

    public UserInfo(Context context){
        GetDefault();
    }

    public String GetUserName(){
        return this.UserName;
    }

    public boolean GetLoginState(){
        return this.mLoginState;
    }

    public void SetLoginState(boolean State){
        this.mLoginState = State;
    }

    public String GetPassword(){
        return this.Passwd;
    }

    public String GetUserType(){
        return this.UserType;
    }

    public void GetDefault(){
        this.UserName = "txl0591";
        this.Passwd = "34085504";
        this.UserType = "admin";
    }

    public static UserInfo getInstance(Context context) {
        UserInfo inst = mInstance;
        if (inst == null) {
            synchronized (UserInfo.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new UserInfo(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public void SetUserInfo(String Name, String Passwd, String Usertype, int Serialno){
        this.UserName = Name;
        this.Passwd = Passwd;
        this.UserType = Usertype;
        this.SerialNo = Serialno;
    }

    public HashMap<String, String> GetUserLoginInfo(){
        HashMap<String, String> Param  = new HashMap<String, String>();
        Param.put("SerialNo", String.valueOf(this.SerialNo));
        Param.put("UserType", this.UserType);
        Param.put("UserName", this.UserName);
        Param.put("Passwd", this.Passwd);
        Param.put("Timestamp", String.valueOf(System.currentTimeMillis()));
        return Param;
    }

    public HashMap<String, String> GetUserInfo(){
        HashMap<String, String> Param  = new HashMap<String, String>();
        Param.put("SerialNo", String.valueOf(this.SerialNo));
        Param.put("UserType", this.UserType);
        Param.put("UserName", this.UserName);
        Param.put("Passwd", this.Passwd);
        Param.put("Timestamp", String.valueOf(System.currentTimeMillis()));
        return Param;
    }


}
