package com.utlife.user.ui.main;

import android.support.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.utlife.core.BaseActivity;

/**
 * Created by xuqiang on 2017/4/11.
 */

public class MainPresenter implements MainContract.Presenter,BDLocationListener {

    private BaseActivity activity;

    public MainPresenter(BaseActivity activity){
        this.activity = activity;
    }

    private MainContract.View mView;
    @Override
    public void attachView(@NonNull MainContract.View view) {
        this.mView = view;
    }

    LocationClient mLocationClient = null;
    private void initLocation(){
        mLocationClient = new LocationClient(activity);
        mLocationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
                bdLocation.getLocType()  ==  BDLocation.TypeNetWorkLocation ||
                bdLocation.getLocType() == BDLocation.TypeOffLineLocation){
            //定位成功
            mLocationClient.stop();
            mView.updateLocation(bdLocation);
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
