package com.concrete.logic;

import android.content.Context;
import android.graphics.Bitmap;

import com.concrete.app.R;
import com.concrete.common.Common;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.net.HttpDef;
import com.concrete.net.HttpDownload;
import com.concrete.net.HttpEcho;
import com.concrete.net.HttpLogic;
import com.concrete.net.HttpUpload;
import com.concrete.type.ChipInfo;
import com.concrete.type.ImageInfo;
import com.concrete.type.JzrInfo;
import com.concrete.type.JzrInfoList;
import com.concrete.type.PrjectInfo;
import com.concrete.type.ProjectInfoList;
import com.concrete.type.SJBHInfo;
import com.concrete.type.UserInfo;

import java.io.File;
import java.util.ArrayList;

import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;

/**
 * Created by Tangxl on 2018/1/2.
 */

public class CommonLoigic {
    /************************************************************************************
     *      RFID 数据操作
     */

    public static boolean DeleteRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic,ArrayList<Long> RFID, String SJBH){
        int i;
        ArrayList<ChipInfo> item = new ArrayList<ChipInfo>();

        for (i = 0; i < RFID.size(); i++){
            if(!item.isEmpty()){
                item.clear();
            }
            ChipInfo mChipInfo =  new ChipInfo(java.util.UUID.randomUUID().toString(),SJBH, String.valueOf(RFID.get(i)), 1,
                    HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW);
            item.add(0,mChipInfo);
            mHttpLogic.OperRFIDBlock(HANDLE_HTTP_DELETE_RFID,item,UserInfo.getInstance(mContext).GetUserInfo());
            mSqliteLogic.DelteRFID(String.valueOf(RFID.get(i)), SJBH);
        }

        return true;
    }

    public static boolean InsteRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic,ArrayList<Long> RFID, String SJBH){
        int i;
        ArrayList<ChipInfo> item = new ArrayList<ChipInfo>();


        for(i = 0; i < RFID.size(); i++){
            if(!item.isEmpty()){
                item.clear();
            }
            ChipInfo mChipInfo =  new ChipInfo(java.util.UUID.randomUUID().toString(),SJBH, String.valueOf(RFID.get(i)), 1,
                    HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW);
            item.add(0,mChipInfo);
            if(null != QueryRFID(mContext,mSqliteLogic, mHttpLogic, RFID.get(i),SJBH))
            {
                if (mHttpLogic.OperRFIDBlock(HANDLE_HTTP_UPDATE_RFID, item, UserInfo.getInstance(mContext).GetUserInfo())) {
                    mSqliteLogic.SyncRFID(mChipInfo);
                }
            }
            else {
                if (mHttpLogic.OperRFIDBlock(HANDLE_HTTP_INSERT_RFID, item, UserInfo.getInstance(mContext).GetUserInfo())) {
                    mSqliteLogic.InsertRFID(mChipInfo, (byte) 0);
                }
            }
        }
        return true;
    }

    public static void SyncRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic,Long RFID, String SJBH){
        ChipInfo mChipInfo = QueryRFID(mContext,mSqliteLogic, mHttpLogic,RFID,SJBH);
        if(mChipInfo != null){
            mSqliteLogic.UpdateCollectRFID(mChipInfo);
        }
    }

    public static ChipInfo QueryRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Long RFID, String SJBH){
        return mHttpLogic.QueryRFIDBlock(SJBH,String.valueOf(RFID));
    }

    public static boolean CanInsertRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Long RFID, String SJBH){
        boolean ret = true;
        ArrayList<ChipInfo> mChipList = mHttpLogic.QueryRFIDFromSJBHlock(SJBH);
        if(null == mChipList)
        {
            ret = true;
        }
        else
        {
            if(mChipList.size() > 3 || mChipList.size() == 0){
                ret = false;
            }
            else
            {
                for(int i = 0; i < mChipList.size(); i++){
                    if(mChipList.get(i).RFID.equals(String.valueOf(RFID))){
                        ret = false;
                        break;
                    }
                }
            }
        }

        return true;
    }


    public static boolean UpdateCollectRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, ChipInfo mChipInfo){
        int i;
        boolean ret = false;
        ArrayList<ChipInfo> item = new ArrayList<ChipInfo>();
        ArrayList<Long> RFID = new ArrayList<Long>();
        RFID.add(0,Long.decode(mChipInfo.RFID));
        for(i = 0; i < RFID.size(); i++){
            if(!item.isEmpty()){
                item.clear();
            }
            item.add(0,mChipInfo);
            if(mHttpLogic.OperRFIDBlock(HANDLE_HTTP_UPDATE_RFID,item,UserInfo.getInstance(mContext).GetUserInfo())){
                mSqliteLogic.UpdateCollectRFID(mChipInfo);
                mSqliteLogic.UpdateCollectState(mChipInfo.RFID,1,0,1);
                ret = true;
            }
        }
        return ret;
    }

    public static ChipInfo QueryLastRFID(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Long RFID){
        ArrayList<ChipInfo> mChipList = mHttpLogic.QueryRFIDBlock(String.valueOf(RFID));
        if(mChipList != null && mChipList.size() > 0){
            return mChipList.get(0);
        }
        return null;
    }

    public static ChipInfo UpdateCollectRFIDSYInfo(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Long RFID){
        ChipInfo mChipInfo = QueryLastRFID(mContext,mSqliteLogic, mHttpLogic, RFID);
        if(mChipInfo != null && mChipInfo.TBL_SYJG.equals(HttpDef.UNKNOW) && mChipInfo.TBL_SYRQ.equals(HttpDef.UNKNOW)){
            mChipInfo.TBL_SYJG = UserInfo.getInstance(mContext).GetUserDanWei();
            mChipInfo.TBL_SYRQ = Common.getData();
            UpdateCollectRFID(mContext,mSqliteLogic, mHttpLogic,mChipInfo);
            SJBHInfo mSJBHInfo = QueryGCProject(mContext,mSqliteLogic, mHttpLogic, mChipInfo.TBL_SJBH);
            mSJBHInfo.TBL_SYJG = mChipInfo.TBL_SYJG;
            mSJBHInfo.TBL_SYRQ = mChipInfo.TBL_SYRQ;
            UpdateGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBHInfo);
        }

        return mChipInfo;
    }

    public static void SyncRFIDFromSJBH(Context mContext, SqliteLogic mSqliteLogic, HttpLogic mHttpLogic,String mSJBH){
        ArrayList<ChipInfo> mChipList = mHttpLogic.QueryRFIDFromSJBHlock(mSJBH);
        if(mChipList != null && mChipList.size() > 0){
            for(int i = 0 ; i < mChipList.size(); i++){
                mSqliteLogic.SyncRFID(mChipList.get(i));
            }
        }
    }

    /************************************************************************************
     *      工程信息 数据操作
     */

    public static void DeleteGCProject(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, SJBHInfo mSJBHInfo){
        ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
        item.add(0, mSJBHInfo);
        mHttpLogic.OperSJBHInfolock(HANDLE_HTTP_DELETE_SJBH,item,UserInfo.getInstance(mContext).GetUserInfo());
    }

    public static boolean InsertGCProject(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, SJBHInfo mSJBHInfo){
        ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
        item.add(0, mSJBHInfo);
        DeleteGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBHInfo);
        boolean ret = mHttpLogic.OperSJBHInfolock(HANDLE_HTTP_INSERT_SJBH,item,UserInfo.getInstance(mContext).GetUserInfo());
        if(ret){
            mSqliteLogic.InsertSJBH(mSJBHInfo,(byte)0);
        }

        return ret;
    }

    public static boolean UpdateGCProject(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, SJBHInfo mSJBHInfo){
        ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
        item.add(0, mSJBHInfo);
        boolean ret = mHttpLogic.OperSJBHInfolock(HANDLE_HTTP_UPDATE_SJBH,item,UserInfo.getInstance(mContext).GetUserInfo());
        if(ret){
            mSqliteLogic.UpdateSJBH(mSJBHInfo);
        }
        return ret;
    }

    public static SJBHInfo QueryGCProject(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, String SJBH){
        return mHttpLogic.QuerySJBHBlock(SJBH);
    }

    public static void SyncGCProject(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, String SJBH){
        SJBHInfo mSJBHInfo = mHttpLogic.QuerySJBHBlock(SJBH);

        if(null != mSJBHInfo){
            mSqliteLogic.UpdateSJBH(mSJBHInfo);
        }
    }

    /************************************************************************************
     *      图片 数据操作
     */

    public static void UploadImageFile(String Name, Bitmap mBitmap){
        String Path = IntentDef.DEFAULT_PATH+"/" + Name + ".jpg";
        Common.ScaleBmp(Path,mBitmap);
        HttpUpload.UploadFile(HttpDef.INTENT_UPLOAD_ADDR, Path,  new IntentDef.OnHttpReportListener(){

            @Override
            public void OnHttpDataReport(int Oper, long param1, long param2) {
                nlog.Info("Oper ["+Oper+"param1 ["+param1+"] ["+param2+"]");
            }
        });
    }

    public static void DeleteImageFile(String Name){
        String Path = IntentDef.DEFAULT_PATH+"/" + Name + ".jpg";
        File f = new File(Path);
        if (f.exists()) {
            f.delete();
        }
    }

    public static ImageInfo QueryImage(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Long RFID, String SJBH){
        ImageInfo mImageInfo = null;
        ChipInfo mChipInfo = QueryRFID(mContext,mSqliteLogic, mHttpLogic, RFID, SJBH);
        if(mChipInfo != null){
            mImageInfo = mHttpLogic.QueryImagelock(mChipInfo.SKs_UUid);
        }
        return mImageInfo;
    }

    public static boolean DeleteImage(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Long RFID, String SJBH){
        ImageInfo mImageInfo = QueryImage(mContext,mSqliteLogic, mHttpLogic, RFID, SJBH);
        if(null != mImageInfo){
            ArrayList<ImageInfo> item = new ArrayList<ImageInfo>();
            item.add(0,mImageInfo);
            mHttpLogic.OperImagelock(HANDLE_HTTP_DELETE_IMAGE, item, UserInfo.getInstance(mContext).GetUserInfo());
            DeleteImageFile(mImageInfo.SKs_UUid);
        }

        return true;
    }

    public static boolean InsertImage(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, Bitmap mBitmap, Long RFID, String SJBH, String Mode){

        nlog.Info("InsertImage ============ ["+RFID+"] SJBH ["+SJBH+"]");

        ChipInfo mChipInfo = QueryRFID(mContext,mSqliteLogic, mHttpLogic, RFID,SJBH);
        if(mChipInfo != null){
            nlog.Info("InsertImage =====QueryRFID======= ["+mChipInfo.RFID+"] SJBH ["+mChipInfo.TBL_SJBH+"]");
            DeleteImage(mContext,mSqliteLogic,mHttpLogic,RFID, SJBH);
            String UUID = java.util.UUID.randomUUID().toString();
            ImageInfo mImageInfo = new ImageInfo(UUID,mChipInfo.SKs_UUid,".jpg", "", Mode);
            ArrayList<ImageInfo> item = new ArrayList<ImageInfo>();
            item.add(0,mImageInfo);
            if(mHttpLogic.OperImagelock(HANDLE_HTTP_INSERT_IMAGE, item, UserInfo.getInstance(mContext).GetUserInfo())){
                UploadImageFile(UUID,mBitmap);
            }
        }

        return true;
    }

    public static ArrayList<ImageInfo> QueryImageFromSJBH(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic,String SJBH){
        ArrayList<ImageInfo> mImageList = new ArrayList<ImageInfo>();
        ArrayList<ChipInfo> mChipList = mHttpLogic.QueryRFIDFromSJBHlock(SJBH);
        if(mChipList != null && mChipList.size() > 0){
            for(int i = 0; i < mChipList.size(); i++){
                ImageInfo mImageInfo = QueryImage(mContext,mSqliteLogic, mHttpLogic, Long.decode(mChipList.get(i).RFID),SJBH);
                if(mImageInfo != null){
                    mImageList.add(mImageList.size(),mImageInfo);
                }

            }
        }

        return mImageList;
    }

    /************************************************************************************
     *      见证人 数据操作
     */

    public static JzrInfoList QueryJZR(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, String JZDW){
        return mHttpLogic.QueryJzrInfoBlock(JZDW);
    }

    public static void DeleteJZR(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, JzrInfo mJzrInfo){
        ArrayList<JzrInfo> item = new ArrayList<JzrInfo>();
        item.add(0,mJzrInfo);
        mHttpLogic.OperJzrInfolock(HANDLE_HTTP_DELETE_JZRPRJ,item,UserInfo.getInstance(mContext).GetUserInfo());
    }

    public static void InsertJZR(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, JzrInfo mJzrInfo){
        DeleteJZR(mContext,mSqliteLogic, mHttpLogic, mJzrInfo);
        ArrayList<JzrInfo> item = new ArrayList<JzrInfo>();
        item.add(0,mJzrInfo);
        mHttpLogic.OperJzrInfolock(HANDLE_HTTP_INSERT_JZRPRJ,item,UserInfo.getInstance(mContext).GetUserInfo());
    }

    /************************************************************************************
     *      工程信息 数据操作
     */

    public static ProjectInfoList QueryGCInfo(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, String GCMC){
        return mHttpLogic.QueryGGInfolock(GCMC);
    }

    public static void DeleteGCInfo(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, PrjectInfo mPrjectInfo){
        ArrayList<PrjectInfo> item = new ArrayList<PrjectInfo>();
        item.add(0,mPrjectInfo);
        mHttpLogic.OperGCInfolock(HANDLE_HTTP_DELETE_YFPROJECT,item,UserInfo.getInstance(mContext).GetUserInfo());
    }

    public static void InsertGCInfo(Context mContext,SqliteLogic mSqliteLogic, HttpLogic mHttpLogic, PrjectInfo mPrjectInfo){
        DeleteGCInfo(mContext,mSqliteLogic, mHttpLogic, mPrjectInfo);
        ArrayList<PrjectInfo> item = new ArrayList<PrjectInfo>();
        item.add(0,mPrjectInfo);
        mHttpLogic.OperGCInfolock(HANDLE_HTTP_INSERT_JZRPRJ,item,UserInfo.getInstance(mContext).GetUserInfo());
    }
}

