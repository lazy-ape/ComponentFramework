package com.lzy.imagepicker;

import com.linked.annotion.Provider;
import com.utlife.commonbeanandresource.bean.ProcessConfig;
import com.utlife.routercore.UtlifeProvider;

/**
 * Created by xuqiang on 2017/3/24.
 */
@Provider(processName = ProcessConfig.MAIN_PROCESS_NAME)
public class ImagePickerProvider extends UtlifeProvider {
    @Override
    protected String getName() {
        return "imagePicker";
    }
}
