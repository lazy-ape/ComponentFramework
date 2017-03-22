package com.utlife.user.greedywallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.Utils;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.utlife.core.BaseActivity;
import com.utlife.core.baseadapter.RecyclerViewHolder;
import com.utlife.core.baseadapter.normal.recycler.CommonAdapter;
import com.utlife.core.bean.BaseResponseData;
import com.utlife.core.http.oss.ImageProvider;
import com.utlife.core.http.oss.UploadImageCallback;
import com.utlife.core.http.oss.UploadImageCallbackModel;
import com.utlife.core.http.oss.UploadImageUtils;
import com.utlife.core.http.retrofit.BaseSubscriber;
import com.utlife.core.http.retrofit.ExceptionHandle;
import com.utlife.core.utils.PreferencesUtils;
import com.utlife.user.greedywallet.api.UserApi;
import com.utlife.user.greedywallet.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String PRE_QINIU_TOKEN = "pre_qiniu_token";
    private static final int CHOOSE_PICTURE = 1;

    ActivityMainBinding activityMainBinding;
    UserApi userApi;

    @Override
    public View onCreateContentView(LayoutInflater inflater) {
        activityMainBinding = DataBindingUtil.inflate(inflater,R.layout.activity_main,null,false);
        userApi = new UserApi(mRequestHelper);
        return activityMainBinding.getRoot();
    }
    ProgressDialog progressDialog;
    List<String> imageData = new ArrayList<>();
    CommonAdapter commonAdapter;
    @Override
    public void initUI() {
        ImagePicker.getInstance().setImageLoader(new GlideImageLoader());

        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("请稍后...");

        showProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showContent(false);
            }
        },3500);
        userApi.getToken()
                .subscribe(new BaseSubscriber<BaseResponseData<String>>(this) {
                    @Override
                    public void onError(ExceptionHandle.ResponseThrowable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(BaseResponseData<String> s) {
                        activityMainBinding.content.setText(s.getData());
                        PreferencesUtils.put(mActivity, PRE_QINIU_TOKEN, s.getData());
                        UploadImageUtils.init(mActivity, s.getData());
                    }
                });

        activityMainBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.getInstance().setMultiMode(true);
                Intent i = new Intent(mActivity, ImageGridActivity.class);
                startActivityForResult(i,CHOOSE_PICTURE);
            }
        });

        activityMainBinding.recyclerview.setLayoutManager(new GridLayoutManager(mActivity,3));
        commonAdapter = new CommonAdapter<String>(this,R.layout.image_item,imageData) {

            @Override
            public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            protected void convert(RecyclerViewHolder holder, String o, int position) {
                holder.setSize(R.id.rounded_image_view, Utils.getImageItemWidth(mActivity), Utils.getImageItemWidth(mActivity));
                Glide.with(mActivity)
                        .load(o)
                        .asBitmap()
                        .placeholder(R.color.colorPrimary)
                        .into((ImageView) holder.getView(R.id.rounded_image_view));
            }
        };
        activityMainBinding.recyclerview.setAdapter(commonAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ImagePicker.RESULT_CODE_ITEMS){
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                if (data != null && requestCode == requestCode) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    List<ImageProvideItem> uploadData = new ArrayList<>();
                    String path = "";
                    if(images != null){
                        for (ImageItem item :images){
                            path += (item.path + "\n");
                            uploadData.add(new ImageProvideItem(item));
                        }
                    }

                    Toast.makeText(this, "返回成功", Toast.LENGTH_SHORT).show();
                    Log.e("main",path);

                    if(uploadData.size() > 0){
                        progressDialog.show();
                        UploadImageUtils.getInstance(mActivity).uploadImageByMultiThread(uploadData, true, null,
                                new UploadImageCallback<List<UploadImageCallbackModel>>() {
                            @Override
                            public void onProgress(float progress) {
                                super.onProgress(progress);
                            }

                            @Override
                            public void success(UploadImageCallbackModel path) {
                                super.success(path);
                                Log.e("main", "success : " + path.filePath);
                            }

                            @Override
                            public void complete(List<UploadImageCallbackModel> callback) {
                                super.complete(callback);
                                progressDialog.dismiss();
                                for (UploadImageCallbackModel model : callback){
                                    if(model.isUploadSuccess){
                                        imageData.add(model.filePath);
                                    }
                                }
                                commonAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void fail(UploadImageCallbackModel path) {
                                super.fail(path);
                                progressDialog.dismiss();
                            }
                        });
                    }

                } else {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    class ImageProvideItem implements ImageProvider {

        private ImageItem imageItem;
        public ImageProvideItem(ImageItem item){
            this.imageItem = item;
        }
        @Override
        public String getImagePath() {
            return imageItem.path;
        }
    }
    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return false;
    }

}
