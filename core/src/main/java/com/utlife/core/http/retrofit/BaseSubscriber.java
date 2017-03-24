package com.utlife.core.http.retrofit;


import com.utlife.core.BaseActivity;
import com.utlife.core.utils.NetworkUtils;
import com.utlife.core.utils.ToastUtils;

import rx.Subscriber;

/**
 * Created by xuqiang on 2017/1/4.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    protected boolean mIsNetworkAvailable;
    private BaseActivity context;

    public BaseSubscriber(BaseActivity context) {
        this.context = context;
    }
    @Override
    public void onStart() {
        super.onStart();
        mIsNetworkAvailable = NetworkUtils.isNetWorkEnable(context);
        if(!mIsNetworkAvailable){
            ToastUtils.showLong(context,"当前网络不可用，请检查网络");
            onCompleted();
            return;
        }
    }


    @Override
    public void onError(Throwable e) {
        if(e instanceof ExceptionHandle.ResponseThrowable){
            onError((ExceptionHandle.ResponseThrowable)e);
        } else {
            onError(new ExceptionHandle.ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    public abstract void onError(ExceptionHandle.ResponseThrowable e);
}
