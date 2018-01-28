package com.concrete.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.concrete.common.Common;
import com.concrete.common.NetUtil;
import com.concrete.common.nlog;
import com.concrete.ctrl.CommonBase;
import com.concrete.database.SysParam;
import com.concrete.logic.CommonLoigic;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpDef;
import com.concrete.net.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.net.HttpLogic;
import com.concrete.type.ProjectInfoList;
import com.concrete.type.UserClass;
import com.concrete.type.UserEcho;
import com.concrete.type.UserInfo;
import com.concrete.type.UserType;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import static com.concrete.common.NetUtil.NETWORN_NONE;
import static com.concrete.ctrl.LeftMenuFragment.BROADCAST_MENU;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;
import static com.concrete.net.HttpDef.INTENT_HANDLE_HTTP_ECHO;


/**
 * Created by Tangxl on 2017/11/22.
 */

public class LoginActivity extends Activity implements IntentDef.OnFragmentListener, View.OnClickListener {

    public final static boolean DEBUG  = true;

    public final static String LOGIN_MODE = "LOGIN_MODE";
    public final static String LOGIN_MODE_PARAM = "LOGIN_MODE_PARAM";

    public static final int LOGIN_MODE_IN = 0xAA01;
    public static final int LOGIN_MODE_OUT = 0xAA02;
    public static final int LOGIN_MODE_REGISTER = 0xAA03;


    private static final int HANDLER_EXIT = 0xAB21;
    public final static int HANDLER_SHOW_WAIT = 0xAB22;
    public final static int HANDLER_HIDE_WAIT = 0xAB23;
    public final static int HANDLER_HIDE_TOAST = 0xAB24;
    public final static int HANDLER_MODE_LOGIN = 0xAB25;

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private EditText et_username = null;
    private EditText et_password = null;
    private EditText et_usertype = null;
    private EditText et_userdw = null;
    private TextView btn_login = null;
    private TextView btn_logout = null;
    private HttpEvent mHttpEvent = null;
    private HttpLogic mLogic = null;
    private HttpBroadCast mHttpBroadCast = null;
    private Context mContext = null;
    private CommonBase mCommonBase = null;
    private SysParam mSysParam = null;

    private int mPAGEMode = LOGIN_MODE_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mPAGEMode = getIntent().getIntExtra(LOGIN_MODE, LOGIN_MODE_IN);
        setContentView(R.layout.activity_login);

        mSysParam = new SysParam(this);
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.left_menu);
        mCommonBase = new CommonBase(this);

        mLeftMenuFragment = new LeftMenuFragment(this,0);
        getFragmentManager().beginTransaction()
                .add(R.id.left_menuconfig, mLeftMenuFragment)
                .commit();

        mLeftMenuFragment.setOnFragmentListener(this);
        mLogic = new HttpLogic(this);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_usertype = findViewById(R.id.et_usertype);
        et_userdw = findViewById(R.id.et_userdw);
        et_usertype.setOnClickListener(this);
        et_usertype.setVisibility(View.INVISIBLE);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(this);
        SetDefault();
        mHttpEvent = new HttpEvent();
        mHttpBroadCast = new HttpBroadCast(this,mHttpEvent);
    }

    private void SetDefault(){
        if(DEBUG){
            et_username.setText(UserInfo.getInstance(this).GetUserName());
            et_password.setText(UserInfo.getInstance(this).GetPassword());
            et_usertype.setText(UserInfo.getInstance(this).GetUserTypeName());
        }

        UserInfo.getInstance(this).SetUserInfo(mSysParam.GetUserName(),mSysParam.GetUserPasswd());
    }

    private void SetLogoutMode(){
        mPAGEMode = LOGIN_MODE_OUT;
        et_username.setText(UserInfo.getInstance(this).GetUserName());
        UserInfo.getInstance(this).SetUserInfo("", "", UserType.USERTYPE_SG, "");
        et_username.setEnabled(false);
        et_password.setVisibility(View.INVISIBLE);
        et_password.setVisibility(View.INVISIBLE);
        btn_logout.setVisibility(View.INVISIBLE);
        et_userdw.setVisibility(View.INVISIBLE);
        btn_login.setText(R.string.hit_quit);
    }

    private void SetLoginMode(){
        mPAGEMode = LOGIN_MODE_IN;
        et_username.setText(UserInfo.getInstance(this).GetUserName());
        et_username.setEnabled(true);
        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_password.setText(UserInfo.getInstance(this).GetPassword());
        btn_logout.setText(R.string.pref_sys_logout_regeister);
        btn_login.setText(R.string.pref_sys_login_oper);
        et_usertype.setVisibility(View.INVISIBLE);
        et_userdw.setVisibility(View.INVISIBLE);
    }

    private void SetRegisterMode(){
        et_usertype.setVisibility(View.VISIBLE);
        et_username.setText("");
        et_username.setEnabled(true);
        et_password.setText("");
        et_userdw.setVisibility(View.VISIBLE);
        et_userdw.setText("");
        et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        mPAGEMode = LOGIN_MODE_REGISTER;
        btn_logout.setText(R.string.hit_ok);
        btn_login.setText(R.string.hit_cancle);
        UserInfo.getInstance(mContext).SetUserType(UserType.USERTYPE_SG);
        et_usertype.setText(UserInfo.getInstance(this).GetUserTypeName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHttpBroadCast.Start();
        if(UserInfo.getInstance(mContext).GetLoginState()){
            SetLogoutMode();
        }else{
            SetLoginMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHttpBroadCast.Close();
    }

    @Override
    public void OnFragmentReport(View view) {

    }

    @Override
    public void OnFragmentReport(int Id) {

    }

    private void register_oper(){
        String username= et_username.getText().toString().trim();
        String password= et_password.getText().toString().trim();
        String type= et_usertype.getText().toString().trim();
        String dw= et_userdw.getText().toString().trim();
        if(!username.isEmpty() && !password.isEmpty() && !dw.isEmpty()){
            new RegisterThread().start();
        }else{
            mCommonBase.Toast(R.string.toast_hit_register_error_user,null);
        }
    }

    private void login_oper(){
        String username= et_username.getText().toString().trim();
        String password= et_password.getText().toString().trim();
        String type= et_usertype.getText().toString().trim();
        if(!username.isEmpty() && !password.isEmpty()){
            UserInfo.getInstance(this).SetUserInfo(username,password);
            new LoginThread().start();
        }else{
            mCommonBase.Toast(R.string.toast_hit_register_error_user,null);
        }
    }

    private void cancle_oper(){
        mCommonBase.ShowWaitDialog(R.string.toast_hit_login_wait);
        mLogic.LogOper(HANDLE_HTTP_LOGOUT, UserInfo.getInstance(this).GetUserLoginInfo());
    }

    public void showpopwindow(){
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.user_type)).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(getResources().getStringArray(R.array.YHLX_REG), (UserInfo.getInstance(mContext).GetUserType()-1),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UserInfo.getInstance(mContext).SetUserType(which+1);
                        mLeftMenuFragment.setUserType();
                        et_usertype.setText(UserInfo.getInstance(mContext).GetUserTypeName());
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_login:
                if(mPAGEMode == LOGIN_MODE_IN){
                    login_oper();
                }else if(mPAGEMode == LOGIN_MODE_OUT){
                    cancle_oper();
                }else if(mPAGEMode == LOGIN_MODE_REGISTER){
                    if(UserInfo.getInstance(mContext).GetLoginState()){
                        SetLogoutMode();
                    }else{
                        SetLoginMode();
                    }
                }

                break;

            case R.id.btn_logout:
                if(mPAGEMode == LOGIN_MODE_IN){
                    SetRegisterMode();
                }else if(mPAGEMode == LOGIN_MODE_REGISTER){
                    register_oper();
                }
                break;

            case R.id.et_usertype:
                showpopwindow();
                break;
        }
    }

    class HttpEvent extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){

                case HANDLE_HTTP_LOGOUT:
                    UserInfo.getInstance(mContext).SetLoginState(false);
                    mLeftMenuFragment.setUserName();
                    getApplicationContext().sendBroadcast(new Intent(BROADCAST_MENU));
                    mCommonBase.HideWaitDialog();
                    finish();
                    break;

                case HANDLER_SHOW_WAIT:
                    mCommonBase.ShowWaitDialog(R.string.toast_hit_login_wait);
                    break;

                case HANDLER_HIDE_WAIT:
                    mCommonBase.HideWaitDialog();
                    break;

                case HANDLER_MODE_LOGIN:
                    SetLoginMode();
                    break;

                case HANDLER_HIDE_TOAST:
                    mCommonBase.Toast(msg.arg1,null);
                    break;

                case HANDLER_EXIT:
                    finish();
                    break;
            }
        }
    }

    class LoginThread extends Thread{

        @Override
        public void run() {
            boolean ret = false;
            super.run();
             if (NETWORN_NONE != NetUtil.getNetworkState(mContext)){
                 mHttpEvent.sendEmptyMessage(HANDLER_SHOW_WAIT);
                 UserEcho mUserEcho = mLogic.LogOperlock(HANDLE_HTTP_LOGIN, UserInfo.getInstance(mContext).GetUserLoginInfo());
                 if(mUserEcho != null && mUserEcho.result){
                     UserInfo.getInstance(mContext).SetLoginState(true);
                     UserInfo.getInstance(mContext).SetUserInfo( et_username.getText().toString(), et_password.getText().toString(),  Integer.valueOf(mUserEcho.items.TBL_USERTYPE), mUserEcho.items.TBL_USERDanWei);
                     SysParam.getInstance(mContext).SetUserName( UserInfo.getInstance(mContext).GetUserName());
                     SysParam.getInstance(mContext).SetUserPasswd( UserInfo.getInstance(mContext).GetPassword());
                     mContext.sendBroadcast(new Intent(BROADCAST_MENU));
                     ret =true;
                 }
                 mHttpEvent.sendEmptyMessage(HANDLER_HIDE_WAIT);
                 mHttpEvent.sendEmptyMessage(HANDLER_EXIT);
                 if(ret == false){
                     Message msg = new Message();
                     msg.what = HANDLER_HIDE_TOAST;
                     msg.arg1 = R.string.toast_hit_login_fail;
                     mHttpEvent.sendMessage(msg);
                 }
             }else{
                Message msg = new Message();
                 msg.what = HANDLER_HIDE_TOAST;
                 msg.arg1 = R.string.toast_hit_net_error;
                 mHttpEvent.sendMessage(msg);
             }
        }
    }

    class RegisterThread extends Thread {
        @Override
        public void run() {
            boolean ret = false;
            super.run();
            if (NETWORN_NONE != NetUtil.getNetworkState(mContext)) {
                mHttpEvent.sendEmptyMessage(HANDLER_SHOW_WAIT);
                Message msg = new Message();
                msg.what = HANDLER_HIDE_TOAST;
                String username = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String type = et_usertype.getText().toString().trim();
                String dw = et_userdw.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty()) {
                    mHttpEvent.sendEmptyMessage(HANDLER_SHOW_WAIT);
                    UserClass mUserClass = new UserClass(username, password, type, dw);
                    ArrayList<UserClass> items = new ArrayList<UserClass>();
                    items.add(0, mUserClass);
                    if (mLogic.OperUserInfolock(HANDLE_HTTP_INSERT_USER, items, UserInfo.getInstance(mContext).GetUserInfo())) {
                        msg.arg1 = R.string.toast_hit_register_success;
                    } else {
                        msg.arg1 = R.string.toast_hit_register_error;
                    }

                } else {
                    msg.arg1 = R.string.toast_hit_register_error;
                }
                mHttpEvent.sendEmptyMessage(HANDLER_HIDE_WAIT);
                mHttpEvent.sendMessage(msg);
                mHttpEvent.sendEmptyMessage(HANDLER_MODE_LOGIN);

            }
        }
    }

}
