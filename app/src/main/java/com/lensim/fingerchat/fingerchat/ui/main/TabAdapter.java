package com.lensim.fingerchat.fingerchat.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import com.lensim.fingerchat.commons.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by LL130386 on 2018/8/8.
 */

public class TabAdapter extends FragmentPagerAdapter {

    private List<PageModel> pageModels;
    FragmentManager fm;

    public TabAdapter(FragmentManager fm) {
        super(fm);
        pageModels = new ArrayList<>();
        this.fm = fm;
    }

    public void setPageModels(List<PageModel> list) {
        if (pageModels != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (PageModel model : pageModels) {
                ft.remove(model.getFragment());
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        pageModels = list;
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        return pageModels.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return pageModels.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

}
