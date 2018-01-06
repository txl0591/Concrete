package com.concrete.logic;

import android.content.Context;

import com.concrete.app.R;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.SJBHInfo;
import com.concrete.type.UserInfo;

import java.util.ArrayList;

import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;

/**
 * Created by Tangxl on 2018/1/2.
 */

public class CommonLoigic {
    public static void SyncSJBH(String mSJBH, SqliteLogic mSqliteLogic, HttpLogic mHttpLogic){
        SJBHInfo mSJBHInfo = mHttpLogic.QuerySJBHBlock(mSJBH);

        if(null != mSJBHInfo){
            mSqliteLogic.SyncSJBH(mSJBHInfo);
        }
    }

    public static void SyncRFIDFromSJBH(String mSJBH, SqliteLogic mSqliteLogic, HttpLogic mHttpLogic){
        ArrayList<ChipInfo> mChipList = mHttpLogic.QueryRFIDFromSJBHlock(mSJBH);
        if(mChipList != null && mChipList.size() > 0){
            for(int i = 0 ; i < mChipList.size(); i++){
                mSqliteLogic.SyncRFID(mChipList.get(i));
            }
        }
    }

    public static void SyncSJBHAndRFID(String mSJBH, SqliteLogic mSqliteLogic, HttpLogic mHttpLogic){
        SyncSJBH(mSJBH,mSqliteLogic,mHttpLogic);
        SyncRFIDFromSJBH(mSJBH,mSqliteLogic,mHttpLogic);
    }

    public static boolean UpdateSJBH(Context mContext, String mSJBH, SqliteLogic mSqliteLogic, HttpLogic mHttpLogic){
        SJBHInfo nSJBHInfo = mSqliteLogic.QuerySJBH(mSJBH);
        ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
        item.add(0, nSJBHInfo);
        return mHttpLogic.OperSJBHInfolock(HANDLE_HTTP_UPDATE_SJBH,item,UserInfo.getInstance(mContext).GetUserInfo());
    }

    public static ArrayList<Long> GetDIff(ArrayList<Long> List1, ArrayList<Long> List2){
        ArrayList<Long> List3 = new ArrayList<Long>();
        for (int j =  0; j < List1.size(); j++){
            List3.add(j, List1.get(j));
        }

        List3.removeAll(List2);

        return List3;
    }


    /*********************************
     *  逻辑操作
     */

    public static boolean UpdateSYRQInSJBHTable(Context mContext, String mSJBH, SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, String SYRQ,String SYJG){
        SJBHInfo mSJBHInfo = mHttpLogic.QuerySJBHBlock(mSJBH);
        if(null == mSJBHInfo){
            mSJBHInfo = mSqliteLogic.QuerySJBH(mSJBH);
        }

        mSJBHInfo.TBL_SYJG = SYJG;
        mSJBHInfo.TBL_SYRQ = SYRQ;
        boolean  ret = UpdateSJBH(mContext, mSJBH, mSqliteLogic, mHttpLogic);
        if(ret){
            mSqliteLogic.SyncSJBH(mSJBHInfo);
        }
        return ret;
    }

}

