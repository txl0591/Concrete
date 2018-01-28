package com.concrete.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.concrete.common.Common;
import com.concrete.ctrl.CommonBase;
import com.concrete.ctrl.HistoryAdapter;
import com.concrete.ctrl.HistoryItem;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.ctrl.ListCtrl;
import com.concrete.ctrl.ListCtrlAdapter;
import com.concrete.database.CardCollectDBHelper;
import com.concrete.database.SJBHDBHelper;
import com.concrete.logic.CommonLoigic;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.SJBHInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Tangxl on 2017/12/31.
 */

public class CollectHistory extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final int HANDLER_SPIN_START = 0xAB11;
    private static final int HANDLER_SPIN_STOP = 0xAB12;
    private static final int HANDLER_SHOW_DAILOG = 0xAB13;

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private HttpLogic mLoigc = null;
    private Handler mHandler = null;
    private HistoryAdapter mHistoryAdapter = null;
    private SqliteLogic mSqliteLogic = null;
    private ArrayList<HistoryInfo> mSJBH = null;
    private CommonBase mCommonBase = null;
    private HandlerEvent mHandlerEvent = null;
    private HistoryActivity.HttpHandlerEvent mHttpHandlerEvent = null;
    private HttpLogic mHttpLogic = null;
    private String mSJBHActive = null;

    private final static int LISTWidth[] = {
            700, 300, 450};

    private ListCtrl mListView = null;
    private ListCtrl mListTop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mCommonBase = new CommonBase(this);

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
        mSqliteLogic = new SqliteLogic(this);
        mHandlerEvent = new HandlerEvent();
        mHttpLogic = new HttpLogic(this);
        mSJBH = new ArrayList<HistoryInfo>();
        LoadTop();
        LoadData(Common.getData());
        loadlist();
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
            LoadData(Date);
            loadlist();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSlidingMenu.isMenuShowing())
            mSlidingMenu.toggle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSqliteLogic.Close();
        mLoigc.Close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ShowSJBHInfo(mSJBH.get(i).SJBH);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    public boolean isInSJBH(String SJBH){
        boolean ret = false;
        for(int i = 0; i < mSJBH.size(); i++){
            if(mSJBH.get(i).SJBH.equals(SJBH)){
                ret = true;
                break;
            }
        }
        return ret;
    }

    public void LoadTop(){
        int TextId[] = {R.string.pref_card_gcmc, R.string.pref_card_qddj,R.string.pref_card_zzrq};
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

    public void LoadData(String Date){
        if(!mSJBH.isEmpty()){
            mSJBH.clear();
        }
        Cursor mCursor = mSqliteLogic.QueryByOrder(Date);
        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                String SJBH = mCursor.getString(mCursor.getColumnIndex(CardCollectDBHelper.TBL_SJBH));
                if(!isInSJBH(SJBH)){
                    SJBHInfo mSJBHInfo = mSqliteLogic.QuerySJBH(SJBH);
                    if(mSJBHInfo != null){
                        HistoryInfo mHistoryInfo = new HistoryInfo(SJBH,
                                mSJBHInfo.TBL_QDDJ, mSJBHInfo.TBL_ZZRQ, mSJBHInfo.TBL_GCMC);
                        mSJBH.add(mSJBH.size(),mHistoryInfo);
                    }

                }
            }
        }
    }

    public void loadlist(){
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

        for(int i = 0 ; i < mSJBH.size(); i++){
            nString[0] = mSJBH.get(i).GCMC;
            nString[1] = mSJBH.get(i).QDDJ;
            nString[2] = mSJBH.get(i).ZZRQ;
            adapter.ListCtrlAdd(TextId, nString, LISTWidth, nGravity);
        }
        mListView.setAdapter(adapter);
    }

    public void showeditdwindows(String sjbh){
        ArrayList<ChipInfo> mChipInfoOper =  mSqliteLogic.QureyRFIDFromSJBH(sjbh);
        SJBHInfo mSJBHInfo = mSqliteLogic.QuerySJBH(sjbh);
        mCommonBase.ShowSJBHInfo(mSJBHInfo, mChipInfoOper, false, null);
    }

    public void ShowSJBHInfo(String SJBH){
        mSJBHActive = SJBH;
        new SyncSJBHThread(this).start();
    }

    public void ShowSJBHInfoFromDB(String SJBH){
        SJBHInfo mSJBHInfo = mSqliteLogic.QuerySJBH(SJBH);
        ArrayList<ChipInfo> mChipInfoOper = mSqliteLogic.QureyRFIDFromSJBH(SJBH);
        mCommonBase.ShowSJBHInfo(mSJBHInfo,mChipInfoOper,false,null);
    }

    class SyncSJBHThread extends Thread{
        private Context mContext = null;

        public SyncSJBHThread(Context context){
            mContext = context;
        }

        @Override
        public void run() {
            super.run();
            mHandlerEvent.sendEmptyMessage(HANDLER_SPIN_START);
            CommonLoigic.SyncGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBHActive);
            CommonLoigic.SyncRFIDFromSJBH(mContext,mSqliteLogic,mHttpLogic,mSJBHActive);
            mHandlerEvent.sendEmptyMessage(HANDLER_SPIN_STOP);
            mHandlerEvent.sendEmptyMessage(HANDLER_SHOW_DAILOG);
        }
    }


    class HandlerEvent extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case HANDLER_SPIN_START:
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_sync_wait);
                    break;

                case HANDLER_SPIN_STOP:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_SHOW_DAILOG:
                    ShowSJBHInfoFromDB(mSJBHActive);
                    break;

                default:
                    break;
            }
        }
    }

    class HistoryInfo {
        public String SJBH;
        public String GCMC;
        public String QDDJ;
        public String ZZRQ;

        public HistoryInfo(String SJBH, String QDDJ, String ZZRQ, String GCMC){
            this.QDDJ = QDDJ;
            this.SJBH = SJBH;
            this.ZZRQ = ZZRQ;
            this.GCMC = GCMC;
        }
    }
}
