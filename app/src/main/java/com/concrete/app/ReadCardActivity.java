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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.concrete.common.CardManager;
import com.concrete.common.Common;
import com.concrete.ctrl.CommonBase;
import com.concrete.logic.CommonLoigic;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.ctrl.FragmentBase;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.ChipInfoList;
import com.concrete.type.ImageInfo;
import com.concrete.type.SJBHInfo;
import com.concrete.type.SJBHInfoList;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import static com.concrete.common.Common.ByteArrayToHexString;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;

/**
 * Created by Tangxl on 2017/11/18.
 */

public class ReadCardActivity extends Activity implements IntentDef.OnFragmentListener {

    private final static int HANDLER_READCARD_ONlY = 0xF1F1;
    private final static int HANDLER_READCARD_NET = 0xF1F2;
    private static final int HANDLER_DAILOG_SHOW = 0xF1F3;
    private static final int HANDLER_DAILOG_HIDE = 0xF1F4;
    private static final int HANDLER_FLASH_LIST = 0xF1F5;
    private static final int HANDLER_CLEAR = 0xF1F6;
    private static final int HANDLER_QUIT = 0xF1F7;
    private static final int HANDLER_LOADIMAGE = 0xF1F8;

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private ReadCardInfoFragment mReadCardInfoFragment = null;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private long mCardNum = 0;
    private HandlerEvent mHandlerEvent = null;
    private HttpLogic mHttpLogic = null;
    private Context mContext = null;
    private HttpBroadCast mHttpBroadCast = null;
    private CommonBase mCommonBase = null;
    private boolean mIsExit;
    private ArrayList<ImageInfo> mImageList = null;
    private final int mReadCardInfo[] =
    {
            R.string.key_pref_title_card_info,
            R.string.key_pref_card_sjbh,
            R.string.key_pref_card_card,
            R.string.key_pref_card_gcmc,
            R.string.key_pref_card_wtdw,
            R.string.key_pref_card_sgdw,
            R.string.key_pref_card_gjbw,
            R.string.key_pref_card_jzdw,
            R.string.key_pref_card_jzr,
            R.string.key_pref_card_jzbh,
            R.string.key_pref_card_bzdw,
            R.string.key_pref_card_phbbh,
            R.string.key_pref_card_yhfs,
            R.string.key_pref_card_qddj,
            R.string.key_pref_card_sclsh,
            R.string.key_pref_card_yplx,
            R.string.key_pref_card_zzrq,
            R.string.key_pref_card_jcjg,
            R.string.key_pref_card_wtbh,
            R.string.key_pref_card_ypbh,
            R.string.key_pref_card_zhz,
            R.string.key_pref_card_kyqd,
            R.string.key_pref_card_sysj,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_readcard);
        mContext = this;
        if (savedInstanceState == null) {

            mReadCardInfoFragment = new ReadCardInfoFragment(this,0);
            mReadCardInfoFragment.setOnFragmentListener(this);
            getFragmentManager().beginTransaction()
                    .add(R.id.read_layout1, mReadCardInfoFragment)
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
        }
        mCommonBase = new CommonBase(this);
        mHttpLogic = new HttpLogic(this);
        mHandlerEvent = new HandlerEvent(this);
        InitNFCLocal();
        mHttpBroadCast = new HttpBroadCast(this,mHandlerEvent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mHttpLogic.Close();
        mHttpBroadCast.Close();
        nlog.Info("ReadActivity=============================onDestroy");
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

    public int GetCardId(String Id){
        int mId = 0;

        for(int i = 0; i < mReadCardInfo.length; i++){
            if (Id.equals(getResources().getString(mReadCardInfo[i]))){
                mId = mReadCardInfo[i];
                break;
            }
        }
        return mId;
    }

    private void InitNFCLocal(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(null != mNfcAdapter){
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        }
        if(false == Common.IsNfcOpen(mContext)){
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

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String str = ByteArrayToHexString(tag.getId());
            mCardNum = Long.parseLong(str, 16);
            Message msg = new Message();
            msg.what = HANDLER_READCARD_NET;
            Bundle mBundle = new Bundle();
            mBundle.putString("RFID",String.valueOf(mCardNum));
            msg.setData(mBundle);
            mHandlerEvent.sendMessage(msg);
        }
    }

    @Override
    public void OnFragmentReport(View view) {

    }

    public void StartImageActivity(String UUID){
        String Path = null;
        Intent intent =new Intent(this,ImageDisplay.class);
        Bundle bundle=new Bundle();
        bundle.putLong("RFID", 0);
        bundle.putString("UUID", UUID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void OnFragmentReport(int Id) {
        if(Id == R.string.key_pref_card_sjbh){
            new ReadImageThread().start();
        }else{
            ShowFragmentInfo(Id);
        }

    }

    private void ShowImageList(){
        if(mImageList != null && mImageList.size() > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.pref_image_lsit);
            String[] cities = new String[mImageList.size()];
            for(int i = 0; i < mImageList.size(); i++){
                cities[i] = mImageList.get(i).cIMG_UUID;
            }
            builder.setItems(cities, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    StartImageActivity(mImageList.get(which).cIMG_UUID);
                }
            });
            builder.show();
        }

    }

    public void ShowFragmentInfo(int Id){

        Preference mPreference = mReadCardInfoFragment.GetPreferenceParam(Id);
        if(mPreference != null && mPreference.getSummary() != null){
            new  AlertDialog.Builder(this)
                    .setTitle(mPreference.getTitle().toString())
                    .setMessage(mPreference.getSummary().toString())
                    .setPositiveButton(getResources().getString(R.string.hit_ok) ,  null )
                    .show();
        }
    }

    @SuppressLint("ValidFragment")
    class ReadCardInfoFragment extends FragmentBase {

        private Preference mCardPreference = null;
        private ArrayList<Preference> mPreferenceList = null;


        public ReadCardInfoFragment(Context context, int SelfId) {
            super(context, SelfId);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencereadcard);
            mPreferenceList = new ArrayList<Preference>();
            for(int i = 0; i < mReadCardInfo.length; i++)
            {
                Preference mPreference = findPreference(getResources().getString(mReadCardInfo[i]));
                mPreferenceList.add(mPreference);
            }

            mCardPreference = findPreference(getResources().getString(R.string.key_pref_card_card));
        }

        public String GetPreference(int Id){
            Preference mPreference = findPreference(getResources().getString(Id));
            if(mPreference.getSummary() != null){
                return mPreference.getSummary().toString();
            }
            return null;
        }

        public Preference GetPreferenceParam(int Id){
            Preference mPreference = findPreference(getResources().getString(Id));
            return mPreference;
        }

        public Preference GetPreferenceParam(String Id){
            Preference mPreference = findPreference(Id);
            return mPreference;
        }

        public void ClearRFID()
        {
            for(int i = 1; i < mPreferenceList.size(); i++)
            {
                Preference mPreference = mPreferenceList.get(i);
                mPreference.setSummary(" ");
            }
        }

        public void UpDataRFIDFromInfoList(ArrayList<String> Value)
        {
            ClearRFID();
            for(int i = 1; i < mPreferenceList.size(); i++)
            {
                Preference mPreference = mPreferenceList.get(i);
                if(Value.get(i-1) != null){
                    mPreference.setSummary(Value.get(i-1));
                }
            }
        }

        public void UpDataRFIDFromSJBHInfoList(SJBHInfo mSJBHInfo){
            int index = 0;
            ArrayList<String> Value =  new ArrayList<String>();
            Value.add(index++,mSJBHInfo.TBL_SJBH);
            Value.add(index++,String.valueOf(mCardNum));
            Value.add(index++,mSJBHInfo.TBL_GCMC);
            Value.add(index++,mSJBHInfo.TBL_WTDW);
            Value.add(index++,mSJBHInfo.TBL_SGDW);
            Value.add(index++,mSJBHInfo.TBL_GJBW);
            Value.add(index++,mSJBHInfo.TBL_JZDW);
            Value.add(index++,mSJBHInfo.TBL_JZR);
            Value.add(index++,mSJBHInfo.TBL_JZBH);
            Value.add(index++,mSJBHInfo.TBL_BZDW);
            Value.add(index++,mSJBHInfo.TBL_PHBBH);
            Value.add(index++,mSJBHInfo.TBL_YHFS);
            Value.add(index++,mSJBHInfo.TBL_QDDJ);
            Value.add(index++,mSJBHInfo.TBL_SCLSH);
            Value.add(index++,mSJBHInfo.TBL_YPLX);
            Value.add(index++,mSJBHInfo.TBL_ZZRQ);
            Value.add(index++,"");//jcjg
            Value.add(index++,""); //wtbh
            Value.add(index++,""); // ypbh
            Value.add(index++,""); // zhz
            Value.add(index++,""); // kyqd
            Value.add(index++,""); // sysj
            Value.add(index++,""); // sysj
            UpDataRFIDFromInfoList(Value);
        }

        public void UpDataRFIDNumber(String num){
            mCardPreference.setSummary(num);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(null != mOnFragmentListener){
                mOnFragmentListener.OnFragmentReport(GetCardId(preference.getKey()));
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    class HandlerEvent extends Handler{

        private Context mContext;

        public HandlerEvent(Context context){
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case HANDLER_READCARD_ONlY:
                    mReadCardInfoFragment.UpDataRFIDNumber(String.valueOf(mCardNum));
                    break;

                case HANDLER_READCARD_NET:
                    new SyncInfoThread(Long.decode(msg.getData().getString("RFID"))).start();
                    break;

                case HANDLER_DAILOG_SHOW :
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_load_wait);
                    break;

                case HANDLER_DAILOG_HIDE:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_FLASH_LIST: {
                    SJBHInfo mSJBHInfo  = (SJBHInfo) msg.getData().getSerializable("PARAM");
                    mReadCardInfoFragment.UpDataRFIDFromSJBHInfoList(mSJBHInfo);
                    break;
                }

                case HANDLER_CLEAR:
                    mReadCardInfoFragment.ClearRFID();
                    break;

                case HANDLER_QUIT:
                    break;

                case HANDLER_LOADIMAGE:
                    ShowImageList();
                    break;

                default:
                    break;
            }
        }
    }

    class ReadImageThread extends Thread{
        @Override
        public void run() {
            super.run();
            mHandlerEvent.sendEmptyMessage(HANDLER_DAILOG_SHOW);
            String mSJBH = mReadCardInfoFragment.GetPreference(R.string.key_pref_card_sjbh);
            if(mImageList != null && mImageList.isEmpty()){
                mImageList.clear();
            }
            mImageList = CommonLoigic.QueryImageFromSJBH(mContext,null,mHttpLogic,mSJBH);
            mHandlerEvent.sendEmptyMessage(HANDLER_DAILOG_HIDE);
            mHandlerEvent.sendEmptyMessage(HANDLER_LOADIMAGE);
        }
    }

    class SyncInfoThread extends Thread{

        private long mRFID ;

        public SyncInfoThread(Long RFID){
            mRFID = RFID;
        }

        @Override
        public void run() {
            super.run();

            mHandlerEvent.sendEmptyMessage(HANDLER_CLEAR);
            mHandlerEvent.sendEmptyMessage(HANDLER_DAILOG_SHOW);
            mHandlerEvent.sendEmptyMessage(HANDLER_READCARD_ONlY);
            ChipInfo mChipInfo = CommonLoigic.QueryLastRFID(mContext,null,mHttpLogic,mRFID);
            if(mChipInfo != null){
                SJBHInfo mSJBHInfo = CommonLoigic.QueryGCProject(mContext,null,mHttpLogic,mChipInfo.TBL_SJBH);
                Message Msg = new Message();
                Msg.what = HANDLER_FLASH_LIST;
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("PARAM",mSJBHInfo);
                Msg.setData(mBundle);
                mHandlerEvent.sendMessage(Msg);
            }
            mHandlerEvent.sendEmptyMessage(HANDLER_DAILOG_HIDE);
        }
    }

    private void QuitOper(){
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle(mContext.getResources().getString(R.string.toast_hit_quit));
        mAlertDialog.setPositiveButton(mContext.getResources().getString(R.string.hit_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mHandlerEvent.sendEmptyMessage(HANDLER_QUIT);
            }
        });
        mAlertDialog.setNegativeButton(mContext.getResources().getString(R.string.hit_cancle), null);
        mAlertDialog.show();
    }

    private void Quit(){
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            if (mIsExit) {
                Quit();
            } else {
                Toast.makeText(this, R.string.toast_hit_quit, Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
