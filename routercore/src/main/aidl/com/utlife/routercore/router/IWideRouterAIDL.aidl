// IRouterAIDL.aidl
package com.utlife.routercore.router;
import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.routercore.router.RouterRequest;
// Declare any non-default types here with import statements

interface IWideRouterAIDL {
    boolean checkResponseAsync(String domain,in RouterRequest routerRequset);
    UtlifeActionResult route(String domain,in RouterRequest routerRequest);
    boolean stopRouter(String domain);
}
