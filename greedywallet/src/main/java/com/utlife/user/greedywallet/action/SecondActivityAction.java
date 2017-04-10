package com.utlife.user.greedywallet.action;

import android.content.Context;
import android.content.Intent;

import com.linked.annotion.Action;
import com.utlife.commonbeanandresource.bean.ProcessConfig;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.user.greedywallet.SecondActivity;

/**
 * Created by xuqiang on 2017/4/6.
 */
@Action(processName = ProcessConfig.MAIN_PROCESS_NAME,providerName = "greedywallet")
public class SecondActivityAction implements UtlifeAction {
    @Override
    public boolean isAsync(Context context, RouterRequest routerRequest) {
        return false;
    }

    @Override
    public UtlifeActionResult invoke(Context context, RouterRequest routerRequest) {
        Intent i = new Intent(context, SecondActivity.class);
        context.startActivity(i);
        return new UtlifeActionResult.Builder().code(UtlifeActionResult.CODE_SUCCESS).msg("").build();
    }

    @Override
    public String getName() {
        return "second";
    }

    @Override
    public Class<?> getParamBean() {
        return Object.class;
    }
}
