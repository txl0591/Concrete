package com.concrete.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.concrete.common.CardManager;
import com.concrete.common.Common;
import com.concrete.common.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.fragment.FragmentBase;
import com.concrete.fragment.LeftMenuFragment;
import com.concrete.logic.Logic;
import com.concrete.type.ChipInfo;
import com.concrete.type.SJBHInfo;
import com.concrete.type.UserInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.concrete.logic.Logic.*;

/**
 * Created by Tangxl on 2017/11/25.
 */

public class AddCardActivity extends Activity implements IntentDef.OnFragmentListener {

    public final static int MAXCARD = 3;
    public final static int HANDLER_SETDEFAULT = 0x1110;

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
    private Logic mLogic = null;
    private String UUID  = null;
    private Context mContext = null;
    private ProgressDialog mProgressDialog = null;

    private final int mAddCardInfo[] =
    {
        R.string.key_pref_card_sjbh,
        R.string.key_pref_card_zzrq,
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
        if (savedInstanceState == null) {

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

            mEventHandler.sendEmptyMessage(HANDLER_SETDEFAULT);

        }
        InitNFCLocal();
        if(!mCardNum.isEmpty()){
            mCardNum.clear();
        }
        mLogic = new Logic(this);
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
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        DisableNFC();
        if (mSlidingMenu.isMenuShowing())
            mSlidingMenu.toggle();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        EnableNFC();
        mSlidingMenu.showContent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String str = Common.ByteArrayToHexString(tag.getId());
            long CardNum = Long.parseLong(str, 16);
            nlog.Info("NFC==============================["+str+"] ["+String.valueOf(CardNum)+"]");
            if(mCardNum.size() < MAXCARD)
            {
                int index = mCardNum.size();
                mCardNum.add(index,CardNum);
                mAddCardInfoFragment.SetPreference(mCardNumList[index],String.valueOf(CardNum));
                UploadChipInfo();
            }

        }
    }

    @Override
    public void OnFragmentReport(View view) {

    }

    @Override
    public void OnFragmentReport(String Id) {
        int mID = GetCardId(Id);
        switch(mID){
            case R.string.key_pref_card_yhfs:
            case R.string.key_pref_card_yplx:
            case R.string.key_pref_card_qddj:
                showpopwindow(mID);
                break;

            case R.string.key_pref_card_sjbh:
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
                showeditdwindows(mID);
                break;

            case R.string.key_pref_card_number1:
            case R.string.key_pref_card_number2:
            case R.string.key_pref_card_number3:
                break;

            default:
                nlog.Info("OnFragmentReport=============================["+Id+"]");
                break;
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

    public void showpopwindow(int Id){
        int Title = R.string.pref_card_yhfs;
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
        }

        new AlertDialog.Builder(this).setTitle(getResources().getString(Title)).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(mPopWindowStr, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAddProjectInfoFragment.SetPreference(mPopWindowID,mPopWindowStr[which]);
                        dialog.dismiss();
                    }
                }).show();
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

        nlog.Info("UploadChipInfo========================= mCardNum.size() ["+mCardNum.size()+"]");
        if(mCardNum.size() == MAXCARD){
            ArrayList<ChipInfo> item = new ArrayList<ChipInfo>();
            for(int i = 0; i < 1; i++){
                ChipInfo mChipInfo =  new ChipInfo(UUID,mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh), String.valueOf(mCardNum.get(i)), 1);
                item.add(i, mChipInfo);
            }
            ShowWaitDialog();
            mLogic.OperRFID(HANDLE_HTTP_INSERT_RFID,item,UserInfo.getInstance(this).GetUserLoginInfo(),mEventHandler);
        }else{

        }
    }

    public void UploadGongcheng(){
        ArrayList<SJBHInfo> item = new ArrayList<SJBHInfo>();
        SJBHInfo mSJBHInfo =  new SJBHInfo(UUID,
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sjbh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_zzrq),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gcmc),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_wtdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sgdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzr),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_jzbh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_gjbw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_yhfs),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_bzdw),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_qddj),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_yplx),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_phbbh),
                mAddProjectInfoFragment.GetPreference(R.string.key_pref_card_sclsh)
                );
        item.add(0, mSJBHInfo);
        mLogic.OperGongChenInfo(HANDLE_HTTP_INSERT_SJBH,item,UserInfo.getInstance(this).GetUserLoginInfo(),mEventHandler);
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

            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd");
            String mDate = mDateFormat.format(new java.util.Date());
            String SJBH = Common.getMac() +mDate+"0001";
            SetPreference(R.string.key_pref_card_sjbh, SJBH);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(null != mOnFragmentListener){
                mOnFragmentListener.OnFragmentReport(preference.getKey());
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    @SuppressLint("ValidFragment")
    class AddCardInfoFragment extends FragmentBase{

        public AddCardInfoFragment(Context context, int SelfId) {
            super(context, SelfId);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferenceaddcard_cardinfo);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(null != mOnFragmentListener){
                mOnFragmentListener.OnFragmentReport(preference.getKey());
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
    }

    class EventHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case HANDLER_SETDEFAULT:
                    UUID = java.util.UUID.randomUUID().toString();
                    mAddProjectInfoFragment.SetDefault();
                    break;

                case HANDLE_HTTP_INSERT_RFID:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        UploadGongcheng();
                    }else{
                        HideWaitDialog();
                        HttpToast(mContext,msg.arg1);
                    }
                    break;

                case HANDLE_HTTP_INSERT_SJBH:
                    HideWaitDialog();
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        HttpToast(mContext,R.string.toast_hit_upload_success);
                    }else{
                        HttpToast(mContext,msg.arg1);
                    }
                    break;
            }
        }
    }

    public void ShowWaitDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage(getText(R.string.toast_hit_upload_wait));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void HideWaitDialog()
    {
        if(null != mProgressDialog) {
            mProgressDialog.cancel();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void HttpToast(Context context,int Error){
        Toast.makeText(context, HttpEcho.GetHttpEcho(Error),Toast.LENGTH_SHORT).show();
    }
}
