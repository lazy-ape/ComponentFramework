package com.lzy.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

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

    public static final String WEB_REQUEST_ID = "web_request_id";

    @Override
    public boolean isAsync(Context context, RouterRequest routerRequest) {
        return false;
    }

    @Override
    public UtlifeActionResult invoke(Context context, RouterRequest routerRequest) {
        if(context instanceof Activity) {
            ImagePickerParams params = (ImagePickerParams) routerRequest.getRequestObject();
            Intent intent = new Intent(context, ImageGridActivity.class);
            if(!TextUtils.isEmpty(routerRequest.getId())) {
                intent.putExtra(WEB_REQUEST_ID, routerRequest.getId());
            }
            if(params != null){
                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setMultiMode(params.isMultiMode);
                imagePicker.setSelectLimit(params.selectLimit);
                imagePicker.setCrop(params.crop);
                ((Activity)context).startActivityForResult(intent, params.requestCode == 0 ? 0x101 : params.requestCode);
            }else{
                ((Activity)context).startActivityForResult(intent, 0x101);
            }

        }else{
            throw new  RuntimeException("imagepicker need context must instance of activity");
        }
        return new UtlifeActionResult.Builder().code(UtlifeActionResult.CODE_NEED_ASYNC_CALLBACK).msg("success").data("").build();
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
