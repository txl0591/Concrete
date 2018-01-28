package com.concrete.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.concrete.type.ImageInfo;

import java.io.File;
import java.io.IOException;

import static com.concrete.common.IntentDef.DEFAULT_PATH;

/**
 * Created by Tangxl on 2018/1/13.
 */

public class ImageDBHelp extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ImageInfo.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TBL_NAME = "ImageDataBase";

    public static final String TBL_ID = "ID";

    public static final String cIMG_UUID  = "cIMG_UUID";
    public static final String SKs_UUid  = "SKs_UUid";
    public static final String cfilehouzui  = "cfilehouzui";
    public static final String imgFile  = "imgFile";
    public static final String TBL_STATE  = "TBL_STATE";

    private static final String CREATE_TBL = " create table " + TBL_NAME
            + "(" + TBL_ID + " INTEGER primary key autoincrement,"
            + cIMG_UUID + " varchar(50),"
            + SKs_UUid + " varchar(50),"
            + cfilehouzui + " cfilehouzui(50),"
            + imgFile + " imgFile(512),"
            + TBL_STATE + " char)";

    private SQLiteDatabase mSQLiteDatabase = null;
    private SQLiteDatabase mSQLiteLocal = null;
    private Context mContext = null;
    public static final String dbPath = DEFAULT_PATH;
    public static final String DB_NAME = dbPath + "/" + DATABASE_NAME;

    public ImageDBHelp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub

        if(null != mSQLiteLocal){
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

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void Close(){
        if (mSQLiteDatabase != null){
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
    }

    public void Delete(String Sks_uuid){
        mSQLiteDatabase = getWritableDatabase();
        String selection = SKs_UUid+"=?";
        String[] selectionArgs = new String[]{Sks_uuid};
        mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public Cursor QueryImage(String uuid){
        mSQLiteDatabase = getWritableDatabase();
        String selection = SKs_UUid+"=?";
        String[] selectionArgs = new String[]{uuid};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public boolean UpdateImage(ImageInfo mImageInfo){
        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }
        ContentValues mContentValues = new ContentValues();


        mContentValues.put(cIMG_UUID, mImageInfo.cIMG_UUID);
        mContentValues.put(SKs_UUid, mImageInfo.SKs_UUid);
        mContentValues.put(cfilehouzui, mImageInfo.cfilehouzui);
        mContentValues.put(imgFile, mImageInfo.imgFile );
        mContentValues.put(TBL_STATE, mImageInfo.TBL_STATE );

        String selection = SKs_UUid+"=?";
        String[] selectionArgs = new String[]{mImageInfo.SKs_UUid};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
        return true;
    }

    public boolean InsertImage(ImageInfo mImageInfo){
        mSQLiteDatabase = getWritableDatabase();

        if(mSQLiteDatabase == null){
            return false;
        }

        Cursor mCursor = QueryImage(mImageInfo.SKs_UUid);
        if(mCursor.getCount() > 0){
            UpdateImage(mImageInfo);
        }
        else{
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(cIMG_UUID, mImageInfo.cIMG_UUID);
            mContentValues.put(SKs_UUid, mImageInfo.SKs_UUid);
            mContentValues.put(cfilehouzui, mImageInfo.cfilehouzui);
            mContentValues.put(imgFile, mImageInfo.imgFile );
            mContentValues.put(TBL_STATE, mImageInfo.TBL_STATE );
            mSQLiteDatabase.insert(TBL_NAME, null, mContentValues);
        }

        mCursor.close();
        return true;
    }
}
