package com.utlife.user.adapterhelper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.bumptech.glide.Glide;
import com.utlife.user.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by xuqiang on 2017/4/14.
 */

public class SubViewPagerAdapter extends DelegateAdapter.Adapter<SubViewPagerAdapter.ViewHolder> {

    private Context mContext;
    private List<String> data;
    public SubViewPagerAdapter(Context context,List<String> data){
        this.mContext = context;
        this.data = data;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_main_viewpager,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<View> views = new ArrayList<>();
        for(String s : data){
            ImageView imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.adapter_imageview,null);
            Glide.with(mContext)
                    .load(s)
                    .into(imageView);
            views.add(imageView);
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views);
        holder.viewPager.setAdapter(adapter);
        holder.circleIndicator.setViewPager(holder.viewPager);
        adapter.registerDataSetObserver(holder.circleIndicator.getDataSetObserver());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ViewPager viewPager;
        CircleIndicator circleIndicator;
        public ViewHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.viewpager_main);
            circleIndicator = (CircleIndicator) itemView.findViewById(R.id.indicator_main);
        }
    }

}
