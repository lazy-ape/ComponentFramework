package com.utlife.core.baseadapter.normal.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.utlife.core.baseadapter.ListViewHolder;
import com.utlife.core.baseadapter.normal.ItemViewDelegate;

import java.util.List;

public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T>
{

    public CommonAdapter(Context context, final int layoutId, List<T> datas)
    {
        super(context, datas);

        addItemViewDelegate(new ItemViewDelegate<T,ListViewHolder>()
        {
            @Override
            public int getItemViewLayoutId()
            {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position)
            {
                return true;
            }

            @Override
            public void convert(ListViewHolder holder, T t, int position)
            {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ListViewHolder viewHolder, T item, int position);

}
