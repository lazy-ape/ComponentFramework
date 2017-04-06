package com.utlife.user.greedywallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.linked.annotion.Action;
import com.utlife.commonbeanandresource.bean.ProcessConfig;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.UtlifeActionResult;

/**
 * Created by xuqiang on 2017/3/24.
 */
@Action(processName = ProcessConfig.MAIN_PROCESS_NAME, providerName = "greedywallet")
public class GreedyWalletAction implements UtlifeAction {
    @Override
    public boolean isAsync(Context context, RouterRequest routerRequest) {
        return false;
    }

    @Override
    public UtlifeActionResult invoke(Context context, RouterRequest routerRequest) {
        if(context instanceof Activity){
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
        }else{
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        return new UtlifeActionResult.Builder().code(UtlifeActionResult.CODE_SUCCESS).msg("success").data("").build();
    }

    @Override
    public String getName() {
        return "index";
    }

    @Override
    public Class<?> getParamBean() {
        return null;
    }
}
