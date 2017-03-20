package com.utlife.core.http.oss;



/**
 * 图片上传成功后的回调数据
 * Created by rubin on 2016-06-01.
 */
public class UploadImageCallbackModel {
    public UploadImageCallbackModel(ImageProvider data,String filePath){
        this(data,filePath,false);
    }

    public UploadImageCallbackModel(ImageProvider data,String filePath,boolean isSuccess){
        this.data = data;
        this.filePath = filePath;
        this.isUploadSuccess = isSuccess;
    }

    /**上传时的数据*/
    public ImageProvider data;
    /**上传成功后图片的访问路径*/
    public String filePath;
    /**是否上传成功*/
    public boolean isUploadSuccess;
}
