package com.utlife.routercore;

import android.content.Context;

import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.routercore.router.RouterRequest;


/**
 * Created by wanglei on 2016/12/28.
 */

public class ErrorAction implements UtlifeAction {

    private static final String DEFAULT_MESSAGE = "Something was really wrong. Ha ha!";
    private int mCode;
    private String mMessage;
    private boolean mAsync;
    public ErrorAction() {
        //mCode = MaActionResult.CODE_ERROR;
        mMessage = DEFAULT_MESSAGE;
        mAsync = false;
    }

    public ErrorAction(boolean isAsync,int code, String message) {
        this.mCode = code;
        this.mMessage = message;
        this.mAsync = isAsync;
    }

    @Override
    public boolean isAsync(Context context, RouterRequest requestData) {
        return mAsync;
    }

    @Override
    public UtlifeActionResult invoke(Context context, RouterRequest requestData) {
        UtlifeActionResult result = new UtlifeActionResult.Builder()
                .code(mCode)
                .msg(mMessage)
                .data(null)
                .result(null)
                .build();
        return result;
    }

    @Override
    public String getName() {
        return "error";
    }

    @Override
    public Class<?> getParamBean() {
        return Object.class;
    }

}
