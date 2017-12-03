// Generated code from Butter Knife. Do not modify!
package com.example.android.camera2basic;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainFragment_ViewBinding implements Unbinder {
  private MainFragment target;

  @UiThread
  public MainFragment_ViewBinding(MainFragment target, View source) {
    this.target = target;

    target.mTakePictureButton = Utils.findRequiredViewAsType(source, R.id.fragment_main_take_button, "field 'mTakePictureButton'", Button.class);
    target.mChoosePictureButton = Utils.findRequiredViewAsType(source, R.id.fragment_main_choose_button, "field 'mChoosePictureButton'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTakePictureButton = null;
    target.mChoosePictureButton = null;
  }
}
