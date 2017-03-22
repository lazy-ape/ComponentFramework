package com.utlife.core.baseadapter.normal;




/**
 * Created by zhy on 16/6/22.
 */
public interface ItemViewDelegate<T,H>
{

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(H holder, T t, int position);

}
