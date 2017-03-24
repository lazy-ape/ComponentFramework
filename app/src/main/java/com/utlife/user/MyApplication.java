package com.utlife.user;

import com.linked.annotion.Module;
import com.linked.annotion.Modules;
import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.router.WideRouter;
import com.utlife.user.greedywallet.GreedyWalletApplicationLogic;

/**
 * Created by xuqiang on 2017/3/24.
 */
@Modules(modules = {"greedywallet"})
public class MyApplication extends UtlifeRouterApplication {
    @Override
    public void initializeAllProcessRouter() {
        WideRouter.registerLocalRouter("com.utlife.user",MainRouterConnectService.class);
    }

    @Override
    protected void initializeLogic() {
        registerApplicationLogic("com.utlife.user",999,MainApplicationLogic.class);
        registerApplicationLogic("com.utlife.user",998, GreedyWalletApplicationLogic.class);
    }

    @Override
    public boolean needMultipleProcess() {
        return false;
    }
}
