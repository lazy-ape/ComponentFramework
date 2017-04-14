package com.utlife.user;

import android.content.Context;

import com.linked.annotion.Action;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.UtlifeActionResult;

/**
 * Created by xuqiang on 2017/3/24.
 */
@Action(processName = "com.utlife.user", providerName = "main")
public class MainAction implements UtlifeAction {
    @Override
    public boolean isAsync(Context context, RouterRequest routerRequest) {
        return false;
    }

    @Override
    public UtlifeActionResult invoke(Context context, RouterRequest routerRequest) {
        return null;
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public Class<?> getParamBean() {
        return Object.class;
    }
}
