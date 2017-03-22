package com.utlife.user.greedywallet;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.loader.ImageLoader;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by xuqiang on 2017/3/21.
 */

public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        DrawableTypeRequest typeRequest = Glide.with(activity)
                .load(path);
        if(imageView instanceof RoundedImageView){
            typeRequest.asBitmap();
            typeRequest.into(imageView);
        }else{
            typeRequest.into(imageView);
        }
    }

    @Override
    public void clearMemoryCache() {

    }
}
