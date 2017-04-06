package com.utlife.user.greedywallet;

import com.linked.annotion.Modules;
import com.lzy.imagepicker.ImagePickerApplicationLogic;
import com.utlife.commonbeanandresource.bean.ProcessConfig;
import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.router.WideRouter;
import com.utlife.routercore.tools.ProcessUtil;

/**
 * Created by xuqiang on 2017/3/30.
 */
@Modules(modules = {"greedywallet","imagePicker"})
public class GreedyWalletApplication extends UtlifeRouterApplication {
    @Override
    public void initializeAllProcessRouter() {
        WideRouter.registerLocalRouter("com.utlife.user.greedywallet",GreedyWalletRouterConnectService.class);
    }

    @Override
    protected void initializeLogic() {
        registerApplicationLogic("com.utlife.user.greedywallet",999,ImagePickerApplicationLogic.class);
        registerApplicationLogic("com.utlife.user.greedywallet",998, GreedyWalletApplicationLogic.class);
    }

    @Override
    public boolean needMultipleProcess() {
        return false;
    }
}
