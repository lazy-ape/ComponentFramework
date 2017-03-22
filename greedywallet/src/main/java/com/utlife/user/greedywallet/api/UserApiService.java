package com.utlife.user.greedywallet.api;

import com.utlife.core.bean.BaseResponseData;

import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by xuqiang on 2017/3/20.
 */

public interface UserApiService {

    @GET("getToken.php")
    Observable<BaseResponseData<String>> getQiniuToken();

}
