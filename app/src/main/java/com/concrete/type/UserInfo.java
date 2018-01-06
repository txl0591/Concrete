package com.concrete.type;

import android.content.Context;

import com.concrete.app.R;
import com.concrete.common.nlog;
import com.concrete.database.SysParam;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tangxl on 2017/11/25.
 */

public class UserInfo {

    private static volatile UserInfo mInstance;//单利引用

    private Context mContext = null;
    private static String UserName = "XXXX";
    private static String Passwd = "XXXX";
    private static UserType mUserType = new UserType(UserType.USERTYPE_ADMIN,"");
    private static ArrayList<PrjectInfo> PrjectInfoList = new ArrayList<PrjectInfo>();
    private static boolean mLoginState = false;

    public UserInfo(Context context){
        mContext = context;
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

    public int GetUserType(){
        return this.mUserType.TBL_USERTYPE;
    }

    public void SetUserType(int type){
        this.mUserType.TBL_USERTYPE = type;
    }


    public String GetUserDanWei(){return this.mUserType.TBL_USERDanWei; }

    public String GetUserTypeName(){
        String[] mtype = mContext.getResources().getStringArray(R.array.YHLX);
        return mtype[this.mUserType.TBL_USERTYPE];
    }



    public void SetPrjectInfoList(ArrayList<PrjectInfo> item){
        if(!this.PrjectInfoList.isEmpty()){
            this.PrjectInfoList.clear();
        }
        for(int i = 0; i < item.size(); i++){
            this.PrjectInfoList.add(i,item.get(i));
        }
    }

    public ArrayList<PrjectInfo> GetPrjectInfoList(){
        return this.PrjectInfoList;
    }

    public String[] GetProjectList(){
        if(null == this.PrjectInfoList){
            return null;
        }
        String[] mList = new String[this.PrjectInfoList.size()];
        for(int i = 0; i < this.PrjectInfoList.size(); i++){
            mList[i] = this.PrjectInfoList.get(i).project_id;
        }
        return mList;
    }

    public PrjectInfo GetPrjectInfo(String ID){
        PrjectInfo mPrjectInfo = null;
        if(null == PrjectInfoList){
            return null;
        }
        for(int i = 0; i < PrjectInfoList.size(); i++){
            if(ID.equals(PrjectInfoList.get(i).project_id)){
                mPrjectInfo = PrjectInfoList.get(i);
                break;
            }
        }
        return mPrjectInfo;
    }

    public void GetDefault(){
        this.UserName = SysParam.getInstance(mContext).GetUserName();
        this.Passwd = "34085504";
        this.mUserType.TBL_USERTYPE = SysParam.getInstance(mContext).GetUserType();
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

    public void SetUserInfo(String Name, String Passwd, int Usertype, String UserDw){
        this.UserName = Name;
        this.Passwd = Passwd;
        this.mUserType.TBL_USERTYPE = Usertype;
        this.mUserType.TBL_USERDanWei = UserDw;
    }

    public HashMap<String, String> GetUserLoginInfo(){
        HashMap<String, String> Param  = new HashMap<String, String>();
        Param.put("UserName", this.UserName);
        Param.put("Passwd", this.Passwd);
        Param.put("Timestamp", String.valueOf(System.currentTimeMillis()));
        return Param;
    }

    public HashMap<String, String> GetUserInfo(){
        HashMap<String, String> Param  = new HashMap<String, String>();
        Param.put("UserType", String.valueOf(this.mUserType.TBL_USERTYPE));
        Param.put("UserName", this.UserName);
        Param.put("Timestamp", String.valueOf(System.currentTimeMillis()));
        return Param;
    }


}
