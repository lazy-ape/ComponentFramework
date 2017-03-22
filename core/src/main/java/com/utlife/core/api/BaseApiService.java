package com.utlife.core.api;



import com.utlife.core.bean.BaseResponseData;
import com.utlife.core.http.okhttp.OkHttpClientFactory;
import com.utlife.core.http.retrofit.CustomGsonConverterFactory;
import com.utlife.core.http.retrofit.ExceptionHandle;
import com.utlife.core.http.retrofit.RequestHelper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xuqiang on 2017/1/5.
 */

public class BaseApiService {

    public static final String base_url = "http://192.168.60.42";
    protected OkHttpClient mOKOkHttpClient;
    protected RequestHelper mRequestHelper;
    protected Retrofit mRetrofit;
    public BaseApiService(RequestHelper requestHelper){
        this.mRequestHelper = requestHelper;
        this.mOKOkHttpClient = OkHttpClientFactory.INSTANCE.getInstance();
    }

    public Retrofit createRetrofit(String baseUrl){
        if(mRetrofit == null){
            mRetrofit =
                    new Retrofit.Builder().addConverterFactory(CustomGsonConverterFactory.create())
                            .client(mOKOkHttpClient)
                            .baseUrl(baseUrl)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
        }
        return mRetrofit;
    }

    public Observable.Transformer schedulersTransformer() {
        return new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable)  observable).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    //针对没有错误码的使用
    public  Observable.Transformer transformer2() {

        return new Observable.Transformer() {

            @Override
            public Object call(Object observable) {
                return ((Observable) observable).onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    //针对返回值含有错误码的使用
    public <T> Observable.Transformer<BaseResponseData<T>, T> transformer() {

        return new Observable.Transformer() {

            @Override
            public Object call(Object observable) {
                return ((Observable) observable).onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }


    private class HandleFuc<T> implements Func1<BaseResponseData<T>, T> {
        @Override
        public T call(BaseResponseData<T> response) {
            if (!response.isSuccess()) throw new RuntimeException(response.getErrorCode() + "" + response.getErrorMsg() != null ? response.getErrorMsg(): "");
            return response.getData();
        }
    }


    private static class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override public Observable<T> call(Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

}
