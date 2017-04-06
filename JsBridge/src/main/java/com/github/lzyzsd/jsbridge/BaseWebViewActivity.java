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
import android.webkit.WebView;
import android.widget.Toast;

import com.github.lzyzsd.library.R;
import com.google.gson.Gson;
import com.utlife.commonbeanandresource.bean.ImageItem;
import com.utlife.commonbeanandresource.bean.ImagePickerParams;
import com.utlife.routercore.UtlifeAction;
import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.router.LocalRouter;
import com.utlife.routercore.router.RouterRequest;
import com.utlife.routercore.router.RouterRequestUtil;
import com.utlife.routercore.router.UtlifeActionResult;
import com.utlife.routercore.tools.ProcessUtil;
import com.utlife.routercore.tools.ThreadPool;

import org.json.JSONObject;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xuqiang on 2017/3/27.
 */

public class BaseWebViewActivity  extends AppCompatActivity{

    public static final int RESULT_CODE_ITEMS = 1004;
    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 0x123;
    public static final String TAG = "BaseWebViewActivity";
    protected BridgeWebView mWebView;
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mWebView.registerHandler("routerHandler", new BridgeHandler() {
            @Override
            public void handler(String data, final CallBackFunction function) {
                Log.e(TAG,data);
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String domain = jsonObject.getString("domain");
                    String provider = jsonObject.getString("provider");
                    String action = jsonObject.getString("action");
                    String params = jsonObject.getString("params");

                    RouterRequest routerRequest = RouterRequestUtil.obtain(BaseWebViewActivity.this)
                            .domain(TextUtils.isEmpty(domain)?
                                    ProcessUtil.getProcessName(BaseWebViewActivity.this, ProcessUtil.getMyProcessId()):
                                    domain)
                            .provider(provider)
                            .action(action);
                    final UtlifeAction utlifeAction = LocalRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                            .findRequestAction(routerRequest);
                    if(!TextUtils.isEmpty(params)) {
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
                                    Gson gson = new Gson();
                                    function.onCallBack(gson.toJson(utlifeActionResult));
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
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(EXTRA_RESULT_ITEMS);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void startActivity(Context context, String url){
        Intent i = new Intent (context,BaseWebViewActivity.class);
        i.putExtra("url",url);
        context.startActivity(i);
    }

}
