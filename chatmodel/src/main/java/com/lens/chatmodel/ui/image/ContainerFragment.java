package com.lens.chatmodel.ui.image;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.data.bean.LongImageBean;
import com.lensim.fingerchat.components.widget.CircleProgressView;


/**
 * User: qii
 * Date: 14-4-30
 */
public class ContainerFragment extends Fragment {

  public static ContainerFragment newInstance(String url, AnimationRect rect,
      LongImageBean longImageBean,
      boolean animationIn, boolean firstOpenPage) {
    ContainerFragment fragment = new ContainerFragment();
    Bundle bundle = new Bundle();
    bundle.putString("url", url);
    bundle.putParcelable("rect", rect);
    bundle.putParcelable("longImage", longImageBean);
    bundle.putBoolean("animationIn", animationIn);
    bundle.putBoolean("firstOpenPage", firstOpenPage);
    fragment.setArguments(bundle);
    return fragment;
  }

  private TextView wait;
  private TextView error;
  private CircleProgressView progressView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.gallery_container_layout, container, false);
    progressView = (CircleProgressView) view.findViewById(R.id.loading);
    wait = (TextView) view.findViewById(R.id.wait);
    error = (TextView) view.findViewById(R.id.error);

    Bundle bundle = getArguments();
    String url = bundle.getString("url");
    boolean animateIn = bundle.getBoolean("animationIn");
    bundle.putBoolean("animationIn", false);

    displayPicture(url, animateIn);

    return view;
  }


  private void displayPicture(String path, boolean animateIn) {
    GalleryAnimationActivity activity = (GalleryAnimationActivity) getActivity();

    AnimationRect rect = getArguments().getParcelable("rect");
    LongImageBean longImageBean = getArguments().getParcelable("longImage");
    boolean firstOpenPage = getArguments().getBoolean("firstOpenPage");

    if (firstOpenPage) {
      if (animateIn) {
        ObjectAnimator animator = activity.showBackgroundAnimate();
        animator.start();
      } else {
        activity.showBackgroundImmediately();
      }
      getArguments().putBoolean("firstOpenPage", false);
    }

    Fragment fragment = null;
    fragment = GeneralPictureFragment.newInstance(path, rect, longImageBean, animateIn);
    getChildFragmentManager().beginTransaction().replace(R.id.child, fragment)
        .commitAllowingStateLoss();

  }

  public void animationExit(ObjectAnimator backgroundAnimator) {
    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.child);
    if (fragment instanceof GeneralPictureFragment) {
      GeneralPictureFragment child = (GeneralPictureFragment) fragment;
      child.animationExit(backgroundAnimator);
    }
  }

  public boolean canAnimateCloseActivity() {
    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.child);
    if (fragment instanceof GeneralPictureFragment) {
      return true;
    } else {
      return false;
    }
  }

}
