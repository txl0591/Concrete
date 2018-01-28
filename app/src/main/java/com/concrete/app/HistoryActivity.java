package com.concrete.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.concrete.common.Common;
import com.concrete.common.nlog;
import com.concrete.ctrl.CommonBase;
import com.concrete.ctrl.HistoryAdapter;
import com.concrete.ctrl.HistoryItem;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.ctrl.ListAdapter;
import com.concrete.ctrl.ListCtrl;
import com.concrete.ctrl.ListCtrlAdapter;
import com.concrete.ctrl.ListItem;
import com.concrete.database.SJBHDBHelper;
import com.concrete.logic.CommonLoigic;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpDef;
import com.concrete.net.HttpDownload;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.SJBHInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Tangxl on 2017/12/17.
 */

public class HistoryActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final int HANDLER_SPIN_START = 0xAB11;
    private static final int HANDLER_SPIN_STOP = 0xAB12;
    private static final int HANDLER_SHOW_DAILOG = 0xAB13;

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private HttpLogic mLoigc = null;
    private Handler mHandler = null;
    private HistoryAdapter mHistoryAdapter = null;
    private int mIndex = -1;
    private SqliteLogic mSqliteLogic = null;
    private ArrayList<HistoryInfo> mSJBHList = new ArrayList<HistoryInfo>();
    private HttpHandlerEvent mHttpHandlerEvent = null;
    private HttpBroadCast mHttpBroadCast = null;
    private HttpLogic mHttpLogic = null;
    private String mSJBH = null;
    private CommonBase mCommonBase = null;
    private final static int LISTWidth[] = {
            700, 300, 450};

    private ListCtrl mListView = null;
    private ListCtrl mListTop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mListView = (ListCtrl)findViewById(R.id.BrowerCard);
        mListView.setOnItemClickListener(this);

        mListTop = (ListCtrl)findViewById(R.id.BrowerTop);

        mLoigc = new HttpLogic(this);
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.left_menu);

        mLeftMenuFragment = new LeftMenuFragment(this,0);
        getFragmentManager().beginTransaction()
                .add(R.id.left_menuconfig, mLeftMenuFragment)
                .commit();


        mCommonBase = new CommonBase(this);
        mSqliteLogic = new SqliteLogic(this);

        mHttpHandlerEvent = new HttpHandlerEvent(this);
        mHttpBroadCast = new HttpBroadCast(this,mHttpHandlerEvent);
        mHttpLogic = new HttpLogic(this);
        LoadTop();
        LoadList(Common.getData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
            String Date = mYear+"-"+mMonth+"-"+mDay;
            LoadList(Date);
        }
    };

    private void ShowTimeChoose(){
        Calendar ca = Calendar.getInstance();
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, onDateSetListener, mYear, mMonth, mDay).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                ShowTimeChoose();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSlidingMenu.showContent();
        mHttpBroadCast.Start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHttpBroadCast.Close();
        if (mSlidingMenu.isMenuShowing())
            mSlidingMenu.toggle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoigc.Close();
        mHttpBroadCast.Close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SyncInfo(mSJBHList.get(i).SJBH);
    }

    public void DeleteAlertDialog(int j){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.hit_sure))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(getResources().getString(R.string.toast_hit_delete_record))
                .setPositiveButton(getResources().getString(R.string.hit_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mIndex = -1;
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.hit_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mIndex = -1;
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    public void LoadTop(){
        int TextId[] = {R.string.pref_card_gjbw, R.string.pref_card_qddj,R.string.pref_card_zzrq};
        String nString[] = {
                null,null,null,
        };

        int nGravity[] = {
                Gravity.CENTER_VERTICAL | Gravity.CENTER,
                Gravity.CENTER_VERTICAL | Gravity.CENTER,
                Gravity.CENTER_VERTICAL | Gravity.CENTER,
        };

        ListCtrlAdapter adapter = (ListCtrlAdapter) new ListCtrlAdapter(this);
        adapter.ListCtrlCreate(ListCtrlAdapter.ListType1, mListTop);
        adapter.ListCtrlAdd(TextId, nString, LISTWidth, nGravity);
        mListTop.setAdapter(adapter);
    }

    public void LoadList(String Date){
        int TextId[] = { 0, 0, 0};
        String nString[] = {
                null,//构件部位
                null,//强度等级
                null,//制作日期
        };

        int nGravity[] = {
                Gravity.CENTER_VERTICAL | Gravity.CENTER,
                Gravity.CENTER_VERTICAL | Gravity.CENTER,
                Gravity.CENTER_VERTICAL | Gravity.CENTER,
        };

        ListCtrlAdapter adapter = (ListCtrlAdapter) new ListCtrlAdapter(this);
        adapter.ListCtrlCreate(ListCtrlAdapter.ListType2, mListView);

        int j = 0;
        if(!mSJBHList.isEmpty()){
            mSJBHList.clear();
        }
        Cursor mCursor = mSqliteLogic.GetSJBHDBHelper().Query(Date);
        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                HistoryInfo mHistoryInfo =  new HistoryInfo(mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_SJBH)),
                        mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_QDDJ)),
                        mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_ZZRQ)),
                        mCursor.getString(mCursor.getColumnIndex(SJBHDBHelper.TBL_GJBW)));
                mSJBHList.add(j,mHistoryInfo);
                j++;
            }

        }

        for(int i = 0 ; i < mSJBHList.size(); i++){
            nString[0] = mSJBHList.get(i).GJBW;
            nString[1] = mSJBHList.get(i).QDDJ;
            nString[2] = mSJBHList.get(i).ZZRQ;
            adapter.ListCtrlAdd(TextId, nString, LISTWidth, nGravity);
        }
        mListView.setAdapter(adapter);
    }

    private void UpdateSJBH(String SJBH){
        ShowInfoFromDB(SJBH);
    }

    private void SyncInfo(String SJBH){
        mSJBH = SJBH;
        new SyncSJBHThread(this).start();
    }

    private void ShowInfoFromDB(String SJBH){
        SJBHInfo mSJBHInfo = mSqliteLogic.QuerySJBH(SJBH);
        ArrayList<ChipInfo> mChipInfoOper = mSqliteLogic.QureyRFIDFromSJBH(SJBH);
        mCommonBase.ShowSJBHInfo(mSJBHInfo,mChipInfoOper,IsEnableEdit(mChipInfoOper),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StartAddActivity();
            }
        } );
    }

    private void StartAddActivity(){
        Intent intent= new Intent(this, AddCardActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt(AddCardActivity.SC_MODE,AddCardActivity.SG_MODE_EDIT);
        bundle.putString(AddCardActivity.SC_MODE_PARAM,mSJBH);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean IsEnableEdit(ArrayList<ChipInfo> mChipInfoOper){
        boolean ret = true;
        for(int i = 0; i < mChipInfoOper.size(); i++){
            ChipInfo nChipInfo = mChipInfoOper.get(i);
            if(!nChipInfo.TBL_SYRQ.equals(HttpDef.UNKNOW) || !nChipInfo.TBL_SYJG.equals(HttpDef.UNKNOW)){
                ret = false;
                break;
            }
        }
        return ret;
    }

    class SyncSJBHThread extends Thread{
        private Context mContext = null;

        public SyncSJBHThread(Context context){
            mContext = context;
        }

        @Override
        public void run() {
            super.run();
            mHttpHandlerEvent.sendEmptyMessage(HANDLER_SPIN_START);
            CommonLoigic.SyncGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBH);
            CommonLoigic.SyncRFIDFromSJBH(mContext,mSqliteLogic,mHttpLogic,mSJBH);
            mHttpHandlerEvent.sendEmptyMessage(HANDLER_SPIN_STOP);
            mHttpHandlerEvent.sendEmptyMessage(HANDLER_SHOW_DAILOG);
        }
    }

    class HttpHandlerEvent extends Handler {
        private Context mContext;

        public HttpHandlerEvent(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SPIN_START:
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_sync_wait);
                    break;

                case HANDLER_SPIN_STOP:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_SHOW_DAILOG:
                    UpdateSJBH(mSJBH);
                    break;

                default:
                    break;
            }
        }
    }

    class HistoryInfo {
        public String SJBH;
        public String GJBW;
        public String QDDJ;
        public String ZZRQ;

        public HistoryInfo(String SJBH, String QDDJ, String ZZRQ, String GJBW){
            this.QDDJ = QDDJ;
            this.SJBH = SJBH;
            this.ZZRQ = ZZRQ;
            this.GJBW = GJBW;
        }
    }
}
