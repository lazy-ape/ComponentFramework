package com.utlife.routercore.router;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.utlife.routercore.UtlifeRouterApplication;


/**
 * Created by wanglei on 2016/11/29.
 */

public class LocalRouterConnectService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MRCS","onBind");
        return stub;
    }

    ILocalRouterAIDL.Stub stub = new ILocalRouterAIDL.Stub() {

        @Override
        public boolean checkResponseAsync(RouterRequest routerRequest) throws RemoteException {
            return LocalRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication()).
                    answerWiderAsync(routerRequest);
        }

        @Override
        public UtlifeActionResult route(RouterRequest routerRequest) {
            try {
                LocalRouter localRouter = LocalRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication());
                RouterRequest routerRequest1 = routerRequest;
                return localRouter.route(LocalRouterConnectService.this,routerRequest1);
            } catch (Exception e) {
                e.printStackTrace();
                return new UtlifeActionResult.Builder().msg(e.getMessage()).build();
            }
        }

        @Override
        public boolean stopWideRouter() throws RemoteException {
            LocalRouter
                    .getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                    .disconnectWideRouter();
            return true;
        }

        @Override
        public void connectWideRouter() throws RemoteException {
            LocalRouter
                    .getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                    .connectWideRouter();
        }
    };
}
