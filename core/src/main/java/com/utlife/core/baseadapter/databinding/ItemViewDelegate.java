package com.utlife.core.baseadapter.databinding;

/**
 * Created by xuqiang on 2016/12/22.
 */

public interface ItemViewDelegate<T,H> {
    /**
     * item 的 layout id
     * @return
     */
    public abstract int getItemViewLayoutId();

    /**
     * 当前数据是否与当前viewType匹配
     * @param item
     * @param position
     * @return
     */
    public abstract boolean isForViewType(T item, int position);


    /**
     * 布局文件中定义的变量id  即是BR.item
     * @return
     */
    public abstract int getVariableId();

    /**
     * 更新数据
     * @param holder
     * @param item
     * @param position
     */
    public abstract void covert(H holder, T item, int position);

}
