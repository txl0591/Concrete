package com.concrete.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.concrete.common.CardManager;
import com.concrete.common.Common;
import com.concrete.common.FileUtil;
import com.concrete.ctrl.CommonBase;
import com.concrete.database.SysParam;
import com.concrete.logic.CommonLoigic;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpDef;
import com.concrete.net.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.ctrl.FragmentBase;
import com.concrete.ctrl.LeftMenuFragment;
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
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.concrete.common.IntentDef.DEFAULT_PATH;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;


/**
 * Created by Tangxl on 2017/11/25.
 */

public class AddCardActivity extends Activity implements IntentDef.OnFragmentListener {

    public final static String SC_MODE = "SC_MODE";
    public final static String SC_MODE_PARAM = "SC_MODE_PARAM";

    public final static int SG_MODE_ADD = 0xF1A1;
    public final static int SG_MODE_EDIT = 0xF1A2;
    public final static int SG_MODE_VIEW = 0xF1A3;

    public final static int MAXCARD = 3;
    public final static int HANDLER_SETDEFAULT = 0x0110;
    public final static int HANDLER_GONGCHENGINFO = 0x111;
    public final static int HANDLER_SNAP = 0x112;
    public final static int HANDLER_UPDATA_GCINFO = 0x113;
    public final static int HANDLER_UPDATA_DAILOG = 0x114;
    public final static int HANDLER_SETPARAM = 0x115;
    public final static int HANDLER_SHOW_WAIT = 0x116;
    public final static int HANDLER_HIDE_WAIT = 0x117;
    public final static int HANDLER_TOAST = 0x118;
    public final static int HANDLER_CHOOSE_GCMC = 0x0119;
    public final static int HANDLER_CHOOSE_JZDW = 0x011A;
    public final static int HANDLER_GOTO_NEXT = 0x011B;
    public final static int HANDLER_UPDATA_RFID = 0x11C;
    public final static int HANDLER_SYNC_RFIDUI = 0x011D;
    public final static int HANDLER_UPDATE_IMAGE = 0x011E;
    public final static int HANDLER_CLEAR_IMAGE = 0x011F;

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
    private HttpLogic mHttpLogic = null;
    private String mSJBH  = null;
    private Context mContext = null;
    private HttpBroadCast mHttpBroadCast = null;
    private SqliteLogic mSqliteLogic = null;
    private CommonBase mCommonBase = null;
    private ProjectInfoList mPrjectInfoList = null;
    private JzrInfoList mJzrInfoList = null;
    private AlertDialog mEditAlertDialog = null;
    private boolean mRFIDAdd = false;

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
        setContentView(R.layout.activity_addcard);
        mContext = this;

        mCommonBase = new CommonBase(this);

        mSGMode = getIntent().getIntExtra(SC_MODE, SG_MODE_ADD);
        if(mSGMode == SG_MODE_EDIT || mSGMode == SG_MODE_VIEW){
            mSJBH = getIntent().getStringExtra(SC_MODE_PARAM);
        }

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
        mSqliteLogic = new SqliteLogic(this);
        mHttpLogic = new HttpLogic(this);
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_upload_gcinfo:
                UploadGCInfoDialog();
                break;
            case R.id.menu_upload_next:
                GotoNext();
                break;

        }

        return super.onOptionsItemSelected(item);
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
            nlog.Info("mCardNum ========["+mCardNum.size()+"] mSGMode ["+mSGMode+"] mRFIDAdd ["+mRFIDAdd+"]");
            if(mCardNum.size() < MAXCARD)
            {
                if(mRFIDAdd || mSGMode == SG_MODE_EDIT || mSGMode == SG_MODE_VIEW){
                    if(false == IsSameCard(CardNum)){
                        Message msg = new Message();
                        msg.what = HANDLER_UPDATA_RFID;
                        Bundle mBundle = new Bundle();
                        mBundle.putLong("RFID",CardNum);
                        msg.setData(mBundle);
                        mEventHandler.sendMessage(msg);
                    }else{
                        mCommonBase.Toast(R.string.toast_hit_same_id, null);
                    }
                }
                else
                {
                    mCommonBase.Toast(R.string.toast_hit_gcinfo_needadd, null);
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
                showpopwindow(Id);
                break;

            case R.string.key_pref_card_zzrq:
                mCommonBase.ChooseDateDialog(new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                       String Date = year+"-"+(++month)+"-"+day;
                        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_zzrq,Date);
                    }
                });
                break;

            case R.string.key_pref_card_wtdw:
            case R.string.key_pref_card_sgdw:
            case R.string.key_pref_card_jzr:
            case R.string.key_pref_card_jzbh:
            case R.string.key_pref_card_gjbw:
            case R.string.key_pref_card_bzdw:
            case R.string.key_pref_card_phbbh:
            case R.string.key_pref_card_sclsh:
                showeditdwindows(Id);
                break;

            case R.string.key_pref_card_gcmc:
            case R.string.key_pref_card_jzdw:
               ShowQueryDialog(Id);
                break;

            case R.string.key_pref_card_number1:
                if(mCardNum.size() > 0){
                    if(mCardNum.get(0) > 0){
                        showrfidoper(Id);
                    }
                }
                break;

            case R.string.key_pref_card_number2:
                if(mCardNum.size() > 0){
                    if(mCardNum.get(1) > 0){
                        showrfidoper(Id);
                    }
                }
                break;

            case R.string.key_pref_card_number3:
                if(mCardNum.size() > 0){
                    if(mCardNum.get(2) > 0){
                        showrfidoper(Id);
                    }
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
                    String nSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
                    if(mRFIDAdd || mSGMode == SG_MODE_EDIT){
                        if(null != data && null != data.getExtras()) {
                            Bitmap bm = (Bitmap) data.getExtras().get("data");
                            switch (mPopWindowID) {
                                case R.string.key_pref_card_number1:
                                    AddMode_InsertImage(mCardNum.get(0),nSJBH, bm);
                                    break;

                                case R.string.key_pref_card_number2:
                                    AddMode_InsertImage(mCardNum.get(1),nSJBH, bm);
                                    break;

                                case R.string.key_pref_card_number3:
                                    AddMode_InsertImage(mCardNum.get(2),nSJBH, bm);
                                    break;
                            }
                        }
                    }else{
                        mCommonBase.Toast(R.string.toast_hit_gcinfo_needadd, null);
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
        mPopWindowID = R.string.key_pref_card_number1;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, HANDLER_SNAP);
    }

    public void ShowImage(int Id){
        String Path = null;
        String SJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
        Intent intent =new Intent(this,ImageDisplay.class);
        Bundle bundle=new Bundle();
        bundle.putString("SJBH",SJBH);
        switch(Id){
            case R.string.key_pref_card_number1:
                bundle.putLong("RFID", mCardNum.get(0));
                break;

            case R.string.key_pref_card_number2:
                bundle.putLong("RFID", mCardNum.get(1));
                break;

            case R.string.key_pref_card_number3:
                bundle.putLong("RFID", mCardNum.get(2));
                break;
        }
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

                    DeleteRFIDOper(mCardNum.get(index),mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh));

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
                    SyncImage(mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh));
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

    private void ShowQueryDialog(int Id){
        mPopWindowID = Id;
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        View view = View
                .inflate(this, R.layout.inputdialog, null);
        mAlertDialog.setView(view);
        mAlertDialog.setCancelable(true);
        TextView title= (TextView) view
                .findViewById(R.id.title);//设置标题

        final EditText input_edt= (EditText) view.findViewById(R.id.dialog_edit);
        input_edt.setText(mAddProjectInfoFragment.GetPreference(mPopWindowID));
        switch(mPopWindowID){
            case R.string.key_pref_card_gcmc:
                title.setText(R.string.pref_card_gcmc);
                break;
            case R.string.key_pref_card_jzdw:
                title.setText(R.string.pref_card_jzdw);
                break;
        }

        Button btn_cancel=(Button)view
                .findViewById(R.id.btn_cancel);//取消按钮
        Button btn_comfirm=(Button)view
                .findViewById(R.id.btn_comfirm);//确定按钮

        Button btn_find=(Button)view
                .findViewById(R.id.btn_find);//确定按钮

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditAlertDialog.dismiss();
            }
        });

        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Input = input_edt.getText().toString();
                if(!Input.isEmpty()){
                    mAddProjectInfoFragment.SetPreference(mPopWindowID,input_edt.getText().toString());
                }
                mEditAlertDialog.dismiss();
            }
        });

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Input = input_edt.getText().toString();
                if(!Input.isEmpty()){
                    mAddProjectInfoFragment.SetPreference(mPopWindowID,input_edt.getText().toString());
                    switch(mPopWindowID){
                        case R.string.key_pref_card_gcmc:
                            OperQueryGCMC();
                            break;
                        case R.string.key_pref_card_jzdw:
                            OperQureyJZR();
                            break;
                    }
                }
                mEditAlertDialog.dismiss();
            }
        });

        //取消或确定按钮监听事件处理
        mEditAlertDialog = mAlertDialog.create();
        mEditAlertDialog.show();

    }

    public void UploadGCInfoDialog(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.hit_sure))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(getResources().getString(R.string.toast_hit_upgrade_gcinfo))
                .setPositiveButton(getResources().getString(R.string.hit_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.hit_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEventHandler.sendEmptyMessage(HANDLER_UPDATA_GCINFO);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void GotoNext(){
        if(mSGMode == SG_MODE_ADD){
            mEventHandler.sendEmptyMessage(HANDLER_GOTO_NEXT);
        }
    }

    @SuppressLint("ValidFragment")
    class AddProjectInfoFragment extends FragmentBase {

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

        private void ClearImage(){
            for(int i = 0; i < mCardNumList.length; i++){
                SetPreferenceIcon(mCardNumList[i],R.drawable.bookmark_err);
            }
        }

        public void Update(){
            Clear();
            for(int i = 0; i < mCardNum.size(); i++){
                long ret = mCardNum.get(i);
                if(ret > 0){
                    SetPreference(mCardNumList[i],String.valueOf(ret));
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

            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_sjbh,mSJBHInfo.TBL_SJBH);
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_zzrq,Common.getData());
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
                }

                mAddCardInfoFragment.Update();
                SyncImage(mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh));
            }
        }
    }

    public void InitFromDBForLast(){
        SJBHInfo mSJBHInfo = mSqliteLogic.QuerySJBH();
        if(null == mSJBHInfo){
            mAddProjectInfoFragment.SetDefault();
        }else{
            mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_zzrq,Common.getData());
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
            mAddProjectInfoFragment.UpdateSJBH();
        }
    }

    private void HttpToast(Context context,int Error){
        Toast.makeText(context, HttpEcho.GetHttpEcho(Error),Toast.LENGTH_LONG).show();
    }

    public void SyncUI(){
        if(mRFIDAdd){
            mRFIDAdd = false;
            mAddCardInfoFragment.Clear();
            if (!mCardNum.isEmpty()) {
                mCardNum.clear();
            }
            SysParam.getInstance(mContext).AddSGIndex();
            mAddProjectInfoFragment.UpdateSJBH();
        }
        else{
            mCommonBase.Toast(R.string.toast_hit_gcinfo_save_error,null);
        }
    }

    private void SyncRFIDUI(long CardNum){
        int index = mCardNum.size();
        mCardNum.add(index,CardNum);
        mAddCardInfoFragment.SetPreference(mCardNumList[index],String.valueOf(CardNum));

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
                    UploadGCInfoDialog();
                    break;

                case HANDLER_UPDATA_GCINFO:
                    nlog.Info("HANDLER_UPDATA_GCINFO ======================== ["+mSGMode+"]");
                    if(mSGMode == SG_MODE_ADD){
                        AddMode_InsertGCProject();
                    }else{
                        EditMode_InsertGCProject();
                    }
                    break;

                case HANDLER_UPDATA_RFID: {
                    long RFID = msg.getData().getLong("RFID");
                    if (mSGMode == SG_MODE_ADD || mSGMode == SG_MODE_EDIT) {
                        AddMode_InsertRFID(RFID);
                    }
                    break;
                }

                case HANDLER_SETDEFAULT:
                    mAddProjectInfoFragment.SetDefault();
                    InitFromDBForLast();
                    break;

                case HANDLER_GONGCHENGINFO:
                    mAddProjectInfoFragment.setGongChengInfo();
                    break;

                case HANDLER_SHOW_WAIT:
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_upload_wait);
                    break;

                case HANDLER_HIDE_WAIT:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_TOAST:
                    mCommonBase.Toast(msg.arg1,null);
                    break;

                case HANDLER_CHOOSE_GCMC:
                    ShowChooseGCMC(mPrjectInfoList);
                    break;

                case HANDLER_CHOOSE_JZDW:
                    ShowChooseJZR(mJzrInfoList);
                    break;

                case HANDLER_GOTO_NEXT:
                    SyncUI();
                    break;

                case HANDLER_SYNC_RFIDUI: {
                    long RFID = msg.getData().getLong("RFID");
                    SyncRFIDUI(RFID);
                    break;
                }

                case HANDLER_UPDATE_IMAGE:
                {
                    int ID = msg.getData().getInt("ID");
                    int Image = msg.getData().getInt("IMAGE");
                    mAddCardInfoFragment.SetPreferenceIcon(ID,Image);
                    break;
                }

                case HANDLER_CLEAR_IMAGE:
                    mAddCardInfoFragment.ClearImage();
                    break;
            }
        }
    }

    /***********************************************************************
     *      工程单位部分
     */

    private void SetGCMCFromDB(int index){
        PrjectInfo nPrjectInfo = mPrjectInfoList.items.get(index);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_gcmc, nPrjectInfo.project);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_wtdw, nPrjectInfo.check_unit);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_sgdw, nPrjectInfo.consCorpNames);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_gcid, nPrjectInfo.project_id);

    }

    private void ShowChooseGCMC(ProjectInfoList mPrjectInfoList){
        int Title = R.string.pref_card_gcmc;
        int Selete = 0;
        String[] PopStr = new String[mPrjectInfoList.items.size()];

        for(int i = 0; i < mPrjectInfoList.items.size(); i++){
            PopStr[i] = mPrjectInfoList.items.get(i).project+mPrjectInfoList.items.get(i).check_unit+mPrjectInfoList.items.get(i).consCorpNames;
        }

        new AlertDialog.Builder(this).setTitle(getResources().getString(Title)).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(PopStr, Selete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SetGCMCFromDB(which);
                        dialog.dismiss();
                    }
                }).show();
    }

    public void SyncGCOper(){
        String project = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc);
        String check_unit = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_wtdw);
        String consCorpNames = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sgdw);

        ProjectInfoList mProjectInfoList = CommonLoigic.QueryGCInfo(mContext,mSqliteLogic, mHttpLogic, project);
        if(mProjectInfoList == null || mProjectInfoList.items == null){
            PrjectInfo mPrjectInfo = new PrjectInfo(java.util.UUID.randomUUID().toString(),
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    project,
                    HttpDef.UNKNOW,
                    consCorpNames,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    check_unit,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    Common.getData(),
                    Common.getData(),
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW,
                    HttpDef.UNKNOW);

            CommonLoigic.InsertGCInfo(mContext,mSqliteLogic, mHttpLogic, mPrjectInfo);
        }
    }

    class QueryGCMCThread extends Thread {
        @Override
        public void run() {
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            String GCMC = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc);
            mPrjectInfoList = CommonLoigic.QueryGCInfo(mContext, mSqliteLogic, mHttpLogic,GCMC);
            if(null != mPrjectInfoList && mPrjectInfoList.items != null && 0 != mPrjectInfoList.items.size()){
                mEventHandler.sendEmptyMessage(HANDLER_CHOOSE_GCMC);
            }

            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
        }
    }

    private void OperQueryGCMC(){
        new QueryGCMCThread().start();
    }

    /***********************************************************************
     *      见证人信息部分
     */

    private void SetJZRFromDB(int index){
        JzrInfo nJzrInfo = mJzrInfoList.items.get(index);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_jzdw,nJzrInfo.JZDW);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_jzr,nJzrInfo.JZR);
        mAddProjectInfoFragment.SetPreference(R.string.key_pref_card_jzbh,nJzrInfo.JZH);
    }

    private void ShowChooseJZR(JzrInfoList nJzrInfoList){
        int Title = R.string.pref_card_jzdw;
        int Selete = -1;
        String[] PopStr = new String[nJzrInfoList.items.size()];

        for(int i = 0; i < nJzrInfoList.items.size(); i++){
            PopStr[i] = nJzrInfoList.items.get(i).JZDW + " " + nJzrInfoList.items.get(i).JZR;
        }


        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setTitle(getResources().getString(Title));
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setPositiveButton(getResources().getString(R.string.hit_cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mAlertDialog.setSingleChoiceItems(PopStr, Selete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SetJZRFromDB(which);
                        dialog.dismiss();
                    }
                });
        mAlertDialog .show();
    }

    public void SyncJzrOper(){
        String Jzdw = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzdw);
        String Jzr = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzr);
        String Jzbh = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzbh);

        mJzrInfoList = CommonLoigic.QueryJZR(mContext,mSqliteLogic, mHttpLogic, Jzdw);
        if(mJzrInfoList == null || mJzrInfoList.items == null){
            JzrInfo mJzrInfo = new JzrInfo(HttpDef.UNKNOW,Jzdw,Jzr,Jzbh);
            CommonLoigic.InsertJZR(mContext,mSqliteLogic, mHttpLogic,mJzrInfo);
        }
    }
    
    class QureyJZRThread extends Thread {
        @Override
        public void run() {
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            String JZDW = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzdw);
            mJzrInfoList = CommonLoigic.QueryJZR(mContext, mSqliteLogic, mHttpLogic,JZDW);
            if(null != mJzrInfoList && mJzrInfoList.items != null && 0 != mJzrInfoList.items.size()){
                mEventHandler.sendEmptyMessage(HANDLER_CHOOSE_JZDW);
            }

            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
        }
    }
    
    private void OperQureyJZR(){
        new QureyJZRThread().start();
    }

    /**********************************************************************
     *   工程信息新增部分
     */
    
    public boolean InsertGCProject(){
        String GCID = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcid);
        if(null == GCID){
            GCID = HttpDef.UNKNOW;
        }
        mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
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
                GCID,
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc),
                HttpDef.UNKNOW,
                HttpDef.UNKNOW,
                0,
                0,
                (byte)0,
                HttpDef.UNKNOW,
                HttpDef.UNKNOW
        );
        
        return CommonLoigic.InsertGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBHInfo);
    }

    class GCProjectThread extends Thread{
        @Override
        public void run() {
            boolean ret = false;
            int ID = 0;
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
            SJBHInfo mSJBHInfo = CommonLoigic.QueryGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBH);
            if(null == mSJBHInfo){
                ret = InsertGCProject();
                if(ret){
                    SyncJzrOper();
                    SyncGCOper();
                    ID = R.string.toast_hit_upload_success;
                    mRFIDAdd = true;
                }
                else
                {
                    ID = R.string.toast_hit_upload_error;
                }
            }else
            {
                ID = R.string.toast_hit_upload_not_empty;
            }
            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
            Message msg = new Message();
            msg.what = HANDLER_TOAST;
            msg.arg1 = ID;
            mEventHandler.sendMessage(msg);
        }
    }

    public void AddMode_InsertGCProject(){
        boolean empty = false;
        int[] ID = {R.string.key_pref_card_sjbh,
                R.string.key_pref_card_yplx,
                R.string.key_pref_card_gjbw,
                R.string.key_pref_card_qddj,
                R.string.key_pref_card_yhfs,
                R.string.key_pref_card_zzrq,
                R.string.key_pref_card_phbbh,
                R.string.key_pref_card_sclsh,
                R.string.key_pref_card_bzdw,
                R.string.key_pref_card_sgdw,
                R.string.key_pref_card_wtdw,
                R.string.key_pref_card_jzdw,
                R.string.key_pref_card_jzr,
                R.string.key_pref_card_jzbh,
                R.string.key_pref_card_gcmc,
        };

        for(int i = 0; i < ID.length; i++){
            String Value = mAddProjectInfoFragment.GetPreference(ID[i]);
            if(Value == null){
                empty = true;
                break;
            }
        }

        if(empty){
            mCommonBase.Toast(R.string.toast_hit_gcinfo_empty,null);
        }else{
            new GCProjectThread().start();
        }
    }

    /**********************************************************************
     *   工程信息修改部分
     */
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
            SJBHInfo mSJBHInfo = null;
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
            SJBHInfo nSJBHInfo =  CommonLoigic.QueryGCProject(mContext, mSqliteLogic, mHttpLogic, mSJBH);

            if(nSJBHInfo == null){
                mSJBHInfo =  new SJBHInfo(mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh),
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
                        HttpDef.UNKNOW);
            }else{
                mSJBHInfo =  new SJBHInfo(mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh),
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
            }

            ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
            if(CommonLoigic.UpdateGCProject(mContext,mSqliteLogic, mHttpLogic, mSJBHInfo))
            {
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

    public void EditMode_InsertGCProject(){
        new EditSyncThread().start();
    }

    /**********************************************************************
     *   RFID信息修改部分
     */

    private boolean InsertRFID(long CardNum){
        ArrayList<Long> CardList = new ArrayList<Long>();
        mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
        CardList.add(0,CardNum);
        boolean ret = CommonLoigic.InsteRFID(mContext,mSqliteLogic,mHttpLogic,CardList, mSJBH);
        return ret;
    }

    private boolean DeleteRFID(long CardNum){
        ArrayList<Long> CardList = new ArrayList<Long>();
        CardList.add(0,CardNum);
        mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
        return CommonLoigic.DeleteRFID(mContext,mSqliteLogic, mHttpLogic,CardList,mSJBH);
    }

    class InsterRFIDThread extends Thread{

        private long CardNum = 0;

        public InsterRFIDThread(long RFID){
            CardNum = RFID;
        }

        @Override
        public void run() {
            int ID = 0;
            boolean ret = false;
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            mSJBH = mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
            if(CommonLoigic.CanInsertRFID(mContext,mSqliteLogic, mHttpLogic, CardNum,mSJBH)){
                ret = InsertRFID(CardNum);
                if(ret){
                    ID = R.string.toast_hit_upload_success;
                    Message msg = new Message();
                    msg.what = HANDLER_SYNC_RFIDUI;
                    Bundle mBundle = new Bundle();
                    mBundle.putLong("RFID",CardNum);
                    msg.setData(mBundle);
                    mEventHandler.sendMessage(msg);
                }
                else
                {
                    ID = R.string.toast_hit_upload_error;
                }
            }
            else
            {
                ID = R.string.toast_hit_upload_not_empty;
            }

            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
            Message msg = new Message();
            msg.what = HANDLER_TOAST;
            msg.arg1 = ID;
            mEventHandler.sendMessage(msg);
        }
    }

    class DeleteRFIDThread extends Thread{
        private String nSJBH = null;
        private long mRFID = 0;

        public DeleteRFIDThread(Long RFID, String SJBH){
            mRFID = RFID;
            nSJBH = SJBH;
        }

        @Override
        public void run() {
            int ID = 0;
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            DeleteImage(mRFID,nSJBH);
            if(DeleteRFID(mRFID)){
                ID = R.string.toast_hit_delete_success;
            }
            else
            {
                ID = R.string.toast_hit_delete_fail;
            }

            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
            Message msg = new Message();
            msg.what = HANDLER_TOAST;
            msg.arg1 = ID;
            mEventHandler.sendMessage(msg);
        }
    }

    private void AddMode_InsertRFID(long RFID){
        new InsterRFIDThread(RFID).start();
    }

    private void DeleteRFIDOper(long RFID,String SJBH){
        new DeleteRFIDThread(RFID,SJBH).start();
    }

    /**********************************************************************
     *   图片操作部分
     */
    public boolean InsertImage(Bitmap mBitmap, Long RFID, String SJBH){
        boolean ret = CommonLoigic.InsertImage(mContext,mSqliteLogic, mHttpLogic, mBitmap, RFID, SJBH,HttpDef.HTTP_IMAGE.HTTP_IMAGE_ADD);
        return ret;
    }

    public boolean DeleteImage(Long RFID, String SJBH){
        boolean ret = CommonLoigic.DeleteImage(mContext,mSqliteLogic, mHttpLogic, RFID,SJBH);
        return ret;
    }

    class ImageThread extends Thread{
        private String nSJBH = null;
        private long CardNum = 0;
        private Bitmap mBitmap = null;

        public ImageThread(long RFID, String SJBH, Bitmap nBitmap){
            CardNum = RFID;
            mBitmap = nBitmap;
            nSJBH = SJBH;
        }

        @Override
        public void run() {
            int ID = 0;
            boolean ret = false;
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_SHOW_WAIT);
            InsertImage(mBitmap,CardNum,nSJBH);
            for(int i = 0; i < mCardNum.size(); i++){
                if(mCardNum.get(i) == CardNum){
                    Message Msg = new Message();
                    Msg.what = HANDLER_UPDATE_IMAGE;
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("ID",mCardNumList[i]);
                    mBundle.putInt("IMAGE",R.drawable.bookmark_ok);
                    Msg.setData(mBundle);
                    mEventHandler.sendMessage(Msg);
                    break;
                }
            }

            mEventHandler.sendEmptyMessage(HANDLER_HIDE_WAIT);
        }
    }

    class UpdateImageThread extends Thread{

        private String nSJBH = null;

        public UpdateImageThread(String SJBH){
            nSJBH = SJBH;
        }

        @Override
        public void run() {
            super.run();
            mEventHandler.sendEmptyMessage(HANDLER_CLEAR_IMAGE);
            for(int i = 0; i < mCardNum.size(); i++){
                long ret = mCardNum.get(i);
                if(ret > 0){
                    ImageInfo mImageInfo  = CommonLoigic.QueryImage(mContext,mSqliteLogic, mHttpLogic, ret,nSJBH);
                    if(null == mImageInfo) {
                        Message Msg = new Message();
                        Msg.what = HANDLER_UPDATE_IMAGE;
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("ID",mCardNumList[i]);
                        mBundle.putInt("IMAGE",R.drawable.bookmark_err);
                        Msg.setData(mBundle);
                        mEventHandler.sendMessage(Msg);
                    }else{
                        Message Msg = new Message();
                        Msg.what = HANDLER_UPDATE_IMAGE;
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("ID",mCardNumList[i]);
                        mBundle.putInt("IMAGE",R.drawable.bookmark_ok);
                        Msg.setData(mBundle);
                        mEventHandler.sendMessage(Msg);
                    }

                }
            }

        }
    }

    private void AddMode_InsertImage(long RFID, String SJBH, Bitmap nBitmap){
        new ImageThread(RFID,SJBH,nBitmap).start();
    }

    private void SyncImage(String SJBH){
        new UpdateImageThread(SJBH).start();
    }
}
