package com.utlife.user.ui.main;

import com.baidu.location.BDLocation;
import com.utlife.core.ui.BasePresenter;
import com.utlife.core.ui.BaseView;

/**
 * Created by xuqiang on 2017/4/11.
 */

public class MainContract {
    interface View extends BaseView{
        void updateLocation(BDLocation location);
    }

    interface Presenter extends BasePresenter<View>{

    }
}
