package com.utlife.routercore;

import android.content.Context;

import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.routercore.router.RouterRequest;


/**
 * Created by wanglei on 2016/11/29.
 */

public interface UtlifeAction<T> {
    boolean isAsync(Context context, RouterRequest<T> routerRequest);

    UtlifeActionResult invoke(Context context, RouterRequest<T> routerRequest);

    String getName();

    Class<?> getParamBean();
}
