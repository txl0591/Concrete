package com.concrete.logic;

import android.content.Context;
import android.database.Cursor;

import com.concrete.common.nlog;
import com.concrete.database.CardCollectDBHelper;
import com.concrete.database.CardDBHelper;
import com.concrete.database.GCProjectDBHelper;
import com.concrete.database.ImageDBHelp;
import com.concrete.database.JZRDBHelper;
import com.concrete.database.SJBHDBHelper;
import com.concrete.type.ChipInfo;
import com.concrete.type.ChipInfoOper;
import com.concrete.type.ImageInfo;
import com.concrete.type.JzrInfo;
import com.concrete.type.PrjectInfo;
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
    private GCProjectDBHelper mGCProjectDBHelper = null;
    private JZRDBHelper mJZRDBHelper = null;
    private ImageDBHelp mImageDBHelp = null;

    public SqliteLogic(Context context){
        mContext = context;
        mCardDBHelper = new CardDBHelper(mContext);
        mSJBHDBHelper = new SJBHDBHelper(mContext);
        mGCProjectDBHelper = new GCProjectDBHelper(mContext);
        mCardCollectDBHelper = new CardCollectDBHelper(mContext);
        mJZRDBHelper = new JZRDBHelper(mContext);
        mImageDBHelp = new ImageDBHelp(mContext);
    }

    public CardDBHelper GetCardDBHelper(){
        return mCardDBHelper;
    }
    public SJBHDBHelper GetSJBHDBHelper() {return mSJBHDBHelper;}
    public GCProjectDBHelper GetGCProjectDBHelper() {return mGCProjectDBHelper;}
    public JZRDBHelper GetJZRDBHelper() {return mJZRDBHelper;}
    public CardCollectDBHelper GetCollectCardDBHelper(){
        return mCardCollectDBHelper;
    }

    public void Close(){
        mCardDBHelper.Close();
        mSJBHDBHelper.Close();
        mCardCollectDBHelper.Close();
        mGCProjectDBHelper.Close();
        mJZRDBHelper.Close();
        mImageDBHelp.close();
    }

    public boolean InsertRFID(ChipInfo mChipInfo, Byte State){
        return mCardDBHelper.InsertChipInfo(mChipInfo,State);
    }

    public boolean SyncRFID(ChipInfo mChipInfo){
        return mCardDBHelper.UpdateChipInfo(mChipInfo);
    }

    public int DelteRFID(String RFID, String SJBH){
        return mCardDBHelper.DeleteRFID(RFID,SJBH);
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

    public SJBHInfo QuerySJBH(){
        SJBHInfo mSJBHInfo = null;
        Cursor mCursor = GetSJBHDBHelper().Query();
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

    public boolean UpdateSJBH(SJBHInfo mSJBHInfo){
        return mSJBHDBHelper.Update(mSJBHInfo);
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

    public boolean InsertCollectChipInfo(ChipInfo ChipInfo){
        return mCardCollectDBHelper.InsertChipInfo(ChipInfo,1);
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

    public Cursor QueryByOrder(String Date){
        return mCardCollectDBHelper.QueryByOrder(Date);
    }

    public int UpdateCollectState(String RFID, int State, int Echo, int Upload){
        if(mCardCollectDBHelper.UpdateStateFromChipId(RFID,State,Echo, Upload)){
            return 1;
        }
        return 0;
    }

    /****************************
     *   GCProject
     */

    public ArrayList<PrjectInfo> QueryGCProject(String GCMC){
        ArrayList<PrjectInfo> mPrjectInfoList = new ArrayList<PrjectInfo>();
        Cursor mCursor = mGCProjectDBHelper.QueryFromGCMC(GCMC);
        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                PrjectInfo mPrjectInfo =  new PrjectInfo(mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.project_UUID)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.project_id)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.dcPK)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.compact_type)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.project_code)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.project_info)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.check_unit_id)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.consCorpNames)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.superCorpNames)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.corpname)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.check_unit)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.consCorp_id)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.superCorp_id)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.corpcode)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.createDate)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.updatetime)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.district_id)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.safety_id)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.gongchengmianji)),
                        mCursor.getString(mCursor.getColumnIndex(GCProjectDBHelper.touzijinne)));

                mPrjectInfoList.add(mPrjectInfoList.size(),mPrjectInfo);
            }
        }
        return mPrjectInfoList;
    }

    public boolean InstallGCProjectInfo( PrjectInfo Info){
        return mGCProjectDBHelper.InsertPrjectInfo(Info);
    }

    public boolean QueryGCProject(String project,String check_unit, String consCorpNames){
        boolean ret = false;
        Cursor mCursor = mGCProjectDBHelper.Query(project, check_unit, consCorpNames);
        nlog.Info("QueryGCProject mCursor.getCount() ============== ["+mCursor.getCount()+"]");
        if(mCursor.getCount() > 0){
            ret = true;
        }
        return ret;
    }

    /****************************
     *   JZRProject
     */

    public ArrayList<JzrInfo> QueryJzrProject(String JZDW){
        ArrayList<JzrInfo> mJzrInfoList = new ArrayList<JzrInfo>();
        Cursor mCursor = mJZRDBHelper.QueryJZDW(JZDW);
        nlog.Info("QueryJzrProject mCursor.getCount() ============== ["+mCursor.getCount()+"]");
        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                JzrInfo mJzrInfo = new JzrInfo(mCursor.getString(mCursor.getColumnIndex(JZRDBHelper.JZDW_ID)),
                        mCursor.getString(mCursor.getColumnIndex(JZRDBHelper.JZDW)),
                        mCursor.getString(mCursor.getColumnIndex(JZRDBHelper.JZR)),
                        mCursor.getString(mCursor.getColumnIndex(JZRDBHelper.JZH)));

                mJzrInfoList.add(mJzrInfoList.size(),mJzrInfo);
            }
        }
        return mJzrInfoList;
    }

    public boolean InstallJzrInfo( JzrInfo Info){
        return mJZRDBHelper.InsertJzrInfo(Info);
    }

    public boolean QueryJzrProject(String Jzh,String Jzr, String Jzdw){
        boolean ret = false;
        Cursor mCursor = mJZRDBHelper.Query(Jzh, Jzr, Jzdw);
        nlog.Info("QueryJzrProject mCursor.getCount() ============== ["+mCursor.getCount()+"]");
        if(mCursor.getCount() > 0){
            ret = true;
        }
        return ret;
    }

    /*****************************************************************
     *  Image
     */

    public boolean InstallImage(ImageInfo mImageInfo){
        return mImageDBHelp.InsertImage(mImageInfo);
    }

}
