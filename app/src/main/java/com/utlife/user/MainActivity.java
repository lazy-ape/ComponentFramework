package com.utlife.user;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.router.LocalRouter;
import com.utlife.routercore.router.RouterRequestUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_greedywallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LocalRouter.getInstance(UtlifeRouterApplication.getUtlifeRouterApplication())
                            .rxRoute(MainActivity.this, RouterRequestUtil.obtain(MainActivity.this)
                            .provider("greedywallet")
                            .action("index")
                            );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
