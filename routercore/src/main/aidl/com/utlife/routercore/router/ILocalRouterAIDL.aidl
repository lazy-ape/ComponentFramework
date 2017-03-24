package com.utlife.routercore.router;
import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.routercore.router.RouterRequest;

interface ILocalRouterAIDL {
    boolean checkResponseAsync(in RouterRequest routerRequset);
    UtlifeActionResult route(in RouterRequest routerRequest);
    boolean stopWideRouter();
    void connectWideRouter();
}
