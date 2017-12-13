package com.concrete.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.concrete.common.HttpEcho;
import com.concrete.common.IntentDef;
import com.concrete.fragment.LeftMenuFragment;
import com.concrete.logic.Logic;
import com.concrete.type.UserInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import static com.concrete.logic.Logic.*;


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

    private Logic mLogic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
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
            mLogic = new Logic(this);
        }

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_usertype = findViewById(R.id.et_usertype);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        SetDefault();
        mHttpEvent = new HttpEvent(this);
    }

    private void SetDefault(){
        et_username.setText(UserInfo.getInstance(this).GetUserName());
        et_password.setText(UserInfo.getInstance(this).GetPassword());
        et_usertype.setText(UserInfo.getInstance(this).GetUserType());
    }

    @Override
    public void OnFragmentReport(View view) {

    }

    @Override
    public void OnFragmentReport(String Id) {

    }

    private void login_oper(){
        ShowWaitDialog();
        mLogic.LogOper(HANDLE_HTTP_LOGIN, UserInfo.getInstance(this).GetUserLoginInfo(), mHttpEvent);
    }

    private void cancle_oper(){
        finish();
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

        private Context mContext = null;

        public HttpEvent(Context context){
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){

                case HANDLE_HTTP_LOGIN:
                    if(msg.arg1 == HttpEcho.SUCCESS){
                        UserInfo.getInstance(mContext).SetLoginState(true);
                        UserInfo.getInstance(mContext).SetUserInfo( et_username.getText().toString(), et_password.getText().toString(),  et_usertype.getText().toString(), 1);
                        finish();
                    }else{
                        UserInfo.getInstance(mContext).SetLoginState(false);
                        Toast.makeText(mContext,HttpEcho.GetHttpEcho(msg.arg1),Toast.LENGTH_SHORT).show();
                    }
                    HideWaitDialog();
                    break;

                case HANDLE_HTTP_LOGOUT:
                    break;
            }
        }
    }
}
