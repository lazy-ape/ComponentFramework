package com.utlife.user.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.utlife.routercore.UtlifeRouterApplication;
import com.utlife.routercore.router.LocalRouter;
import com.utlife.routercore.router.RouterRequestUtil;
import com.utlife.user.R;
import com.utlife.user.adapterhelper.SubModuleAdapter;
import com.utlife.user.adapterhelper.SubViewPagerAdapter;
import com.utlife.user.bean.ModuleItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DelegateAdapter adapter = new DelegateAdapter(layoutManager);

        List<String> viewpagerData = new ArrayList<>();
        viewpagerData.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2128568119,2020679487&fm=23&gp=0.jpg");
        viewpagerData.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1406668637,2493237568&fm=23&gp=0.jpg");
        viewpagerData.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1443817543,4124882906&fm=23&gp=0.jpg");
        adapter.addAdapter(new SubViewPagerAdapter(this,viewpagerData));

        List<ModuleItem> moduleItems = new ArrayList<>();
        moduleItems.add(new ModuleItem(R.mipmap.park,"停车场"));
        moduleItems.add(new ModuleItem(R.mipmap.pay,"付款买单"));
        moduleItems.add(new ModuleItem(R.mipmap.sale,"超值低价"));
        moduleItems.add(new ModuleItem(R.mipmap.timelimit,"限时优惠"));
        adapter.addAdapter(new SubModuleAdapter(this,moduleItems));

        recyclerView.setAdapter(adapter);

    }
}
