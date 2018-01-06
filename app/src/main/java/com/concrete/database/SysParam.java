package com.concrete.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.concrete.common.Common;
import com.concrete.type.UserType;

import java.text.SimpleDateFormat;

/**
 * Created by Tangxl on 2017/12/31.
 */

public class SysParam {

    public static final String CORESOFT_PARAM = "CORESOFT_PARAM";

    public static final String CORESOFT_USERNAME = "CORESOFT_USERNAME";
    public static final String CORESOFT_USERTYPE = "CORESOFT_USERTYPE";
    public static final String CORESOFT_SCDATE = "CORESOFT_SCDATE";
    public static final String CORESOFT_SCINDEX = "CORESOFT_SCINDEX";

    private static volatile SysParam mInstance;//单利引用
    public Context mContext = null;

    public SysParam(Context context){
        mContext = context;
    }

    public static SysParam getInstance(Context context) {
        SysParam inst = mInstance;
        if (inst == null) {
            synchronized (SysParam.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new SysParam(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public String GetUserName(){
        SharedPreferences read = mContext.getSharedPreferences(CORESOFT_PARAM, Context.MODE_PRIVATE);
        String value = read.getString(CORESOFT_USERNAME, "txl0591");
        return value;
    }

    public int GetUserType(){
        SharedPreferences read = mContext.getSharedPreferences(CORESOFT_PARAM, Context.MODE_PRIVATE);
        return read.getInt(CORESOFT_USERTYPE, UserType.USERTYPE_ADMIN);
    }

    public void SetUserInfo(String Username, int Type){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(CORESOFT_PARAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(CORESOFT_USERTYPE,Type);
        mEditor.putString(CORESOFT_USERNAME,Username);
        mEditor.commit();
    }

    public void SetSGDate(){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(CORESOFT_PARAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(CORESOFT_SCDATE,Common.getData());
        mEditor.commit();

    }

    public int GetSGIndex(){
        int index = 1;
        SharedPreferences read = mContext.getSharedPreferences(CORESOFT_PARAM, Context.MODE_PRIVATE);
        String Date = read.getString(CORESOFT_SCDATE,"Unknow");
        if(Date.equals("Unknow") || !Common.getData().equals(Date)){
            SetSGIndex(1);
            SetSGDate();
        }else{
            index = read.getInt(CORESOFT_SCINDEX, 1);
        }

        return index;
    }

    public void SetSGIndex(int index){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(CORESOFT_PARAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(CORESOFT_SCINDEX,index);
        mEditor.commit();
    }

    public void AddSGIndex(){
        int index = GetSGIndex();
        index++;
        SetSGIndex(index);
    }
}
