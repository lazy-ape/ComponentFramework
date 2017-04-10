package com.github.lzyzsd.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by xuqiang on 2017/4/10.
 */
@SuppressLint("SetJavaScriptEnabled")
public class CompatWebView extends BridgeWebView  {

    public CompatWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CompatWebView(Context context) {
        super(context);
    }

    private float startx;
    private float starty;
    private float offsetx;
    private float offsety;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startx = event.getX();
                starty = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                offsetx = Math.abs(event.getX() - startx);
                offsety = Math.abs(event.getY() - starty);
                if (offsetx > offsety) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                   getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
