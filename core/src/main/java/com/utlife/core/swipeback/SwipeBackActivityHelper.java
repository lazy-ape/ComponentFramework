package com.utlife.core.swipeback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.utlife.core.R;
import com.utlife.core.utils.CommonConstant;


/**
 * @author Yrom
 */
public class SwipeBackActivityHelper {

    public static final String PRE_SWIPE_BACK_EDGE_MODE = "pre_swipe_back_edge_mode";

    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("deprecation")
    public void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mActivity).inflate(R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                Utils.convertActivityToTranslucent(mActivity);
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    //保存回退模式
    public static void setSwipeBackEdgeMode(Context context,String name ,int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonConstant.PRE_FILE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(name,value).commit();
    }

    //获取回退模式
    public static int  getSwipeBackEdgeMode(Context context,String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonConstant.PRE_FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(name,0);
    }


}
