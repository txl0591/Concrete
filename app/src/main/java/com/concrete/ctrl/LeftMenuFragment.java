package com.concrete.ctrl;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.concrete.app.AddCardActivity;
import com.concrete.app.CollectActivity;
import com.concrete.app.CollectHistory;
import com.concrete.app.HistoryActivity;
import com.concrete.app.LoginActivity;
import com.concrete.app.R;
import com.concrete.app.ReadCardActivity;
import com.concrete.app.SysInfoActivity;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.type.UserInfo;

import static com.concrete.type.UserType.*;

/**
 * Created by Tangxl on 2017/10/2.
 */

@SuppressLint("ValidFragment")
public class LeftMenuFragment extends FragmentBase implements IntentDef.OnLogUserReportListener {

    public static final String BROADCAST_MENU = "com.concrete.ctrl.LeftMenuFragment";

    private PreferenceScreen mUserPreferenceScreen = null;
    private PreferenceCategory mCardAdd = null;
    private PreferenceCategory mCardCollete = null;
    private MenuBroadCast mMenuBroadCast = null;

    public LeftMenuFragment(Context context, int SelfId) {
        super(context, SelfId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencemenu_base);
        mUserPreferenceScreen = (PreferenceScreen) getPreferenceScreen().findPreference(getString(R.string.key_pref_menu_login_user));
        mUserPreferenceScreen.setSummary(getString(R.string.pref_sys_logout));
        mCardAdd = (PreferenceCategory) findPreference("CardAdd");
        getPreferenceScreen().removePreference(mCardAdd);

        mCardCollete = (PreferenceCategory) findPreference("CardCollete");
        getPreferenceScreen().removePreference(mCardCollete);
        Start();
        setUserName();
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
        } else if(preference.getKey().equals(getString(R.string.key_pref_menu_recordcaiji))){
            StartCollectHistory();
        }else if(preference.getKey().equals(getString(R.string.key_pref_menu_recordcard))){
            StartHistory();
        } else if(preference.getKey().equals(getString(R.string.key_pref_menu_collect))) {
            StartCollect();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void setUserName(){
        if(UserInfo.getInstance(mContext).GetLoginState()){
            mUserPreferenceScreen.setSummary(UserInfo.getInstance(mContext).GetUserName());
            switch(UserInfo.getInstance(mContext).GetUserType()){
                case USERTYPE_ADMIN:
                    nlog.Info("==========USERTYPE_ADMIN========");
                    getPreferenceScreen().addPreference(mCardAdd);
                    getPreferenceScreen().addPreference(mCardCollete);
                    break;
                case USERTYPE_SG:
                    nlog.Info("==========USERTYPE_SG========");
                    getPreferenceScreen().addPreference(mCardAdd);
                    break;
                case USERTYPE_JC:
                    nlog.Info("==========USERTYPE_JC========");
                    getPreferenceScreen().addPreference(mCardCollete);
                    break;
                case USERTYPE_JD:
                    nlog.Info("==========USERTYPE_JD========");
                    break;
            }
        }else {
            mUserPreferenceScreen.setSummary(getString(R.string.pref_sys_logout));
            getPreferenceScreen().removePreference(mCardAdd);
            getPreferenceScreen().removePreference(mCardCollete);
        }
    }

    public void setUserType(){
        mUserPreferenceScreen.setSummary(UserInfo.getInstance(mContext).GetUserTypeName());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Close();
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
        Bundle bundle=new Bundle();
        bundle.putInt(AddCardActivity.SC_MODE,AddCardActivity.SG_MODE_ADD);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void StartHistory(){
        Intent intent= new Intent(mContext, HistoryActivity.class);
        startActivity(intent);
    }

    private void StartCollectHistory(){
        Intent intent= new Intent(mContext, CollectHistory.class);
        startActivity(intent);
    }

    private void StartCollect(){
        Intent intent= new Intent(mContext, CollectActivity.class);
        startActivity(intent);
    }


    @Override
    public void OnLogUserReport(boolean State) {
        setUserName();
    }


    public void Start(){
        mMenuBroadCast = new MenuBroadCast();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_MENU);
        mContext.registerReceiver(mMenuBroadCast,intentFilter);
    }

    public void Close(){
        mContext.unregisterReceiver(mMenuBroadCast);
    }


    class MenuBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BROADCAST_MENU)){
                setUserName();
            }
        }
    }
}
