package com.concrete.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.concrete.common.CardManager;
import com.concrete.common.Common;
import com.concrete.common.CrcUtil;
import com.concrete.common.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.fragment.FragmentBase;
import com.concrete.fragment.LeftMenuFragment;
import com.concrete.logic.Logic;
import com.concrete.type.ChipInfo;
import com.concrete.type.ChipInfoList;
import com.concrete.type.SJBHInfo;
import com.concrete.type.SJBHInfoList;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import static com.concrete.common.Common.ByteArrayToHexString;
import static com.concrete.common.StringUtils.convertByteArrayToHexString;
import static com.concrete.common.StringUtils.convertHexStringToByteArray;
import static com.concrete.common.StringUtils.removeSpaces;
import static com.concrete.logic.Logic.*;

/**
 * Created by Tangxl on 2017/11/18.
 */

public class ReadCardActivity extends Activity implements IntentDef.OnFragmentListener {

    private final static int HANDLER_READCARD_ONlY = 0xF1F1;
    private final static int HANDLER_READCARD_NET = 0xF1F2;

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private ReadCardInfoFragment mReadCardInfoFragment = null;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private long mCardNum = 0;
    private HandlerEvent mHandlerEvent = null;
    private HttpHandlerEvent mHttpHandlerEvent = null;
    private ProgressDialog mProgressDialog = null;
    private Logic mLogic = null;
    private SJBHInfoList mSJBHInfoList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_readcard);
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
        mLogic = new Logic(this);
        mHandlerEvent = new HandlerEvent(this);
        mHttpHandlerEvent = new HttpHandlerEvent(this);
        InitNFCLocal();
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
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        nlog.Info("=================intent.getAction() ["+intent.getAction()+"]");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String str = ByteArrayToHexString(tag.getId());
            mCardNum = Long.parseLong(str, 16);
            nlog.Info("NFC==============================["+str+"] ["+String.valueOf(mCardNum)+"]");
            //readTagClassic(tag);
            readMifareClassic(tag);
//            Message msg = new Message();
//            msg.what = HANDLER_READCARD_NET;
//            Bundle mBundle = new Bundle();
//            mBundle.putString("RFID",String.valueOf(mCardNum));
//            msg.setData(mBundle);
//            mHandlerEvent.sendMessage(msg);
           // readTagClassic(tag);
        }
    }

    public void ShowWaitDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage(getText(R.string.toast_hit_load_wait));
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

    @Override
    public void OnFragmentReport(View view) {

    }

    @Override
    public void OnFragmentReport(String Id) {

    }

    @SuppressLint("ValidFragment")
    class ReadCardInfoFragment extends FragmentBase {

        private Preference mCardPreference = null;
        private ArrayList<Preference> mPreferenceList = null;
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
            nlog.Info("Update ============== ["+Value.size()+"] mPreferenceList.size() ["+mPreferenceList.size()+"]");
            ClearRFID();
            for(int i = 1; i < mPreferenceList.size(); i++)
            {
                Preference mPreference = mPreferenceList.get(i);
                if(Value.get(i-1) != null){
                    mPreference.setSummary(Value.get(i-1));
                }
            }
        }

        public void UpDataRFIDFromSJBHInfoList(SJBHInfoList List){
            int index = 0;
            ArrayList<String> Value =  new ArrayList<String>();
            SJBHInfo mSJBHInfo = List.items.get(0);
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
                mOnFragmentListener.OnFragmentReport(preference.getKey());
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
                    ShowWaitDialog();
                    mLogic.QueryRFID(msg.getData().getString("RFID"),mHttpHandlerEvent);
                    break;

                default:
                    break;
            }
        }
    }

    class HttpHandlerEvent extends Handler{
        private Context mContext;

        public HttpHandlerEvent(Context context){
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case HANDLE_HTTP_QUERY_RFID:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        ChipInfoList mChipInfoList = (ChipInfoList)msg.getData().getSerializable("HANDLE_HTTP_QUERY_RFID");
                        mChipInfoList.PrintChipInfoList();
                        ChipInfo mChipInfo = mChipInfoList.items.get(0);
                        mLogic.QuerySJBH(mChipInfo.TBL_SJBH,mHttpHandlerEvent);
                    }else{
                        mHandlerEvent.sendEmptyMessage(HANDLER_READCARD_ONlY);
                        HideWaitDialog();
                        HttpToast(mContext,msg.arg1);
                    }
                    break;

                case HANDLE_HTTP_QUERY_SJBH:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        mSJBHInfoList = (SJBHInfoList)msg.getData().getSerializable("HANDLE_HTTP_QUERY_SJBH");
                        mReadCardInfoFragment.UpDataRFIDFromSJBHInfoList(mSJBHInfoList);
                        HideWaitDialog();
                    }else{
                        mHandlerEvent.sendEmptyMessage(HANDLER_READCARD_ONlY);
                        HideWaitDialog();
                        HttpToast(mContext,msg.arg1);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void HttpToast(Context context,int Error){
        Toast.makeText(context, HttpEcho.GetHttpEcho(Error),Toast.LENGTH_SHORT).show();
    }

    public static byte[] setParamCRC(byte[] buf)
    {
        int MASK = 0x0001, CRCSEED = 0x0810;
        int remain = 0;

        byte val;
        for (int i = 0; i < buf.length; i++)
        {
            val = buf[i];
            for (int j = 0; j < 8; j++)
            {
                if (((val ^ remain) & MASK) != 0)
                {
                    remain ^= CRCSEED;
                    remain >>= 1;
                    remain |= 0x8000;
                }
                else
                {
                    remain >>= 1;
                }
                val >>= 1;
            }
        }

        byte[] crcByte = new byte[2+buf.length];

        for(int i = 0;i < buf.length; i++){
            crcByte[i] = buf[i];
        }
        crcByte[buf.length] = (byte) ((remain >> 8) & 0xff);
        crcByte[buf.length+1] = (byte) (remain & 0xff);

        // 将新生成的byte数组添加到原数据结尾并返回
        return crcByte;
    }

    public boolean readTagClassic(Tag tag) {

        NfcA nfca = NfcA.get(tag);

        try {
            nfca.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] atqa = nfca.getAtqa();

        nlog.Info("readTagClassic ============atqa ["+atqa[0]+" "+atqa[1]+"]");

        byte sak = (byte)nfca.getSak();
        nlog.Info("readTagClassic ============atqa ["+sak+"] nfca getMaxTransceiveLength ["+nfca.getMaxTransceiveLength()+"]");

        byte[] cmd = new byte[]{0x60,0x0c,0x00,0x00,0x00,0x00,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF, (byte)0xFF, (byte)0xFF};
        //(byte)0xFF,(byte)0xFF

        try {
            byte[] echo = nfca.transceive(cmd);
            nlog.Info("readTagClassic =======transceive=====OK ["+Common.toHexString(echo)+"]");
        } catch (IOException e) {
            e.printStackTrace();
            nlog.Info("readTagClassic =======transceive=====Error");
        }
        try {
            nfca.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    private Boolean isKeyMifareClassicEnable(MifareClassic mfc,int sectorIndex,byte[] myKeyA){
        boolean auth = false;
        try {
            auth = mfc.authenticateSectorWithKeyA(sectorIndex,
                    MifareClassic.KEY_DEFAULT);
            if(!auth){
                auth = mfc.authenticateSectorWithKeyA(sectorIndex,
                        myKeyA);
            }
            if(!auth){
                auth = mfc.authenticateSectorWithKeyA(sectorIndex,
                        MifareClassic.KEY_NFC_FORUM);
            }

            auth = mfc.authenticateSectorWithKeyB(sectorIndex,
                    MifareClassic.KEY_DEFAULT);
            if(!auth){
                auth = mfc.authenticateSectorWithKeyB(sectorIndex,
                        myKeyA);
            }
            if(!auth){
                auth = mfc.authenticateSectorWithKeyB(sectorIndex,
                        MifareClassic.KEY_NFC_FORUM);
            }
        } catch (IOException e) {
            nlog.Info("IOException while authenticateSectorWithKey MifareClassic...");
        }
        return auth;
    }

    public String readMifareClassic(Tag tag) {
        boolean auth = false;

       // byte[] mifarekey ={(byte) 0x13,(byte)0x59,(byte)0x94,(byte)0x46,(byte)0x81,(byte)0x3f};
        byte[] mifarekey ={(byte) 0xA0,(byte)0xA1,(byte)0xA2,(byte)0xA3,(byte)0xA4,(byte)0xA5};

        MifareClassic mfc = MifareClassic.get(tag);
        // 读取TAG
        try {
            String metaInfo = "";
            mfc.connect();
            int type = mfc.getType();// 获取TAG的类型
            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }

            if(mfc.isConnected()){
                nlog.Info("================Connect");
            }else{
                nlog.Info("==Dis==============Connect");
            }

            for(int i = 0; i < mfc.getSectorCount(); i++){
                boolean ret = mfc.authenticateSectorWithKeyA(i,MifareClassic.KEY_DEFAULT);
                nlog.Info("authenticateSectorWithKeyA ["+i+"] ret = ["+ret+"]");
            }

            metaInfo += "CardType: " + typeS + "\n Total" + sectorCount + "Sector\n"
                    + mfc.getBlockCount() + "Block\nSize: " + mfc.getSize()
                    + "B\n";
            for (int j = 0; j < sectorCount; j++) {
                // Authenticate a sector with key A.

                auth = isKeyMifareClassicEnable(mfc,j,mifarekey);
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + j + ":Success\n";
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        metaInfo += "Block " + bIndex + " : "
                                + ByteArrayToHexString(data) + "\n";
                        bIndex++;
                    }
                } else {
                    metaInfo += "Sector " + j + ":Failue\n";
                }
            }

            nlog.Info("metaInfo ========== ["+metaInfo+"]");
            mfc.close();
            return metaInfo;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        }


        return null;

    }

}
