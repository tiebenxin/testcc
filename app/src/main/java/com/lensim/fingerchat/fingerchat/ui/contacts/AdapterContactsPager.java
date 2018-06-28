package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.lensim.fingerchat.commons.base.BaseFragment;
import java.util.List;

/**
 * Created by LL130386 on 2017/11/29.
 */

public class AdapterContactsPager extends FragmentPagerAdapter {

  private List<BaseFragment> fragments;
  private List<String> titles;

  public void setTitles(List<String> l) {
    titles = l;
    notifyDataSetChanged();
  }

  public AdapterContactsPager(FragmentManager fm, List<BaseFragment> l, List<String> t) {
    super(fm);
    fragments = l;
    titles = t;

  }

  public void setFragments(List<BaseFragment> l) {
    fragments = l;
    notifyDataSetChanged();
  }

  @Override
  public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    //super.destroyItem(container, position, object);
  }

  @Override
  public CharSequence getPageTitle(int position) {
    if (titles == null || titles.size() <= 0) {
      return "";
    }
    return titles.get(position);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }

}
