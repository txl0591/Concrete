package com.concrete.net;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.concrete.common.IntentDef.HttpState.*;


/**
 * Created by Tangxl on 2017/6/10.
 */

public class HttpDownload {

    public static IntentDef.OnHttpReportListener mOnHttpReportListener = null;
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");

    public static void DownloadFile(String urlStr, String fileName, String savePath, IntentDef.OnHttpReportListener Listener)
    {
        mOnHttpReportListener = Listener;
        nlog.Info("URL ["+urlStr+"] savePath ["+savePath+"] fileName ["+fileName+"]");
        DownloadHttp(urlStr,savePath,fileName);
    }

    public static void HttpReport(int oper, long param1, long param2){
        if(mOnHttpReportListener != null){
            mOnHttpReportListener.OnHttpDataReport(oper,param1,param2);
        }
        nlog.Info("Download ============= ["+oper+"] param1 ["+param1+"] param2 ["+param2+"]");
    }

    public static void DownloadFileThread(String urlStr, String fileName, String savePath, IntentDef.OnHttpReportListener Listener)
    {
        mOnHttpReportListener = Listener;
        nlog.Info("URL ["+urlStr+"] savePath ["+savePath+"] fileName ["+fileName+"]");
         new DownloadThread(urlStr,savePath,fileName).start();

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

    public static <T> void DownloadHttp(String fileUrl, String destFileDir, String fileName) {
        final File file = new File(destFileDir, fileName);
        if (file.exists()) {
            HttpReport(DOWNLOAD_SUCCESS,0,0);
            return;
        }

        nlog.Info("fileUrl ======== ["+fileUrl+"]");

        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //failedCallBack("下载失败", callBack);
                HttpReport(DOWNLOAD_ERROR,0,0);
                nlog.Info("onFailure======================");
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
                    nlog.Info("onResponse=========Eror=============");
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
