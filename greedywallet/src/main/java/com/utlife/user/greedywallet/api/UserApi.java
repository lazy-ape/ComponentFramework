package com.utlife.user.greedywallet.api;

import com.utlife.core.api.BaseApiService;
import com.utlife.core.bean.BaseResponseData;
import com.utlife.core.http.retrofit.RequestHelper;

import rx.Observable;

/**
 * Created by xuqiang on 2017/3/20.
 */

public class UserApi extends BaseApiService {
    UserApiService userApiService;
    public UserApi(RequestHelper requestHelper) {
        super(requestHelper);
        mRetrofit = createRetrofit(base_url);
        userApiService = mRetrofit.create(UserApiService.class);
    }

    public Observable<BaseResponseData<String>> getToken(){
        return userApiService.getQiniuToken().compose(schedulersTransformer()).compose(this.transformer2());
    }

}
