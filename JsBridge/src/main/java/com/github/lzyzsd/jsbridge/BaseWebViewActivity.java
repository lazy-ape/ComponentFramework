package com.github.lzyzsd.jsbridge;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.lzyzsd.library.R;
import com.google.gson.Gson;
import com.utlife.commonbeanandresource.bean.ImageItem;
import com.utlife.core.http.oss.ImageProvider;
import com.utlife.core.http.oss.UploadImageCallback;
import com.utlife.core.http.oss.UploadImageCallbackModel;
import com.utlife.core.http.oss.UploadImageUtils;
import com.utlife.core.utils.ToastUtils;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.action.EventAsyncChoosePictureResult;
import com.utlife.routercore.action.EventAsyncRouterResult;
import com.utlife.routercore.router.LocalRouter;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.RouterRequestUtil;
import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.routercore.tools.ProcessUtil;
import com.utlife.routercore.tools.ThreadPool;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by xuqiang on 2017/3/27.
 */

public class BaseWebViewActivity  extends AppCompatActivity{

    public static final int RESULT_CODE_ITEMS = 1004;
    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";

    public static final String TAG = "BaseWebViewActivity";
    protected BridgeWebView mWebView;
    protected Toolbar mToolbar;

    private HashMap<String,CallBackFunction> mRequestMap = new HashMap<>();
    private MaterialDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HermesEventBus.getDefault().register(this);

        mProgressDialog = new MaterialDialog.Builder(this)
                .progressIndeterminateStyle(true)
                .progress(true,0)
                .build();


        setContentView(R.layout.js_bridge_activity_base_webview);
        mWebView = (BridgeWebView) findViewById(R.id.bridge_webview);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);

        initToolbar();
        initWebView();
    }

    protected void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void initWebView(){
        mWebView.registerHandler("routerHandler", routerBridgeHandler);
        mWebView.setDefaultHandler(new DefaultHandler());

        Intent dataIntent = getIntent();
        if(dataIntent.hasExtra("url")){
            String url = dataIntent.getStringExtra("url");
            mWebView.loadUrl(url, new CallBackFunction() {
                @Override
                public void onCallBack(String data) {
                    Log.e(TAG,data);
                }
            });
        }
    }


    //结合router进行页面跳转
    private BridgeHandler routerBridgeHandler = new BridgeHandler() {
        @Override
        public void handler(String data, final CallBackFunction function) {
            Log.e(TAG,data);
            try {
                //解析参数
                JSONObject jsonObject = new JSONObject(data);
                String domain = "";
                if(jsonObject.has("domain")){
                    domain = jsonObject.getString("domain");
                }
                String provider = jsonObject.getString("provider");
                String action = jsonObject.getString("action");
                String params = "";
                if (jsonObject.has(params)) {
                    params = jsonObject.getString("params");
                }
                final String requestId = BridgeUtil.getMD5(data + System.currentTimeMillis());
                //根据provider和action构建routerRequest
                RouterRequest routerRequest = RouterRequestUtil.obtain(BaseWebViewActivity.this)
                        .domain(TextUtils.isEmpty(domain)?
                                ProcessUtil.getProcessName(BaseWebViewActivity.this, ProcessUtil.getMyProcessId()):
                                domain)
                        .id(requestId)
                        .provider(provider)
                        .action(action);
                //查找出action，获取params对应的实体类，用于解析传递的params 的 json
                final UtlifeAction utlifeAction = LocalRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                        .findRequestAction(routerRequest);
                if(!TextUtils.isEmpty(params)) {
                    //解析params
                    Gson gson = new Gson();
                    routerRequest.reqeustObject(gson.fromJson(params, utlifeAction.getParamBean()));
                }
                LocalRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                        .rxRoute(BaseWebViewActivity.this,routerRequest)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.from(ThreadPool.getThreadPoolSingleton()))
                        .subscribe(new Action1<UtlifeActionResult>() {
                            @Override
                            public void call(UtlifeActionResult utlifeActionResult) {
                                if(utlifeActionResult == null ||
                                        utlifeActionResult.getCode() == UtlifeActionResult.CODE_NEED_ASYNC_CALLBACK){
                                    //如果返回null 则表示结果会通过eventBus发送
                                    //此处将requestId和function保存，以便接收到event后再通过function回调给web
                                    mRequestMap.put(requestId,function);
                                }else{
                                    Gson gson = new Gson();
                                    function.onCallBack(gson.toJson(utlifeActionResult));
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                UtlifeActionResult result = new UtlifeActionResult.Builder().code(UtlifeActionResult.CODE_ERROR)
                                        .msg(throwable.getMessage())
                                        .data("")
                                        .result(null)
                                        .build();
                                Gson gson = new Gson();
                                function.onCallBack(gson.toJson(result));
                                throwable.printStackTrace();
                            }
                        });
            }catch (Exception e){
                UtlifeActionResult result = new UtlifeActionResult.Builder().code(UtlifeActionResult.CODE_ERROR)
                        .msg(e.getMessage())
                        .data("")
                        .result(null)
                        .build();
                Gson gson = new Gson();
                function.onCallBack(gson.toJson(result));
                e.printStackTrace();
            }
        }
    };

    /**
     * 发送数据
     * @param data
     */
    protected void send(String data){
        if(mWebView != null) {
            mWebView.send(data);
        }
    }

    /**
     * 发送数据
     * @param data
     * @param function  发送结果回调
     */
    protected void send(String data,CallBackFunction function){
        if(mWebView != null) {
            mWebView.send(data, function);
        }
    }


    //接收异步发送的结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventAsyncRouterResult result) {
        if(result != null && result.requestId != null){
            CallBackFunction function = mRequestMap.remove(result.requestId);
            if(function != null && result.actionResult != null){
                Gson gson = new Gson();
                function.onCallBack(gson.toJson(result.actionResult));
            }
        }
    }

    //接收选择图片
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiverImages(final EventAsyncChoosePictureResult result){
        if(result != null && result.requestId != null && result.itemsResult != null){
            List<ImageItem> imageItems = result.itemsResult;
            mProgressDialog.setContent(getString(R.string.js_bridge_upload_image));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            //多线程上传图片
            UploadImageUtils.getInstance(this).uploadImageByMultiThread(covert(imageItems), true, null,
                    new UploadImageCallback<List<UploadImageCallbackModel>>() {
                        @Override
                        public void complete(List<UploadImageCallbackModel> callback) {
                            super.complete(callback);
                            if(mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                ToastUtils.showLong(BaseWebViewActivity.this, getString(R.string.js_bridge_upload_success));

                                //将上传成功的图片url回调给web
                                List<String> imagePaths = new ArrayList<>();
                                for (UploadImageCallbackModel model : callback){
                                    imagePaths.add(model.filePath);
                                }
                                UtlifeActionResult utlifeActionResult = new UtlifeActionResult.Builder()
                                        .code(UtlifeActionResult.CODE_SUCCESS)
                                        .msg("success")
                                        .result(imagePaths)
                                        .build();
                                CallBackFunction function = mRequestMap.remove(result.requestId);
                                if(function != null){
                                    Gson gson = new Gson();
                                    function.onCallBack(gson.toJson(utlifeActionResult));
                                }
                            }
                        }
                    });
        }
    }

    private   List<TempImageProvider> covert(List<ImageItem> items){
        List<TempImageProvider> tempImageProviders = new ArrayList<>();
        for (ImageItem item : items){
            TempImageProvider provider = new TempImageProvider(item);
            tempImageProviders.add(provider);
        }
        return tempImageProviders;
    }

    class TempImageProvider implements ImageProvider{
        private ImageItem imageItem;
        public TempImageProvider(ImageItem item){
            this.imageItem = item;
        }
        @Override
        public String getImagePath() {
            return imageItem.path;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HermesEventBus.getDefault().unregister(this);
    }

    private void register(){
        if(!HermesEventBus.getDefault().isRegistered(this)){
            HermesEventBus.getDefault().register(this);
        }
    }

    private void unRegister(){
        if(HermesEventBus.getDefault().isRegistered(this)){
            HermesEventBus.getDefault().unregister(this);
        }
    }

    public static void startActivity(Context context, String url){
        Intent i = new Intent (context,BaseWebViewActivity.class);
        i.putExtra("url",url);
        context.startActivity(i);
    }

}
