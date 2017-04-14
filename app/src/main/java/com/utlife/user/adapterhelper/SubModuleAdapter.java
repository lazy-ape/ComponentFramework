package com.utlife.user.adapterhelper;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.bumptech.glide.Glide;
import com.utlife.core.utils.DisplayUtil;
import com.utlife.user.R;
import com.utlife.user.bean.ModuleItem;

import java.util.List;

/**
 * Created by xuqiang on 2017/4/14.
 */

public class SubModuleAdapter extends DelegateAdapter.Adapter<SubModuleAdapter.ViewHolder>{

    private Context mContext;
    private List<ModuleItem> items;
    public SubModuleAdapter(Context context, List<ModuleItem> items){
        this.mContext = context;
        this.items = items;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new GridLayoutHelper(4, items.size(),DisplayUtil.dip2px(mContext,5), DisplayUtil.dip2px(mContext,10));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_module,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModuleItem item = items.get(position);
        holder.text.setText(item.name);
        Glide.with(mContext)
                .load(item.imgUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_module);
            text = (TextView) itemView.findViewById(R.id.name_module);
        }
    }

}
