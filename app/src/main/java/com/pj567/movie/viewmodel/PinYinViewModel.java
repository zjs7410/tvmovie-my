package com.pj567.movie.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.pj567.movie.api.ApiConfig;

/**
 * @author pj567
 * @date :2020/12/18
 * @description: 首字母转汉字
 */
public class PinYinViewModel extends ViewModel {
    public MutableLiveData<String> pinYinResult;

    public PinYinViewModel() {
        pinYinResult = new MutableLiveData<>();
    }

    public void getChineseCharacters(String pinYin) {
        OkGo.<String>get(ApiConfig.PinYinUrl)
                .params("gn", "p")
                .params("py", pinYin)
                .params("number", 1)
                .execute(new AbsCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        pinYinResult.postValue(response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        pinYinResult.postValue(pinYin);
                    }

                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        String result = "";
                        if (response.body() == null) {
                            result = "";
                        } else {
                            result = response.body().string();
                            if ("查询不到结果".equals(result)) {
                                result = "";
                            }
                        }
                        return result;
                    }
                });
    }
}