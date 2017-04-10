package com.utlife.user.greedywallet;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.utlife.core.BaseActivity;
import com.utlife.core.http.okhttp.HttpLoggingInterceptor;

import java.util.concurrent.ExecutionException;

/**
 * Created by xuqiang on 2017/4/6.
 */

public class SecondActivity extends BaseActivity {

    @Override
    public View onCreateContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.gw_activity_second,null);
    }

    ImageView imageView;
    @Override
    public void initUI() {
        imageView = (ImageView) findViewById(R.id.image);
        new AsyncTask<Void,Void,Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    Bitmap theBitmap = Glide.with(SecondActivity.this)
                            .load("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=352521598,665609374&fm=23&gp=0.jpg")
                            .asBitmap()
                            .into(100,100)
                            .get();
                    return theBitmap;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }else{
                    Log.e("Second", "bitmap is null");
                }
            }
        }.execute();
        showContent(true);
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
