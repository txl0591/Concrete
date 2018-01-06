package com.concrete.logic;

import android.content.Context;
import android.database.Cursor;

import com.concrete.database.CardCollectDBHelper;
import com.concrete.database.CardDBHelper;
import com.concrete.database.SJBHDBHelper;
import com.concrete.type.ChipInfo;
import com.concrete.type.ChipInfoOper;
import com.concrete.type.SJBHInfo;

import java.util.ArrayList;

/**
 * Created by Tangxl on 2017/11/25.
 */

public class SqliteLogic {

    private Context mContext = null;
    private CardDBHelper mCardDBHelper = null;
    private SJBHDBHelper mSJBHDBHelper = null;
    private CardCollectDBHelper mCardCollectDBHelper = null;

    public SqliteLogic(Context context){
        mContext = context;
        mCardDBHelper = new CardDBHelper(mContext);
        mSJBHDBHelper = new SJBHDBHelper(mContext);
        mCardCollectDBHelper = new CardCollectDBHelper(mContext);
    }

    public CardDBHelper GetCardDBHelper(){
        return mCardDBHelper;
    }
    public SJBHDBHelper GetSJBHDBHelper() {return mSJBHDBHelper;}

    public CardCollectDBHelper GetCardCollectDBHelper() {return mCardCollectDBHelper;}

    public CardCollectDBHelper GetCollectCardDBHelper(){
        return mCardCollectDBHelper;
    }

    public void Close(){
        mCardDBHelper.Close();
        mSJBHDBHelper.Close();
        mCardCollectDBHelper.Close();
    }

    public boolean InsertRFID(ChipInfo mChipInfo, Byte State){
        return mCardDBHelper.InsertChipInfo(mChipInfo,State);
    }

    public boolean SyncRFID(ChipInfo mChipInfo){
        return mCardDBHelper.UpdateChipInfo(mChipInfo);
    }

    public int DelteRFID(String RFID){
        return mCardDBHelper.DeleteRFID(RFID);
    }

    public int DeleteRFIDFromSJBH(String SJBH){
        return mCardDBHelper.Delete(SJBH);
    }

    public ChipInfo QueryRFIDFromChipID(String RFID){
        ChipInfo mChipInfo = null;
        Cursor mCursor = mCardDBHelper.QueryRFID(RFID);
        if(mCursor.getCount() > 0){
            mCursor.moveToNext();
            mChipInfo =  new ChipInfo(mCursor.getString(mCursor.getColumnIndex(CardDBHelper.SKs_UUid)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SJBH)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.RFID)),
                    mCursor.getInt(mCursor.getColumnIndex(CardDBHelper.SerialNo)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SYJG)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SYRQ)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_LDR)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_LRSJ)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_JCRY)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_JCRQ)));
        }

        return mChipInfo;
    }

    public ArrayList<ChipInfo> QureyRFIDFromSJBH(String SJBH){
        int i = 0;
        ArrayList<ChipInfo> mChipInfoList = new  ArrayList<ChipInfo>();
        Cursor mCursor = mCardDBHelper.QuerySJBH(SJBH);
        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                ChipInfo mChipInfo =  new ChipInfo(mCursor.getString(mCursor.getColumnIndex(CardDBHelper.SKs_UUid)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SJBH)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.RFID)),
                        mCursor.getInt(mCursor.getColumnIndex(CardDBHelper.SerialNo)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SYJG)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SYRQ)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_LDR)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_LRSJ)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_JCRY)),
                        mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_JCRQ)));
                mChipInfoList.add(i++,mChipInfo);
            }
            ;

        }

        return mChipInfoList;

    }

    public SJBHInfo QuerySJBH(String SJBH){
        SJBHInfo mSJBHInfo = null;
        Cursor mCursor = GetSJBHDBHelper().QuerySJBH(SJBH);
        if(mCursor.getCount() > 0){
            mCursor.moveToNext();
            mSJBHInfo = new SJBHInfo(
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_SJBH)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_YPLX)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_GCMC)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_GJBW)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_QDDJ)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_YHFS)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_ZZRQ)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_PHBBH)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_SCLSH)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_BZDW)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_SGDW)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_WTDW)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_JZDW)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_JZR)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_JZBH)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_GCID)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_GCDM)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_YPBH)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_JCJG)),
                    mCursor.getFloat(mCursor.getColumnIndex(SJBHDBHelper.TBL_JCresult)),
                    mCursor.getFloat(mCursor.getColumnIndex(SJBHDBHelper.TBL_JCbfb)),
                    (byte)mCursor.getInt(mCursor.getColumnIndex(SJBHDBHelper.TBL_STATE)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_SYJG)),
                    mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_SYRQ))
            );
        }


        return mSJBHInfo;
    }

    public boolean InsertSJBH(SJBHInfo mSJBHInfo, Byte State){
        return mSJBHDBHelper.Insert(mSJBHInfo, State);
    }

    public boolean SyncSJBH(SJBHInfo mSJBHInfo){
        return mSJBHDBHelper.Sync(mSJBHInfo);
    }

    public int DeleteSJBH(String SJBH){
        return mSJBHDBHelper.Delete(SJBH);
    }


    public ChipInfo QureyCreateRFID(String RFID){
        ChipInfo mChipInfo = null;
        Cursor mCursor = mCardCollectDBHelper.QueryCard(RFID);
        if(mCursor.getCount() > 0){
            mCursor.moveToNext();
            mChipInfo =  new ChipInfo(mCursor.getString(mCursor.getColumnIndex(CardDBHelper.SKs_UUid)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SJBH)),
                    RFID,
                    mCursor.getInt(mCursor.getColumnIndex(CardDBHelper.SerialNo)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SYJG)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_SYRQ)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_LDR)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_LRSJ)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_JCRY)),
                    mCursor.getString(mCursor.getColumnIndex(CardDBHelper.TBL_JCRQ)));
        }

        return mChipInfo;
    }

    public int CollectChipInfo(String ChipId){
        return mCardCollectDBHelper.CollectChipInfo(ChipId);
    }

    public int DelteCollectRFID(String RFID){
        return mCardCollectDBHelper.DeleteRFID(RFID);
    }

    public int UpdateCollectRFID(ChipInfo RFID){
        if(mCardCollectDBHelper.UpdateChipInfo(RFID)){
            return 1;
        }
        return 0;
    }

    public Cursor QueryByOrder(){
        return mCardCollectDBHelper.QueryByOrder();
    }

    public int UpdateCollectState(String RFID, int State, int Echo, int Upload){
        if(mCardCollectDBHelper.UpdateStateFromChipId(RFID,State,Echo, Upload)){
            return 1;
        }
        return 0;
    }


}
