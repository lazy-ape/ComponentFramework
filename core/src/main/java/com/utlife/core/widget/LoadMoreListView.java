package com.utlife.core.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by xuqiang on 2016/12/23.
 */

public class LoadMoreListView extends ListView {
    public LoadMoreListView(Context context) {
        super(context);
        init();
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private LoadMoreFooter mLoadMoreFooter;

    private OnScrollListener mOnScrollListener;
    private ILoadMoreCallback mLoadMoreCallback;

    private int mCurrentScrollState;

    private void init() {
        mLoadMoreFooter = new LoadMoreFooter(getContext());
        addFooterView(mLoadMoreFooter.getView());

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    view.invalidateViews();
                }

                mCurrentScrollState = scrollState;

                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll(absListView, firstVisibleItem,
                            visibleItemCount, totalItemCount);
                }

                if (!isScreenFull(absListView,firstVisibleItem,visibleItemCount,totalItemCount)) {
                    mLoadMoreFooter.setState(LoadMoreFooter.State.Idle);
                    return;
                }

                if (mLoadMoreFooter.getState() == LoadMoreFooter.State.Loading
                        || mLoadMoreFooter.getState() == LoadMoreFooter.State.End) {
                    return;
                }

                if (firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != (getHeaderViewsCount() + getFooterViewsCount())
                        && mLoadMoreCallback != null
                        && mCurrentScrollState != SCROLL_STATE_IDLE) {
                    mLoadMoreFooter.setState(LoadMoreFooter.State.Loading);
                    mLoadMoreCallback.loadMore();
                }
            }
        });
    }


    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }


    public void setState(LoadMoreFooter.State status) {
        mLoadMoreFooter.setState(status);
    }

    public void setState(LoadMoreFooter.State status, long delay) {
        mLoadMoreFooter.setState(status, delay);
    }

    public void loadComplete(boolean hasMore) {
        if (hasMore) {
            mLoadMoreFooter.setState(LoadMoreFooter.State.Idle);
        } else {
            mLoadMoreFooter.setState(LoadMoreFooter.State.End);
        }
    }

    public void setLoadMoreCallback(ILoadMoreCallback callback) {
        this.mLoadMoreCallback = callback;
    }

    /**
     * is more than one screen
     */
    private boolean isScreenFull(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
        if (visibleItemCount == totalItemCount) {
           View lastView = absListView.getChildAt(visibleItemCount - 1);
            if(lastView != null){
                if(lastView.getBottom() >= getBottom()){
                    return true;
                }
            }
        }else if(totalItemCount > visibleItemCount){
            return true;
        }
        return false;
    }

}
