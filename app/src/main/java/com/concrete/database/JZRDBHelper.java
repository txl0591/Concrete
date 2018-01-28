package com.concrete.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.concrete.type.JzrInfo;

import java.io.File;
import java.io.IOException;

import static com.concrete.common.IntentDef.DEFAULT_PATH;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class JZRDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "JZInfo.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TBL_NAME = "JZInfo";

    public static final String TBL_ID = "ID";
    public static final String JZDW_ID = "JZDW_ID";
    public static final String JZDW = "JZDW";
    public static final String JZR = "JZR";
    public static final String JZH = "JZH";

    private static final String CREATE_TBL = " create table " + TBL_NAME
            + "(" + TBL_ID + " INTEGER primary key autoincrement,"
            + JZDW_ID + " varchar(50),"
            + JZDW + " varchar(50),"
            + JZR + " varchar(20),"
            + JZH+" varchar(20))";

    private SQLiteDatabase mSQLiteDatabase = null;
    private Context mContext = null;
    public static final String dbPath = DEFAULT_PATH;
    public static final String DB_NAME = dbPath + "/" + DATABASE_NAME;

    public JZRDBHelper(Context context) {
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
            mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbf, null);
            return mSQLiteDatabase;
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

    public Cursor Query(String Jzh,String Jzr, String Jzdw){
        mSQLiteDatabase = getWritableDatabase();
        String selection = JZH+"=?"+" and "+ JZR+"=?"+" and "+ JZDW+"=?";
        String[] selectionArgs = new String[]{Jzh, Jzr, Jzdw};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor Query(String Id){
        mSQLiteDatabase = getWritableDatabase();
        String selection = JZDW_ID+"=?";
        String[] selectionArgs = new String[]{Id};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor Query(){
        mSQLiteDatabase = getWritableDatabase();
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, null, null, null, null, null);
        return c;
    }

    public Cursor QueryJZDW(String Id){
        mSQLiteDatabase = getWritableDatabase();
        String selection = JZDW+" LIKE ?";
        String[] selectionArgs = new String[]{"%" + Id + "%"};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public void Delete(String ID){
        mSQLiteDatabase = getWritableDatabase();
        String selection = JZDW_ID+"=?";
        String[] selectionArgs = new String[]{ID};
        mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public boolean UpdateJzrInfo(JzrInfo mJzrInfo){
        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }
        ContentValues mContentValues = new ContentValues();

        mContentValues.put(JZDW_ID, mJzrInfo.JZDW_ID);
        mContentValues.put(JZDW, mJzrInfo.JZDW);
        mContentValues.put(JZR, mJzrInfo.JZR);
        mContentValues.put(JZH, mJzrInfo.JZH);

        String selection = JZDW_ID+"=?";
        String[] selectionArgs = new String[]{mJzrInfo.JZDW_ID};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
        return true;
    }

    public boolean InsertJzrInfo(JzrInfo mJzrInfo){

        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }

        Cursor mCursor = Query(mJzrInfo.JZDW_ID);
        if(mCursor.getCount() > 0){
            UpdateJzrInfo(mJzrInfo);
        }
        else{
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(JZDW_ID, mJzrInfo.JZDW_ID);
            mContentValues.put(JZDW, mJzrInfo.JZDW);
            mContentValues.put(JZR, mJzrInfo.JZR);
            mContentValues.put(JZH, mJzrInfo.JZH);
            mSQLiteDatabase.insert(TBL_NAME, null, mContentValues);
        }

        mCursor.close();
        return true;
    }
}
