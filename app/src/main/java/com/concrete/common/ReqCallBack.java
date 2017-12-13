package com.concrete.common;


import android.os.Handler;

/**
 * Created by Tangxl on 2017/11/22.
 */

public interface ReqCallBack<T> {

    /**
     * 响应成功
     */
    void onReqSuccess(T result);

    /**
     * 响应失败
     */
    void onReqFailed(String errorMsg);
}
