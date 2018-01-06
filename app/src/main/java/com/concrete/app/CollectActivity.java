package com.concrete.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.concrete.common.CardManager;
import com.concrete.common.Common;
import com.concrete.common.nlog;
import com.concrete.ctrl.CommonBase;
import com.concrete.ctrl.HistoryAdapter;
import com.concrete.ctrl.HistoryItem;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.database.CardCollectDBHelper;
import com.concrete.database.CardDBHelper;
import com.concrete.logic.CommonLoigic;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.UserInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import static com.concrete.common.Common.ByteArrayToHexString;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;

/**
 * Created by Tangxl on 2017/12/26.
 */

public class CollectActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    private static final int HANDLER_ADDCARD = 0xF1F1;
    private static final int HANDLER_INIT_UI = 0xF1F2;
    private static final int HANDLER_DAILOG_SHOW = 0xF1F3;
    private static final int HANDLER_DAILOG_HIDE = 0xF1F4;
    private static final int HANDLER_FLASH_LIST = 0xF1F5;

    private TextView BigTextView = null;
    private ListView ColletList = null;
    private Button mUpload = null;
    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private HandlerEvent mHandlerEvent = null;
    private long mCardNum = 0;
    private SqliteLogic mSqliteLogic = null;
    private ArrayList<RFIDInfo> mRFIDList = new ArrayList<RFIDInfo>();
    private HistoryAdapter mHistoryAdapter = null;
    private ProgressDialog mProgressDialog = null;
    private AlertDialog.Builder mAlertDialogEdit = null;
    private int mIndex = -1;
    private HttpLogic mHttpLogic = null;
    private HttpBroadCast mHttpBroadCast = null;
    private ImageButton mUploadButton = null;
    private CommonBase mCommonBase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);

        mCommonBase = new CommonBase(this);

        mUploadButton = findViewById(R.id.Upload_Button);
        mUploadButton.setOnClickListener(this);
        mUploadButton.setBackgroundColor(getResources().getColor(R.color.touming));
        mUploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    view.setBackgroundColor(getResources().getColor(R.color.common_bg_highlight));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    view.setBackgroundColor(getResources().getColor(R.color.touming));
                }
                return false;
            }
        });

        BigTextView = findViewById(R.id.BigTextView);
        BigTextView.setText("RFID");
        ColletList = findViewById(R.id.ColletList);
        ColletList.setOnItemLongClickListener(this);
        ColletList.setOnItemClickListener(this);
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
        InitNFCLocal();
        mHandlerEvent.sendEmptyMessageDelayed(HANDLER_INIT_UI,500);
        mHttpLogic = new HttpLogic(this);
        mHttpBroadCast = new HttpBroadCast(this,mHandlerEvent);
    }

    private void InitNFCLocal(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(null != mNfcAdapter){
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        }
        if(false == Common.IsNfcOpen(this)){
            new  AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage(getResources().getString(R.string.toast_hit_nfc_close))
                    .setPositiveButton(getResources().getString(R.string.hit_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent("android.settings.NFC_SETTINGS"));
                        }
                    })
                    .show();
        }
    }

    private void DisableNFC(){
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    private void EnableNFC(){
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, CardManager.FILTERS, CardManager.TECHLISTS);
    }

    private void LoadList(){
        ArrayList<HistoryItem> mHistoryItemList = new ArrayList<HistoryItem>();
        int ImageId = 0;
        int index = 0;

        for(int i = mRFIDList.size(); i > 0 ; i--){
            if(mRFIDList.get(i-1).State == 1 && mRFIDList.get(i-1).Upload == 0)
            {
                ImageId = R.drawable.upload;
            }else{
                ImageId = 0;
            }

            HistoryItem mHistoryItem = new HistoryItem(ImageId,getResources().getString(R.string.pref_card_card)+": "+mRFIDList.get(i-1).RFID);
            mHistoryItemList.add(index++,mHistoryItem);
        }
        mHistoryAdapter = new HistoryAdapter(mHistoryItemList,this);
        ColletList.setAdapter(mHistoryAdapter);
    }

    private void UpdateList(boolean force){
        ArrayList<HistoryItem> mHistoryItemList = new ArrayList<HistoryItem>();
        int ImageId = 0;
        int index = 0;
        if(mRFIDList.size() > 0){
            for(int i = mRFIDList.size(); i > 0 ; i--){
                if(mRFIDList.get(i-1).State == 1 && mRFIDList.get(i-1).Upload == 0)
                {
                    ImageId = R.drawable.upload;
                }else{
                    ImageId = 0;
                }

                HistoryItem mHistoryItem = new HistoryItem(ImageId,getResources().getString(R.string.pref_card_card)+": "+mRFIDList.get(i-1).RFID);
                mHistoryItemList.add(index++,mHistoryItem);
            }
        }
        mHistoryAdapter.UpdateListData(ColletList,mHistoryItemList,force);

    }

    private void LoadData(){
        if(!mRFIDList.isEmpty()){
            mRFIDList.clear();
        }

        Cursor mCursor = mSqliteLogic.GetCollectCardDBHelper().Query();
        int index = 0;
        int State = 0;

        if(mCursor.getCount() > 0){
            while(mCursor.moveToNext()){
                State = mCursor.getInt(mCursor.getColumnIndex(CardDBHelper.TBL_STATE));
                if(State > 0){
                    RFIDInfo nRFIDInfo = new RFIDInfo(
                            mCursor.getString(mCursor.getColumnIndex(CardCollectDBHelper.RFID)),
                            mCursor.getInt(mCursor.getColumnIndex(CardCollectDBHelper.TBL_UPLOAD)),
                             mCursor.getInt(mCursor.getColumnIndex(CardCollectDBHelper.TBL_STATE)),
                            mCursor.getInt(mCursor.getColumnIndex(CardCollectDBHelper.TBL_UPLOAD_ECHO)));
                    mRFIDList.add(index++, nRFIDInfo);
                }

            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        EnableNFC();
        mHttpBroadCast.Start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DisableNFC();
        mHttpBroadCast.Close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSqliteLogic.Close();
        mHttpLogic.Close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String str = ByteArrayToHexString(tag.getId());
            mCardNum = Long.parseLong(str, 16);
            Message msg = new Message();
            msg.what = HANDLER_ADDCARD;
            Bundle mBundle = new Bundle();
            mBundle.putString("RFID",String.valueOf(mCardNum));
            msg.setData(mBundle);
            mHandlerEvent.sendMessage(msg);
        }
    }

    private void AddCard(String Id){
        BigTextView.setText(Id);
        int ret = mSqliteLogic.CollectChipInfo(Id);
        if(ret > 0){
            RFIDInfo mRFIDInfo = new RFIDInfo(Id, 0, 1, 0);
            mRFIDList.add(mRFIDList.size(),mRFIDInfo);
            UpdateList(true);
        }else if(ret == 0){
            Toast.makeText(this,getResources().getText(R.string.toast_hit_same_id),Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadCard(){
        new UploadTHread(this).start();
    }

    public void ShowUpload(){
        mAlertDialogEdit =  new AlertDialog.Builder(this);
        mAlertDialogEdit.setMessage(getResources().getString(R.string.toast_hit_upload));
        mAlertDialogEdit.setNegativeButton(R.string.hit_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UploadCard();
            }
        });

        mAlertDialogEdit.setPositiveButton(R.string.hit_cancle,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();;
            }
        });

        mAlertDialogEdit.show();
    }

    public void delete_card(){
        int Real = mRFIDList.size()-1-mIndex;
        RFIDInfo nRFIDInfo = mRFIDList.get(Real);
        mSqliteLogic.DelteCollectRFID(nRFIDInfo.RFID);
        mRFIDList.remove(nRFIDInfo);
        UpdateList(false);
    }

    public void showdelete(int index){
        if(mRFIDList.size() > 0)
            mIndex = index;
            mAlertDialogEdit =  new AlertDialog.Builder(this);
            mAlertDialogEdit.setMessage(getResources().getString(R.string.toast_hit_delete_card));
            mAlertDialogEdit.setNegativeButton(R.string.hit_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete_card();
                    dialog.dismiss();;
                }
            });

            mAlertDialogEdit.setPositiveButton(R.string.hit_cancle,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();;
                }
            });

            mAlertDialogEdit.show();
        }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showdelete(i);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.Upload_Button:
                if(mRFIDList.size() > 0) {
                    ShowUpload();
                }
                break;
        }
    }

    class HandlerEvent extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case HANDLER_ADDCARD:
                    AddCard(msg.getData().getString("RFID"));
                    break;

                case HANDLER_INIT_UI:
                    LoadData();
                    LoadList();
                    break;

                case HANDLER_DAILOG_SHOW :
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_upload_wait);
                    break;

                case HANDLER_DAILOG_HIDE:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_FLASH_LIST:
                    UpdateList(true);
                    break;
            }
        }
    }

    class RFIDInfo {
        public String RFID;
        public int Upload;
        public int State;
        public int Echo;

        public RFIDInfo(String RFID,int Upload, int State, int Echo){
            this.RFID = RFID;
            this.Upload = Upload;
            this.State = State;
            this.Echo = Echo;
        }
    }

    class UploadTHread extends Thread{

        public boolean UploadTHreadRun = true;
        public Context mContext = null;

        public UploadTHread(Context context){
            mContext = context;
        }

        @Override
        public void run() {
            super.run();
            mHandlerEvent.sendEmptyMessage(HANDLER_DAILOG_SHOW);
            for(int i = 0; i < mRFIDList.size(); i++){
                RFIDInfo nRFIDInfo = mRFIDList.get(i);
                ChipInfo mChipInfo = mHttpLogic.QueryRFIDBlock(nRFIDInfo.RFID);
                if(null != mChipInfo){
                    mChipInfo.TBL_SYJG = UserInfo.getInstance(mContext).GetUserDanWei();
                    mChipInfo.TBL_SYRQ = Common.getData();
                    mSqliteLogic.UpdateCollectRFID(mChipInfo);

                    ArrayList<ChipInfo> item = new ArrayList<ChipInfo>();
                    item.add(0,mChipInfo);
                    if(mHttpLogic.OperRFIDBlock(HANDLE_HTTP_UPDATE_RFID,item,UserInfo.getInstance(mContext).GetUserInfo())){
                        nRFIDInfo.Upload = 1;
                        nRFIDInfo.Echo = 0;
                        mRFIDList.set(i,nRFIDInfo);
                        mSqliteLogic.UpdateCollectState(nRFIDInfo.RFID,1, 0, 1);
                        CommonLoigic.UpdateSYRQInSJBHTable(mContext,mChipInfo.TBL_SJBH, mSqliteLogic, mHttpLogic,mChipInfo.TBL_SYRQ,mChipInfo.TBL_SYJG);
                    }
                    else
                    {
                        nRFIDInfo.Upload = 1;
                        nRFIDInfo.Echo = 1;
                        mRFIDList.set(i,nRFIDInfo);
                        mSqliteLogic.UpdateCollectState(nRFIDInfo.RFID,1, 1, 0);
                    }



                }else{
                    nRFIDInfo.Upload = 1;
                    nRFIDInfo.Echo = 1;
                    mRFIDList.set(i,nRFIDInfo);
                    mSqliteLogic.UpdateCollectState(nRFIDInfo.RFID,1, 1, 0);
                }
            }
                mHandlerEvent.sendEmptyMessage(HANDLER_DAILOG_HIDE);
                mHandlerEvent.sendEmptyMessage(HANDLER_FLASH_LIST);
        }
    }

}
