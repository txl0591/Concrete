package com.concrete.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.concrete.app.AddCardActivity;
import com.concrete.app.LoginActivity;
import com.concrete.app.R;
import com.concrete.app.ReadCardActivity;
import com.concrete.app.SysInfoActivity;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.type.UserInfo;

/**
 * Created by Tangxl on 2017/10/2.
 */

@SuppressLint("ValidFragment")
public class LeftMenuFragment extends FragmentBase implements IntentDef.OnLogUserReportListener {
    private PreferenceScreen mUserPreferenceScreen = null;

    public LeftMenuFragment(Context context, int SelfId) {
        super(context, SelfId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencemenu);
        mUserPreferenceScreen = (PreferenceScreen) getPreferenceScreen().findPreference(getString(R.string.key_pref_menu_login_user));
        mUserPreferenceScreen.setSummary(getString(R.string.pref_sys_logout));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if(preference.getKey().equals(getString(R.string.key_pref_menu_sysinfo))){
            StartSysInfo();
        } else if(preference.getKey().equals(getString(R.string.key_pref_menu_readcard))){
            StartReadCard();
        }else if(preference.getKey().equals(getString(R.string.key_pref_menu_login_user))){
            StartLogin();
        } else if(preference.getKey().equals(getString(R.string.key_pref_menu_addcard))){
            StartAddCard();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void setUserName(){
        if(UserInfo.getInstance(mContext).GetLoginState()){
            mUserPreferenceScreen.setSummary(UserInfo.getInstance(mContext).GetUserName());
        }else {
            mUserPreferenceScreen.setSummary(getString(R.string.pref_sys_logout));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nlog.Info("LeftMenuFragment ===================== onResume");
        setUserName();
    }

    private void StartSysInfo(){
        Intent intent= new Intent(mContext, SysInfoActivity.class);
        startActivity(intent);
    }

    private void StartReadCard(){
        Intent intent= new Intent(mContext, ReadCardActivity.class);
        startActivity(intent);
    }

    private void StartLogin(){
        Intent intent= new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }

    private void StartAddCard(){
        Intent intent= new Intent(mContext, AddCardActivity.class);
        startActivity(intent);
    }


    @Override
    public void OnLogUserReport(boolean State) {
        setUserName();
    }
}
