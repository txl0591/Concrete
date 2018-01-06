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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.concrete.common.Common;
import com.concrete.common.nlog;
import com.concrete.database.SysParam;
import com.concrete.net.HttpBroadCast;
import com.concrete.net.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.ctrl.LeftMenuFragment;
import com.concrete.net.HttpLogic;
import com.concrete.type.ProjectInfoList;
import com.concrete.type.UserInfo;
import com.concrete.type.UserType;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import static com.concrete.ctrl.LeftMenuFragment.BROADCAST_MENU;
import static com.concrete.net.HttpDef.HTTP_OPER_CMD.*;


/**
 * Created by Tangxl on 2017/11/22.
 */

public class LoginActivity extends Activity implements IntentDef.OnFragmentListener, View.OnClickListener {

    private SlidingMenu mSlidingMenu = null;
    private LeftMenuFragment mLeftMenuFragment = null;
    private EditText et_username = null;
    private EditText et_password = null;
    private EditText et_usertype = null;
    private TextView btn_login = null;
    private TextView btn_logout = null;
    private ProgressDialog mProgressDialog = null;
    private Button mButton = null;
    private Handler mHandler = null;
    private HttpEvent mHttpEvent = null;
    private HttpLogic mLogic = null;
    private HttpBroadCast mHttpBroadCast = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
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

        mLeftMenuFragment.setOnFragmentListener(this);
        mLogic = new HttpLogic(this);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_usertype = findViewById(R.id.et_usertype);
        et_usertype.setOnClickListener(this);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        SetDefault();
        mHttpEvent = new HttpEvent();
        mHttpBroadCast = new HttpBroadCast(this,mHttpEvent);
    }

    private void SetDefault(){

        et_username.setText(UserInfo.getInstance(this).GetUserName());
        et_password.setText(UserInfo.getInstance(this).GetPassword());
        et_usertype.setText(UserInfo.getInstance(this).GetUserTypeName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHttpBroadCast.Start();
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

    private void login_oper(){
        ShowWaitDialog();
        mLogic.LogOper(HANDLE_HTTP_LOGIN, UserInfo.getInstance(this).GetUserLoginInfo());
    }

    private void cancle_oper(){
        ShowWaitDialog();
        mLogic.LogOper(HANDLE_HTTP_LOGOUT, UserInfo.getInstance(this).GetUserLoginInfo());
    }

    public void showpopwindow(){
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.user_type)).setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(getResources().getStringArray(R.array.YHLX), UserInfo.getInstance(mContext).GetUserType(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UserInfo.getInstance(mContext).SetUserType(which);
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
                login_oper();
                break;

            case R.id.btn_logout:
                cancle_oper();
                break;

            case R.id.et_usertype:
                showpopwindow();
                break;
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

    class HttpEvent extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){

                case HANDLE_HTTP_LOGIN:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        UserType mUserType = (UserType)msg.getData().getSerializable("HANDLE_HTTP_LOGIN");
                        UserInfo.getInstance(mContext).SetLoginState(true);
                        UserInfo.getInstance(mContext).SetUserInfo( et_username.getText().toString(), et_password.getText().toString(),  Integer.valueOf(mUserType.TBL_USERTYPE), mUserType.TBL_USERDanWei);
                        mLogic.QueryGGInfo(et_username.getText().toString());
                        mContext.sendBroadcast(new Intent(BROADCAST_MENU));
                    }else{
                        UserInfo.getInstance(mContext).SetLoginState(false);
                        Toast.makeText(mContext,HttpEcho.GetHttpEcho(msg.arg1),Toast.LENGTH_SHORT).show();
                    }
                    HideWaitDialog();
                    break;

                case HANDLE_HTTP_LOGOUT:
                    UserInfo.getInstance(mContext).SetLoginState(false);
                    mLeftMenuFragment.setUserName();
                    getApplicationContext().sendBroadcast(new Intent(BROADCAST_MENU));
                    HideWaitDialog();
                    finish();
                    break;

                case HANDLE_HTTP_QUREY_GCID:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        ProjectInfoList mProjectInfoList = (ProjectInfoList)msg.getData().getSerializable("HANDLE_HTTP_QUREY_GCID");
                        UserInfo.getInstance(mContext).SetPrjectInfoList(mProjectInfoList.items);
                    }
                    finish();
                    break;
            }
        }
    }
}
