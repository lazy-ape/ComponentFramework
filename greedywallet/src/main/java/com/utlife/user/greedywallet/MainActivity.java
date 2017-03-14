package com.utlife.user.greedywallet;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.utlife.core.BaseActivity;
import com.utlife.user.greedywallet.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {


    ActivityMainBinding activityMainBinding;
    @Override
    public View onCreateContentView(LayoutInflater inflater) {
        activityMainBinding = DataBindingUtil.inflate(inflater,R.layout.activity_main,null,false);
        return activityMainBinding.getRoot();
    }
    @Override
    public void initUI() {
        showProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showContent(false);
            }
        },3500);
    }

    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return false;
    }

}
