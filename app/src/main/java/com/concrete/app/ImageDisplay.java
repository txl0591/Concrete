package com.concrete.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TabHost;

import com.concrete.common.Common;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;
import com.concrete.ctrl.CommonBase;
import com.concrete.logic.CommonLoigic;
import com.concrete.logic.SqliteLogic;
import com.concrete.net.HttpDef;
import com.concrete.net.HttpDownload;
import com.concrete.net.HttpLogic;
import com.concrete.type.ImageInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.concrete.common.IntentDef.HttpState.*;

/**
 * Created by Tangxl on 2017/12/24.
 */

public class ImageDisplay extends Activity {

    private static final int HANDLE_LOAD = 0xE010;

    private long mRFID = 0;
    private String mSJBH = null;
    private ImageView mImageShow = null;
    private ImageLoadingDialog mImageLoadingDialog = null;
    private Handler mHandler = null;
    private HttpLogic mHttpLogic = null;
    private String FileName = null;
    private String UUID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageShow = findViewById(R.id.ImageShow);
        Bundle bundle = this.getIntent().getExtras();
        mRFID = bundle.getLong("RFID");
        UUID = bundle.getString("UUID");
        mSJBH = bundle.getString("SJBH");
        nlog.Info("ImageDisplay======["+mRFID+"] UUID ["+UUID+"] UUID ["+mSJBH+"]");
        mHttpLogic = new HttpLogic(this);
        mImageLoadingDialog = new ImageLoadingDialog(this);
        mImageLoadingDialog.show();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what)
                {
                    case HANDLE_LOAD:
                        new DownLoadThread().start();
                        break;

                    case DOWNLOAD_START:
                        break;
                    case DOWNLOAD_ING:
                        break;
                    case DOWNLOAD_SUCCESS:
                        ShowImage();
                        break;
                    case DOWNLOAD_ERROR:
                        mImageLoadingDialog.dismiss();
                        break;

                    case  UPLOAD_START:
                        break;
                    case  UPLOAD_ING:
                        break;
                    case  UPLOAD_SUCCESS:
                        break;
                    case  UPLOAD_ERROR:
                        break;

                    default:
                        break;
                }
            }
        };
        Message msg = new Message();
        msg.what = HANDLE_LOAD;
        mHandler.sendMessageAtTime(msg,100);

    }

    private void ShowImage(){
        mImageLoadingDialog.dismiss();
        String SavePath = IntentDef.DEFAULT_PATH+"/";
        String Path = SavePath+FileName;
        Bitmap mBitmap = getLoacalBitmap(Path);
        if(null != mBitmap){
            mImageShow.setImageBitmap(mBitmap);
        }
    }

    private void DownLoadFile(){
        if(0 != mRFID){
            ImageInfo mImageInfo = CommonLoigic.QueryImage(this,null,mHttpLogic,mRFID,mSJBH);
            if(mImageInfo != null){
                FileName = mImageInfo.cIMG_UUID + ".jpg";
                String SavePath = IntentDef.DEFAULT_PATH+"/";
                String Path = HttpDef.INTENT_DOWNLOAD_ADDR+FileName;
                nlog.Info("DownLoadFile =========== ["+SavePath+FileName+"]");
                if(!Common.IsFileExit(SavePath+FileName)){

                    HttpDownload.DownloadFile(Path,FileName,SavePath,new IntentDef.OnHttpReportListener(){
                        @Override
                        public void OnHttpDataReport(int Oper, long param1, long param2) {
                            mHandler.sendEmptyMessage(Oper);
                        }
                    });
                }else{
                    mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                }
            }else{
                mImageLoadingDialog.dismiss();
            }
        }else{
            FileName = UUID + ".jpg";
            String SavePath = IntentDef.DEFAULT_PATH+"/";
            String Path = HttpDef.INTENT_DOWNLOAD_ADDR+FileName;
            nlog.Info("DownLoadFile =========== ["+SavePath+FileName+"]");
            if(!Common.IsFileExit(SavePath+FileName)){

                HttpDownload.DownloadFile(Path,FileName,SavePath,new IntentDef.OnHttpReportListener(){
                    @Override
                    public void OnHttpDataReport(int Oper, long param1, long param2) {
                        mHandler.sendEmptyMessage(Oper);
                    }
                });
            }else{
                mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
            }
        }


    }

    class DownLoadThread extends Thread {
        @Override
        public void run() {
            super.run();
            DownLoadFile();
        }
    }


    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHttpLogic.Close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }



    class ImageLoadingDialog extends Dialog {

        public ImageLoadingDialog(Context context) {
            super(context, R.style.ImageloadingDialogStyle);
            //setOwnerActivity((Activity) context);// 设置dialog全屏显示
        }

        private ImageLoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_imageloading);
        }

    }
}
