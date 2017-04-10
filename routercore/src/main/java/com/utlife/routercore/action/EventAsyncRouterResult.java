package com.utlife.routercore.action;

import com.utlife.routercore.router.UtlifeActionResult;

/**
 * 用于异步执行后发送事件
 * Created by xuqiang on 2017/4/7.
 */

public class EventAsyncRouterResult {
    public String requestId;
    public UtlifeActionResult actionResult;
}
