package com.utlife.user.greedywallet;

import com.linked.annotion.Provider;
import com.utlife.routercore.UtlifeProvider;

/**
 * Created by xuqiang on 2017/3/24.
 */
@Provider(processName = "com.utlife.user")
public class GreedyWalletProvider extends UtlifeProvider {
    @Override
    protected String getName() {
        return "greedywallet";
    }
}
