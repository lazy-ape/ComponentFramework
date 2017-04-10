package com.utlife.user.greedywallet.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.linked.annotion.Action;
import com.utlife.commonbeanandresource.bean.ProcessConfig;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.action.EventAsyncRouterResult;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.UtlifeActionResult;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by xuqiang on 2017/4/6.
 */
@Action(processName = ProcessConfig.MAIN_PROCESS_NAME,providerName = "greedywallet")
public class DialogAction implements UtlifeAction {
    @Override
    public boolean isAsync(Context context, RouterRequest routerRequest) {
        return false;
    }

    @Override
    public UtlifeActionResult invoke(final Context context, final RouterRequest routerRequest) {
               new MaterialDialog.Builder(context)
                       .title("提醒")
                       .content("这是一个测试弹出框")
                       .positiveText("确定")
                       .onPositive(new MaterialDialog.SingleButtonCallback() {
                           @Override
                           public void onClick(@NonNull MaterialDialog dialog, @NonNull com.afollestad.materialdialogs.DialogAction which) {
                               UtlifeActionResult result = new UtlifeActionResult.Builder()
                                        .code(UtlifeActionResult.CODE_SUCCESS)
                                        .msg("成功")
                                        .data("点击了确定")
                                        .build();
                               EventAsyncRouterResult routerResult = new EventAsyncRouterResult();
                               routerResult.requestId = routerRequest.getId();
                               routerResult.actionResult = result;
                               HermesEventBus.getDefault().post(routerResult);
                           }
                       }).show();
        return null;
    }

    @Override
    public String getName() {
        return "dialog";
    }

    @Override
    public Class<?> getParamBean() {
        return Object.class;
    }
}
