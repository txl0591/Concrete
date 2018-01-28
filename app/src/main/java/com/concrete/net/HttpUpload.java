package com.concrete.net;

import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static com.concrete.common.IntentDef.HttpState.*;

/**
 * Created by Tangxl on 2017/7/10.
 */

public class HttpUpload {

    public static IntentDef.OnHttpReportListener mOnHttpReportListener = null;

    private static final int TIME_OUT = 5 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    public static void UploadFile(String urlStr, String fileName, IntentDef.OnHttpReportListener Listener)
    {
        mOnHttpReportListener = Listener;
        File mFile = new File(fileName);
        if(mFile.exists()){
            uploadFile(mFile,urlStr);
        }
    }

    public static void SetOnHttpReportListener(IntentDef.OnHttpReportListener Listener){
        mOnHttpReportListener = Listener;
    }

    public static void HttpReport(int oper, long param1, long param2){
        if(mOnHttpReportListener != null){
            mOnHttpReportListener.OnHttpDataReport(oper,param1,param2);
        }
        nlog.Info("HttpUpload ============= ["+oper+"] param1 ["+param1+"] param2 ["+param2+"]");
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

    /**
     * Android上传文件到服务端
     *
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--";
        String LINE_END = "\r\n";
        //String CONTENT_TYPE = "multipart/form-data;charset=utf-8"; // 内容类型
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        nlog.Info("RequestURL ["+RequestURL+"]");
        HttpDownload.HttpReport(UPLOAD_START,0,0);
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true); // 允许输入流
                conn.setDoOutput(true); // 允许输出流
                conn.setUseCaches(true); // 不允许使用缓存
                conn.setRequestMethod("POST"); // 请求方式
                conn.setRequestProperty("Charset", CHARSET); // 设置编码
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            } catch (IOException e) {
                e.printStackTrace();
                nlog.Info("openConnection ============= Error");
                HttpReport(UPLOAD_ERROR,0,0);
            }

            if (file != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition:form-data:name=\"file\";filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type:application/octet-stream"+LINE_END+LINE_END);
                dos.write(sb.toString().getBytes());

                InputStream is = new FileInputStream(file);
                long maxlen = file.length();
                long index = 0;
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();

                StringBuffer Image = new StringBuffer();
                Image.append(LINE_END);
                Image.append(PREFIX);
                Image.append(BOUNDARY);
                Image.append(PREFIX);
                Image.append(LINE_END);
                dos.write(Image.toString().getBytes());
                dos.flush();
                nlog.Info("Send==============================");
                int res = conn.getResponseCode();
                nlog.Info("response code:" + res);

                if(res==200)
                {
                    nlog.Info("request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    nlog.Info("result : " + result);
                    HttpReport(UPLOAD_SUCCESS,0,0);
                 }
                 else{
                    nlog.Info("request error");
                    HttpReport(UPLOAD_ERROR,0,0);
                 }
            }else{
                nlog.Info("No File ============= Error");
                HttpReport(UPLOAD_ERROR,0,0);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
