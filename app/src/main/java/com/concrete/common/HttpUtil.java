package com.concrete.common;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.concrete.type.ChipInfoList;
import com.concrete.type.JsonEcho;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.concrete.common.IntentDef.HttpState.*;


/**
 * Created by Tangxl on 2017/6/10.
 */

public class HttpUtil {

    public static IntentDef.OnHttpReportListener mOnHttpReportListener = null;
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");

    public static void DownloadFile(String urlStr, String fileName, String savePath, IntentDef.OnHttpReportListener Listener)
    {
        mOnHttpReportListener = Listener;
        new DownloadThread(urlStr, fileName, savePath).start();
    }

    public static void UploadFile(String urlStr, String fileName, IntentDef.OnHttpReportListener Listener)
    {
        mOnHttpReportListener = Listener;
        new UploadThread(urlStr, fileName).start();
    }

    public static void HttpReport(int oper, long param1, long param2){
        if(mOnHttpReportListener != null){
            mOnHttpReportListener.OnHttpDataReport(oper,param1,param2);
        }
        nlog.Info("Download ============= ["+oper+"] param1 ["+param1+"] param2 ["+param2+"]");
    }

    static class DownloadThread extends Thread{

        private String Url;
        private String FileName;
        private String SavePath;

        public DownloadThread(String urlStr, String savePath, String  fileName){
            Url = urlStr;
            FileName = fileName;
            SavePath = savePath;
            File path1 = new File(SavePath);
            if (!path1.exists()) {
                path1.mkdirs();
            }
        }

        @Override
        public void run() {
            super.run();
            DownloadHttp(Url,SavePath,FileName);
        }
    }

    static class UploadThread extends Thread{

        private String Url;
        private String FileName;

        public UploadThread(String urlStr, String fileName){
            Url = urlStr;
            FileName = fileName;
        }

        @Override
        public void run() {
            super.run();
            File mFile = new File(FileName);
            HttpUpload.uploadFile(mFile,Url);
        }
    }

    public static <T> void DownloadHttp(String fileUrl, String destFileDir, String fileName) {
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            HttpReport(DOWNLOAD_SUCCESS,0,0);
            return;
        }
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //failedCallBack("下载失败", callBack);
                HttpReport(DOWNLOAD_ERROR,0,0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    HttpReport(DOWNLOAD_START, total, 0);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        HttpReport(DOWNLOAD_ING, total, current);
                    }
                    fos.flush();
                    HttpReport(DOWNLOAD_SUCCESS,0,0);
                } catch (IOException e) {
                    HttpReport(DOWNLOAD_ERROR,0,0);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
