package com.utlife.core.baseadapter.databinding.list;

import android.content.Context;
import android.databinding.ViewDataBinding;



import com.utlife.core.baseadapter.ListViewHolder;
import com.utlife.core.baseadapter.databinding.ItemViewDelegate;

import java.util.List;

public abstract class CommonListAdapter<T> extends MultiItemTypeListAdapter<T> {

    public CommonListAdapter(Context context, final int layoutId, final int variableId, List<T> datas) {
        super(context, datas);

        addItemViewDelegate(new ItemViewDelegate<T,ViewDataBinding>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public int getVariableId() {
                return variableId;
            }

            @Override
            public void covert(ViewDataBinding holder, T item, int position) {
                convert(holder,item,position);
            }
        });
    }
    protected abstract void convert(ViewDataBinding viewHolder, T item, int position);
}
