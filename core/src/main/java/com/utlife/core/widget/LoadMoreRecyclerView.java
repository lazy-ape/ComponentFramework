package com.utlife.core.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * 支持上拉加载更多的RecyclerView
 */
public class LoadMoreRecyclerView extends RecyclerView {
    /**
     * item 类型
     */
    public final static int TYPE_HEADER = -0x10001;//头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = -0x10002;//底部--往往是loading_more

    private boolean mIsFooterEnable = true;//是否允许加载更多

    /**
     * 自定义实现了头部和底部加载更多的adapter
     */
    private AutoLoadAdapter mAutoLoadAdapter;

    /**
     * 加载更多的监听-业务需要实现加载数据
     */
    private ILoadMoreCallback mListener;

    private LoadMoreFooter mLoadMoreFooter;

    private OnScrollListener mOuterOnScrollListener;
    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mListener && mIsFooterEnable
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最好一条--及加载更多
     * </pre>
     */
    private void init() {
        mLoadMoreFooter = new LoadMoreFooter(getContext());
        super.setOnScrollListener(new OnScrollListener() {

            private int mCurrentScrollState;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mOuterOnScrollListener != null){
                    mOuterOnScrollListener.onScrollStateChanged(recyclerView,newState);
                }
                mCurrentScrollState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mOuterOnScrollListener != null){
                    mOuterOnScrollListener.onScrolled(recyclerView,dx,dy);
                }
                if (mLoadMoreFooter.getState() == LoadMoreFooter.State.Loading
                        || mLoadMoreFooter.getState() == LoadMoreFooter.State.End) {
                    return;
                }

                if (null != mListener
                        && mIsFooterEnable
                        && dy > 0
                        &&mCurrentScrollState != SCROLL_STATE_IDLE) {
                    int lastVisiblePosition = getLastVisiblePosition();

                    //Footer在隐藏的状态下获取lastvisibleposition不包括footer
                    if(mLoadMoreFooter.getState() == LoadMoreFooter.State.Idle){
                        lastVisiblePosition ++ ;
                    }
                    if (lastVisiblePosition + 1 >= mAutoLoadAdapter.getItemCount()) {
                        mLoadMoreFooter.setState(LoadMoreFooter.State.Loading);
                        mAutoLoadAdapter.notifyItemChanged(mAutoLoadAdapter.getItemCount() - 1);
                        mListener.loadMore();
                    }
                }
            }
        });
    }

    public void addOnScrollListener(OnScrollListener listener){
        this.mOuterOnScrollListener = listener;
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setLoadMoreListener(ILoadMoreCallback listener) {
        mListener = listener;
    }


    /**
     *
     */
    private class AutoLoadAdapter extends Adapter<ViewHolder> {

        /**
         * 数据adapter
         */
        private Adapter mInternalAdapter;

        private boolean mIsHeaderEnable;
        private int mHeaderResId;

        public AutoLoadAdapter(Adapter adapter) {
            mInternalAdapter = adapter;
            mIsHeaderEnable = false;
            mInternalAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (headerPosition == position && mIsHeaderEnable && mHeaderResId > 0) {
                return TYPE_HEADER;
            }
            if (footerPosition == position && mIsFooterEnable) {
                return TYPE_FOOTER;
            }

            return mInternalAdapter.getItemViewType(position);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        mHeaderResId, parent, false));
            }
            if (viewType == TYPE_FOOTER) {
                return new FooterViewHolder(mLoadMoreFooter.getView());
            } else {
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        public class FooterViewHolder extends ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(lp);
            }
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER) {
                mInternalAdapter.onBindViewHolder(holder, position);
            }
        }



        /**
         * 需要计算上加载更多和添加的头部俩个
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (mIsFooterEnable) count++;
            if (mIsHeaderEnable) count++;
            return count;
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if(lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(getItemViewType(holder.getPosition()) == TYPE_HEADER);
                p.setFullSpan(getItemViewType(holder.getPosition()) == TYPE_FOOTER);
            }

            LayoutManager manager = getLayoutManager();
            if(manager != null && manager instanceof GridLayoutManager){
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (getItemViewType(position) == TYPE_HEADER) || ( getItemViewType(position) == TYPE_FOOTER)
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }

        }

        public void setHeaderEnable(boolean enable) {
            mIsHeaderEnable = enable;
        }

        public void addHeaderView(int resId) {
            mHeaderResId = resId;
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mAutoLoadAdapter = new AutoLoadAdapter(adapter);
        }
        super.swapAdapter(mAutoLoadAdapter, true);
    }





    /**
     * 切换layoutManager
     *
     * 为了保证切换之后页面上还是停留在当前展示的位置，记录下切换之前的第一条展示位置，切换完成之后滚动到该位置
     * 另外切换之后必须要重新刷新下当前已经缓存的itemView，否则会出现布局错乱（俩种模式下的item布局不同），
     * RecyclerView提供了swapAdapter来进行切换adapter并清理老的itemView cache
     *
     * @param layoutManager
     */
    public void switchLayoutManager(LayoutManager layoutManager) {
        int firstVisiblePosition = getFirstVisiblePosition();
//        getLayoutManager().removeAllViews();
        setLayoutManager(layoutManager);
        //super.swapAdapter(mAutoLoadAdapter, true);
        getLayoutManager().scrollToPosition(firstVisiblePosition);
    }

    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    private int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 添加头部view
     *
     * @param resId
     */
    public void addHeaderView(int resId) {
        mAutoLoadAdapter.addHeaderView(resId);
    }

    /**
     * 设置头部view是否展示
     * @param enable
     */
    public void setHeaderEnable(boolean enable) {
        mAutoLoadAdapter.setHeaderEnable(enable);
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param autoLoadMore
     */
    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        mIsFooterEnable = autoLoadMore;
    }


    /**
     * 通知更多的数据已经加载
     *
     * 每次加载完成之后添加了Data数据，用notifyItemRemoved来刷新列表展示，
     * 而不是用notifyDataSetChanged来刷新列表
     *
     * @param hasMore
     */
    public void loadComplete(boolean hasMore) {
        if(hasMore){
            mLoadMoreFooter.setState(LoadMoreFooter.State.Idle);
        }else{
            mLoadMoreFooter.setState(LoadMoreFooter.State.End);
        }
    }
}