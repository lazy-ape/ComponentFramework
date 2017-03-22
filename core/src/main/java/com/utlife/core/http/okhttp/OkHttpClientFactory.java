package com.utlife.core.http.okhttp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by xuqiang on 2017/3/20.
 */

public enum  OkHttpClientFactory {

    INSTANCE;
    private OkHttpClient instance;
    OkHttpClientFactory(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        builder.retryOnConnectionFailure(true);
        instance = builder.build();
    }

    public OkHttpClient getInstance(){
        return instance;
    }


}
