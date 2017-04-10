package com.utlife.core.http.oss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.utlife.core.utils.SecurityUtils;
import com.utlife.core.utils.StringUtils;
import com.utlife.core.utils.ThreadManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.R.attr.data;

/**
 * Created by xuqiang on 2017/3/16.
 */

public class UploadImageUtils {

    public static final String TAG = UploadImageUtils.class.getClass().getSimpleName();
    final String baseUrl = "http://oh0s186nv.bkt.clouddn.com/";
    private static String mToken;
    private static Context mContext;

    public static void init(Context context,String token){
        mToken = token;
        mContext = context;
    }

    private static UploadImageUtils mInstance;
    private UploadManager mUploadManager;
    private Configuration mUploadConfig;
    private UploadImageUtils(Context context){
        mContext = context;
        mUploadConfig = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                .zone(Zone.zone2) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        mUploadManager = new UploadManager(mUploadConfig);
    }


    public static UploadImageUtils getInstance(Context context){
        if(mInstance == null || mContext == null){
            synchronized (UploadImageUtils.class){
                mInstance = new UploadImageUtils(mContext == null ? context :mContext);
            }
        }
        return mInstance;
    }

    //压缩图片
    public void compressImage(ImageProvider data, Point imageSize, final CompressCallback callback){
        final File finalUrl = new File(data.getImagePath());
        Compressor compressor = null;
        if (imageSize != null) {
            compressor = new Compressor.Builder(mContext)
                    .setMaxHeight(imageSize.y)
                    .setMaxWidth(imageSize.x)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .build();
        } else {
            compressor = Compressor.getDefault(mContext);
        }
        compressor.compressToFileAsObservable(finalUrl)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        callback.compressComplete(file.getAbsolutePath());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //出现异常直接上传
                        callback.compressComplete(finalUrl.getAbsolutePath());
                    }
                });

    }


    /**
     * 上传图片
     * @param needCompress  是否需要压缩
     * @param imageSize   压缩的大小
     */
    public void uploadImage(final ImageProvider data,
                            final UploadImageCallback<UploadImageCallbackModel> callback,boolean needCompress, Point imageSize){
        if(TextUtils.isEmpty(mToken)){
            throw new RuntimeException("七牛 token 未被初始化");
        }

        if(needCompress){
            compressImage(data, imageSize, new CompressCallback() {
                @Override
                public void compressComplete(String filePath) {
                    realUpload(data, filePath,callback);
                }
            });
        }else{
            realUpload(data, data.getImagePath(),callback);
        }

    }

    //真正的开始上传
    private void realUpload(final ImageProvider provider , String realPath, final UploadImageCallback<UploadImageCallbackModel> callback){
        if(mUploadManager == null){
            mUploadManager = new UploadManager(mUploadConfig);
        }
        mUploadManager.put(realPath, com.utlife.core.utils.FileUtil.getFileNameKey(realPath), mToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if(callback != null){
                    try{
                        if(info.isOK()){
                            UploadImageCallbackModel model = new UploadImageCallbackModel(provider,baseUrl + key,true);
                            callback.success(model);
                            callback.complete(model);
                        }else{
                            UploadImageCallbackModel model = new UploadImageCallbackModel(provider,null,false);
                            callback.fail(model);
                            callback.complete(model);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        },new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.i(TAG, key + ": " + percent);
                if (callback != null) {
                   callback.onProgress((float) percent);
                }
            }
        },null));
    }



    private List<UploadImage> mUploadImages = null;
    private UploadImageCallback mMultiUploadCallback = null;
    private UploadImageProgressHandler mHandler;
    private List<UploadImageCallbackModel> mUploadCallbackData = null;
    /**
     * 多线程上传图片
     * @param filePaths  上传图片的路径
     * @param callback  上传图片的回调
     */
    public void uploadImageByMultiThread(List<? extends ImageProvider> filePaths, final boolean needCompress, final Point imageSize, final UploadImageCallback callback){

        if(TextUtils.isEmpty(mToken)){
            throw new RuntimeException("七牛 token 未被初始化");
        }

        mMultiUploadCallback = callback;

        mUploadImages = initUploadImageData(filePaths);
        mUploadCallbackData = initUploadCallbackData(filePaths);
        mHandler = new UploadImageProgressHandler();

        for(int i = 0 ; i < filePaths.size() ;i++ ){
            final ImageProvider str = filePaths.get(i);
            if(needCompress){
                compressImage(str, imageSize, new CompressCallback() {
                    @Override
                    public void compressComplete(String filePath) {
                        realMultiThreadUpload(str,filePath);
                    }
                });
            }else{
                realMultiThreadUpload(str,str.getImagePath());
            }
        }

    }

    /**图片上传成功*/
    private final static int UPLOAD_IMAGE_SUCCESS=1;
    /**图片上传失败*/
    private final static int UPLOAD_IMAGE_FAIL=2;
    private void realMultiThreadUpload(final ImageProvider provider , final String realPath){
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                String ext = StringUtils.getExtByPath(realPath);
                final String uploadName = SecurityUtils.md5(realPath + System.currentTimeMillis()) + "." + ext;
                // 构造上传请求
                if(mUploadManager == null){
                    mUploadManager = new UploadManager(mUploadConfig);
                }
                mUploadManager.put(realPath, uploadName, mToken, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        if (info.isOK()) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = UPLOAD_IMAGE_SUCCESS;
                            msg.obj = new UploadImageCallbackModel(provider, baseUrl + key,true);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = mHandler.obtainMessage();
                            msg.what = UPLOAD_IMAGE_FAIL;
                            msg.obj = new UploadImageCallbackModel(provider,null,false);
                            mHandler.sendMessage(msg);
                        }
                    }
                },new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        Log.i(TAG, key + ": " + percent);
                    }
                },null));
            }
        });
    }

    /**
     * 关闭上传
     */
    public void destory(){
        ThreadManager.getInstance().stop();
        mUploadImages = null;
        mMultiUploadCallback = null;
    }

    /**更新上传后返回的数据*/
    private void updateUploadCallbackData(UploadImageCallbackModel model){
        if(model == null || mUploadCallbackData == null){
            return;
        }
        for(UploadImageCallbackModel m:mUploadCallbackData){
            if(m.data.getImagePath().equals(model.data.getImagePath())){
                m.filePath = model.filePath;
                m.isUploadSuccess = model.isUploadSuccess;
                break;
            }
        }
    }

    /**更新正在上传的数据*/
    private void updateUploadImageStatus(String path,boolean isSuccess){
        if(mUploadImages == null){
            return ;
        }
        for(UploadImage uploadImage : mUploadImages){
            if(path.equals(uploadImage.extras.getImagePath())){
                uploadImage.isUploading = false;
                uploadImage.isUploadSuccess = isSuccess;
                break;
            }
        }
    }

    /**初始化上传图片的数据*/
    private List<UploadImage> initUploadImageData(List<? extends ImageProvider> filePaths){
        List<UploadImage> uploadImages = new ArrayList<>();
        if(filePaths != null){
            for (ImageProvider s : filePaths){
                UploadImage uploadImage = new UploadImage();
                uploadImage.isUploading = true;
                uploadImage.isUploadSuccess = false;
                uploadImage.extras = s;
                uploadImages.add(uploadImage);
            }
        }
        return uploadImages;
    }

    /**初始化上传后返回的数据*/
    private List<UploadImageCallbackModel> initUploadCallbackData(List<? extends ImageProvider> filePaths){
        List<UploadImageCallbackModel> uploadImages = new ArrayList<>();
        if(filePaths != null){
            for (ImageProvider s : filePaths){
                UploadImageCallbackModel uploadImage = new UploadImageCallbackModel(s,null,false);
                uploadImages.add(uploadImage);
            }
        }
        return uploadImages;
    }


    /**图片上传成功后的接收器，用于判断是否图片全部上传完成*/
    private class UploadImageProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            UploadImageCallbackModel data = (UploadImageCallbackModel) msg.obj;
            updateUploadCallbackData(data);
            switch (what){
                case UPLOAD_IMAGE_SUCCESS:
                    if(mMultiUploadCallback != null){
                        mMultiUploadCallback.success(data);
                    }
                    updateUploadImageStatus(data.data.getImagePath(),true);
                    break;
                case UPLOAD_IMAGE_FAIL:
                    if(mMultiUploadCallback != null){
                        mMultiUploadCallback.fail(data);
                    }
                    updateUploadImageStatus(data.data.getImagePath(),false);
                    break;
            }
            if(isUploadComplete()){
                if(mMultiUploadCallback != null){
                    mMultiUploadCallback.complete(mUploadCallbackData);
                }
                /**完全上传成功后调用destory*/
                destory();
            }
        }
    }

    /**判断是否全部上传完成*/
    private boolean isUploadComplete(){
        if(mUploadImages == null){
            return true;
        }
        for(UploadImage uploadImage : mUploadImages){
            if(uploadImage.isUploading){
                return false;
            }
        }
        return true;
    }



    private class UploadImage {
        /**是否正在上传*/
        public boolean isUploading;
        /**上传是否成功*/
        public boolean isUploadSuccess = false;
        /**携带的额外数据*/
        public ImageProvider extras;

    }

    interface CompressCallback{
        void compressComplete(String filePath);
    }

}
