package com.utlife.core.state;




import com.utlife.core.R;

/**
 * Created by sll on 2015/3/13.
 */
public class ErrorState extends AbstractShowState {
  @Override public void show(boolean animate) {
    showViewById(R.id.epf_error, animate);
  }

  @Override public void dismiss(boolean animate) {
    dismissViewById(R.id.epf_error, animate);
  }
}
