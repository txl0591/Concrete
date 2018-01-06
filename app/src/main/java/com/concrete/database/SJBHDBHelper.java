package com.concrete.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.concrete.common.Common;
import com.concrete.common.nlog;
import com.concrete.type.SJBHInfo;
import com.concrete.type.SJBHInfoOper;

import java.io.File;
import java.io.IOException;

import static com.concrete.common.IntentDef.DEFAULT_PATH;

/**
 * Created by Tangxl on 2017/12/16.
 */

public class SJBHDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SJBHInfo.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TBL_NAME = "SJBHDataBase";

    public static final String TBL_ID = "ID";
    public static final String TBL_SJBH = "TBL_SJBH";
    public static final String TBL_YPLX = "TBL_YPLX";
    public static final String TBL_GCMC = "TBL_GCMC";
    public static final String TBL_GJBW = "TBL_GJBW";
    public static final String TBL_QDDJ = "TBL_QDDJ";
    public static final String TBL_YHFS = "TBL_YHFS";
    public static final String TBL_ZZRQ = "TBL_ZZRQ";
    public static final String TBL_PHBBH = "TBL_PHBBH";
    public static final String TBL_SCLSH = "TBL_SCLSH";
    public static final String TBL_BZDW = "TBL_BZDW";
    public static final String TBL_WTDW = "TBL_WTDW";
    public static final String TBL_SGDW = "TBL_SGDW";
    public static final String TBL_JZDW = "TBL_JZDW";
    public static final String TBL_JZR = "TBL_JZR";
    public static final String TBL_JZBH = "TBL_JZBH";

    public static final String TBL_GCID = "TBL_GCID";
    public static final String TBL_GCDM = "TBL_GCDM";
    public static final String TBL_YPBH = "TBL_YPBH";
    public static final String TBL_JCJG = "TBL_JCJG";
    public static final String TBL_JCresult = "TBL_JCresult";
    public static final String TBL_JCbfb = "TBL_JCbfb";

    public static final String TBL_STATE = "TBL_STATE";

    public static final String TBL_SYRQ = "TBL_SYRQ";
    public static final String TBL_SYJG = "TBL_SYJG";


    private static final String CREATE_TBL = " create table " + TBL_NAME
            + "(" + TBL_ID + " INTEGER primary key autoincrement,"
            + TBL_SJBH + " varchar(50),"
            + TBL_YPLX + " varchar(50),"
            + TBL_GCMC + " varchar(200),"
            + TBL_GJBW + " varchar(200),"
            + TBL_QDDJ + " varchar(50),"
            + TBL_YHFS + " varchar(50),"
            + TBL_ZZRQ + " varchar(50),"
            + TBL_PHBBH + " varchar(50),"
            + TBL_SCLSH + " varchar(50),"
            + TBL_BZDW + " varchar(100),"
            + TBL_WTDW + " varchar(100),"
            + TBL_SGDW + " varchar(100),"
            + TBL_JZDW + " varchar(100),"
            + TBL_JZR + " varchar(50),"
            + TBL_JZBH + " varchar(50),"
            + TBL_GCID + " varchar(50),"
            + TBL_GCDM + " varchar(50),"
            + TBL_YPBH + " varchar(50),"
            + TBL_JCJG + " varchar(50),"
            + TBL_JCresult + " FLOAT,"
            + TBL_JCbfb + " FLOAT,"
            + TBL_STATE + " INTEGER,"
            + TBL_SYJG + " varchar(50),"
            + TBL_SYRQ + " varchar(50))";

    private SQLiteDatabase mSQLiteDatabase = null;
    private SQLiteDatabase mSQLiteLocal = null;
    public static final String dbPath = DEFAULT_PATH;
    public static final String DB_NAME = dbPath + "/" + DATABASE_NAME;

    public SJBHDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mSQLiteDatabase = getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub
        if(mSQLiteLocal != null){
            return mSQLiteLocal;
        }

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
            mSQLiteLocal = SQLiteDatabase.openOrCreateDatabase(dbf, null);
            return mSQLiteLocal;
        }
        else {
            return null;
        }
    }

    public void Close(){
        if (mSQLiteDatabase != null){
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
    }

    public Cursor QuerySJBH(String SJBH){
        mSQLiteDatabase = getWritableDatabase();
        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{SJBH};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor Query(){
        mSQLiteDatabase = getWritableDatabase();
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, null, null, null, null, null);
        return c;
    }

    public void UpdateState(String SJBH,byte State){
        mSQLiteDatabase = getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TBL_STATE, State);
        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{SJBH};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
    }

    public boolean Insert(SJBHInfo mSJBHInfo, Byte State){
        boolean ret = true;
        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }
        Cursor c = QuerySJBH(mSJBHInfo.TBL_SJBH);
        if (c.getCount() > 0){
            ret = false;
        }

        if(ret){
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(TBL_SJBH, mSJBHInfo.TBL_SJBH);
            mContentValues.put(TBL_YPLX, mSJBHInfo.TBL_YPLX);
            mContentValues.put(TBL_GCMC, mSJBHInfo.TBL_GCMC);
            mContentValues.put(TBL_GJBW, mSJBHInfo.TBL_GJBW);
            mContentValues.put(TBL_QDDJ, mSJBHInfo.TBL_QDDJ);
            mContentValues.put(TBL_YHFS, mSJBHInfo.TBL_YHFS);
            mContentValues.put(TBL_ZZRQ, mSJBHInfo.TBL_ZZRQ);
            mContentValues.put(TBL_PHBBH, mSJBHInfo.TBL_PHBBH);
            mContentValues.put(TBL_SCLSH, mSJBHInfo.TBL_SCLSH);
            mContentValues.put(TBL_BZDW, mSJBHInfo.TBL_BZDW);
            mContentValues.put(TBL_WTDW, mSJBHInfo.TBL_WTDW);
            mContentValues.put(TBL_SGDW, mSJBHInfo.TBL_SGDW);
            mContentValues.put(TBL_JZDW, mSJBHInfo.TBL_JZDW);
            mContentValues.put(TBL_JZR, mSJBHInfo.TBL_JZR);
            mContentValues.put(TBL_JZBH, mSJBHInfo.TBL_JZBH);
            mContentValues.put(TBL_GCID, mSJBHInfo.TBL_GCID);
            mContentValues.put(TBL_GCDM, mSJBHInfo.TBL_GCDM);
            mContentValues.put(TBL_YPBH, mSJBHInfo.TBL_YPBH);
            mContentValues.put(TBL_JCJG, mSJBHInfo.TBL_JCJG);
            mContentValues.put(TBL_JCresult, mSJBHInfo.TBL_JCresult);
            mContentValues.put(TBL_JCbfb, mSJBHInfo.TBL_JCbfb);
            mContentValues.put(TBL_STATE, State);
            mContentValues.put(TBL_SYRQ, mSJBHInfo.TBL_SYRQ);
            mContentValues.put(TBL_SYJG, mSJBHInfo.TBL_SYJG);
            mSQLiteDatabase.insert(TBL_NAME, null, mContentValues);
        }
        c.close();
        return ret;
    }

    public int Delete(String SJBH){
        mSQLiteDatabase = getWritableDatabase();
        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{SJBH};
        return mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public boolean Sync(SJBHInfo mSJBHInfo){

        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TBL_YPLX, mSJBHInfo.TBL_YPLX);
        mContentValues.put(TBL_GCMC, mSJBHInfo.TBL_GCMC);
        mContentValues.put(TBL_GJBW, mSJBHInfo.TBL_GJBW);
        mContentValues.put(TBL_QDDJ, mSJBHInfo.TBL_QDDJ);
        mContentValues.put(TBL_YHFS, mSJBHInfo.TBL_YHFS);
        mContentValues.put(TBL_ZZRQ, mSJBHInfo.TBL_ZZRQ);
        mContentValues.put(TBL_PHBBH, mSJBHInfo.TBL_PHBBH);
        mContentValues.put(TBL_SCLSH, mSJBHInfo.TBL_SCLSH);
        mContentValues.put(TBL_BZDW, mSJBHInfo.TBL_BZDW);
        mContentValues.put(TBL_WTDW, mSJBHInfo.TBL_WTDW);
        mContentValues.put(TBL_SGDW, mSJBHInfo.TBL_SGDW);
        mContentValues.put(TBL_JZDW, mSJBHInfo.TBL_JZDW);
        mContentValues.put(TBL_JZR, mSJBHInfo.TBL_JZR);
        mContentValues.put(TBL_JZBH, mSJBHInfo.TBL_JZBH);
        mContentValues.put(TBL_GCID, mSJBHInfo.TBL_GCID);
        mContentValues.put(TBL_GCDM, mSJBHInfo.TBL_GCDM);
        mContentValues.put(TBL_YPBH, mSJBHInfo.TBL_YPBH);
        mContentValues.put(TBL_JCJG, mSJBHInfo.TBL_JCJG);
        mContentValues.put(TBL_JCresult, mSJBHInfo.TBL_JCresult);
        mContentValues.put(TBL_JCbfb, mSJBHInfo.TBL_JCbfb);
        mContentValues.put(TBL_SYRQ, mSJBHInfo.TBL_SYRQ);
        mContentValues.put(TBL_SYJG, mSJBHInfo.TBL_SYJG);

        String selection = TBL_SJBH+"=?";
        String[] selectionArgs = new String[]{ mSJBHInfo.TBL_SJBH};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);

        return true;
    }

}
