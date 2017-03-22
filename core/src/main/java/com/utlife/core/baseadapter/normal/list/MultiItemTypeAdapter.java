package com.utlife.core.baseadapter.normal.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.utlife.core.baseadapter.ListViewHolder;
import com.utlife.core.baseadapter.normal.ItemViewDelegate;
import com.utlife.core.baseadapter.normal.ItemViewDelegateManager;

import java.util.List;

public class MultiItemTypeAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;

    private ItemViewDelegateManager mItemViewDelegateManager;


    public MultiItemTypeAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    private boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    @Override
    public int getViewTypeCount() {
        if (useItemViewDelegateManager())
            return mItemViewDelegateManager.getItemViewDelegateCount();
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (useItemViewDelegateManager()) {
            int viewType = mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
            return viewType;
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(mDatas.get(position), position);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ListViewHolder viewHolder = null ;
        if (convertView == null)
        {
            View itemView = LayoutInflater.from(mContext).inflate(layoutId, parent,
                    false);
            viewHolder = new ListViewHolder(mContext, itemView, parent, position);
            viewHolder.mLayoutId = layoutId;
            onViewHolderCreated(viewHolder,viewHolder.getConvertView());
        } else
        {
            viewHolder = (ListViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
        }


        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    protected void convert(ListViewHolder viewHolder, T item, int position) {
        mItemViewDelegateManager.convert(viewHolder, item, position);
    }

    public void onViewHolderCreated(ListViewHolder holder , View itemView )
    {}

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
