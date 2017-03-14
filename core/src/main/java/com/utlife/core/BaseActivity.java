package com.utlife.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.utlife.core.state.ContentState;
import com.utlife.core.state.EmptyState;
import com.utlife.core.state.ErrorState;
import com.utlife.core.state.NonState;
import com.utlife.core.state.ProgressState;
import com.utlife.core.state.ShowState;
import com.utlife.core.utils.ResourceUtil;
import com.utlife.core.utils.StatusBarUtil;
import com.utlife.core.widget.ProgressBarCircularIndeterminate;



/**
 * Created by sll on 2016/3/9.
 */
public abstract class BaseActivity extends AppCompatActivity {

  public boolean isPrepare = false;
  private LayoutInflater inflater;
  private View mContentView;
  private ShowState mEmptyState, mProgressState, mErrorState, mContentState, mLoginState,
          mCollectState;
  private Animation mAnimIn, mAnimOut;
  private ShowState mLastState = new NonState();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inflater = LayoutInflater.from(this);
    ViewGroup main = (ViewGroup) inflater.inflate(R.layout.epf_layout,null);
    setContentView(main);

    View content = onCreateContentView(inflater);
    View error = onCreateContentErrorView(inflater);
    View empty = onCreateContentEmptyView(inflater);
    View progress = onCreateProgressView(inflater);

    replaceViewById(main, R.id.epf_content, content);
    replaceViewById(main, R.id.epf_error, error);
    replaceViewById(main, R.id.epf_empty, empty);
    replaceViewById(main, R.id.epf_progress, progress);

    mContentView = main;

    mAnimIn = onCreateAnimationIn();
    mAnimOut = onCreateAnimationOut();

    initStates();
    isPrepare = true;

    initUI();
    setTranslucentStatus(isApplyStatusBarTranslucency());
    setStatusBarColor(isApplyStatusBarColor());
    AppManager.getAppManager().addActivity(this);
  }

  public Animation onCreateAnimationIn() {
    return AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
  }

  public Animation onCreateAnimationOut() {
    return AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
  }

  private void initStates() {

    mEmptyState = new EmptyState();
    mProgressState = new ProgressState();
    mErrorState = new ErrorState();
    mContentState = new ContentState();

    initState(mEmptyState);
    initState(mProgressState);
    initState(mErrorState);
    initState(mContentState);
  }

  private void initState(ShowState state) {
    state.setAnimIn(mAnimIn);
    state.setAnimOut(mAnimOut);
    state.setRootView(mContentView);
  }

  //Override this method to change error view
  public View onCreateContentErrorView(LayoutInflater inflater) {
    View error = inflater.inflate(R.layout.error_view_layout, null);
    error.findViewById(R.id.btnReload).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        reloadClick();
      }
    });
    return error;
  }

  public View onCreateContentEmptyView(LayoutInflater inflater) {
    View empty = inflater.inflate(R.layout.empty_view_layout, null);
    empty.findViewById(R.id.btnReload).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        reloadClick();
      }
    });
    return empty;
  }

  public View onCreateProgressView(LayoutInflater inflater) {
    View loading = inflater.inflate(R.layout.loading_view_layout, null);
    ProgressBarCircularIndeterminate progressBar =
            (ProgressBarCircularIndeterminate) loading.findViewById(R.id.progress_view);
    progressBar.setBackgroundColor(ResourceUtil.getThemeColor(this));
    return loading;
  }
  private void replaceViewById(ViewGroup container, int viewId, View newView) {
    if (newView == null) {
      return;
    }
    newView.setId(viewId);
    View oldView = container.findViewById(viewId);
    int index = container.indexOfChild(oldView);
    container.removeView(oldView);
    container.addView(newView, index);

    newView.setVisibility(View.GONE);
  }

  public void showContent(boolean animate) {
    if (mLastState == mContentState) {
      return;
    }
    mContentState.show(animate);
    mLastState.dismiss(animate);
    mLastState = mContentState;
  }

  public void showEmpty(boolean animate) {
    if (mLastState == mEmptyState) {
      return;
    }
    mEmptyState.show(animate);
    mLastState.dismiss(animate);
    mLastState = mEmptyState;
  }

  public void showError(boolean animate) {
    if (mLastState == mErrorState) {
      return;
    }
    mErrorState.show(animate);
    mLastState.dismiss(animate);
    mLastState = mErrorState;
  }

  public void showProgress(boolean animate) {
    if (mLastState == mProgressState) {
      return;
    }
    mProgressState.show(animate);
    mLastState.dismiss(animate);
    mLastState = mProgressState;
  }


  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  //Override this method to change content view
  public abstract View onCreateContentView(LayoutInflater inflater);
  /**
   * 初始化UI
   */
  public abstract void initUI();

  /**
   * is applyStatusBarTranslucency
   */
  protected abstract boolean isApplyStatusBarTranslucency();

  /**
   * set status bar translucency
   */
  protected void setTranslucentStatus(boolean on) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window win = getWindow();
      WindowManager.LayoutParams winParams = win.getAttributes();
      final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      if (on) {
        winParams.flags |= bits;
      } else {
        winParams.flags &= ~bits;
      }
      win.setAttributes(winParams);
    }
  }

  protected abstract boolean isApplyStatusBarColor();

  /**
   * use SystemBarTintManager
   */
  public void setStatusBarColor(boolean on) {
    if (on) {
      StatusBarUtil.setColor(this, ResourceUtil.getThemeColor(this), 0);
    }
  }

  /**
   * 重写该方法实现重新加载
   */
  public void reloadClick(){

  }


  public void reload() {
    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
    overridePendingTransition(0, 0);
    startActivity(intent);
  }

  public int getStatusBarHeight() {
    return ResourceUtil.getStatusBarHeight(this);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    AppManager.getAppManager().finishActivity(this);
  }


}
