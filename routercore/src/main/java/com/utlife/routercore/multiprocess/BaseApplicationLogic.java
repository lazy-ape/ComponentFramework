package com.utlife.routercore.multiprocess;

import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.utlife.routercore.UtlifeRouterApplication;


/**
 * Created by wanglei on 2016/11/25.
 */

public class BaseApplicationLogic {
    protected UtlifeRouterApplication mApplication;
    public BaseApplicationLogic() {
    }

    public void setApplication(@NonNull UtlifeRouterApplication application) {
        mApplication = application;
    }

    public void onCreate() {
    }

    public void onTerminate() {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
