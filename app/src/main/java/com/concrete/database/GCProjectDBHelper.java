package com.concrete.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.concrete.common.nlog;
import com.concrete.type.PrjectInfo;

import java.io.File;
import java.io.IOException;

import static com.concrete.common.IntentDef.DEFAULT_PATH;

/**
 * Created by Tangxl on 2018/1/6.
 */

public class GCProjectDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GCProject.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TBL_NAME = "GCProject";

    public static final String TBL_ID = "ID";
    public static final String project_UUID = "project_UUID";
    public static final String project_id = "project_id";
    public static final String dcPK = "dcPK";
    public static final String compact_type = "compact_type";
    public static final String project_code = "project_code";
    public static final String project_info = "project";
    public static final String check_unit_id = "check_unit_id";
    public static final String consCorpNames = "consCorpNames";
    public static final String superCorpNames = "superCorpNames";
    public static final String corpname = "corpname";
    public static final String check_unit = "check_unit";
    public static final String consCorp_id = "consCorp_id";
    public static final String superCorp_id = "superCorp_id";
    public static final String corpcode = "corpcode";
    public static final String createDate = "createDate";
    public static final String updatetime = "updatetime";
    public static final String district_id = "district_id";
    public static final String safety_id = "safety_id";
    public static final String gongchengmianji = "gongchengmianji";
    public static final String touzijinne = "touzijinne";


    private static final String CREATE_TBL = " create table " + TBL_NAME
            + "(" + TBL_ID + " INTEGER primary key autoincrement,"
            + project_UUID + " varchar(100),"
            + project_id + " varchar(100),"
            + dcPK + " varchar(100),"
            + compact_type+" varchar(100),"
            + project_code + " varchar(100),"
            + project_info + " varchar(200),"
            + check_unit_id + " varchar(50),"
            + consCorpNames + " varchar(500),"
            + superCorpNames + " varchar(100),"
            + corpname + " varchar(200),"
            + check_unit + " varchar(200),"
            + consCorp_id + " varchar(100),"
            + superCorp_id + " varchar(100),"
            + corpcode + " varchar(100),"
            + createDate + " varchar(100),"
            + updatetime + " varchar(100),"
            + district_id + " varchar(100),"
            + safety_id + " varchar(100),"
            + gongchengmianji + " varchar(100),"
            + touzijinne+" varchar(100))";

    private SQLiteDatabase mSQLiteDatabase = null;
    private SQLiteDatabase mSQLiteLocal = null;
    private Context mContext = null;
    public static final String dbPath = DEFAULT_PATH;
    public static final String DB_NAME = dbPath + "/" + DATABASE_NAME;

    public GCProjectDBHelper(Context context) {
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

    public Cursor Query(String project1,String check_unit1, String consCorpNames1){
        mSQLiteDatabase = getWritableDatabase();
        String selection = project_info+"=?"+" and "+ check_unit+"=?"+" and "+ consCorpNames+"=?";
        String[] selectionArgs = new String[]{project1, check_unit1, consCorpNames1};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor Query(String UUID){
        mSQLiteDatabase = getWritableDatabase();
        String selection = project_UUID+"=?";
        String[] selectionArgs = new String[]{UUID};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor QueryFromGCMC(String GCMC){
        mSQLiteDatabase = getWritableDatabase();
        String selection = project_info+" LIKE ?";
        String[] selectionArgs = new String[]{"%" + GCMC + "%"};
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        return c;
    }

    public Cursor Query(){
        mSQLiteDatabase = getWritableDatabase();
        Cursor c = mSQLiteDatabase.query(TBL_NAME, null, null, null, null, null, null);
        return c;
    }

    public void Delete(String UUID){
        mSQLiteDatabase = getWritableDatabase();
        String selection = project_UUID+"=?";
        String[] selectionArgs = new String[]{UUID};
        mSQLiteDatabase.delete(TBL_NAME, selection, selectionArgs);
    }

    public boolean UpdatePrjectInfo(PrjectInfo mPrjectInfo){
        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }
        ContentValues mContentValues = new ContentValues();

        mContentValues.put(project_UUID, mPrjectInfo.project_UUID);
        mContentValues.put(project_id, mPrjectInfo.project_id);
        mContentValues.put(dcPK, mPrjectInfo.dcPK);
        mContentValues.put(compact_type, mPrjectInfo.compact_type);
        mContentValues.put(project_code, mPrjectInfo.project_code);
        mContentValues.put(project_info, mPrjectInfo.project);
        mContentValues.put(check_unit_id, mPrjectInfo.check_unit_id);
        mContentValues.put(consCorpNames, mPrjectInfo.consCorpNames);
        mContentValues.put(superCorpNames, mPrjectInfo.superCorpNames);
        mContentValues.put(corpname, mPrjectInfo.corpname);
        mContentValues.put(check_unit, mPrjectInfo.check_unit);
        mContentValues.put(consCorp_id, mPrjectInfo.consCorp_id);
        mContentValues.put(superCorp_id, mPrjectInfo.superCorp_id);
        mContentValues.put(corpcode, mPrjectInfo.corpcode);
        mContentValues.put(createDate, mPrjectInfo.createDate);
        mContentValues.put(updatetime, mPrjectInfo.updatetime);
        mContentValues.put(district_id, mPrjectInfo.district_id);
        mContentValues.put(safety_id, mPrjectInfo.safety_id);
        mContentValues.put(gongchengmianji, mPrjectInfo.gongchengmianji);
        mContentValues.put(touzijinne, mPrjectInfo.touzijinne);

        String selection = project_UUID+"=?";
        String[] selectionArgs = new String[]{mPrjectInfo.project_UUID};
        mSQLiteDatabase.update(TBL_NAME, mContentValues, selection, selectionArgs);
        return true;
    }

    public boolean InsertPrjectInfo(PrjectInfo mPrjectInfo){

        mSQLiteDatabase = getWritableDatabase();
        if(mSQLiteDatabase == null){
            return false;
        }

        Cursor mCursor = Query(mPrjectInfo.project_UUID);
        nlog.Info("InsertPrjectInfo =============== ["+mCursor.getCount()+"]");
        if(mCursor.getCount() > 0){
            UpdatePrjectInfo(mPrjectInfo);
        }
        else{
            ContentValues mContentValues = new ContentValues();

            mContentValues.put(project_UUID, mPrjectInfo.project_UUID);
            mContentValues.put(project_id, mPrjectInfo.project_id);
            mContentValues.put(dcPK, mPrjectInfo.dcPK);
            mContentValues.put(compact_type, mPrjectInfo.compact_type);
            mContentValues.put(project_code, mPrjectInfo.project_code);
            mContentValues.put(project_info, mPrjectInfo.project);
            mContentValues.put(check_unit_id, mPrjectInfo.check_unit_id);
            mContentValues.put(consCorpNames, mPrjectInfo.consCorpNames);
            mContentValues.put(superCorpNames, mPrjectInfo.superCorpNames);
            mContentValues.put(corpname, mPrjectInfo.corpname);
            mContentValues.put(check_unit, mPrjectInfo.check_unit);
            mContentValues.put(consCorp_id, mPrjectInfo.consCorp_id);
            mContentValues.put(superCorp_id, mPrjectInfo.superCorp_id);
            mContentValues.put(corpcode, mPrjectInfo.corpcode);
            mContentValues.put(createDate, mPrjectInfo.createDate);
            mContentValues.put(updatetime, mPrjectInfo.updatetime);
            mContentValues.put(district_id, mPrjectInfo.district_id);
            mContentValues.put(safety_id, mPrjectInfo.safety_id);
            mContentValues.put(gongchengmianji, mPrjectInfo.gongchengmianji);
            mContentValues.put(touzijinne, mPrjectInfo.touzijinne);
            mSQLiteDatabase.insert(TBL_NAME, null, mContentValues);
        }

        mCursor.close();
        return true;
    }
}
