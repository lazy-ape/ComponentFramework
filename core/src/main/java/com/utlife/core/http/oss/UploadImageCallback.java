package com.utlife.core.http.oss;

/**
 * Created by xuqiang on 2017/3/16.
 */

public abstract class UploadImageCallback<T> {
    /**
     * 上传进度
     * 单张上传的时候才会回调，多张上传的时候不会回调
     * @param progress  当前页上传进度
     */
    public void onProgress(float progress){

    }

    /**
     * 上传成功的回调，每上传成功一张就会回调
     * 多张上传的时候才会回调，单张的时候不回调
     * @param path 上传成功后的图片的路径
     */
    public  void success(UploadImageCallbackModel path){

    }

    /**
     * 全部上传完成后才会回调
     * 单张或者多张都会进行回调
     */
    public void complete(T callback){

    }

    /**
     * 上传失败的回调
     * 存在上传失败的图片时才会回掉，全部上传成功不进行回调
     * @param path  上传失败的图片的路径
     */
    public void fail(UploadImageCallbackModel path){

    }
}
