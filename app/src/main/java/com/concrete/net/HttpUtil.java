package com.concrete.net;

import android.content.Context;
import android.os.Handler;

import com.concrete.common.AESUtils;
import com.concrete.common.nlog;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tangxl on 2017/11/22.
 */

public class HttpUtil {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static volatile HttpUtil mInstance;//单利引用
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_PARAM_JSON = 2;//post请求参数为json
    public static final int TYPE_POST_FORM = 3;//post请求参数为表单
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信


    public HttpUtil(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    public static HttpUtil getInstance(Context context) {
        HttpUtil inst = mInstance;
        if (inst == null) {
            synchronized (HttpUtil.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new HttpUtil(context);
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2");
        return builder;
    }




    private String requestGetBySyn(String actionUrl, int encrypt, HashMap<String, String> paramsMap, String Json) {
        String Echo = null;
        Request request = null;
        String requestUrl = null;
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            if(null != paramsMap){
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }

                if(encrypt == 0){
                    requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
                }else{
                    String ParamsEN = AESUtils.encode(tempParams.toString());
                    requestUrl = String.format("%s?%s", actionUrl, ParamsEN);
                }
                request = addHeaders().url(requestUrl).build();
                nlog.Info("requestGetByAsyn requestUrl ["+requestUrl+"]");
            }else{
                requestUrl = String.format("%s", actionUrl);
                nlog.Info("requestGetByAsyn requestUrl ["+requestUrl+"] Json.getBytes().toString() ["+Json.getBytes().toString()+"]");

                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, Json);
                request = addHeaders().url(requestUrl).post(body).build();
            }
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            final Response response = call.execute();
            if (response.isSuccessful()) {
                Echo = response.body().string();
            }
        } catch (Exception e) {
            nlog.Info(e.toString());
        }

        return Echo;
    }

    private String requestPostJsonBySyn(String actionUrl, int encrypt, HashMap<String, String> paramsMap, String Json) {
        String Echo = null;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }

            nlog.Info("tempParams.toString() ["+tempParams.toString()+"]");

            String requestUrl = null;
            if(encrypt == 0){
                requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
            }else{
                String ParamsEN = AESUtils.encode(tempParams.toString());
                requestUrl = String.format("%s?%s", actionUrl, ParamsEN);
            }

            nlog.Info("requestUrl ["+requestUrl+"]");

            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, Json);
            final Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            //请求执行成功
            if (response.isSuccessful()) {
                Echo = response.body().string();
            }
        } catch (Exception e) {
            nlog.Info( e.toString());
        }

        return Echo;
    }

    private String requestPostBySyn(String actionUrl, int encrypt, HashMap<String, String> paramsMap) {
        String Echo = null;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String params = tempParams.toString();
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            String requestUrl = actionUrl;
            final Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            //请求执行成功
            if (response.isSuccessful()) {
                Echo = response.body().string();
            }
        } catch (Exception e) {
            nlog.Info( e.toString());
        }

        return Echo;
    }

    private String requestPostBySynWithForm(String actionUrl, HashMap<String, String> paramsMap) {
        String Echo = null;
        try {
            //创建一个FormBody.Builder
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key, paramsMap.get(key));
            }
            //生成表单实体对象
            RequestBody formBody = builder.build();
            //补全请求地址
            String requestUrl = actionUrl;
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(formBody).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            if (response.isSuccessful()) {
                Echo = response.body().string();
            }
        } catch (Exception e) {
            nlog.Info( e.toString());
        }

        return Echo;
    }

    public String SendRequest(String actionUrl, int requestType, int encrypt, HashMap<String, String> paramsMap, String Json) {
        String Echo = null;
        switch (requestType) {
            case TYPE_GET:
                Echo = requestGetBySyn(actionUrl, encrypt, paramsMap, Json);
                break;

            case TYPE_POST_JSON:
                Echo = requestPostJsonBySyn(actionUrl, encrypt, paramsMap, Json);
                break;

            case TYPE_POST_PARAM_JSON:
                Echo = requestPostBySyn(actionUrl, encrypt, paramsMap);
                break;

            case TYPE_POST_FORM:
                Echo = requestPostBySynWithForm(actionUrl, paramsMap);
                break;
        }

        return Echo;
    }

    private <T> Call requestGetByAsyn(String actionUrl, int encrypt, HashMap<String, String> paramsMap, String Json, final ReqCallBack<T> callBack) {
        String requestUrl = null;
        Request request = null;
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            if(null != paramsMap){
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }

                if(encrypt == 0){
                    requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
                }else{
                    String ParamsEN = AESUtils.encode(tempParams.toString());
                    requestUrl = String.format("%s?%s", actionUrl, ParamsEN);
                }
                request = addHeaders().url(requestUrl).build();
                nlog.Info("requestGetByAsyn requestUrl ["+requestUrl+"]");
            }else{
                requestUrl = String.format("%s", actionUrl);
                nlog.Info("requestGetByAsyn requestUrl ["+requestUrl+"] Json.getBytes().toString() ["+Json.getBytes().toString()+"]");

                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, Json);
                request = addHeaders().url(requestUrl).post(body).build();
            }

            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    nlog.Info( e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            nlog.Info( e.toString());
        }
        return null;
    }

    private <T> Call requestPostByAsyn(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String params = tempParams.toString();
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            String requestUrl = actionUrl;
            final Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    nlog.Info( e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        nlog.Info( "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            nlog.Info( e.toString());
        }
        return null;
    }

    private <T> Call requestPostJsonByAsyn(String actionUrl, int encrypt, HashMap<String, String> paramsMap, String Json, final ReqCallBack<T> callBack) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }

            nlog.Info("tempParams.toString() ["+tempParams.toString()+"]");

            String requestUrl = null;
            if(encrypt == 0){
                requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
            }else{
                String ParamsEN = AESUtils.encode(tempParams.toString());
                requestUrl = String.format("%s?%s", actionUrl, ParamsEN);
            }

            nlog.Info("requestUrl ["+requestUrl+"]");

            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, Json);
            final Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    nlog.Info( e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        nlog.Info( "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            nlog.Info( e.toString());
        }
        return null;
    }

    private <T> Call requestPostByAsynWithForm(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
            RequestBody formBody = builder.build();
            String requestUrl = actionUrl;
            final Request request = addHeaders().url(requestUrl).post(formBody).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    nlog.Info( e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        nlog.Info("response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            nlog.Info( e.toString());
        }
        return null;
    }

    public <T> Call postrequest(String actionUrl, int requestType, int encrypt, HashMap<String, String> paramsMap, String Json, ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetByAsyn(actionUrl, encrypt, paramsMap, Json, callBack);
                break;

            case TYPE_POST_JSON:
                call = requestPostJsonByAsyn(actionUrl, encrypt, paramsMap, Json, callBack);
                break;

            case TYPE_POST_PARAM_JSON:
                call = requestPostByAsyn(actionUrl, paramsMap, callBack);
                break;

            case TYPE_POST_FORM:
                call = requestPostByAsynWithForm(actionUrl, paramsMap, callBack);
                break;
        }
        return call;
    }

    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }
}
