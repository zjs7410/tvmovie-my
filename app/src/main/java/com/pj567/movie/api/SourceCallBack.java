package com.pj567.movie.api;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
public class SourceCallBack<T> extends AbsCallback<T> {

    @Override
    public void onSuccess(Response<T> response) {

    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        if (response.body() != null) {
            return (T) response.body();
        } else {
            throw new IllegalStateException("网络请求错误");
        }
    }
}