package com.utlife.routercore.router;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.tools.Logger;



/**
 * Created by wanglei on 2016/11/29.
 */

public final class WideRouterConnectService extends Service {
    private static final String TAG = "WideRouterConnectService";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!(getApplication() instanceof UtlifeRouterApplication)) {
            throw new RuntimeException("Please check your AndroidManifest.xml and make sure the application is instance of MaApplication.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String domain = intent.getStringExtra("domain");
        if (WideRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication()).mIsStopping) {
            Logger.e(TAG, "Bind error: The wide router is stopping.");
            return null;
        }
        if (domain != null && domain.length() > 0) {
            boolean hasRegistered = WideRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication()).checkLocalRouterHasRegistered(domain);
            if (!hasRegistered) {
                Logger.e(TAG, "Bind error: The local router of process " + domain + " is not bidirectional." +
                        "\nPlease create a Service extend LocalRouterConnectService then register it in AndroidManifest.xml and the initializeAllProcessRouter method of MaApplication." +
                        "\nFor example:" +
                        "\n<service android:name=\"XXXConnectService\" android:process=\"your process name\"/>" +
                        "\nWideRouter.registerLocalRouter(\"your process name\",XXXConnectService.class);");
                return null;
            }
            WideRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication()).connectLocalRouter(domain);
        } else {
            Logger.e(TAG, "Bind error: Intent do not have \"domain\" extra!");
            return null;
        }
        return stub;
    }

    IWideRouterAIDL.Stub stub = new IWideRouterAIDL.Stub() {

        @Override
        public boolean checkResponseAsync(String domain, RouterRequest routerRequest) throws RemoteException {
            return
                    WideRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                            .answerLocalAsync(domain, routerRequest);
        }

        @Override
        public UtlifeActionResult route(String domain, RouterRequest routerRequest) {
            try {
                return WideRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                        .route(domain, routerRequest);
            } catch (Exception e) {
                e.printStackTrace();
                return new UtlifeActionResult.Builder()
                        .code(UtlifeActionResult.CODE_ERROR)
                        .msg(e.getMessage())
                        .build();
            }
        }

        @Override
        public boolean stopRouter(String domain) throws RemoteException {
            return WideRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                    .disconnectLocalRouter(domain);
        }

    };
}
