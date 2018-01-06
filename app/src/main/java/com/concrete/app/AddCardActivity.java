package com.concrete.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.concrete.common.CardManager;
import com.concrete.common.Common;
import com.concrete.common.FileUtil;
import com.concrete.ctrl.CommonBase;
import com.concrete.database.SysParam;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpDef;
import com.concrete.net.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.ctrl.FragmentBase;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.PrjectInfo;
import com.concrete.type.SJBHInfo;
import com.concrete.type.UserInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.concrete.common.IntentDef.DEFAULT_PATH;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;


/**
 * Created by Tangxl on 2017/11/25.
 */

public class AddCardActivity extends Activity implements IntentDef.OnFragmentListener, View.OnClickListener {

    public final static String SC_MODE = "SC_MODE";
    public final static String SC_MODE_PARAM = "SC_MODE_PARAM";

    public final static int SG_MODE_ADD = 0xF1A1;
    public final static int SG_MODE_EDIT = 0xF1A2;
    public final static int SG_MODE_VIEW = 0xF1A3;

    public final static int MAXCARD = 3;
    public final static int HANDLER_SETDEFAULT = 0x1110;
    public final static int HANDLER_GONGCHENGINFO = 0x1111;
    public final static int HANDLER_SNAP = 0x1112;
    public final static int HANDLER_UPDATA = 0x1113;
    public final static int HANDLER_UPDATA_DAILOG = 0x1114;
    public final static int HANDLER_SETPARAM = 0x1115;
    public final static int HANDLER_SHOW_WAIT = 0x1116;
    public final static int HANDLER_HIDE_WAIT = 0x1117;
    public final static int HANDLER_TOAST = 0x1118;

    private int mSGMode = SG_MODE_ADD;

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private AddProjectInfoFragment mAddProjectInfoFragment = null;
    private AddCardInfoFragment mAddCardInfoFragment = null;
    private EventHandler mEventHandler = null;
    private int mPopWindowID = 0;
    private String[] mPopWindowStr = null;
    private AlertDialog.Builder mAlertDialogEdit = null;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private ArrayList<Long> mCardNum = new ArrayList<Long>();
    private ArrayList<Long> mCardNumOld = new ArrayList<Long>();
    private HttpLogic mLogic = null;
    private String UUID  = null;
    private String mSJBH  = null;
    private Context mContext = null;
    private HttpBroadCast mHttpBroadCast = null;
    private SqliteLogic mSqliteLogic = null;
    private ImageButton mUploadButton = null;
    private CommonBase mCommonBase = null;

    private final int mAddCardInfo[] =
    {
        R.string.key_pref_card_sjbh,
        R.string.key_pref_card_zzrq,
        R.string.key_pref_card_gcid,
        R.string.key_pref_card_gcmc,
        R.string.key_pref_card_wtdw,
        R.string.key_pref_card_sgdw,
        R.string.key_pref_card_jzdw,
        R.string.key_pref_card_jzr,
        R.string.key_pref_card_jzbh,
        R.string.key_pref_card_gjbw,
        R.string.key_pref_card_yhfs,
        R.string.key_pref_card_bzdw,
        R.string.key_pref_card_qddj,
        R.string.key_pref_card_yplx,
        R.string.key_pref_card_phbbh,
        R.string.key_pref_card_sclsh,
    };

    private final int mAddCardValue[] =
    {
        R.string.pref_card_sjbh,
        R.string.pref_card_zzrq,
        R.string.pref_card_gcid,
        R.string.pref_card_gcmc,
        R.string.pref_card_wtdw,
        R.string.pref_card_sgdw,
        R.string.pref_card_jzdw,
        R.string.pref_card_jzr,
        R.string.pref_card_jzbh,
        R.string.pref_card_gjbw,
        R.string.pref_card_yhfs,
        R.string.pref_card_bzdw,
        R.string.pref_card_qddj,
        R.string.pref_card_yplx,
        R.string.pref_card_phbbh,
        R.string.pref_card_sclsh,
    };

    private final int mCardNumList[] =
    {
        R.string.key_pref_card_number1,
        R.string.key_pref_card_number2,
        R.string.key_pref_card_number3,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addcard);
        mContext = this;

        mCommonBase = new CommonBase(this);

        mSGMode = getIntent().getIntExtra(SC_MODE, SG_MODE_ADD);
        if(mSGMode == SG_MODE_EDIT || mSGMode == SG_MODE_VIEW){
            mSJBH = getIntent().getStringExtra(SC_MODE_PARAM);
        }

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

        mAddProjectInfoFragment = new AddProjectInfoFragment(this,0);
        mAddProjectInfoFragment.setOnFragmentListener(this);
        getFragmentManager().beginTransaction()
                .add(R.id.read_layout1, mAddProjectInfoFragment)
                .commit();

        mAddCardInfoFragment = new AddCardInfoFragment(this,0);
        mAddCardInfoFragment.setOnFragmentListener(this);
        getFragmentManager().beginTransaction()
                .add(R.id.read_layout2, mAddCardInfoFragment)
                .commit();

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
        mEventHandler = new EventHandler();

        InitNFCLocal();
        if(!mCardNum.isEmpty()){
            mCardNum.clear();
        }
        if(!mCardNumOld.isEmpty()){
            mCardNumOld.clear();
        }
        mSqliteLogic = new SqliteLogic(this);
        mLogic = new HttpLogic(this);
        mHttpBroadCast = new HttpBroadCast(this,mEventHandler);

        switch(mSGMode){
            case SG_MODE_ADD:
                mEventHandler.sendEmptyMessage(HANDLER_SETDEFAULT);
                break;

            case SG_MODE_EDIT:
            case SG_MODE_VIEW:
                if(mSJBH == null){
                    mEventHandler.sendEmptyMessage(HANDLER_SETDEFAULT);
                }else{
                    mEventHandler.sendEmptyMessage(HANDLER_SETPARAM);
                }
                break;

            default:
                break;
        }

    }

    private void InitNFCLocal(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(null != mNfcAdapter){
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mSqliteLogic.Close();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        DisableNFC();
        if (mSlidingMenu.isMenuShowing())
            mSlidingMenu.toggle();
        mHttpBroadCast.Close();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        EnableNFC();
        mSlidingMenu.showContent();
        mHttpBroadCast.Start();
    }

    public boolean IsSameCard(long Card){
        boolean ret = false;
        if(mCardNum.size() > 0){
            for (int i = 0; i < mCardNum.size(); i++){
                long Id = mCardNum.get(i);
                if(Id == Card){
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String str = Common.ByteArrayToHexString(tag.getId());
            long CardNum = Long.parseLong(str, 16);
            if(mCardNum.size() < MAXCARD)
            {
                if(false == IsSameCard(CardNum)){
                    int index = mCardNum.size();
                    mCardNum.add(index,CardNum);
                    mAddCardInfoFragment.SetPreference(mCardNumList[index],String.valueOf(CardNum));
                    if(MAXCARD == mCardNum.size()){
                        mEventHandler.sendEmptyMessage(HANDLER_UPDATA_DAILOG);
                    }
                }else{
                    Toast.makeText(this,getResources().getText(R.string.toast_hit_same_id),Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void OnFragmentReport(View view) {

    }

    @Override
    public void OnFragmentReport(int Id) {
        switch(Id){
            case R.string.key_pref_card_yhfs:
            case R.string.key_pref_card_yplx:
            case R.string.key_pref_card_qddj:
            case R.string.key_pref_card_gcid:
                showpopwindow(Id);
                break;

            case R.string.key_pref_card_zzrq:
            case R.string.key_pref_card_gcmc:
            case R.string.key_pref_card_wtdw:
            case R.string.key_pref_card_sgdw:
            case R.string.key_pref_card_jzdw:
            case R.string.key_pref_card_jzr:
            case R.string.key_pref_card_jzbh:
            case R.string.key_pref_card_gjbw:
            case R.string.key_pref_card_bzdw:
            case R.string.key_pref_card_phbbh:
            case R.string.key_pref_card_sclsh:
                showeditdwindows(Id);
                break;

            case R.string.key_pref_card_number1:
                if(mCardNum.get(0) > 0){
                    showrfidoper(Id);
                }
                break;

            case R.string.key_pref_card_number2:
                if(mCardNum.get(1) > 0){
                    showrfidoper(Id);
                }
                break;

            case R.string.key_pref_card_number3:
                if(mCardNum.get(2) > 0){
                    showrfidoper(Id);
                }
                break;

            case R.string.key_pref_card_sjbh:
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(0 != resultCode){
            switch(requestCode){
                case HANDLER_SNAP: {
                    String dbPath = null;
                    switch (mPopWindowID) {
                        case R.string.key_pref_card_number1:
                            dbPath = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(0) + ".jpg";
                            break;

                        case R.string.key_pref_card_number2:
                            dbPath = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(1) + ".jpg";
                            break;

                        case R.string.key_pref_card_number3:
                            dbPath = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(2) + ".jpg";
                            break;
                    }
                    if(null != data && null != data.getExtras()){
                        Bitmap bm = (Bitmap) data.getExtras().get("data");
                        if(null != bm){
                            if(Common.ScaleBmp(dbPath,bm)){
                                mAddCardInfoFragment.Update();
                                Toast.makeText(mContext,R.string.toast_hit_save_success,Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext,R.string.toast_hit_save_error,Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
                break;

                default:
                    break;

            }
        }
    }

    public int GetCardId(String Id){
        int mId = 0;

        for(int i = 0; i < mAddCardInfo.length; i++){
            if (Id.equals(getResources().getString(mAddCardInfo[i]))){
                mId = mAddCardInfo[i];
                break;
            }
        }
        return mId;
    }

    public String GetCardValue(int Id){
        String Value = "";

        for(int i = 0; i < mAddCardInfo.length; i++){
            if (Id == mAddCardInfo[i]){
                Value =  getResources().getString(mAddCardValue[i]);
                break;
            }
        }

        return Value;
    }

    public int GetCardIndexId(String Id){
        int mId = 0;

        for(int i = 0; i < mCardNumList.length; i++){
            if (Id.equals(getResources().getString(mCardNumList[i]))){
                mId = mCardNumList[i];
                break;
            }
        }
        return mId;
    }

    public void SnapImage(int Id){
        int index = -1;
        switch(Id){
            case R.string.key_pref_card_number1:
                index = 0;
                break;

            case R.string.key_pref_card_number2:
                index = 1;
                break;

            case R.string.key_pref_card_number3:
                index = 2;
                break;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, HANDLER_SNAP);
    }

    public void ShowImage(int Id){
        String Path = null;
        switch(Id){
            case R.string.key_pref_card_number1:
                Path = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(0) + ".jpg";
                break;

            case R.string.key_pref_card_number2:
                Path = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(1) + ".jpg";
                break;

            case R.string.key_pref_card_number3:
                Path = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(2) + ".jpg";
                break;
        }

        Intent intent =new Intent(this,ImageDisplay.class);
        Bundle bundle=new Bundle();
        bundle.putString("Path", Path);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showrfidoper(int Id){
        mPopWindowID = Id;
        new  AlertDialog.Builder(this)
                .setTitle(R.string.pref_choose_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(new  String[] {getResources().getString(R.string.pref_choose_snap),
                                getResources().getString(R.string.pref_choose_image),
                                getResources().getString(R.string.pref_choose_delete),
                        },  -1 ,
                        new  DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,  int  which) {
                                switch(which){
                                    case 0:
                                        SnapImage(mPopWindowID);
                                        break;
                                    case 1:
                                        ShowImage(mPopWindowID);
                                        break;
                                    case 2:
                                        delete_card(mPopWindowID);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton(R.string.hit_cancle ,  null )
                .show();
    }

    public void showpopwindow(int Id){
        int Title = R.string.pref_card_yhfs;
        int Selete = 0;
        switch(Id){
            case R.string.key_pref_card_yhfs:
                mPopWindowStr = getResources().getStringArray(R.array.YHFS);
                Title = R.string.pref_card_yhfs;
                mPopWindowID = Id;
                break;
            case R.string.key_pref_card_qddj:
                mPopWindowStr = getResources().getStringArray(R.array.QDDJ);
                Title = R.string.pref_card_qddj;
                mPopWindowID = Id;
                break;
            case R.string.key_pref_card_yplx:
                mPopWindowStr = getResources().getStringArray(R.array.YPLX);
                Title = R.string.pref_card_yplx;
                mPopWindowID = Id;
                break;

            case R.string.key_pref_card_gcid:
                mPopWindowStr = UserInfo.getInstance(this).GetProjectList();
                Title = R.string.pref_card_gcid;
                mPopWindowID = Id;
                int Max = UserInfo.getInstance(this).GetPrjectInfoList().size();
                String Value = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcid);
                if(null != Value){
                    for(int i = 0; i < Max; i++){
                        if(Value.equals(mPopWindowStr[i])){
                            Selete = i;
                            break;
                        }
                    }
                }
                break;
        }

        new AlertDialog.Builder(this).setTitle(getResources().getString(Title)).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(mPopWindowStr, Selete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAddProjectInfoFragment.SetPreference(mPopWindowID,mPopWindowStr[which]);
                        if(mPopWindowID == R.string.key_pref_card_gcid){
                            mEventHandler.sendEmptyMessage(HANDLER_GONGCHENGINFO);
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    public void delete_card(int Id){
        int index = 0;
        switch(Id){
            case R.string.key_pref_card_number1:
                index = 0;
                break;
            case R.string.key_pref_card_number2:
                index = 1;
                break;
            case R.string.key_pref_card_number3:
                index = 2;
                break;
        }
        if(mCardNum.size() > 0 && mCardNum.get(index) > 0){
            mPopWindowID = Id;
            mAlertDialogEdit =  new AlertDialog.Builder(this);
            mAlertDialogEdit.setTitle(mAddCardInfoFragment.GetPreference(Id));
            mAlertDialogEdit.setMessage(getResources().getString(R.string.toast_hit_delete_card));
            mAlertDialogEdit.setNegativeButton(R.string.hit_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAddCardInfoFragment.SetPreference(mPopWindowID,"");
                    ArrayList<Long> Card = new ArrayList<Long>();
                    int index = 0;
                    switch(mPopWindowID){
                        case R.string.key_pref_card_number1:
                            index = 0;
                            break;
                        case R.string.key_pref_card_number2:
                            index = 1;
                            break;
                        case R.string.key_pref_card_number3:
                            index = 2;
                            break;
                    }
                    String dbPath = Common.getInnerSDCardPath() + "/" + "CoreSoft/" + mCardNum.get(index) + ".jpg";
                    File f = new File(dbPath);
                    if (f.exists()) {
                        f.delete();
                    }
                    mCardNum.set(index, (long) 0);
                    for(int i = 0; i < mCardNum.size(); i++){
                        long ret = mCardNum.get(i);
                        if(ret > 0){
                            Card.add(Card.size(),ret);
                        }
                    }
                    mCardNum.clear();
                    for(int i = 0; i < Card.size(); i++){
                        long ret = Card.get(i);
                        if(ret > 0){
                            mCardNum.add(mCardNum.size(),ret);
                        }
                    }
                    mAddCardInfoFragment.Update();
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
    }

    public void showeditdwindows(int Id){
        mPopWindowID = Id;
        final EditText mEditWindow = new EditText(this);
        mEditWindow.setText(mAddProjectInfoFragment.GetPreference(Id));
        mAlertDialogEdit =  new AlertDialog.Builder(this);
        mAlertDialogEdit.setTitle(GetCardValue(mPopWindowID)).setView(mEditWindow);
        mAlertDialogEdit.setNegativeButton(R.string.hit_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAddProjectInfoFragment.SetPreference(mPopWindowID,mEditWindow.getText().toString());
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

    public void UploadChipInfo(){
        if(mCardNum.size() == MAXCARD){
            ArrayList<ChipInfo> item = new ArrayList<ChipInfo>();
            mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
            for(int i = 0; i < MAXCARD; i++){
                ChipInfo mChipInfo =  new ChipInfo(UUID,mSJBH, String.valueOf(mCardNum.get(i)), 1,
                        HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW,HttpDef.UNKNOW);
                item.add(i, mChipInfo);
            }
            mCommonBase.ShowWaitDialog(R.string.toast_hit_upload_wait);
            mSqliteLogic.InsertRFID(item.get(0),(byte)0);
            mSqliteLogic.InsertRFID(item.get(1),(byte)0);
            mSqliteLogic.InsertRFID(item.get(2),(byte)0);
            mLogic.OperRFID(HANDLE_HTTP_INSERT_RFID,item,UserInfo.getInstance(this).GetUserInfo());
        }else{
            mAddCardInfoFragment.Update();
        }
    }

    public void AddMode_UploadGongcheng(){
        ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
        SJBHInfo mSJBHInfo =  new SJBHInfo(mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_yplx),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gjbw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_qddj),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_yhfs),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_zzrq),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_phbbh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sclsh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_bzdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sgdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_wtdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzr),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzbh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcid),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc),
                HttpDef.UNKNOW,
                HttpDef.UNKNOW,
                0,
                0,
                (byte)0,
                HttpDef.UNKNOW,
                HttpDef.UNKNOW
                );
        item.add(0, mSJBHInfo);
        mSqliteLogic.InsertSJBH(mSJBHInfo,(byte)0);
        mLogic.OperSJBHInfo(HANDLE_HTTP_INSERT_SJBH,item,UserInfo.getInstance(this).GetUserInfo());
    }



    public void EditMode_UploadGongcheng(){
        new EditSyncThread().start();
    }

    public void UploadAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.hit_sure))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(getResources().getString(R.string.toast_hit_upgrade_record))
                .setPositiveButton(getResources().getString(R.string.hit_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.hit_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEventHandler.sendEmptyMessage(HANDLER_UPDATA);
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.Upload_Button:
                UploadAlertDialog();
                break;
        }
    }

    @SuppressLint("ValidFragment")
    class AddProjectInfoFragment extends FragmentBase {

        private Preference mCardPreference = null;
        private ArrayList<Preference> mPreferenceList = null;

        public AddProjectInfoFragment(Context context, int SelfId) {
            super(context, SelfId);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferenceaddcard_project);
            mPreferenceList = new ArrayList<Preference>();
            for(int i = 0; i < mAddCardInfo.length; i++)
            {
                Preference mPreference = findPreference(mContext.getResources().getString(mAddCardInfo[i]));
                mPreferenceList.add(mPreference);
            }
        }

        public String GetPreference(int Id){
            Preference mPreference = findPreference(mContext.getResources().getString(Id));
            if(mPreference.getSummary() != null){
                return mPreference.getSummary().toString();
            }
            return null;
        }

        public void SetPreference(int Id, String value){
            Preference mPreference = findPreference(mContext.getResources().getString(Id));
            if(mPreference != null){
                mPreference.setSummary(value);
            }
        }

        public void setGongChengInfo(){

            String Value = GetPreference(R.string.key_pref_card_gcid);
            PrjectInfo mPrjectInfo = UserInfo.getInstance(mContext).GetPrjectInfo(Value);
            if(mPrjectInfo != null){
                SetPreference(R.string.key_pref_card_gcmc, mPrjectInfo.project);
                SetPreference(R.string.key_pref_card_wtdw, mPrjectInfo.check_unit_id);
                SetPreference(R.string.key_pref_card_sgdw, mPrjectInfo.consCorpNames);
                SetPreference(R.string.key_pref_card_jzdw, mPrjectInfo.superCorpNames);
            }
        }

        public void SetDefault()
        {
            SetPreference(R.string.key_pref_card_gcmc, getResources().getString(R.string.pref_add_default_dw)+getResources().getString(R.string.pref_add_card_gcmc));
            SetPreference(R.string.key_pref_card_wtdw, getResources().getString(R.string.pref_add_default_dw)+getResources().getString(R.string.pref_add_card_wtdw));
            SetPreference(R.string.key_pref_card_sgdw, getResources().getString(R.string.pref_add_default_dw)+getResources().getString(R.string.pref_card_card_sgdw));
            SetPreference(R.string.key_pref_card_jzdw, getResources().getString(R.string.pref_add_default_dw)+getResources().getString(R.string.pref_card_card_jldw));

            SetPreference(R.string.key_pref_card_zzrq, Common.getData());
            String[] str = mContext.getResources().getStringArray(R.array.YHFS);
            SetPreference(R.string.key_pref_card_yhfs, str[0]);
            str = mContext.getResources().getStringArray(R.array.YPLX);
            SetPreference(R.string.key_pref_card_yplx, str[0]);
            str = mContext.getResources().getStringArray(R.array.QDDJ);
            SetPreference(R.string.key_pref_card_qddj, str[0]);

            UpdateSJBH();
        }

        public void UpdateSJBH(){
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd");
            String mDate = mDateFormat.format(new java.util.Date());
            String SJBH = Common.getMac() +mDate+ String.format("%04d", SysParam.getInstance(mContext).GetSGIndex());
            SetPreference(R.string.key_pref_card_sjbh, SJBH);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(null != mOnFragmentListener){
                mOnFragmentListener.OnFragmentReport(GetCardId(preference.getKey()));
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    @SuppressLint("ValidFragment")
    class AddCardInfoFragment extends FragmentBase {

        public AddCardInfoFragment(Context context, int SelfId) {
            super(context, SelfId);

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferenceaddcard_cardinfo);
            SetPreferenceIcon(R.string.key_pref_card_number1,R.drawable.bookmark_err);
            SetPreferenceIcon(R.string.key_pref_card_number2,R.drawable.bookmark_err);
            SetPreferenceIcon(R.string.key_pref_card_number3,R.drawable.bookmark_err);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(null != mOnFragmentListener){
                mOnFragmentListener.OnFragmentReport(GetCardIndexId(preference.getKey()));
            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }



        public String GetPreference(int Id){
            Preference mPreference = findPreference(mContext.getResources().getString(Id));
            if(mPreference.getSummary() != null){
                return mPreference.getSummary().toString();
            }
            return null;
        }

        public void SetPreference(int Id, String value){
            Preference mPreference = findPreference(mContext.getResources().getString(Id));
            if(mPreference != null){
                mPreference.setSummary(value);
            }
        }

        public void SetPreferenceIcon(int Id, int Image){
            Preference mPreference = findPreference(mContext.getResources().getString(Id));
            if(mPreference != null){
                mPreference.setIcon(Image);
            }
        }

        private void Clear(){
            for(int i = 0; i < mCardNumList.length; i++){
                SetPreference(mCardNumList[i],"");
                SetPreferenceIcon(mCardNumList[i],R.drawable.bookmark_err);
            }
        }

        public void Update(){
            Clear();
            for(int i = 0; i < mCardNum.size(); i++){
                long ret = mCardNum.get(i);
                if(ret > 0){
                    SetPreference(mCardNumList[i],String.valueOf(ret));
                    String Path = DEFAULT_PATH+"/"+String.valueOf(ret)+".jpg";
                    if(FileUtil.IsFileExist(Path)){
                        SetPreferenceIcon(mCardNumList[i],R.drawable.bookmark_ok);
                    }else{
                        SetPreferenceIcon(mCardNumList[i],R.drawable.bookmark_err);
                    }
                }
            }
        }
    }

    public void InitFromSJBH(){
        SJBHInfo mSJBHInfo = mSqliteLogic.QuerySJBH(mSJBH);
        ArrayList<ChipInfo> mChipInfoOper = mSqliteLogic.QureyRFIDFromSJBH(mSJBH);
        if(null == mSJBHInfo){
            mAddProjectInfoFragment.SetDefault();
        }else{
            if(!mCardNum.isEmpty()){
                mCardNum.clear();
            }

            if(!mCardNumOld.isEmpty()){
                mCardNumOld.clear();
            }

            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_sjbh,mSJBHInfo.TBL_SJBH);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_zzrq,mSJBHInfo.TBL_ZZRQ);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_gcid,mSJBHInfo.TBL_GCID);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_gcmc,mSJBHInfo.TBL_GCMC);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_wtdw,mSJBHInfo.TBL_WTDW);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_sgdw,mSJBHInfo.TBL_SGDW);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_jzdw,mSJBHInfo.TBL_JZDW);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_jzr,mSJBHInfo.TBL_JZR);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_jzbh,mSJBHInfo.TBL_JZBH);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_gjbw,mSJBHInfo.TBL_GJBW);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_yhfs,mSJBHInfo.TBL_YHFS);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_bzdw,mSJBHInfo.TBL_BZDW);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_qddj,mSJBHInfo.TBL_QDDJ);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_yplx,mSJBHInfo.TBL_YPLX);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_phbbh,mSJBHInfo.TBL_PHBBH);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_sclsh,mSJBHInfo.TBL_SCLSH);

            if(mChipInfoOper.size() > 0){
                int Max = mChipInfoOper.size();
                if(Max > MAXCARD){
                    Max = MAXCARD;
                }
                for(int i = 0; i < Max; i++){
                    mAddCardInfoFragment.SetPreference(mCardNumList[i],mChipInfoOper.get(i).RFID);
                    mCardNum.add(i,Long.valueOf(mChipInfoOper.get(i).RFID));
                    mCardNumOld.add(i,Long.valueOf(mChipInfoOper.get(i).RFID));
                }

                mAddCardInfoFragment.Update();
            }
        }
    }

    class EventHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case HANDLER_SETPARAM:
                    InitFromSJBH();
                    break;

                case HANDLER_UPDATA_DAILOG:
                    UploadAlertDialog();
                    break;

                case HANDLER_UPDATA:
                    if(mSGMode == SG_MODE_ADD){
                        UploadChipInfo();
                    }else{
                        EditMode_UploadGongcheng();
                    }
                    break;

                case HANDLER_SETDEFAULT:
                    UUID = java.util.UUID.randomUUID().toString();
                    mAddProjectInfoFragment.SetDefault();
                    break;

                case HANDLER_GONGCHENGINFO:
                    mAddProjectInfoFragment.setGongChengInfo();
                    break;

                case HANDLE_HTTP_INSERT_RFID:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        AddMode_UploadGongcheng();
                    }else{
                        mCommonBase.HideWaitDialog();
                        HttpToast(mContext,msg.arg1);
                        mAddCardInfoFragment.Clear();
                        if(!mCardNum.isEmpty()){
                            mCardNum.clear();
                        }
                        mSqliteLogic.DeleteRFIDFromSJBH(mSJBH);
                    }
                    break;

                case HANDLE_HTTP_INSERT_SJBH:
                    mCommonBase.HideWaitDialog();
                    HttpToast(mContext,msg.arg1);
                    if(mSGMode == SG_MODE_ADD){
                        mAddCardInfoFragment.Clear();
                        if(!mCardNum.isEmpty()){
                            mCardNum.clear();
                        }
                        if(msg.arg1 != HttpEcho.SUCCESS) {
                            mSqliteLogic.DeleteRFIDFromSJBH(mSJBH);
                            mSqliteLogic.DeleteSJBH(mSJBH);
                        }else{
                            SysParam.getInstance(mContext).AddSGIndex();
                            mAddProjectInfoFragment.UpdateSJBH();
                        }
                    }

                    break;

                case HANDLER_SHOW_WAIT:
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_upload_wait);
                    break;

                case HANDLER_HIDE_WAIT:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_TOAST:
                    Toast.makeText(mContext,msg.arg1,Toast.LENGTH_SHORT);
                    break;
            }
        }
    }

    private void HttpToast(Context context,int Error){
        Toast.makeText(context, HttpEcho.GetHttpEcho(Error),Toast.LENGTH_LONG).show();
    }

    public static ArrayList<ChipInfo> GetSameChipInfo(ArrayList<ChipInfo> mChipInfo, ArrayList<Long> Card){
        ArrayList<ChipInfo> nChipInfo = new ArrayList<ChipInfo>();

        for(int i = 0; i < mChipInfo.size(); i++){
            for (int j = 0; j < Card.size(); j++){
                if(mChipInfo.get(i).RFID.equals(String.valueOf(Card.get(j)))){
                    nChipInfo.add(nChipInfo.size(),mChipInfo.get(i));
                    break;
                }
            }
        }

        return nChipInfo;
    }

    class EditSyncThread extends Thread {
        @Override
        public void run() {
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            SJBHInfo nSJBHInfo = mSqliteLogic.QuerySJBH(mSJBH);
            ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
            SJBHInfo mSJBHInfo =  new SJBHInfo(mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_yplx),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gjbw),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_qddj),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_yhfs),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_zzrq),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_phbbh),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sclsh),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_bzdw),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sgdw),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_wtdw),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzdw),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzr),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzbh),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcid),
                    mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc),
                    nSJBHInfo.TBL_YPBH,
                    nSJBHInfo.TBL_JCJG,
                    nSJBHInfo.TBL_JCresult,
                    nSJBHInfo.TBL_JCbfb,
                    nSJBHInfo.TBL_STATE,
                    nSJBHInfo.TBL_SYJG,
                    nSJBHInfo.TBL_SYRQ
            );

            item.add(0, mSJBHInfo);

            if(mLogic.OperSJBHInfolock(HANDLE_HTTP_UPDATE_SJBH,item,UserInfo.getInstance(mContext).GetUserInfo())){
                int i;
                boolean same = false;
                mSqliteLogic.SyncSJBH(mSJBHInfo);

                ArrayList<ChipInfo> mChipAdd = new ArrayList<ChipInfo>();
                ArrayList<ChipInfo> mChipUpdate = new ArrayList<ChipInfo>();
                ArrayList<ChipInfo> mChipOld = mSqliteLogic.QureyRFIDFromSJBH(mSJBH);

                for(i = 0; i < mCardNumOld.size(); i++){
                    nlog.Info("mCardNumOld.get(i).RFID ==== ["+mCardNumOld.get(i)+"]");
                }

                for(i = 0; i < mCardNum.size(); i++){
                    nlog.Info("mCardNum.get(i).RFID ==== ["+mCardNum.get(i)+"]");
                }

                nlog.Info("mChipOld====================["+mChipOld.size()+"]");

                ArrayList<Long> mDelete = Common.GetDIff(mCardNumOld,mCardNum);
                ArrayList<Long> mAdd = Common.GetDIff(mCardNum,mCardNumOld);
                ArrayList<Long> mSame = Common.GetSame(mCardNum,mCardNumOld);

                nlog.Info("*************************ADD****************************");
                for(i = 0; i < mAdd.size(); i++){
                    nlog.Info("mAdd.get(i).RFID ==== ["+mAdd.get(i)+"]");
                }
                nlog.Info("*************************DEL****************************");


                for(i = 0; i < mSame.size(); i++){
                    nlog.Info("mSame.get(i).RFID ==== ["+mSame.get(i)+"]");
                }
                nlog.Info("**************************DIFF***************************");

                for(i = 0; i < mDelete.size(); i++){
                    nlog.Info("mDelete.get(i).RFID ==== ["+mDelete.get(i)+"]");
                }
                nlog.Info("*****************************************************");

                if(mDelete.size() > 0){
                    ArrayList<ChipInfo> mChipDelete = GetSameChipInfo(mChipOld,mDelete);
//                    for(i = 0; i < mDelete.size(); i++){
//                        same = false;
//                        for(int k = 0; k < mChipOld.size(); k++){
//                            if(mDelete.get(i).equals(mChipOld.get(k).RFID)){
//                                mChipDelete.add(mChipDelete.size(),mChipOld.get(k));
//                            }
//                        }
//                    }
                    nlog.Info("mChipDelete =============== ["+mChipDelete.size()+"]");
                    if(mLogic.OperRFIDBlock(HANDLE_HTTP_DELETE_RFID,mChipDelete,UserInfo.getInstance(mContext).GetUserInfo())){
                        for(i = 0; i < mChipDelete.size(); i++){
                            nlog.Info("mChipDelete.get(i).RFID ==== ["+mChipDelete.get(i).RFID+"]");
                            mSqliteLogic.DelteRFID(mChipDelete.get(i).RFID);
                        }
                    }
                }

                if(mSame.size() > 0){
                    for(i = 0; i < mSame.size(); i++){
                        same = false;
                        for(int k = 0; k < mChipOld.size(); k++){
                            if(mSame.get(i).equals(mChipOld.get(k).RFID)){
                                mChipUpdate.add(mChipUpdate.size(),mChipOld.get(k));
                            }
                        }
                    }
                    nlog.Info("mChipUpdate =============== ["+mChipUpdate.size()+"]");
                    if(mLogic.OperRFIDBlock(HANDLE_HTTP_UPDATE_RFID,mChipUpdate,UserInfo.getInstance(mContext).GetUserInfo())){
                        for(i = 0; i < mChipUpdate.size(); i++){
                            mSqliteLogic.SyncRFID(mChipUpdate.get(i));
                        }
                    }
                }

                if(mAdd.size() > 0){
                    for(i = 0; i < mAdd.size(); i++){
                        ChipInfo nChipInfo =  new ChipInfo(
                                mChipOld.get(0).SKs_UUid,
                                mChipOld.get(0).TBL_SJBH,
                                String.valueOf(mAdd.get(i)),
                                mChipOld.get(0).SerialNo,

                                mChipOld.get(0).TBL_SYJG,
                                mChipOld.get(0).TBL_SYRQ,
                                mChipOld.get(0).TBL_LDR,
                                mChipOld.get(0).TBL_LRSJ,
                                mChipOld.get(0).TBL_JCRY,
                                mChipOld.get(0).TBL_JCRQ);
                        mChipAdd.add(i, nChipInfo);
                    }
                    nlog.Info("mChipAdd =============== ["+mChipAdd.size()+"]");
                    if(mLogic.OperRFIDBlock(HANDLE_HTTP_INSERT_RFID,mChipAdd,UserInfo.getInstance(mContext).GetUserInfo())){
                        for(i = 0; i < mChipAdd.size(); i++){
                            mSqliteLogic.InsertRFID(mChipAdd.get(i),(byte)0);
                        }
                    }
                }

                Message msg = new Message();
                msg.what = HANDLER_TOAST;
                msg.arg1 = R.string.toast_hit_upgrade_success;
                mEventHandler.sendMessage(msg);
            }else{
                Message msg = new Message();
                msg.what = HANDLER_TOAST;
                msg.arg1 = R.string.toast_hit_upgrade_error;
                mEventHandler.sendMessage(msg);
            }
            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
        }
    }
}
