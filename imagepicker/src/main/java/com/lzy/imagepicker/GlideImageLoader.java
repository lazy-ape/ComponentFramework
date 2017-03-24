package com.lzy.imagepicker;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.loader.ImageLoader;

/**
 * Created by xuqiang on 2017/3/21.
 */

public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
        .load(path)
        .placeholder(R.mipmap.default_image)
        .error(R.mipmap.default_image)
        .into(imageView);

    }

    @Override
    public void clearMemoryCache() {

    }
}
