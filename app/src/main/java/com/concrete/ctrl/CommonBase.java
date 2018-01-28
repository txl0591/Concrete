package com.concrete.ctrl;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.concrete.app.R;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpEcho;
import com.concrete.net.HttpLogic;
import com.concrete.type.ChipInfo;
import com.concrete.type.SJBHInfo;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Tangxl on 2018/1/2.
 */

public class CommonBase {

    private Context mContext = null;
    private ProgressDialog mProgressDialog = null;

    public CommonBase(Context context){
        mContext = context;
    }

    public void ShowSJBHInfo(SJBHInfo mSJBHInfo, ArrayList<ChipInfo> mChipInfoOper, boolean edit, DialogInterface.OnClickListener Listener){

        int[] mId = {
                R.string.pref_card_sjbh,
                R.string.pref_card_card,
                R.string.pref_card_gcmc,
                R.string.pref_card_wtdw,
                R.string.pref_card_sgdw,
                R.string.pref_card_gjbw,
                R.string.pref_card_jzdw,
                R.string.pref_card_jzr,
                R.string.pref_card_jzbh,
                R.string.pref_card_bzdw,
                R.string.pref_card_phbbh,
                R.string.pref_card_yhfs,
                R.string.pref_card_qddj,
                R.string.pref_card_sclsh,
                R.string.pref_card_yplx,
                R.string.pref_card_zzrq,
                R.string.pref_card_jcjg,
                R.string.pref_card_wtbh,
                R.string.pref_card_ypbh,
                R.string.pref_card_zhz,
                R.string.pref_card_kyqd,
                R.string.pref_card_sysj,
                R.string.pref_card_syrq,
                R.string.pref_card_syjg,
        };
        String[] mList = new String[mId.length];

        for(int i = 0; i < mId.length; i++){
            switch(mId[i])
            {
                case R.string.pref_card_card:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_card)+": \r\n";
                    if(mChipInfoOper.size() > 0){
                        for(int k = 0; k < mChipInfoOper.size(); k++){
                            ChipInfo nChipInfo = mChipInfoOper.get(k);
                            if(k != (mChipInfoOper.size()-1)){
                                mList[i] += nChipInfo.RFID+ "\r\n";
                            }else {
                                mList[i] += nChipInfo.RFID;
                            }
                        }
                    }
                    break;

                case R.string.pref_card_sjbh:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_sjbh)+": \r\n";
                    if(mSJBHInfo.TBL_SJBH != null){
                        mList[i] += mSJBHInfo.TBL_SJBH;
                    }
                    mList[i] += "\r\n";
                    break;
                case R.string.pref_card_gcmc:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_gcmc)+": ";
                    if(mSJBHInfo.TBL_GCMC != null){
                        mList[i] += mSJBHInfo.TBL_GCMC;
                    }
                    break;
                case R.string.pref_card_wtdw:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_wtdw)+": ";
                    if(mSJBHInfo.TBL_WTDW != null){
                        mList[i] += mSJBHInfo.TBL_WTDW;
                    }
                    break;
                case R.string.pref_card_sgdw:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_sgdw)+": ";
                    if(mSJBHInfo.TBL_SGDW != null){
                        mList[i] += mSJBHInfo.TBL_SGDW;
                    }
                    break;
                case R.string.pref_card_gjbw:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_gjbw)+": ";
                    if(mSJBHInfo.TBL_GJBW != null){
                        mList[i] += mSJBHInfo.TBL_GJBW;
                    }
                    break;
                case R.string.pref_card_jzdw:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_jzdw)+": ";
                    if(mSJBHInfo.TBL_JZDW != null){
                        mList[i] += mSJBHInfo.TBL_JZDW;
                    }
                    break;
                case R.string.pref_card_jzr:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_jzr)+": ";
                    if(mSJBHInfo.TBL_JZR != null){
                        mList[i] += mSJBHInfo.TBL_JZR;
                    }
                    break;
                case R.string.pref_card_jzbh:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_jzbh)+": ";
                    if(mSJBHInfo.TBL_JZBH != null){
                        mList[i] += mSJBHInfo.TBL_JZBH;
                    }
                    break;
                case R.string.pref_card_bzdw:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_bzdw)+": ";
                    if(mSJBHInfo.TBL_BZDW != null){
                        mList[i] += mSJBHInfo.TBL_BZDW;
                    }
                    break;
                case R.string.pref_card_phbbh:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_phbbh)+": ";
                    if(mSJBHInfo.TBL_PHBBH != null){
                        mList[i] += mSJBHInfo.TBL_PHBBH;
                    }
                    break;
                case R.string.pref_card_yhfs:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_yhfs)+": ";
                    if(mSJBHInfo.TBL_YHFS != null){
                        mList[i] += mSJBHInfo.TBL_YHFS;
                    }
                    break;
                case R.string.pref_card_qddj:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_qddj)+": ";
                    if(mSJBHInfo.TBL_QDDJ != null){
                        mList[i] += mSJBHInfo.TBL_QDDJ;
                    }
                    break;
                case R.string.pref_card_sclsh:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_sclsh)+": ";
                    if(mSJBHInfo.TBL_SCLSH != null){
                        mList[i] += mSJBHInfo.TBL_SCLSH;
                    }
                    break;
                case R.string.pref_card_yplx:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_yplx)+": ";
                    if(mSJBHInfo.TBL_YPLX != null){
                        mList[i] += mSJBHInfo.TBL_YPLX;
                    }
                    break;
                case R.string.pref_card_zzrq:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_zzrq)+": ";
                    if(mSJBHInfo.TBL_ZZRQ != null){
                        mList[i] += mSJBHInfo.TBL_ZZRQ;
                    }
                    break;
                case R.string.pref_card_jcjg:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_jcjg)+": ";
                    if(mSJBHInfo.TBL_JCJG != null){
                        mList[i] += mSJBHInfo.TBL_JCJG;
                    }
                    break;

                case R.string.pref_card_wtbh:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_wtbh)+": ";
                    break;
                case R.string.pref_card_ypbh:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_ypbh)+": ";
                    break;
                case R.string.pref_card_zhz:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_zhz)+": ";
                    break;
                case R.string.pref_card_kyqd:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_kyqd)+": ";
                    break;
                case R.string.pref_card_sysj:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_sysj)+": ";
                    break;
                 case R.string.pref_card_syrq:
                     mList[i] = mContext.getResources().getString(R.string.pref_card_syrq)+": ";
                     if(mSJBHInfo.TBL_SYRQ != null){
                         mList[i] += mSJBHInfo.TBL_SYRQ;
                     }
                    break;

                case R.string.pref_card_syjg:
                    mList[i] = mContext.getResources().getString(R.string.pref_card_syjg)+": ";
                    if(mSJBHInfo.TBL_SYJG != null){
                        mList[i] += mSJBHInfo.TBL_SYJG;
                    }
                    break;

                default:
                    break;
            }
        }

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle(mContext.getResources().getString(R.string.pref_title_card_info));
        mAlertDialog.setItems(mList, null);
        mAlertDialog.setPositiveButton(mContext.getResources().getString(R.string.hit_ok), null);
        if(edit){
            mAlertDialog.setNegativeButton(mContext.getResources().getString(R.string.hit_edit), Listener);
        }
        mAlertDialog.show();
    }

    public void ShowWaitDialog(int ResId){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage(mContext.getText(ResId));
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

    public void ChooseDateDialog(DatePickerDialog.OnDateSetListener listener){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(mContext, 0,listener,year,month,day).show();
    }

    public void Toast(int Id, String str){
        if(0 != Id){
            Toast.makeText(mContext, Id,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, str,Toast.LENGTH_SHORT).show();
        }

    }
}
