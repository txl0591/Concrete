package com.concrete.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.concrete.common.Common;
import com.concrete.common.nlog;
import com.concrete.type.ChipInfo;
import com.concrete.type.UserInfo;

import java.io.File;
import java.io.IOException;

import static com.concrete.common.IntentDef.DEFAULT_PATH;

/**
 * Created by Tangxl on 2017/12/30.
 */

public class CardCollectDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CardCollect.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TBL_NAME = "ChipDataBase";

    public static final String TBL_ID = "ID";
    public static final String SKs_UUid = "SKs_UUid";
    public static final String TBL_SJBH = "TBL_SJBH";
    public static final String RFID = "RFID";
    public static final String SerialNo = "SerialNo";

    public static final String TBL_SYJG = "TBL_SYJG";
    public static final String TBL_SYRQ = "TBL_SYRQ";
    public static final String TBL_LDR = "TBL_LDR";
    public static final String TBL_LRSJ = "TBL_LRSJ";
    public static final String TBL_JCRY = "TBL_JCRY";
    public static final String TBL_JCRQ = "TBL_JCRQ";
    public static final String TBL_ZZRQ = "TBL_ZZRQ";

    public static final String TBL_STATE = "TBL_STATE";

    public static final String TBL_UPLOAD_ECHO = "TBL_UPLOAD_ECHO";
    public static final String TBL_UPLOAD = "TBL_UPLOAD";

    private static final String CREATE_TBL = " create table " + TBL_NAME
            + "(" + TBL_ID + " INTEGER primary key autoincrement,"
            + TBL_SJBH + " varchar(50),"
            + SKs_UUid + " varchar(50),"
            + RFID + " varchar(50),"
            + SerialNo+" INTEGER,"
            + TBL_SYJG + " varchar(50),"
            + TBL_SYRQ + " varchar(50),"
            + TBL_LDR + " varchar(50),"
            + TBL_LRSJ + " varchar(50),"
            + TBL_JCRY + " varchar(50),"
            + TBL_JCRQ + " varchar(50),"
            + TBL_ZZRQ + " varchar(50),"
            + TBL_STATE + " INTEGER,"
            + TBL_UPLOAD_ECHO + " INTEGER,"
            + TBL_UPLOAD+" INTEGER)";

    private SQLiteDatabase mSQLiteDatabase = null;
    private Context mContext = null;
    public static final String dbPath = DEFAULT_PATH;
    public static final String DB_NAME = dbPath + "/" + DATABASE_NAME;

    public CardCollectDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub
        File dbp = new File(dbPath);
        File dbf = new File(DB_NAME);
        if (!dbp.exists())
        {
            dbp.mkdir();
        }

        boolean isFileCreateSuccess = false;
        if (!dbf.exists())
        {
            try {
                isFileCreateSuccess = dbf.createNewFile();
                if(isFileCreateSuccess)
                {
                    SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(CREATE_TBL);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isFileCreateSuccess = true;
        }

        if (isFileCreateSuccess) {
            return SQLiteDatabase.openOrCreateDatabase(dbf, null);
        }
        else {
            return null;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void Close(){
        if (mSQLiteDatabase != null){
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
    }

    public void Delete(String SJBH, String Card){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = TBL_SJBH+"=?"+" and "+ RFID+"=?";
        String[] selectionArgs = new String[]{SJBH,Card};
        mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public Cursor QuerySJBH(String SJBH){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{SJBH};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor QueryCard(String Card){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = RFID+"=?";
        String[] selectionArgs = new String[]{Card};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor QueryCard(String Card,String SJBH){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = TBL_SJBH+"=?"+" and "+ RFID+"=?";
        String[] selectionArgs = new String[]{SJBH,Card};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor Query(){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, null, null, null, null, null);
        return c;
    }

    public void UpdateState(String SJBH,int State){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TBL_STATE, State);
        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{SJBH};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
    }

    public boolean UpdateChipInfo(ChipInfo mChipInfo){
        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TBL_SJBH, mChipInfo.TBL_SJBH);
        mContentValues.put(SKs_UUid, mChipInfo.SKs_UUid);
        mContentValues.put(RFID, mChipInfo.RFID);
        mContentValues.put(SerialNo, mChipInfo.SerialNo );

        mContentValues.put(TBL_SYJG, mChipInfo.TBL_SYJG );
        mContentValues.put(TBL_SYRQ, mChipInfo.TBL_SYRQ );
        mContentValues.put(TBL_LDR, mChipInfo.TBL_LDR );
        mContentValues.put(TBL_LRSJ, mChipInfo.TBL_LRSJ );
        mContentValues.put(TBL_JCRY, mChipInfo.TBL_JCRY );
        mContentValues.put(TBL_JCRQ, mChipInfo.TBL_JCRQ );
        mContentValues.put(TBL_ZZRQ, Common.getData() );

        String selection = RFID+"=?";
        String[] selectionArgs = new String[]{mChipInfo.RFID};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
        return true;
    }

    public boolean InsertChipInfo(ChipInfo mChipInfo, int State){
        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }

        Cursor mCursor = QueryCard(mChipInfo.TBL_SJBH,mChipInfo.RFID);
        if(mCursor.getCount() > 0){
            UpdateChipInfo(mChipInfo);
        }
        else{
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(TBL_SJBH, mChipInfo.TBL_SJBH);
            mContentValues.put(SKs_UUid, mChipInfo.SKs_UUid);
            mContentValues.put(RFID, mChipInfo.RFID);
            mContentValues.put(SerialNo, mChipInfo.SerialNo );

            mContentValues.put(TBL_SYJG, mChipInfo.TBL_SYJG );
            mContentValues.put(TBL_SYRQ, mChipInfo.TBL_SYRQ );
            mContentValues.put(TBL_LDR, mChipInfo.TBL_LDR );
            mContentValues.put(TBL_LRSJ, mChipInfo.TBL_LRSJ );
            mContentValues.put(TBL_JCRY, mChipInfo.TBL_JCRY );
            mContentValues.put(TBL_JCRQ, mChipInfo.TBL_JCRQ );
            mContentValues.put(TBL_ZZRQ, Common.getData() );
            mContentValues.put(TBL_UPLOAD, false );
            mContentValues.put(TBL_STATE, State);
            mSQLiteDatabase.insert(TBL_NAME, null, mContentValues);
        }

        mCursor.close();
        return true;
    }

    public boolean UpdateStateFromChipId(String Chip,int State, int Echo, int Upload){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TBL_STATE, State);
        mContentValues.put(TBL_UPLOAD, Upload);
        mContentValues.put(TBL_UPLOAD_ECHO, Echo);
        String selection = RFID+"=?";
        String[] selectionArgs = new String[]{Chip};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
        return true;
    }

    public int CollectChipInfo(String Chip){
        int ret = 0;
        Cursor mCursor = QueryCard(Chip);
        if(mCursor.getCount() == 0){
            ChipInfo mChipInfo = new ChipInfo("Unknow", "Unknow", Chip, 0,
                    UserInfo.getInstance(mContext).GetUserDanWei(),
                    Common.getData(),
                    "Unknow",
                    "Unknow",
                    "Unknow",
                    "Unknow");
            InsertChipInfo(mChipInfo,(byte)1);
            ret = 1;
        }
        mCursor.close();
        return ret;
    }

    public int Delete(String SJBH){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{SJBH};
        return mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public int DeleteRFID(String rfid){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = RFID+"=?";
        String[] selectionArgs = new String[]{rfid};
        return mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public Cursor QueryByOrder(){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, null, null, null, null, "TBL_SJBH DESC");
        return c;
    }

    public Cursor QueryByOrder(String Date){
        if(null == mSQLiteDatabase){
            mSQLiteDatabase = getWritableDatabase();
        }
        String selection = TBL_ZZRQ+"=?";
        String[] selectionArgs = new String[]{Date};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, "TBL_SJBH DESC");
        return c;
    }
}
