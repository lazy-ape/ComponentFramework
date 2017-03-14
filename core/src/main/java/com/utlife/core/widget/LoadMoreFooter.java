package com.utlife.core.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.utlife.core.R;


/**
 * Created by xuqiang on 2016/12/23.
 */

public class LoadMoreFooter {

    public static enum State{
        Idle,End,Loading
    }

    private State mState = State.Idle;

    private Context mContext;
    private View mFootView;

    private View mLoadingView;
    private View mEndView;

    public LoadMoreFooter(Context context){
        this(context, R.layout.load_more_footer);
    }
    public LoadMoreFooter(Context context, int layoutId){
        this.mContext = context;
        mFootView = LayoutInflater.from(context).inflate(layoutId,null);
        mFootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mLoadingView = mFootView.findViewById(R.id.layout_loading_data);
        mEndView = mFootView.findViewById(R.id.no_more_data);

        mFootView.setVisibility(View.GONE);
        setState(State.Idle);
    }

    public View getView() {
        return mFootView;
    }

    public State getState() {
        return mState;
    }

    public void setState(final State state, long delay) {
        mFootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(state);
            }
        }, delay);
    }

    public void setState(State status) {
        if (mState == status) {
            return;
        }
        mState = status;

        switch (status) {
            case Loading:
                mFootView.setVisibility(View.VISIBLE);
                mEndView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case End:
                mFootView.setVisibility(View.VISIBLE);
                mEndView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                break;
            default:
                mEndView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mFootView.setVisibility(View.GONE);
                break;
        }
    }


}
