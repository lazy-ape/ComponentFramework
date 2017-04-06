package com.lzy.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.linked.annotion.Action;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.utlife.commonbeanandresource.bean.ImagePickerParams;
import com.utlife.commonbeanandresource.bean.ProcessConfig;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.UtlifeActionResult;

/**
 * Created by xuqiang on 2017/3/24.
 */
@Action(processName = ProcessConfig.MAIN_PROCESS_NAME, providerName = "imagePicker")
public class ImagePickerAction implements UtlifeAction {
    @Override
    public boolean isAsync(Context context, RouterRequest routerRequest) {
        return false;
    }

    @Override
    public UtlifeActionResult invoke(Context context, RouterRequest routerRequest) {
        if(context instanceof Activity) {
            ImagePickerParams params = (ImagePickerParams) routerRequest.getRequestObject();
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setMultiMode(params.isMultiMode);
            imagePicker.setSelectLimit(params.selectLimit);
            imagePicker.setCrop(params.crop);
            Intent intent = new Intent(context, ImageGridActivity.class);
            ((Activity)context).startActivityForResult(intent, params.requestCode);
        }else{
            throw new  RuntimeException("imagepicker need context must instance of activity");
        }
        return new UtlifeActionResult.Builder().code(UtlifeActionResult.CODE_SUCCESS).msg("success").data("").build();
    }

    @Override
    public String getName() {
        return "choosePicture";
    }

    @Override
    public Class<?> getParamBean() {
        return ImagePickerParams.class;
    }
}
